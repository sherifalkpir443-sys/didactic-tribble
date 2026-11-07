# NOVA AI — Technical Specification

Status: implementation baseline 0.3.0  
Application ID: com.nova.ai  
Platform: Android 8.0+ (API 26+)  
Language: Egyptian Arabic (ar-EG)

## Implemented
- Native Kotlin Android app with Arabic-first UI.
- Text input, on-demand SpeechRecognizer input, and Arabic (Egypt) TextToSpeech.
- Microphone permission requested only after explicit user tap.
- Male/female presentation selector.
- Procedural stylized 3D face preview rendered with OpenGL ES 2.0; not a photorealistic model, rig, or lip-sync character.
- User-confirmed shortcut to Android Settings; no setting is changed by the app.
- HTTPS-only Android client for POST /v1/chat with a {"message":"..."} request and {"reply":"..."} response.
- Node.js backend implementation using OpenAI Responses API with server-side credentials, store=false, request validation, basic rate limiting, and timeout.
- CI workflows for Android debug APK build and backend tests.

## Backend configuration
See backend/README.md. Configure OPENAI_API_KEY and OPENAI_MODEL only in the backend hosting environment. Deploy behind HTTPS, then set Android Gradle property NOVA_API_BASE_URL to the backend origin. Never put provider credentials in the APK or Git.

## Architecture
| Layer | Current status |
|---|---|
| Android presentation | Native starter UI |
| Speech | Android APIs connected |
| 3D avatar | Procedural OpenGL ES 2.0 prototype; realistic asset/rigging not implemented |
| AI gateway | Node.js backend source included; deployment and provider secret configuration remain external |
| Device actions | Confirmed opening of Android Settings only |
| Chat history | Not implemented |
| Backend authentication | Optional staging bearer token; production per-user authentication remains required |

## Build baseline
- Android Gradle Plugin 8.7.3
- Kotlin 2.0.21
- Gradle 8.9
- JDK 17
- compileSdk/targetSdk 35; minSdk 26
- Backend Node.js 20+

## Security
- HTTPS required by Android client.
- Provider key stored only in backend environment.
- Backend does not persist conversations and sends store=false to the Responses API.
- No hidden microphone use or background recording.
- No device action without explicit user interaction and required Android permission/system confirmation.
- No bypassing Android sandbox or system permission controls.

## Remaining release requirements
Deploy and secure backend, add production per-user authentication, commission or integrate realistic 3D model assets/rigging, implement any further device actions with consent, add chat history only with privacy controls, and complete device installation/security testing.
