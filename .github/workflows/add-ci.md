---
# Trigger - when should this workflow run?
on:
  workflow_dispatch:  # Manual trigger

permissions:
  contents: read
  issues: read
  pull-requests: read

# Network access
network: defaults

# Outputs - what APIs and tools can the AI use?
# This workflow opens a PR for human review; it does NOT push to main directly.
safe-outputs:
  create-pull-request:
    max: 1
---

# Task: Add CI workflow that builds the debug APK for DEVICE-PRIVACY

You are an Android engineering agent. Operate on the repository in the current
working directory (a Kotlin/AGP Android app: "Privacy Simulator",
namespace `com.example`, `compileSdk = 36`, `minSdk = 26`, Gradle wrapper present).

## Goal
Add a GitHub Actions CI workflow so the app builds automatically on every push
and pull request. The repository currently has only an on-device Termux build
script (`build_apk.sh`) and NO CI. Introduce a proper CI workflow.

## Requirements
1. Create `.github/workflows/build.yml` that:
   - Triggers on `push` (branches: main, master) and `pull_request`.
   - Uses a recent Ubuntu runner.
   - Sets up JDK 17 (Temurin) and the Gradle cache.
   - Runs `chmod +x gradlew`, then `./gradlew assembleDebug` to build the debug APK.
   - Runs `./gradlew testDebugUnitTest` if the project has unit tests configured.
   - Uploads the produced debug APK as a build artifact.
   - Does NOT require any API keys (the build reads `NVIDIA_API_KEY` /
     `MISTRAL_API_KEY` from env with empty fallback, so a plain debug build works).
2. Keep the workflow minimal and correct. Use `actions/checkout@v4`,
   `actions/setup-java@v4` (temurin, 17), and `actions/upload-artifact@v4`.
3. Do NOT modify app source code, only add the CI workflow file.
4. Do NOT attempt to sign a release build (no keystore available in CI).

## Deliverable
Open a pull request titled "ci: add GitHub Actions workflow to build debug APK"
with a short description of what the workflow does. The PR must be opened for
human review; do not merge it yourself.

## Constraints
- Only add files under `.github/workflows/`.
- Do not run `./gradlew` builds locally that require network access beyond what
  CI would have; the actual build runs in CI.
- If you cannot determine the correct Gradle task names, use `./gradlew tasks
  --all` to inspect, then pick `assembleDebug` and `testDebugUnitTest`.
