# IslaBuy — Camiguin Edition

An Android marketplace for the island of Camiguin. Sellers list fresh produce, seafood,
and local goods; buyers browse by category, pin pickup spots on the map, and chat with
the seller about a specific product.

Built with Kotlin, Jetpack Compose, Firebase, and Cloudinary.

## Features

**Buyers**
- Two-column product grid with pagination and cached images
- Search by title, filter by category
- Seller pin on OpenStreetMap, with external navigation to Google Maps or Waze
- Whole-island map view with a "My location" overlay and a one-tap reset
- One conversation per product, so chat never mixes listings
- Delete a message for yourself, or for everyone if you sent it
- 3D-styled cards and buttons with animated press feedback

**Sellers**
- Create listings with up to 5 compressed photos
- Perishable goods require an expiration date and time; expired listings drop out of the feed
- Pick the pickup location by dropping a pin on the map
- Individual storefront per seller
- Seller inbox: every conversation on your listings, as the seller

**Everyone**
- Email/password accounts
- Push notifications on `messages`, `products`, and `announcements` channels
- Admin panel for moderating listings and sellers, managing categories, and editing the news ticker
- Profile screen with a signed-out placeholder and a Settings entry point
- Settings screen: notification preferences per channel and account deletion
- 3D UI across Home, Map, Profile, Settings, ProductDetail, Sell, and Chat (dark-mode aware)

## Tech stack

| Layer | Choice |
|---|---|
| Language / UI | Kotlin, Jetpack Compose, Material 3 |
| Architecture | MVVM — Compose screens → ViewModels → repositories |
| Backend | Firebase Auth, Cloud Firestore, Cloud Messaging |
| Images | Cloudinary, unsigned upload preset, client-side compression |
| Maps | osmdroid / OpenStreetMap |
| Async | Kotlin coroutines + Flow |
| 3D UI | `ui/components/ThreeD.kt` — `Modifier.threeD()`, `ThreeDButton`, `ThreeDCard` (animated press, perspective, depth, dark-mode aware) |
| Build | Gradle 8.4, AGP, JDK 17, `minSdk 24` |

## Project layout

```
app/src/main/java/com/islabuy/camiguin/
├── data/
│   ├── model/          Listing, Conversation, Message, User, Store, Notification, Announcement
│   ├── repository/     ListingRepository, ChatRepository, UserRepository, CloudinaryRepository
│   └── preferences/    NotificationPreferences (DataStore)
├── ui/
│   ├── components/     ThreeD.kt (3D helpers)
│   ├── screens/        Home, ProductDetail, Sell, Chat, Messages, Map, Store, Profile, Settings, Admin
│   ├── viewmodel/      HomeViewModel, ChatViewModel, ConversationListViewModel
│   └── theme/          Theme.kt (original Camiguin palette), Color, Type, Shape
├── notifications/      FCM service
└── util/               ConversationAccess, ListingValidator, MessageValidator, ProductFilter, Categories

firestore.rules                              Security rules
scripts/test-firestore-rules.py              Rules test suite (runs in CI)
docs/account-deletion.html                   Account deletion policy (Play Store requirement)
functions/                                   Scheduled Cloud Functions (optional, needs Blaze)
```

## Getting started

Requirements: JDK 17, Android SDK (platform 34, build-tools 34.0.0), and a Firebase project
you control. Full setup — Firebase, Cloudinary, and CI secrets — is in [SETUP.md](SETUP.md).

```bash
./gradlew test              # unit tests
./gradlew assembleDebug     # debug APK
./gradlew assembleRelease   # signed release APK (needs the keystore, see below)
./gradlew bundleRelease     # signed release AAB for Play Store
```

## Configuration

**Firebase.** `app/google-services.json` must belong to a project with package name
`com.islabuy.camiguin`, Email/Password auth enabled, Firestore created, and Cloud Messaging on.

**Cloudinary.** Uploads are unsigned — no API secret ships in the app. The preset must exist
as an *unsigned* preset in the Cloudinary dashboard:

| Setting | Value |
|---|---|
| Cloud name | `ubezsbu4` |
| Upload preset | `islabuy_products` |
| Folder | `islabuy/products` |

Images are downscaled to 1600 px on the long edge and re-encoded as JPEG q80 before upload.

**Maps.** OpenStreetMap tiles only — no Leaflet, OSRM, or Nominatim, and no in-app routing.
Attribution is displayed, as the OSM tile policy requires.

## Chat model

A conversation is identified by **one product + one buyer + one seller**
(`ConversationAccess.conversationKey`). Membership — who may read or write in a conversation —
is defined once in `util/ConversationAccess.kt` and mirrored in `firestore.rules` by
`isConversationParticipant`. Change one, change the other.

## Security rules

`firestore.rules` allows no blanket `allow read, write: if true`. Public reads are limited to
browsing (`listings`, `stores`, `announcements`); conversations and messages are restricted to
their participants; category, ticker, and audit-log writes are admin-only.

The rules are validated by the **Firestore Rules Validation** workflow on every push to `main`.

## CI/CD

| Workflow | Trigger | Purpose |
|---|---|---|
| `build.yml` | push / PR to `main` | Debug build; release build when signing secrets exist |
| `firestore-rules.yml` | push / PR to `main` | Rules validation suite |
| `release.yml` | tag `v*`, or manual | Signed APK + AAB, attached to a GitHub Release |

Release builds read these repository secrets: `KEYSTORE_BASE64` (the keystore, base64-encoded
as a single line), `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`. See
[GITHUB_SECRETS.md](GITHUB_SECRETS.md) for the exact values to generate.

## Release

`v1.0.0` — [Releases](https://github.com/bernavicg1-beep/IslaBuy/releases/tag/v1.0.0)

| Artifact | Size |
|---|---|
| `IslaBuy-v1.0.0.apk` | 7.8 MB |
| `IslaBuy-v1.0.0.aab` | 8.0 MB |
| `IslaBuy-debug.apk` | ~30 MB (latest 3D build) |

## Known gaps

- **Sign-in is email/password only.** Phone/SMS verification is not wired in, even though the
  Firebase project has Phone auth enabled.
- **Expired-listing cleanup** beyond the client-side feed filter needs the scheduled function in
  `functions/` deployed, which requires the Blaze plan.
- **Release CI** fails at the keystore decode step until `KEYSTORE_BASE64` holds the real
  3,684-character base64 string. The `v1.0.0` artifacts were built and signed locally instead.
- `docs/account-deletion.html` still contains `YOUR_SUPPORT_EMAIL_HERE` and must be filled in
  before submitting to Play Store.
- String centralization still pending.

## Docs

- [SETUP.md](SETUP.md) — Firebase, Cloudinary, and local setup
- [GITHUB_SECRETS.md](GITHUB_SECRETS.md) — CI secrets and how to generate them
- [LOOP_GOAL.md](LOOP_GOAL.md) — acceptance criteria for the build
- [docs/account-deletion.html](docs/account-deletion.html) — account deletion policy
