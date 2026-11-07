# NOVA AI — Android Assistant

Arabic-first native Android assistant starter. Version 0.2.0 adds a configurable HTTPS AI gateway client, persona selector/2D preview, and a user-confirmed shortcut to Android Settings.

## Implemented
- Kotlin Android app, Arabic-first UI.
- Text entry, on-demand Arabic speech recognition, and Arabic text-to-speech.
- Microphone permission requested only after the user taps the voice button.
- Male/female presentation selector with a simple 2D face preview.
- User-confirmed opening of Android Settings; no settings are changed automatically.
- Optional HTTPS client for a NOVA backend endpoint.

## AI backend connection
The app expects an HTTPS backend implementing POST /v1/chat with request JSON {"message":"..."} and response JSON {"reply":"..."}.

Configure the base URL at build time:

    gradle -PNOVA_API_BASE_URL=https://your-domain.example assembleDebug

No API key is stored in the app or repository. A compatible backend must be deployed and configured before real AI responses can work. Without it, the app reports that AI is not connected.

## Honest status / not yet complete
This is not yet a release-ready assistant. The realistic 3D avatar is not implemented (the current face is a 2D preview). Most device-control actions are not enabled; the only action is opening Android Settings after confirmation. The backend is an interface/client contract, not a deployed AI service. APK/device verification depends on a successful GitHub Actions build and subsequent installation test.

## Build
Use Android Studio, JDK 17, Android SDK Platform 35, and Gradle 8.9. Run `gradle assembleDebug`; the debug APK should be produced at `app/build/outputs/apk/debug/app-debug.apk`.

## Documentation
- [Technical specification](docs/TECHNICAL_SPEC.md)
- [Security and permissions model](docs/SECURITY_MODEL.md)
- [Roadmap](docs/ROADMAP.md)

## Toolchain
Kotlin 2.0.21 · Android Gradle Plugin 8.7.3 · Gradle 8.9 · JDK 17 · minSdk 26 / targetSdk 35
