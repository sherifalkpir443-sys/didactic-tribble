# NOVA AI — Technical Specification

Status: Android implementation baseline 0.4.0
Application ID: com.nova.ai
Platform: Android 8.0+ (API 26+)
Language: Egyptian Arabic (ar-EG)

## Implemented
- Native Kotlin Android app with Arabic-first UI.
- Text input, on-demand SpeechRecognizer input, and Arabic (Egypt) TextToSpeech.
- Microphone permission requested only after explicit user tap.
- Male/female presentation selector.
- Procedural stylized 3D face preview rendered with OpenGL ES 2.0; not a photorealistic model, rig, or lip-sync character.
- Basic device status snapshot: model, manufacturer, Android version, battery, storage, RAM, active network transport, display size, locale, and time zone.
- User reviews the device snapshot and confirms before it is sent to the AI gateway.
- Launcher-app discovery and app launching using Android package visibility for MAIN/LAUNCHER activities. This is not a complete installed-package inventory.
- User-selected text, Markdown, JSON, and XML file import through Android Storage Access Framework. Only selected files are read, limited to 64 KiB, previewed, and sent only after confirmation.
- Local conversation history for the latest 20 turns, with a review screen and a user-triggered clear action. Recent turns are sent as context only when the user sends a new message.
- Confirmed shortcuts to Android Wi-Fi, Bluetooth, sound, display, location, and general Settings pages. NOVA opens the relevant system page; the user performs the change.
- HTTPS-only Android client for POST /v1/chat with a { "message": "..." } request and { "reply": "..." } response.
- Node.js backend implementation using OpenAI Responses API with server-side credentials, optional model-driven web search, store=false, request validation, basic rate limiting, and timeout.
- CI workflows for Android debug APK build and backend tests.

## Android access boundaries
NOVA does not bypass Android's application sandbox. It cannot silently inspect another app's private data, all messages, call logs, protected app databases, or every file. Access to contacts, photos, documents, calendars, notifications, location, and other personal data requires separate supported APIs, narrow permissions or Android pickers, and explicit consent. SMS/call-log and accessibility privileges are restricted and are not used as a blanket data-extraction mechanism.

## Backend configuration
See backend/README.md. Configure OPENAI_API_KEY and OPENAI_MODEL only in the backend hosting environment. The current default model is gpt-6-astra; set OPENAI_MODEL to another model enabled for the API project if needed. Deploy behind HTTPS, then set Android Gradle property NOVA_API_BASE_URL to the backend origin. Never put provider credentials in the APK or Git.

## Architecture
| Layer | Current status |
|---|---|
| Android presentation | Native starter UI |
| Speech | Android APIs connected |
| 3D avatar | Procedural OpenGL ES 2.0 prototype; realistic asset/rigging not implemented |
| AI gateway | Node.js backend source included; deployment and provider secret configuration remain external |
| Device awareness | Basic technical snapshot with explicit upload confirmation |
| App management | Launchable apps only; no uninstall, install, force-stop, or hidden package control |
| File understanding | User-selected text/Markdown/JSON/XML up to 64 KiB; images/PDFs not yet implemented |
| Device settings | Confirmed navigation to Android settings screens; user performs changes |
| Chat history | Local private history, latest 20 turns, clear control |
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
- No background microphone use or recording.
- File and device snapshots are sent only after a review and explicit confirmation.
- No device action without explicit user interaction and required Android permission/system confirmation.
- No bypassing Android sandbox or system permission controls.

## Remaining release requirements
Deploy and secure backend, add production per-user authentication, implement encrypted chat history with retention/deletion controls, add separately consented image/PDF and selected personal-data integrations, commission realistic 3D model assets/rigging, and complete physical-device, privacy, and security testing. Produce a signed release build with a private signing key.
