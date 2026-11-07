# NOVA AI — Security and Permissions Model

## Current Android access
- RECORD_AUDIO is requested only after the user taps voice input.
- INTERNET is used for the configured HTTPS gateway.
- ACCESS_NETWORK_STATE is used to read the active network transport.
- Launcher app discovery is limited to activities matching MAIN/LAUNCHER through Android package visibility. It does not enumerate private or all installed packages.
- Conversation history is stored in app-private SharedPreferences, capped to the latest 20 messages, and can be cleared by the user. The recent transcript is sent to the AI gateway only as context when a new message is sent.\n- File import uses ACTION_OPEN_DOCUMENT. The user selects each file; only text, Markdown, JSON, and XML content is accepted; the read limit is 64 KiB; upload requires a review and confirmation.
- Device snapshot fields are limited to non-sensitive technical status. The user reviews and approves each upload.
- Settings shortcuts open Android system pages only after confirmation; the user changes settings manually.

## Explicit exclusions
NOVA does not attempt to bypass Android sandboxing, read another app's private databases, silently collect all photos/files/messages, scrape notifications, capture keystrokes, or activate accessibility/device-admin privileges as a universal-control workaround. SMS/call-log permissions are restricted by Android/Google Play policy and are not added as general-purpose access.

## AI gateway
- The provider API key exists only in the backend hosting environment.
- Android communicates with the backend over HTTPS.
- The backend sends Responses API requests with store=false and does not keep chat history.
- Request size and message length are limited; upstream calls have a timeout and a basic per-process rate limit.
- Provider error bodies and secrets are never returned to the app.
- Optional NOVA_GATEWAY_TOKEN is for controlled staging only. Do not ship a shared production token in a public APK.
- Production deployment still requires per-user authentication, distributed rate limiting, TLS, secret rotation, and monitoring.

## Required tests
- Microphone grant, denial, revocation, and unavailable recognition service.
- File picker cancellation, unsupported MIME type, unreadable file, empty file, and file-size limit.
- App list visibility and launch failures on Android 11+.
- Settings action cancelled and confirmed.
- HTTPS enforcement, absent endpoint, network timeout, non-2xx response, malformed JSON.
- Gateway request size and message length, authentication, rate limiting, provider errors, and empty provider output.
- Future device-action tests for revoked permissions, ambiguity, duplicate requests, and platform restrictions.
