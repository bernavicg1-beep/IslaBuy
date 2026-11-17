package com.islabuy.camiguin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.islabuy.camiguin.navigation.AppNavigation
import com.islabuy.camiguin.ui.theme.IslaBuyTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            LaunchedEffect(Unit) { registerFcmToken() }
            IslaBuyTheme {
                AppNavigation()
            }
        }
    }

    /** Save the current FCM token so Cloud Functions can push to this device. */
    private fun registerFcmToken() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result ?: return@addOnCompleteListener
                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(uid)
                    .update("fcmToken", token)
            }
        }
    }
}