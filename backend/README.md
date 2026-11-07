# NOVA AI Gateway

A minimal Node.js 20+ HTTPS-behind-a-proxy gateway for the Android app. It keeps the provider API key on the server and implements the Android contract:

- `GET /health` — reports service status and whether a provider key is configured; never returns the key.
- `POST /v1/chat` — accepts `{"message":"..."}` and returns `{"reply":"..."}`.

## Configure

Copy `.env.example` to `.env` in your deployment environment (do not commit it), then set:

- `OPENAI_API_KEY`: secret stored only in the server environment.
- `OPENAI_MODEL`: model available to your API project (defaults to `gpt-6-astra`).
- `PORT`: server port (defaults to 8080).
- `NOVA_GATEWAY_TOKEN`: optional bearer token for controlled staging. If enabled, clients must send it. Do not embed a shared production token in a publicly distributed APK; use proper per-user authentication at a trusted gateway instead. For a controlled staging build only, the Android client can be configured with `-PNOVA_GATEWAY_TOKEN=...`; this value is embedded in the APK and is therefore extractable.

Run locally:

    npm test
    OPENAI_API_KEY=replace-me npm start

The service listens on port 8080. Deploy behind a TLS-terminating reverse proxy or managed HTTPS platform. Set the Android build property `NOVA_API_BASE_URL` to the resulting HTTPS origin.

## Security baseline

- Provider credentials are never sent to Android.
- HTTPS is required by the Android client.
- The gateway has a 16 KiB request cap, 8,000-character message limit, a basic per-client rate limit, and upstream timeout.
- Provider errors and response bodies are not exposed to the app.
- Responses API requests set `store: false`.
- The service does not persist conversations or log message content.

Before production: add durable per-user authentication, distributed rate limiting, abuse monitoring without content logging, deployment health monitoring, secret rotation, and privacy/legal review. Set `TRUST_PROXY=true` only when the service is behind a trusted reverse proxy that overwrites `X-Forwarded-For`; otherwise the gateway uses the direct socket peer. The in-memory limiter is per process and is not a substitute for edge protection.
