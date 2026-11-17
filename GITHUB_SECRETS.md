# GitHub Secrets — IslaBuy CI/CD

Adto sa GitHub repo → Settings → Secrets and variables → Actions → New repository secret

## Required Secrets

### 1. FIREBASE_TOKEN
| Field | Value |
|-------|-------|
| Name | `FIREBASE_TOKEN` |
| Value | `1//065KaFt3f_YHnCgYIARAAGAYSNwF-L9IrOr1bFjE4q2aOe_wXWifJTuOHjwrvLbPv4pKwMXsJAct0CCBC69_-Leeqr3QOvLExxa8` |

### 2. KEYSTORE_BASE64
| Field | Value |
|-------|-------|
| Name | `KEYSTORE_BASE64` |
| Value | (I-copy ang tibuok base64 gikan sa `islabuy-release.keystore.base64` file) |

Ang base64 file naa sa project root: `islabuy-release.keystore.base64`

### 3. KEYSTORE_PASSWORD
| Field | Value |
|-------|-------|
| Name | `KEYSTORE_PASSWORD` |
| Value | `islabuy123` |

### 4. KEY_ALIAS
| Field | Value |
|-------|-------|
| Name | `KEY_ALIAS` |
| Value | `islabuy` |

### 5. KEY_PASSWORD
| Field | Value |
|-------|-------|
| Name | `KEY_PASSWORD` |
| Value | `islabuy123` |

## Keystore Info
- File: `islabuy-release.keystore` (naa sa project root)
- Alias: `islabuy`
- Password: `islabuy123`
- SHA-256: `87:89:17:AC:8B:91:04:26:C4:14:D9:05:21:04:DF:B6:CE:C3:BD:5F:8D:6C:87:2B:81:53:FB:F0:D3:5B:C6:6A`

## Importante
- I-backup ang `islabuy-release.keystore` sa luwas nga lugar
- Kung mawala ang keystore, dili na nimo ma-update ang app sa Play Store
- Ang password kay `islabuy123` — pwede nimo i-change para mas secure
- Ang FIREBASE_TOKEN kay sensitive — dili i-share
