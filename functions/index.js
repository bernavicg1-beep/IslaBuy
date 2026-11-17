const functions = require('firebase-functions');
const admin = require('firebase-admin');
const cloudinary = require('cloudinary').v2;

admin.initializeApp();

// Cloudinary config from environment (set via Firebase config)
const cloudinaryConfig = functions.config().cloudinary || {};
cloudinary.config({
  cloud_name: cloudinaryConfig.cloud_name || process.env.CLOUDINARY_CLOUD_NAME,
  api_key: cloudinaryConfig.api_key || process.env.CLOUDINARY_API_KEY,
  api_secret: cloudinaryConfig.api_secret || process.env.CLOUDINARY_API_SECRET
});

const PRESET = 'islabuy_products';
const FOLDER = 'islabuy/products';

/**
 * Generate a signed upload signature for Cloudinary.
 * Android calls this to get authorization before uploading.
 * The API secret is NEVER sent to the client.
 */
exports.getUploadSignature = functions.https.onCall(async (data, context) => {
  // Verify authenticated user
  if (!context.auth) {
    throw new functions.https.HttpsError('unauthenticated', 'Must be logged in');
  }

  const timestamp = Math.round(Date.now() / 1000);
  const params = {
    timestamp: timestamp,
    folder: FOLDER,
    upload_preset: PRESET,
    unique_filename: true,
    overwrite: false
  };

  const signature = cloudinary.utils.api_sign_request(params, cloudinaryConfig.api_secret);

  return {
    signature: signature,
    timestamp: timestamp,
    apiKey: cloudinaryConfig.api_key,
    cloudName: cloudinaryConfig.cloud_name,
    folder: FOLDER,
    preset: PRESET
  };
});

/**
 * Scheduled function: clean up expired perishable listings.
 * Runs every hour. Deletes Cloudinary media, then removes Firestore listing.
 */
exports.cleanupExpiredListings = functions.pubsub.schedule('every 1 hours').onRun(async () => {
  const db = admin.firestore();
  const now = new Date();

  const snapshot = await db.collection('listings')
    .where('status', '==', 'active')
    .where('isPerishable', '==', true)
    .get();

  const batch = db.batch();
  let deletedCount = 0;

  for (const doc of snapshot.docs) {
    const data = doc.data();
    if (!data.expirationDate || !data.expirationTime) continue;

    const expiry = new Date(`${data.expirationDate}T${data.expirationTime}`);
    if (expiry > now) continue;

    // Delete Cloudinary assets
    const publicIds = [
      ...(data.cloudinaryPublicIds || []),
      ...(data.cloudinaryVideoPublicIds || [])
    ];

    for (const pid of publicIds) {
      try {
        await cloudinary.uploader.destroy(pid, { resource_type: 'image' });
      } catch (e) {
        console.error(`Failed to delete image ${pid}:`, e);
        // Retry logic: mark for retry
        await db.collection('cleanupRetry').add({
          publicId: pid,
          listingId: doc.id,
          createdAt: admin.firestore.FieldValue.serverTimestamp(),
          attempts: 1
        });
      }
    }

    // Delete videos
    for (const pid of (data.cloudinaryVideoPublicIds || [])) {
      try {
        await cloudinary.uploader.destroy(pid, { resource_type: 'video' });
      } catch (e) {
        console.error(`Failed to delete video ${pid}:`, e);
      }
    }

    // Mark listing for deletion (or delete after successful cleanup)
    batch.update(doc.ref, { status: 'expired' });
    deletedCount++;
  }

  await batch.commit();
  console.log(`Cleaned up ${deletedCount} expired listings`);
  return null;
});

/**
 * Retry failed media deletions.
 * Runs every 6 hours.
 */
exports.retryFailedDeletions = functions.pubsub.schedule('every 6 hours').onRun(async () => {
  const db = admin.firestore();
  const retrySnapshot = await db.collection('cleanupRetry')
    .where('attempts', '<', 5)
    .get();

  for (const doc of retrySnapshot.docs) {
    const data = doc.data();
    try {
      await cloudinary.uploader.destroy(data.publicId, { resource_type: 'image' });
      await doc.ref.delete();
    } catch (e) {
      await doc.ref.update({ attempts: data.attempts + 1 });
    }
  }
  return null;
});

/**
 * Delete user account and all associated data.
 * Called from Android app.
 */
exports.deleteUserAccount = functions.https.onCall(async (data, context) => {
  if (!context.auth) {
    throw new functions.https.HttpsError('unauthenticated', 'Must be logged in');
  }

  const uid = context.auth.uid;
  const db = admin.firestore();

  // Delete user's listings and media
  const listings = await db.collection('listings').where('sellerId', '==', uid).get();
  for (const doc of listings.docs) {
    const d = doc.data();
    const pids = [...(d.cloudinaryPublicIds || []), ...(d.cloudinaryVideoPublicIds || [])];
    for (const pid of pids) {
      try {
        await cloudinary.uploader.destroy(pid, { resource_type: 'image' });
      } catch (e) {
        console.error(`Failed to delete ${pid}:`, e);
      }
    }
    await doc.ref.delete();
  }

  // Delete stores
  const stores = await db.collection('stores').where('sellerId', '==', uid).get();
  for (const doc of stores.docs) {
    await doc.ref.delete();
  }

  // Delete user document
  await db.collection('users').doc(uid).delete();

  // Delete auth account
  await admin.auth().deleteUser(uid);

  return { success: true };
});


/**
 * Push a notification to the target user's device when a notification document
 * is created. The app writes to `notifications/{id}`; this sends the FCM push.
 */
exports.sendNotificationPush = functions.firestore
  .document('notifications/{notificationId}')
  .onCreate(async (snap, context) => {
    const data = snap.data() || {};
    const userId = data.userId;
    if (!userId) return null;

    const userDoc = await admin.firestore().collection('users').doc(userId).get();
    const token = userDoc.exists ? userDoc.data().fcmToken : null;
    if (!token) return null;

    const message = {
      token: token,
      notification: {
        title: data.title || 'IslaBuy',
        body: data.body || data.message || ''
      },
      data: {
        type: data.type || 'general',
        notificationId: context.params.notificationId
      }
    };

    try {
      await admin.messaging().send(message);
    } catch (e) {
      console.error('FCM send failed:', e);
    }
    return null;
  });

/**
 * Admin-only: set a user's role (buyer, seller, admin).
 * The caller must already be an admin (users/{uid}.role == 'admin').
 */
exports.setUserRole = functions.https.onCall(async (data, context) => {
  if (!context.auth) {
    throw new functions.https.HttpsError('unauthenticated', 'Must be logged in');
  }
  const db = admin.firestore();
  const caller = await db.collection('users').doc(context.auth.uid).get();
  if (!caller.exists || caller.data().role !== 'admin') {
    throw new functions.https.HttpsError('permission-denied', 'Admins only');
  }
  const uid = data && data.uid;
  const role = data && data.role;
  if (!uid || !['buyer', 'seller', 'admin'].includes(role)) {
    throw new functions.https.HttpsError('invalid-argument', 'uid and a valid role are required');
  }
  await db.collection('users').doc(uid).update({ role: role });
  return { success: true };
});

/**
 * One-time bootstrap: if no admin exists yet, make the caller an admin.
 * After the first admin exists this always fails with permission-denied.
 */
exports.claimFirstAdmin = functions.https.onCall(async (data, context) => {
  if (!context.auth) {
    throw new functions.https.HttpsError('unauthenticated', 'Must be logged in');
  }
  const db = admin.firestore();
  const existing = await db.collection('users').where('role', '==', 'admin').limit(1).get();
  if (!existing.empty) {
    throw new functions.https.HttpsError('permission-denied', 'An admin already exists');
  }
  await db.collection('users').doc(context.auth.uid).update({ role: 'admin' });
  return { success: true };
});
