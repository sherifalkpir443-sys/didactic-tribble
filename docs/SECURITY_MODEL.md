# NOVA AI — Security and Permissions Model

## Current permissions
- RECORD_AUDIO: declared and requested only after the user taps the voice button.
- INTERNET: used only for the optional HTTPS AI gateway.
- No contacts, SMS, call logs, location, camera, accessibility service, overlay, or broad file permissions are requested.

## AI gateway
- The Android client requires HTTPS and sends only the user-entered message to POST /v1/chat.
- The server returns a reply field.
- Provider API keys and model credentials must exist only on the backend, never in source control or the APK.
- The backend must authenticate clients, enforce rate limits, validate request size, and define privacy/retention behavior before production use.

## Device actions
The only device action currently implemented is opening Android Settings after an explicit confirmation dialog. It does not change settings. Future actions must each have an allow-list entry, explain the exact operation, request only the required runtime permission, invoke Android system confirmation where required, re-check authorization immediately before execution, and fail closed on denial or ambiguity. The app must not bypass Android security boundaries.

## Voice and data
No background microphone recording is implemented. Cloud history is not stored by this starter. Before enabling history, define retention, deletion, export, encryption, and provider data processing.

## Required tests
- Microphone grant, denial, revocation, and unavailable recognition service.
- HTTPS required, absent endpoint, network timeout, non-2xx response, malformed JSON.
- Device Settings action cancelled and confirmed.
- Future action tests for revoked permissions, ambiguity, duplicate requests, and system-level restrictions.
