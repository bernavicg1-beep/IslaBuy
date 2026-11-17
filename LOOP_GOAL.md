# IslaBuy — Camiguin Edition: Closed-Loop Goal

## Objective
Build the IslaBuy — Camiguin Edition Android marketplace app (Kotlin + Jetpack
Compose + Firebase + Cloudinary) so that every acceptance criterion below passes,
verified by the mechanical gate (Gradle tests/build), not by self-assessment.

## Acceptance criteria (the loop drives toward these)
1. Users can register, log in, log out (Firebase Auth + SMS verification).
2. Buyers can browse products in a 2-column grid with pagination + image caching.
3. Sellers can create listings (max 5 photos, compressed, Cloudinary signed upload).
4. Perishable products require validated expiration date + time.
5. Expired products are removed from active listings; Cloudinary media cleaned up.
6. Sellers can select pickup location via OpenStreetMap pin; coords saved.
7. Buyers can view seller pin and open Google Maps / Waze (external nav only).
8. Notifications + configurable ringtones work (FCM + channels).
9. Admin panel (in Profile) moderates listings/sellers, manages categories + ticker.
10. Running news ticker scrolls admin-controlled announcements from Firestore.
11. Each seller has an individual storefront.
12. Per-product chat: one conversation per (product, buyer, seller).
13. Message delete-for-me and delete-for-everyone with authorization.
14. Account deletion flow (with docs/account-deletion.html published).
15. Firestore security rules pass all 90 behavioral checks (no `allow read,write: if true`).
16. GitHub Actions workflows (firestore-rules, build, release) present and valid.
17. Signed APK + AAB build configuration (keystore via GitHub secrets).
18. Cloudinary preset `camiguin_market_products` / folder `camiguin_market/products`,
    signed uploads, ml_default preserved.
19. OpenStreetMap attribution displayed; no Leaflet/OSRM/Nominatim/internal routing.
20. Existing Camiguin Market design preserved (colors, typography, layout, cards).

## Gate
`.claude/gate.py` (Stop hook). Green = all Gradle tests pass. Red = keep fixing.

## Ceiling
~20-30 turns. Stop and report if still red.

## Tooling status (verified in this environment)
Android tooling IS now installed in this VM:
- JDK 17 (Temurin) at ~/tools/jdk-17.0.13+11
- Gradle 8.4 at ~/tools/gradle-8.4
- Android SDK (platform 34, build-tools 34.0.0) at ~/android-sdk
- Gradle wrapper generated (./gradlew)

The gate runs `./gradlew test` and currently PASSES (green).
Build verified: `./gradlew assembleDebug` → BUILD SUCCESSFUL.

Note: Tooling is in the user's home directory (~/tools, ~/android-sdk), not system-wide.
These may need reinstallation after a checkpoint restore (dependencies/tools are not persisted).
