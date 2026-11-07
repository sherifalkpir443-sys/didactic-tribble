# NOVA AI — Security and Permissions Model

## Android permissions
- RECORD_AUDIO is requested only after the user taps the voice input button.
- INTERNET is used for the configured HTTPS gateway.
- No contacts, SMS, call logs, location, camera, accessibility, overlay, or broad file permissions are requested.

## AI gateway
- The provider API key exists only in the backend hosting environment.
- Android communicates with the backend over HTTPS.
- The backend sends Responses API requests with store=false and does not persist conversation text.
- Request size and message length are limited; upstream calls have a timeout and a basic per-process rate limit.
- Provider error bodies and secrets are never returned to the app.
- Optional NOVA_GATEWAY_TOKEN is for controlled staging only. Do not ship a shared production token in a public APK.
- Production deployment still requires per-user authentication, distributed rate limiting, TLS, secret rotation, and monitoring.

## Device actions
The only implemented device action is opening Android Settings after a clear confirmation dialog. The app does not change settings automatically. Every future action must have a narrow allow-list entry, explain the exact operation, request only the required permission, use Android system confirmation/role UI where required, re-check permission before execution, and fail closed on denial or ambiguity. Never bypass Android sandbox or platform security.

## Voice and data
No background microphone recording is implemented. The backend sends store=false and does not keep chat history. Any future history feature requires user-visible retention, deletion, and export controls.

## Required tests
- Microphone grant, denial, revocation, and unavailable recognition service.
- HTTPS enforcement, absent endpoint, network timeout, non-2xx response, malformed JSON.
- Gateway request size and message length, authentication, rate limiting, provider errors, and empty provider output.
- Settings action cancelled and confirmed.
- Future device-action tests for revoked permissions, ambiguity, duplicate requests, and platform restrictions.
