import test from "node:test";
import assert from "node:assert/strict";
import { createServer } from "../server.mjs";

async function withServer(options, run) {
  const server = createServer(options);
  await new Promise(resolve => server.listen(0, "127.0.0.1", resolve));
  const address = server.address();
  try { await run(`http://127.0.0.1:${address.port}`); }
  finally { await new Promise((resolve, reject) => server.close(error => error ? reject(error) : resolve())); }
}

test("health endpoint does not expose secrets", async () => {
  await withServer({ env: { OPENAI_API_KEY: "private-secret" } }, async base => {
    const response = await fetch(`${base}/health`);
    assert.equal(response.status, 200);
    assert.deepEqual(await response.json(), { status: "ok", aiConfigured: true, model: "gpt-6-astra" });
  });
});

test("chat endpoint refuses requests when provider key is absent", async () => {
  await withServer({ env: {} }, async base => {
    const response = await fetch(`${base}/v1/chat`, {
      method: "POST", headers: { "content-type": "application/json" },
      body: JSON.stringify({ message: "أهلا" })
    });
    assert.equal(response.status, 503);
    assert.deepEqual(await response.json(), { error: "ai_provider_not_configured" });
  });
});

test("chat endpoint validates message", async () => {
  await withServer({ env: { OPENAI_API_KEY: "test" } }, async base => {
    const response = await fetch(`${base}/v1/chat`, {
      method: "POST", headers: { "content-type": "application/json" },
      body: JSON.stringify({ message: " " })
    });
    assert.equal(response.status, 400);
  });
});

test("optional gateway token is enforced", async () => {
  await withServer({ env: { NOVA_GATEWAY_TOKEN: "test-token" } }, async base => {
    const response = await fetch(`${base}/v1/chat`, {
      method: "POST", headers: { "content-type": "application/json" },
      body: JSON.stringify({ message: "hello" })
    });
    assert.equal(response.status, 401);
  });
});

test("upstream output is converted to the mobile reply contract", async () => {
  const fetchImpl = async (_url, options) => {
    assert.equal(options.method, "POST");
    const body = JSON.parse(options.body);
    assert.equal(body.store, false);
    return new Response(JSON.stringify({
      output: [{ type: "message", role: "assistant", content: [{ type: "output_text", text: "أهلا بيك" }] }]
    }), { status: 200, headers: { "content-type": "application/json" } });
  };
  await withServer({ env: { OPENAI_API_KEY: "test", OPENAI_MODEL: "test-model" }, fetchImpl }, async base => {
    const response = await fetch(`${base}/v1/chat`, {
      method: "POST", headers: { "content-type": "application/json" },
      body: JSON.stringify({ message: "أهلا" })
    });
    assert.equal(response.status, 200);
    assert.deepEqual(await response.json(), { reply: "أهلا بيك" });
  });
});


test("gateway rate limits repeated requests from one client", async () => {
  await withServer({ env: {} }, async base => {
    let last;
    for (let index = 0; index < 21; index += 1) {
      last = await fetch(`${base}/v1/chat`, {
        method: "POST", headers: { "content-type": "application/json" },
        body: JSON.stringify({ message: "hello" })
      });
    }
    assert.equal(last.status, 429);
    assert.equal(last.headers.get("retry-after") !== null, true);
  });
});

test("malformed JSON is rejected", async () => {
  await withServer({ env: { OPENAI_API_KEY: "test" } }, async base => {
    const response = await fetch(`${base}/v1/chat`, {
      method: "POST", headers: { "content-type": "application/json" },
      body: "{"
    });
    assert.equal(response.status, 400);
    assert.deepEqual(await response.json(), { error: "invalid_json" });
  });
});
