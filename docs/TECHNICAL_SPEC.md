# NOVA AI — Technical Specification

Status: implementation baseline 0.2.0  
Application ID: com.nova.ai  
Platform: Android 8.0+ (API 26+)  
Language: Egyptian Arabic (ar-EG)

## Implemented
- Native Kotlin Android application with an Arabic-first interface.
- Text input and on-demand speech recognition using Android SpeechRecognizer.
- Arabic text-to-speech using Android TextToSpeech.
- Microphone permission requested only after a microphone tap.
- Male/female presentation selector with a simple 2D face preview. This is not the planned realistic 3D avatar.
- Explicitly confirmed action to open Android Settings. No silent device setting changes are performed.
- Optional HTTPS AI gateway client. It sends POST /v1/chat with {"message":"..."} and expects {"reply":"..."}.

## AI gateway configuration
Set Gradle project property NOVA_API_BASE_URL to the HTTPS base URL of a deployed backend. Example local Gradle invocation:

    gradle -PNOVA_API_BASE_URL=https://your-domain.example assembleDebug

The backend must implement POST /v1/chat and return JSON with a non-empty reply field. No provider credentials are stored in the APK or repository. Without a deployed compatible backend URL, the app clearly reports that AI is not connected.

## Architecture
| Layer | Current status |
|---|---|
| Android presentation | Native starter UI |
| Speech input/output | Android APIs connected |
| AI gateway | HTTPS client connected to a documented contract; backend not deployed |
| Persona selector | Basic male/female UI state and 2D preview |
| Realistic 3D avatar | Not implemented; requires selected 3D runtime and model assets |
| Device actions | Explicit confirmation before opening Android Settings; other actions not enabled |
| Storage/history | Not implemented |

## Build baseline
- Android Gradle Plugin 8.7.3
- Kotlin 2.0.21
- Gradle 8.9
- JDK 17
- compileSdk/targetSdk 35; minSdk 26

## Security requirements
- HTTPS only for the AI gateway.
- No provider API key or private credential in the Android app.
- No hidden microphone use or background recording.
- No device action without a direct user action and any required Android permission/system confirmation.
- Fail closed when gateway URL is absent or network calls fail.
- No bypassing Android sandbox or system permission controls.

## Remaining release requirements
A deployed backend and provider configuration, a real 3D model/runtime, full action-by-action permission/confirmation design, automated tests, successful CI build, and device installation testing remain outstanding.
