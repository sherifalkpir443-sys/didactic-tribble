# NOVA AI — Technical Specification

Status: Initial baseline, version 0.1.0  
Application ID: com.nova.ai  
Platform: Android 8.0+ (API 26+)  
Primary language: Egyptian Arabic (ar-EG)

## Product objective
Build an Arabic-first personal Android assistant with conversational text, voice input/output, selectable male/female presentation profiles, and a realistic 3D digital avatar. Every device action must originate from a clear user command and receive the relevant Android permission or system confirmation.

## Implemented in this baseline
- Native Kotlin Android Activity and Arabic-first starter UI.
- Text entry with an explicitly labeled local placeholder response.
- Speech recognition through Android SpeechRecognizer, started only after the user taps the microphone and grants RECORD_AUDIO.
- Arabic (Egypt) text-to-speech through Android TextToSpeech.
- Text interaction remains available if microphone permission is denied.

## Planned architecture
| Layer | Responsibility | Status |
|---|---|---|
| Presentation | Chat UI, avatar surface, persona selector, consent UI | Basic text UI only |
| Conversation | Message model, session state, history | Planned |
| Speech | Speech-to-text and text-to-speech adapters | Device APIs wired |
| AI gateway | Authenticated model-provider requests through backend | Not implemented |
| Action broker | Validates intent, permission and confirmation | Planned; no actions enabled |
| Device adapters | Narrow Android APIs for approved actions | Not implemented |
| Storage | Preferences and optional encrypted history | Planned |

## Build baseline
- Android Gradle Plugin 8.7.3; Gradle 8.9; Kotlin 2.0.21
- JDK 17; compileSdk 35; targetSdk 35; minSdk 26
- Application ID: com.nova.ai

## Functional requirements
1. Text chat must work without device permissions.
2. Voice recognition starts only after an explicit microphone action.
3. Permission denial or revocation must not crash the app.
4. Speech output is user-triggered.
5. Voice/avatar profiles are presentation choices, not privilege levels.
6. Cloud AI must use a backend gateway; secrets must never be embedded in the APK or Git.
7. Device actions must be allow-listed, explained, permission-checked and confirmed when sensitive.

## Non-functional requirements
- No hidden recording or background microphone activation.
- No silent message sending or device changes.
- TLS for network traffic.
- Data minimization and a clear retention/deletion policy before cloud history is enabled.
- Graceful offline and permission-denied behavior.
- Minimal, reviewed dependencies.

## Version 0.1.0 exclusions
No remote AI provider, 3D avatar engine, phone automation, contacts/SMS access, call control, location access, accessibility service, overlay, or background action service is implemented.
