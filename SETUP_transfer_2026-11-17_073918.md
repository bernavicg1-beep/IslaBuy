# IslaBuy — Camiguin Edition: Setup Guide

## Prerequisites

- Android Studio (latest stable)
- JDK 17+
- Firebase project
- Cloudinary account (Free Plan)
- GitHub repository with Actions enabled

## 1. Firebase Setup

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Create a new project (or use existing)
3. Add an Android app with package name: `com.islabuy.camiguin`
4. Download `google-services.json` and replace the placeholder in `app/google-services.json`
5. Enable Authentication (Email/Password + Phone)
6. Create Firestore database (production mode)
7. Enable Cloud Messaging (FCM)

### Deploy Firestore Rules

```bash
firebase deploy --only firestore:rules
```

### Deploy Cloud Functions

```bash
cd functions && npm install
firebase functions:config:set cloudinary.cloud_name="YOUR_CLOUD" cloudinary.api_key="YOUR_KEY" cloudinary.api_secret="YOUR_SECRET"
firebase deploy --only functions
```

## 2. Cloudinary Setup

1. Create a Cloudinary account (Free Plan)
2. Create upload preset: `camiguin_market_products` (signed mode)
3. Set asset folder: `camiguin_market/products`
4. Enable unique public IDs, disable overwrite
5. Preserve existing `ml_default` preset

## 3. GitHub Secrets (for CI/CD)

Set these in your GitHub repository settings:

| Secret | Description |
|--------|-------------|
| `KEYSTORE_BASE64` | Base64-encoded signing keystore |
| `KEYSTORE_PASSWORD` | Keystore password |
| `KEY_ALIAS` | Key alias |
| `KEY_PASSWORD` | Key password |
| `FIREBASE_TOKEN` | Firebase CI token (`firebase login:ci`) |

## 4. Account Deletion Page

1. Replace `YOUR_SUPPORT_EMAIL_HERE` in `docs/account-deletion.html`
2. Enable GitHub Pages (branch: main, folder: /docs)
3. Submit the published URL in Google Play Console

## 5. Build

```bash
# Debug APK
./gradlew assembleDebug

# Release APK + AAB
./gradlew assembleRelease bundleRelease
```

## 6. Testing

```bash
# Unit tests
./gradlew test

# Firestore security rules (requires emulator)
firebase emulators:start --only firestore
python3 scripts/test-firestore-rules.py
```

## 7. Google Play Store

1. Create app listing in Google Play Console
2. Upload signed AAB
3. Complete store listing, screenshots, description
4. Set account deletion URL
5. Submit for review
