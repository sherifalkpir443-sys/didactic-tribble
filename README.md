# NOVA AI — Android Assistant

Arabic-first native Android assistant with a separate Node.js AI gateway.

## Current Android capabilities
- Arabic text chat UI, on-demand speech recognition, and text-to-speech.
- Procedural stylized 3D face preview (prototype only).
- Basic technical device snapshot with preview and explicit upload consent.
- List and launch apps that expose Android launcher activities.
- Import selected text, Markdown, JSON, and XML files through Android's document picker (64 KiB maximum); review and confirm before upload.
- Open Android Wi-Fi, Bluetooth, sound, display, location, and general Settings pages after confirmation. The user performs setting changes.\n- Keep the latest 20 conversation turns in app-private local storage, review them, and clear the local history.
- No hidden data collection, background recording, sandbox bypass, or universal phone-control claim.

## AI gateway
POST /v1/chat accepts { "message": "..." } and returns { "reply": "..." }.
The Node.js gateway uses the OpenAI Responses API. Provider credentials remain in the server environment. The default model is gpt-6-astra; configure OPENAI_MODEL for a model enabled on your API project. The gateway enables the Responses API web_search tool for current information; the model decides when to search. It uses store=false, input limits, basic per-process rate limiting, and an upstream timeout.

Deploy the backend behind HTTPS, then build Android with:

    gradle -PNOVA_API_BASE_URL=https://your-gateway.example assembleDebug

Never put provider keys or a shared production token in the Android project or Git.

## Android access limits
Android does not let an ordinary app read every file, message, call log, or another app's private database. Personal-data access must use the relevant Android API, narrow permission or system picker, and explicit user consent. Some privileges are restricted by Android and Google Play policy.

## CI and verification
- Android workflow builds a debug APK and runs unit tests.
- Backend workflow runs Node.js tests.
- A successful CI build proves compilation and artifact creation, not physical-device installation or production service availability.

## Remaining before production
- Deploy backend and configure provider key privately.
- Add production per-user authentication and distributed rate limiting.
- Add encryption-at-rest and export controls for chat history.
- Add separately consented PDF/image, contacts/calendar, or media integrations as justified.
- Replace stylized 3D prototype with licensed realistic character assets and rigging if required.
- Run physical-device, accessibility, privacy, and security testing; create signed release build.

## Documentation
- [Technical specification](docs/TECHNICAL_SPEC.md)
- [Security model](docs/SECURITY_MODEL.md)
- [Device integration](docs/DEVICE_INTEGRATION.md)
- [Roadmap](docs/ROADMAP.md)
- [Backend setup](backend/README.md)

## Android toolchain
Kotlin 2.0.21 · Android Gradle Plugin 8.7.3 · Gradle 8.9 · JDK 17 · minSdk 26 / targetSdk 35
