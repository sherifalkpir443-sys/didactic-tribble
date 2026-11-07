# NOVA AI — Android Assistant

NOVA AI is an Arabic-first Android assistant starter project. This repository contains a minimal native Android app, build configuration, technical specifications, security rules, roadmap, and a GitHub Actions build workflow.

## Implemented
- Native Kotlin Android app.
- Arabic-first starter interface.
- Text entry with a clearly labeled local placeholder response.
- Speech-to-text through Android SpeechRecognizer, requested only after the user taps the microphone.
- Arabic text-to-speech through Android TextToSpeech.
- Microphone permission requested at runtime; denial leaves text input usable.

## Current limitations
This is a starter implementation, not a finished AI assistant. No cloud model/provider is connected, no API key is included, and no privileged phone-control actions are implemented. The 3D avatar and selectable male/female profiles are planned, not yet implemented. An automated build workflow has been added, but no successful APK build or device test is claimed until its run completes.

## Build
Open in Android Studio with JDK 17 and Android SDK Platform 35. Sync Gradle and run on Android 8.0 (API 26) or newer.

Command line (Gradle 8.9):

    gradle assembleDebug

If successful, the debug APK is at app/build/outputs/apk/debug/app-debug.apk.

## Documentation
- [Technical specification](docs/TECHNICAL_SPEC.md)
- [Security and permissions model](docs/SECURITY_MODEL.md)
- [Roadmap](docs/ROADMAP.md)

## Baseline versions
- Kotlin 2.0.21
- Android Gradle Plugin 8.7.3
- Gradle 8.9
- compileSdk / targetSdk 35; minSdk 26
- JDK 17

Official references: [Android build](https://developer.android.com/build), [Android permissions](https://developer.android.com/guide/topics/permissions/overview).
