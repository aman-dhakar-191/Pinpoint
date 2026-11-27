# Pinpoint

A map app for Android, built on the Google Maps API. This is the starting point: one screen
with a Google map. Features come next.

- Package: `io.github.amandhakar.pinpoint`
- Kotlin, Jetpack Compose, [Maps Compose](https://github.com/googlemaps/android-maps-compose)
- Android 8.0 (API 26) or newer

## Google Maps API key

The map needs a key from Google Cloud. The key is never committed: builds read it from
`MAPS_API_KEY`, and the app shows a "No Maps API key in this build" card when a build has none.

1. In the [Google Cloud console](https://console.cloud.google.com/), create a project (or pick
   one) and **turn on billing** for it. The Maps SDK doesn't serve maps without billing, even
   within the free monthly allowance.
2. Enable **Maps SDK for Android** (*APIs & Services → Library*).
3. Create an API key (*APIs & Services → Credentials → Create credentials → API key*).
   Restrict it to *Android apps*, package `io.github.amandhakar.pinpoint` with the SHA-1 of
   the release signing key (and of your debug key if you build locally), and to the
   *Maps SDK for Android* API.
4. Use it:
   - **Releases:** add it to this repo as the secret `MAPS_API_KEY`
     (*Settings → Secrets and variables → Actions*). The Release workflow stops if it's missing.
   - **Local builds:** add `MAPS_API_KEY=your-key` to `local.properties` (git ignores that file).

## Releases

Releases work like the other apps in
[private-app-store](https://github.com/aman-dhakar-191/private-app-store): GitHub Releases is the
backend, and the store picks up new versions from here.

### One-time setup

Create the signing key with
[android-keystore-generator](https://github.com/aman-dhakar-191/android-keystore-generator)
(private). Go to *Actions → Generate signing key → Run workflow* and set `target_repo` to
`aman-dhakar-191/Pinpoint`. This writes the four secrets the Release workflow reads straight
into this repo:

| Secret | Value |
| --- | --- |
| `KEYSTORE_BASE64` | the keystore, base64 |
| `KEYSTORE_PASSWORD` | the keystore password |
| `KEY_ALIAS` | the key alias |
| `KEY_PASSWORD` | the key password |

`target_repo` needs that repo's `TARGET_REPO_TOKEN` to have *Secrets: read and write* on
Pinpoint. Without it, add the values from `secrets.txt` here by hand. Download the encrypted
archive from the run within a day and back it up: every update must be signed with the same key,
so losing it means everyone has to uninstall and reinstall.

Also add `MAPS_API_KEY` (above). Optionally add `STORE_DISPATCH_TOKEN` (see the store's
[publishing guide](https://github.com/aman-dhakar-191/private-app-store/blob/main/docs/publishing-an-app.md))
so a release shows in the store right away instead of at the next index run.

### Each release

**Actions → Release → Run workflow** on `main`, then pick **patch**, **minor** or **major**.
The workflow finds the latest `vX.Y.Z` tag, runs the tests, builds a signed APK, and publishes
the release. The first release starts from `0.0.0`, so pick **minor** to get `0.1.0`.
`versionCode` is `major × 1000000 + minor × 1000 + patch`.

The [Android workflow](.github/workflows/android.yml) runs lint, the unit tests and a debug build
on every pull request and on `main`, and keeps the debug APK as a build artifact.

## Building locally

Needs JDK 17 and the Android SDK.

```sh
./gradlew lintDebug testDebugUnitTest assembleDebug
```
