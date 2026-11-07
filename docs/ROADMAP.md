# NOVA AI — Roadmap

## Phase 0 — Android starter
- [x] Native Android project and Gradle configuration
- [x] Arabic text UI and local voice APIs
- [x] Microphone permission requested on demand
- [x] Basic persona selector and 2D face preview
- [x] Confirmed shortcut to Android Settings

## Phase 1 — AI service
- [x] Define HTTPS backend request/response contract
- [x] Add Android gateway client without embedding provider secrets
- [ ] Deploy backend service and connect a selected model provider
- [ ] Add authentication, rate limits, privacy/retention controls
- [ ] Test streaming, cancellation, offline and error paths

## Phase 2 — Avatar
- [ ] Select and integrate a supported 3D runtime
- [ ] Add licensed realistic male/female avatar model assets
- [ ] Implement animation, lip-sync and voice synchronization
- [ ] Test performance on target Android devices

## Phase 3 — Safe device actions
- [x] Open Android Settings only after a confirmation dialog
- [ ] Define a narrow allow-list for each requested action
- [ ] Add action-specific Android permissions/system confirmations
- [ ] Add clear action previews and user-visible action history
- [ ] Test denial, revocation, ambiguity and cancellation

## Phase 4 — Release readiness
- [ ] Unit/instrumentation tests
- [ ] CI build passes and APK artifact is uploaded
- [ ] Install and test on physical devices
- [ ] Accessibility/localization review
- [ ] Signed release build and release documentation
