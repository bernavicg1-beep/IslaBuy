package com.islabuy.camiguin

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.islabuy.camiguin.navigation.AppNavigation
import com.islabuy.camiguin.notifications.LocalNotifications
import com.islabuy.camiguin.ui.theme.IslaBuyTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        requestNotificationPermission()

        setContent {
            LaunchedEffect(Unit) { }
            ListenForNotifications()
            IslaBuyTheme {
                AppNavigation()
            }
        }
    }

    /**
     * Spark-plan friendly push replacement: watch the signed-in user's
     * unread notifications and show a local notification for new ones.
     */
    @androidx.compose.runtime.Composable
    private fun ListenForNotifications() {
        val context = LocalContext.current
        DisposableEffect(Unit) {
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            var registration: ListenerRegistration? = null
            val startedAt = System.currentTimeMillis()

            if (uid != null) {
                registration = FirebaseFirestore.getInstance()
                    .collection("notifications")
                    .whereEqualTo("userId", uid)
                    .whereEqualTo("isRead", false)
                    .addSnapshotListener { snapshot, _ ->
                        snapshot?.documentChanges?.forEach { change ->
                            if (change.type != DocumentChange.Type.ADDED) return@forEach
                            val doc = change.document
                            val createdAt = doc.getLong("createdAt") ?: 0L
                            // Ignore documents that existed before this listener started.
                            if (createdAt < startedAt - 2000) return@forEach
                            val title = doc.getString("title") ?: "IslaBuy"
                            val body = doc.getString("body") ?: doc.getString("message") ?: ""
                            val type = doc.getString("type") ?: "general"
                            LocalNotifications.show(context, doc.id.hashCode(), title, body, type)
                        }
                    }
            }

            onDispose { registration?.remove() }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }
    }
}