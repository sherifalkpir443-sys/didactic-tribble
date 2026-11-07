# NOVA AI — Security and Permissions Model

## Baseline
The app follows least privilege. Version 0.1.0 declares only RECORD_AUDIO because voice input is implemented. It does not request contacts, SMS, call logs, location, camera, accessibility, overlay, or broad file access.

## Microphone consent flow
1. User taps the microphone button.
2. App checks RECORD_AUDIO.
3. If missing, Android's runtime permission prompt appears.
4. Speech recognition starts only after a grant.
5. On denial, the app explains that text input remains available.
6. Speech recognition is destroyed with the Activity lifecycle.

## Future device-action policy
Each action adapter must be explicitly invoked by the user, explain the exact action, request only the required permission at the moment of use, use Android system confirmation/role UI when required, re-check permission immediately before execution, and fail closed on denial, ambiguity, or revocation. The app must never bypass Android sandboxing or platform security controls.

## AI credentials
- Never commit API keys, refresh tokens, private keys, or production credentials.
- Never package provider secrets in the APK.
- Use a server-side gateway with scoped credentials, authentication, rate limits and audit controls.
- Keep secrets out of logs and crash reports.

## Data handling
Cloud conversation history is not enabled. Before enabling it, define data transmitted, purpose, retention/deletion, provider processing, TLS, server access controls, and user-visible export/deletion controls.

## Security tests
Test permission denied/revoked, unavailable speech service, malformed recognition results, network interruption and gateway authentication failures (future), duplicate action requests (future), and ambiguous action intent (future).
