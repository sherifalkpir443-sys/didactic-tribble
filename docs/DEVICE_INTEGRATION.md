# Device awareness and integration plan

## Implemented
- Basic device status snapshot with explicit preview and upload consent.
- Launchable app list and launch action. Android package visibility limits the results to apps exposing a launcher activity.
- User-selected text/Markdown/JSON/XML import through the system document picker, limited to 64 KiB and uploaded only after confirmation.
- Confirmed shortcuts to Wi-Fi, Bluetooth, sound, display, location, and general Android Settings.\n- Local conversation history for the latest 20 messages, with a clear-history control.

## What "all phone data" means on Android
An ordinary third-party Android app cannot read every item on a phone. Android's sandbox intentionally protects app-private databases and content. A file picker grants access only to the document the user selects. Contacts, calendar, media, location, notifications, and similar data each require their own Android API and permissions or system-mediated access. Some capabilities are restricted to default-handler apps, privileged/system apps, or approved special-use cases. NOVA must not claim universal access or try to evade those protections.

## Capability matrix
| Capability | Current state | Next safe implementation |
|---|---|---|
| Text chat | UI and HTTPS gateway source | Deploy backend and configure URL/key |
| Arabic voice input/output | Android speech APIs | Verify target-device voice service |
| Basic device awareness | Implemented | Add optional diagnostic fields only when justified |
| Launch apps | Implemented for visible launcher activities | Optional favorites and recent-app shortcuts |
| Settings navigation | Implemented with confirmation | User makes the actual setting change |
| Selected text files | Implemented, max 64 KiB | PDF and image understanding through a separate consented upload flow |
| Contacts/calendar | Not implemented | Dedicated permission flow and user-visible scope |
| Photos/media | Not implemented | Android Photo Picker; no broad storage permission |
| Notifications | Not implemented | Optional notification-listener access only with clear special-access explanation |
| SMS/call logs | Not implemented | Do not add as a general data-reading feature; platform restrictions apply |
| Device control | Limited to opening system pages and launching apps | Add individual actions through supported Android APIs and confirmation |
| Chat history | Implemented as local app-private history with clear control | Add encryption-at-rest and export controls |
| Web search | Enabled through Responses API web_search tool | Render returned source citations as clickable links in a richer chat UI |
| Realistic animated avatar | Prototype only | Licensed rigged model, animation, and lip-sync testing |

## Release gates
1. Deploy the backend on HTTPS and store provider credentials only in hosting secrets.
2. Configure NOVA_API_BASE_URL for the Android release build.
3. Add production per-user authentication and abuse controls.
4. Run Android build/tests and backend tests on the exact release commit.
5. Test on physical devices, including permission denial, offline state, low storage, and file-picker edge cases.
6. Produce a signed release APK/AAB with a private signing key never committed to Git.
7. Complete privacy disclosures, data-retention policy, and store-policy review.

A successful CI build does not prove physical-device behavior or a live AI connection.
