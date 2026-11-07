# Device awareness and ChatGPT-style capability plan

## Implemented in this revision
- On-device snapshot: manufacturer/model, Android/API version, battery/charging state, storage, RAM, active network transport, logical display size, locale, and time zone.
- The snapshot is generated locally only after the user taps the device-information action.
- A review dialog displays the exact snapshot. It is sent to the NOVA backend only after the user presses **Agree and send**.
- Network-state access uses the narrow `ACCESS_NETWORK_STATE` permission.
- No contacts, SMS, call logs, photos, documents, precise location, microphone recordings, or broad installed-app inventory are collected by the device snapshot.

## Android access boundaries
Android does not provide an ordinary app with unrestricted access to everything on a phone. Each integration must use the supported Android API, declare the narrowest permission, explain the purpose, and obtain runtime consent where required. Some actions must open the relevant system screen for the user to complete. Accessibility, notification access, default-SMS, device-admin, and all-files access are special/high-impact access paths and must never be silently enabled or used as a general surveillance shortcut.

## Capability matrix
| Capability | Current state | Safe completion requirement |
|---|---|---|
| Text chat | UI and HTTPS gateway scaffold exist | Deploy backend and configure URL/key |
| Arabic voice input | Android SpeechRecognizer | Device speech service and microphone permission |
| Spoken replies | Android TextToSpeech | Verify Arabic voice availability on target device |
| Basic device awareness | Implemented in this revision | User reviews and approves each upload |
| Device settings | Opens Android Settings after confirmation | User changes settings manually |
| Conversation history | Not implemented | Encrypted/local retention controls and delete action |
| Image/file understanding | Not implemented | File picker, content URI handling, upload consent, server-side limits |
| Web search | Not implemented | Search provider integration and citations |
| Reminders/calendar | Not implemented | Calendar/notification permissions and explicit scheduling confirmation |
| Contacts/calls/messages | Not implemented | Separate narrowly scoped integrations and explicit per-action consent |
| Device actions | Not implemented beyond opening Settings | One Android API per action, preview, confirmation, and audit trail |
| Realistic animated avatar | Prototype only | Licensed rigged model, animation, performance and lip-sync testing |

## Release gates
1. Configure a production HTTPS backend and store provider credentials only in hosting secrets.
2. Set `NOVA_API_BASE_URL` for the Android release build; never ship provider API keys in the APK.
3. Run Android and backend CI on the exact release commit.
4. Test install, voice input/output, network failure, permission denial, offline behavior, and rotation/lifecycle on physical Android devices.
5. Produce a signed release APK/AAB using a private signing key that is not committed to Git.
6. Verify privacy disclosures, data retention, and Play policy compliance before distribution.

A successful CI build is necessary but does not prove real-device behavior or a live AI connection.
