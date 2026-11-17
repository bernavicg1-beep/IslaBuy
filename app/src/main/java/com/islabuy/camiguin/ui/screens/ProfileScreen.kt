package com.islabuy.camiguin.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.islabuy.camiguin.data.repository.CloudinaryRepository
import com.islabuy.camiguin.data.repository.UserRepository
import com.islabuy.camiguin.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    onSettings: () -> Unit = {},
    onMyProducts: () -> Unit = {},
    onMyOrders: () -> Unit = {},
    onMyStore: () -> Unit = {},
    onMessages: () -> Unit = {},
    onAdmin: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val userRepo = remember { UserRepository() }
    val cloudinary = remember { CloudinaryRepository(context) }
    val user = userRepo.currentUser()
    val profile = remember { mutableStateOf<com.islabuy.camiguin.data.model.User?>(null) }
    var uploading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(user?.uid) {
        if (user?.uid != null) profile.value = userRepo.getUserProfile(user.uid)
    }
    val p = profile.value

    val pickImage = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null && user?.uid != null) {
            scope.launch {
                uploading = true
                errorMessage = ""
                try {
                    val bytes = cloudinary.compressImage(uri)
                    val result = cloudinary.uploadImage(bytes)
                    userRepo.updateProfile(user.uid, mapOf("profileImageUrl" to result.secureUrl))
                        .onSuccess {
                            profile.value = profile.value?.copy(profileImageUrl = result.secureUrl)
                        }
                        .onFailure { errorMessage = it.message ?: "Upload failed" }
                } catch (e: Exception) {
                    errorMessage = e.message ?: "Upload failed"
                } finally {
                    uploading = false
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(72.dp),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = p?.profileImageUrl,
                    contentDescription = "Profile",
                    modifier = Modifier.size(72.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                if (uploading) {
                    CircularProgressIndicator(modifier = Modifier.size(26.dp), strokeWidth = 2.dp)
                } else {
                    IconButton(
                        onClick = { pickImage.launch("image/*") },
                        modifier = Modifier.align(Alignment.BottomEnd).size(28.dp)
                    ) {
                        Icon(
                            Icons.Default.PhotoCamera,
                            contentDescription = "Change profile picture",
                            tint = NavyBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(p?.name ?: user?.displayName ?: "Guest", fontWeight = FontWeight.Bold, color = PrimaryText, fontSize = 18.sp)
                Text(p?.email ?: user?.email ?: "", color = SecondaryText, fontSize = 13.sp)
                if (p?.username?.isNotBlank() == true) Text("@${p.username}", color = SecondaryText, fontSize = 12.sp)
            }
        }
        if (errorMessage.isNotBlank()) {
            Text(errorMessage, color = ErrorRed, fontSize = 12.sp, modifier = Modifier.padding(top = 6.dp))
        }
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatCard("Products", "0", Modifier.weight(1f))
            StatCard("Orders", "0", Modifier.weight(1f))
            StatCard("Sold", "0", Modifier.weight(1f))
        }
        Spacer(Modifier.height(16.dp))
        MenuItem(Icons.Default.Store, "My Products", onMyProducts)
        MenuItem(Icons.Default.ShoppingBag, "My Orders", onMyOrders)
        MenuItem(Icons.Default.Storefront, "My Store", onMyStore)
        MenuItem(Icons.Default.Message, "Messages", onMessages)
        MenuItem(Icons.Default.Settings, "Settings", onSettings)
        if (p?.isAdmin() == true) MenuItem(Icons.Default.AdminPanelSettings, "Admin Panel", onAdmin)
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = { userRepo.signOut(); onLogout() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
        ) { Text("Logout", color = PureWhite) }
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(12.dp)) {
        Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, fontWeight = FontWeight.Bold, color = NavyBlue)
            Text(label, fontSize = 11.sp, color = SecondaryText)
        }
    }
}

@Composable
private fun MenuItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = NavyBlue)
            Spacer(Modifier.width(12.dp))
            Text(label, color = PrimaryText)
            Spacer(Modifier.height(16.dp))
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = SecondaryText)
        }
    }
}