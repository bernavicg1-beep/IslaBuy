#!/usr/bin/env python3
"""Firestore security rules behavioral test suite for IslaBuy.

Runs against the Firestore emulator started by `firebase emulators:exec`.
Every check issues a real request and asserts the emulator's allow/deny
decision, so a green run actually means the rules behave as intended.
Exits non-zero on any failure.
"""
import json
import os
import sys
import urllib.error
import urllib.parse
import urllib.request

FS_HOST = os.environ.get("FIRESTORE_EMULATOR_HOST", "localhost:8080")
AUTH_HOST = os.environ.get("FIREBASE_AUTH_EMULATOR_HOST", "localhost:9099")
PROJECT = "demo-islabuy"
FS_BASE = "http://%s/v1/projects/%s/databases/(default)/documents" % (FS_HOST, PROJECT)
AUTH_URL = "http://%s/identitytoolkit.googleapis.com/v1/accounts:signUp?key=fake-api-key" % AUTH_HOST

passed = 0
failed = 0
failures = []


def check(name, condition):
    global passed, failed
    if condition:
        passed += 1
        print("  PASS  %s" % name)
    else:
        failed += 1
        failures.append(name)
        print("  FAIL  %s" % name)


def http(method, path, token=None, body=None, query=None):
    url = FS_BASE + path
    if query:
        url += "?" + urllib.parse.urlencode(query, doseq=True)
    data = json.dumps(body).encode() if body is not None else None
    headers = {}
    if token:
        headers["Authorization"] = "Bearer " + token
    if data is not None:
        headers["Content-Type"] = "application/json"
    req = urllib.request.Request(url, data=data, headers=headers, method=method)
    try:
        with urllib.request.urlopen(req) as r:
            raw = r.read().decode()
            try:
                return r.status, json.loads(raw) if raw else {}
            except ValueError:
                return r.status, {}
    except urllib.error.HTTPError as e:
        raw = e.read().decode()
        try:
            return e.code, json.loads(raw) if raw else {}
        except ValueError:
            return e.code, {"_raw": raw}
    except urllib.error.URLError as e:
        return 0, {"_error": str(e)}


def sign_up(email, password):
    data = {"email": email, "password": password, "returnSecureToken": True}
    req = urllib.request.Request(
        AUTH_URL, data=json.dumps(data).encode(),
        headers={"Content-Type": "application/json"}, method="POST")
    with urllib.request.urlopen(req) as r:
        j = json.loads(r.read().decode())
    return j["idToken"], j["localId"]


def sv(s):
    return {"stringValue": s}


def dv(n):
    return {"doubleValue": float(n)}


def iv(n):
    return {"integerValue": str(n)}


# --- reachability ---
print("== Reachability ==")
st, _ = http("GET", "/listings")
if st == 0:
    print("FAIL: emulator not reachable at %s" % FS_HOST)
    sys.exit(1)
check("emulator-reachable", st != 0)

# --- mint users ---
print("\n== Mint test users ==")
try:
    tok_a, uid_a = sign_up("alice@test.com", "password123")
    tok_b, uid_b = sign_up("bob@test.com", "password123")
    tok_c, uid_c = sign_up("carol@test.com", "password123")
except Exception as e:
    print("Auth sign-up failed: %s" % e)
    sys.exit(1)
check("minted-three-users", bool(uid_a and uid_b and uid_c))

# --- anonymous access ---
print("\n== Anonymous access ==")
st, _ = http("GET", "/listings")
check("anon-can-read-listings", st == 200)
st, _ = http("POST", "/listings", body={"fields": {"sellerId": sv(uid_a), "price": dv(10)}})
check("anon-cannot-write-listings", st in (401, 403))
st, _ = http("GET", "/conversations")
check("anon-cannot-read-conversations", st in (401, 403))
st, _ = http("GET", "/users")
check("anon-cannot-read-users", st in (401, 403))
st, _ = http("GET", "/messages")
check("anon-cannot-read-messages", st in (401, 403))
st, _ = http("GET", "/announcements")
check("anon-can-read-announcements", st == 200)
st, _ = http("POST", "/announcements", body={"fields": {"text": sv("hi"), "isActive": {"booleanValue": True}}})
check("anon-cannot-create-announcement", st in (401, 403))
st, _ = http("GET", "/auditLog")
check("anon-cannot-read-auditlog", st in (401, 403))
st, _ = http("POST", "/notifications", body={"fields": {"userId": sv(uid_a)}})
check("anon-cannot-write-notifications", st in (401, 403))

# --- listings ---
print("\n== Listings ==")
st, body = http("POST", "/listings", token=tok_a, body={"fields": {
    "sellerId": sv(uid_a), "title": sv("Lanzones"), "price": dv(50),
    "status": sv("active"), "category": sv("Fruits"), "quantity": iv(5),
    "isPerishable": {"booleanValue": False}, "createdAt": iv(1000), "updatedAt": iv(1000),
}})
check("owner-can-create-listing", st == 200)
listing_id = body.get("name", "").split("/")[-1] if st == 200 else None

if listing_id:
    st, _ = http("PATCH", "/listings/%s" % listing_id, token=tok_a,
                 body={"fields": {"sellerId": sv(uid_b)}},
                 query=[("updateMask.fieldPaths", "sellerId")])
    check("owner-cannot-transfer-listing", st in (400, 403))
    st, _ = http("PATCH", "/listings/%s" % listing_id, token=tok_b,
                 body={"fields": {"title": sv("Hacked")}},
                 query=[("updateMask.fieldPaths", "title")])
    check("non-owner-cannot-update-listing", st in (400, 403))
    st, _ = http("PATCH", "/listings/%s" % listing_id, token=tok_a,
                 body={"fields": {"title": sv("Fresh Lanzones")}},
                 query=[("updateMask.fieldPaths", "title")])
    check("owner-can-update-own-listing", st == 200)

# --- users / role ---
print("\n== Users ==")
st, _ = http("PATCH", "/users/%s" % uid_a, token=tok_a, body={"fields": {
    "uid": sv(uid_a), "name": sv("Alice"), "email": sv("alice@test.com"),
    "role": sv("buyer"), "createdAt": iv(1),
}}, query=[("updateMask.fieldPaths", "uid"), ("updateMask.fieldPaths", "name"),
          ("updateMask.fieldPaths", "email"), ("updateMask.fieldPaths", "role"),
          ("updateMask.fieldPaths", "createdAt")])
check("owner-can-create-own-profile", st == 200)
st, _ = http("PATCH", "/users/%s" % uid_a, token=tok_a,
             body={"fields": {"role": sv("admin")}},
             query=[("updateMask.fieldPaths", "role")])
check("user-cannot-change-own-role", st in (400, 403))
st, _ = http("GET", "/users/%s" % uid_a, token=tok_a)
check("owner-can-read-own-profile", st == 200)

# --- conversations & messages ---
print("\n== Conversations & messages ==")
conv_id = None
st, body = http("POST", "/conversations", token=tok_a, body={"fields": {
    "productId": sv("p1"), "buyerId": sv(uid_a), "sellerId": sv(uid_b),
    "productTitle": sv("Lanzones"), "lastMessage": sv(""), "lastMessageAt": iv(1000),
    "unreadCounts": {"mapValue": {"fields": {}}},
}})
check("buyer-can-create-conversation", st == 200)
if st == 200:
    conv_id = body.get("name", "").split("/")[-1]

if conv_id:
    st, _ = http("GET", "/conversations/%s" % conv_id, token=tok_c)
    check("non-participant-cannot-read-conversation", st in (401, 403))
    st, _ = http("GET", "/conversations/%s" % conv_id, token=tok_a)
    check("participant-can-read-conversation", st == 200)

msg_id = None
if conv_id:
    st, body = http("POST", "/messages", token=tok_a, body={"fields": {
        "conversationId": sv(conv_id), "senderId": sv(uid_a), "receiverId": sv(uid_b),
        "text": sv("hi"), "createdAt": iv(1000), "status": sv("sent"),
        "deletedFor": {"arrayValue": {"values": []}},
        "deletedForEveryone": {"booleanValue": False},
    }})
    check("sender-can-send-message", st == 200)
    if st == 200:
        msg_id = body.get("name", "").split("/")[-1]

if msg_id:
    st, _ = http("GET", "/messages/%s" % msg_id, token=tok_c)
    check("non-participant-cannot-read-message", st in (401, 403))
    st, _ = http("PATCH", "/messages/%s" % msg_id, token=tok_c,
                 body={"fields": {"deletedFor": {"arrayValue": {"values": [sv(uid_c)]}}}},
                 query=[("updateMask.fieldPaths", "deletedFor")])
    check("non-participant-cannot-update-message", st in (400, 403))
    st, _ = http("PATCH", "/messages/%s" % msg_id, token=tok_b,
                 body={"fields": {"deletedFor": {"arrayValue": {"values": [sv(uid_b)]}}}},
                 query=[("updateMask.fieldPaths", "deletedFor")])
    check("participant-can-delete-for-me", st == 200)

# --- notifications ---
print("\n== Notifications ==")
st, body = http("POST", "/notifications", token=tok_a, body={"fields": {
    "userId": sv(uid_a), "title": sv("hi"), "isRead": {"booleanValue": False},
    "createdAt": iv(1000),
}})
check("owner-can-create-own-notification", st == 200)
if st == 200:
    nid = body.get("name", "").split("/")[-1]
    st, _ = http("GET", "/notifications/%s" % nid, token=tok_b)
    check("non-owner-cannot-read-notification", st in (401, 403))

print("\nResults: %d passed, %d failed out of %d" % (passed, failed, passed + failed))
if failed:
    print("FAILURES:")
    for f in failures:
        print("  -", f)
    sys.exit(1)
print("All checks passed.")
sys.exit(0)
