import http from "node:http";
import { pathToFileURL } from "node:url";

const MAX_BODY_BYTES = 64 * 1024;
const WINDOW_MS = 60_000;
const MAX_REQUESTS_PER_WINDOW = 20;

function send(res, status, body) {
  res.writeHead(status, {
    "content-type": "application/json; charset=utf-8",
    "cache-control": "no-store",
    "x-content-type-options": "nosniff"
  });
  res.end(JSON.stringify(body));
}

function extractText(response) {
  if (typeof response?.output_text === "string" && response.output_text.trim()) {
    return response.output_text.trim();
  }
  const chunks = [];
  for (const item of response?.output ?? []) {
    if (item?.type !== "message" || item?.role !== "assistant") continue;
    for (const part of item.content ?? []) {
      if (part?.type === "output_text" && typeof part.text === "string") chunks.push(part.text);
    }
  }
  return chunks.join("\n").trim();
}

export function createServer({
  env = process.env,
  fetchImpl = fetch,
  clock = () => Date.now()
} = {}) {
  const buckets = new Map();

  return http.createServer(async (req, res) => {
    const url = new URL(req.url ?? "/", "http://localhost");

    if (req.method === "GET" && url.pathname === "/health") {
      return send(res, 200, {
        status: "ok",
        aiConfigured: Boolean(env.OPENAI_API_KEY),
        model: env.OPENAI_MODEL || "gpt-6-astra"
      });
    }

    if (req.method !== "POST" || url.pathname !== "/v1/chat") {
      return send(res, 404, { error: "not_found" });
    }

    const expectedToken = env.NOVA_GATEWAY_TOKEN;
    if (expectedToken && req.headers.authorization !== `Bearer ${expectedToken}`) {
      return send(res, 401, { error: "unauthorized" });
    }

    // Trust X-Forwarded-For only when a trusted reverse proxy overwrites it.
    const forwardedFor = req.headers["x-forwarded-for"];
    const clientKey = env.TRUST_PROXY === "true" && typeof forwardedFor === "string"
      ? forwardedFor.split(",")[0].trim()
      : (req.socket.remoteAddress || "unknown");
    const now = clock();
    if (buckets.size > 5_000) {
      for (const [key, value] of buckets) {
        if (now - value.start >= WINDOW_MS) buckets.delete(key);
      }
    }
    const bucket = buckets.get(clientKey);
    if (!bucket || now - bucket.start >= WINDOW_MS) {
      buckets.set(clientKey, { start: now, count: 1 });
    } else if (bucket.count >= MAX_REQUESTS_PER_WINDOW) {
      res.setHeader("retry-after", String(Math.ceil((WINDOW_MS - (now - bucket.start)) / 1000)));
      return send(res, 429, { error: "rate_limited" });
    } else {
      bucket.count += 1;
    }

    if (!env.OPENAI_API_KEY) {
      return send(res, 503, { error: "ai_provider_not_configured" });
    }

    let raw = "";
    try {
      for await (const chunk of req) {
        raw += chunk;
        if (Buffer.byteLength(raw, "utf8") > MAX_BODY_BYTES) {
          return send(res, 413, { error: "request_too_large" });
        }
      }
      const body = JSON.parse(raw || "{}");
      const message = typeof body.message === "string" ? body.message.trim() : "";
      if (!message || message.length > 8_000) {
        return send(res, 400, { error: "message_required_or_too_long" });
      }

      const upstream = await fetchImpl("https://api.openai.com/v1/responses", {
        method: "POST",
        headers: {
          authorization: `Bearer ${env.OPENAI_API_KEY}`,
          "content-type": "application/json"
        },
        body: JSON.stringify({
          model: env.OPENAI_MODEL || "gpt-6-astra",
          instructions: "أنت NOVA AI، مساعد بيتكلم بالعربي المصري العامي. جاوب بوضوح وباختصار مفيد. لا تدّعي تنفيذ أي إجراء على الهاتف. لا تطلب كلمات مرور أو مفاتيح سرية.",
          input: message,
          max_output_tokens: 800,
          store: false
        }),
        signal: AbortSignal.timeout(30_000)
      });

      if (!upstream.ok) {
        return send(res, 502, { error: "ai_provider_error" });
      }
      const data = await upstream.json();
      const reply = extractText(data);
      if (!reply) return send(res, 502, { error: "empty_ai_response" });
      return send(res, 200, { reply });
    } catch (error) {
      if (error instanceof SyntaxError) return send(res, 400, { error: "invalid_json" });
      if (error?.name === "AbortError" || error?.name === "TimeoutError") {
        return send(res, 504, { error: "ai_provider_timeout" });
      }
      return send(res, 502, { error: "gateway_error" });
    }
  });
}

if (process.argv[1] && import.meta.url === pathToFileURL(process.argv[1]).href) {
  const port = Number(process.env.PORT || 8080);
  createServer().listen(port, "0.0.0.0", () => {
    process.stdout.write(`NOVA gateway listening on port ${port}\n`);
  });
}
