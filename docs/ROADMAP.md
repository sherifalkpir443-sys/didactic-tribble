# NOVA AI — Roadmap

## Phase 0 — Android starter
- [x] Native Android project and Gradle configuration
- [x] Arabic text UI and local speech APIs
- [x] On-demand microphone permission
- [x] Male/female presentation selector
- [x] Procedural OpenGL ES 2.0 3D face prototype
- [x] Confirmed shortcut to Android Settings

## Phase 1 — AI service
- [x] Define mobile/backend JSON contract
- [x] Implement Node.js gateway using Responses API
- [x] Keep provider key server-side and disable response storage
- [x] Add request limits, timeout, basic rate limit, and automated backend tests
- [ ] Deploy backend and configure provider key privately
- [ ] Add production per-user authentication and distributed rate limiting
- [ ] Verify live end-to-end Android chat

## Phase 2 — Avatar
- [x] Procedural 3D preview with selectable presentation palette
- [ ] Integrate licensed realistic male/female character assets
- [ ] Add rigging, expressions, lip-sync, and voice synchronization
- [ ] Test rendering performance on physical Android devices

## Phase 3 — Safe device actions
- [x] Open Android Settings only after explicit confirmation
- [ ] Define a narrow allow-list for each requested action
- [ ] Add action-specific Android permissions/system confirmations
- [ ] Add action previews and user-visible action history
- [ ] Test denial, revocation, ambiguity, and cancellation

## Phase 4 — Release readiness
- [x] Android CI debug build workflow
- [x] Backend unit/contract test workflow
- [ ] Confirm latest Android and backend CI runs pass
- [ ] Install and test on physical devices
- [ ] Accessibility, privacy, and localization review
- [ ] Signed release build and release documentation
