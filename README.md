# NOVA AI — Android Assistant

Arabic-first native Android assistant. The repository now includes an Android app, a procedural OpenGL ES 2.0 3D avatar preview, and a separate Node.js AI gateway.

## Android app
- Kotlin Android app with Arabic-first UI.
- Text entry, on-demand Arabic speech recognition, and Arabic text-to-speech.
- Microphone permission requested only after the user taps the voice button.
- Male/female presentation selector and a procedural stylized 3D face rendered locally with OpenGL ES 2.0. This is a 3D prototype, not a photorealistic rigged character.
- User-confirmed opening of Android Settings; no device settings are changed automatically.
- HTTPS client for the NOVA backend.

## AI gateway
The Android app expects POST /v1/chat with {"message":"..."} and returns {"reply":"..."}.

A Node.js gateway is included in [backend](backend/README.md). It uses the OpenAI Responses API server-side; the provider API key remains in the server environment and is never packaged in the APK. It sets store=false, applies request-size/message limits, a basic per-process rate limit, and an upstream timeout.

Configure the backend with OPENAI_API_KEY and OPENAI_MODEL. Deploy it behind HTTPS, then build Android with:

    gradle -PNOVA_API_BASE_URL=https://your-gateway.example assembleDebug

Do not put provider keys or a shared production token in the Android project. See backend/.env.example and backend/README.md.

## CI and verification
- Android workflow builds a debug APK and uploads the artifact.
- Backend workflow runs Node.js tests.
- A successful CI build proves compilation and artifact creation, not physical-device installation or production service availability.

## Remaining before production
- Deploy the backend and configure its provider key in a private hosting environment.
- Add per-user authentication and distributed edge rate limiting before public release.
- Replace the stylized 3D prototype with licensed, realistic 3D character assets and rigging if that visual quality is required.
- Implement any additional phone actions individually with explicit user consent and Android's supported permission/system flows.
- Run device testing, accessibility review, privacy review, and signed release build.

## Documentation
- [Technical specification](docs/TECHNICAL_SPEC.md)
- [Security model](docs/SECURITY_MODEL.md)
- [Roadmap](docs/ROADMAP.md)
- [Backend setup](backend/README.md)

## Android toolchain
Kotlin 2.0.21 · Android Gradle Plugin 8.7.3 · Gradle 8.9 · JDK 17 · minSdk 26 / targetSdk 35
