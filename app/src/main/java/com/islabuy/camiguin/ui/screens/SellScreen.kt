package com.islabuy.camiguin.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.islabuy.camiguin.data.model.Listing
import com.islabuy.camiguin.data.repository.CloudinaryRepository
import com.islabuy.camiguin.data.repository.UserRepository
import com.islabuy.camiguin.ui.components.ThreeDButton
import com.islabuy.camiguin.util.Categories
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellScreen(onBack: () -> Unit = {}) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()
    val userRepo = remember { UserRepository() }
    val sellerId = userRepo.currentUser()?.uid ?: ""
    val sellerName = userRepo.currentUser()?.displayName ?: userRepo.currentUser()?.email ?: "Seller"
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("1") }
    var category by remember { mutableStateOf(Categories.ALL.first()) }
    var isPerishable by remember { mutableStateOf(false) }
    var expirationDate by remember { mutableStateOf("") }
    var expirationTime by remember { mutableStateOf("") }
    var imageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var pickupLat by remember { mutableStateOf<Double?>(null) }
    var pickupLng by remember { mutableStateOf<Double?>(null) }
    var showMap by remember { mutableStateOf(false) }
    var isUploading by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf("") }

    // Image picker - max 5 photos
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        val combined = (imageUris + uris).distinct().take(CloudinaryRepository.MAX_PHOTOS)
        imageUris = combined
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Create Product Listing", style = MaterialTheme.typography.headlineSmall)

        // Title
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Product Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // Description
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        // Price and Quantity row
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Price (₱)") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            OutlinedTextField(
                value = quantity,
                onValueChange = { quantity = it },
                label = { Text("Quantity") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
        }

        // Category dropdown
        var expanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
            OutlinedTextField(
                value = category,
                onValueChange = {},
                readOnly = true,
                label = { Text("Category") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                Categories.ALL.forEach { cat ->
                    DropdownMenuItem(
                        text = { Text(cat) },
                        onClick = {
                            category = cat
                            isPerishable = Categories.isPerishableCategory(cat)
                            expanded = false
                        }
                    )
                }
            }
        }

        // Perishable toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Perishable Product")
            Switch(checked = isPerishable, onCheckedChange = { isPerishable = it })
        }

        // Expiration fields (required if perishable)
        if (isPerishable) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = expirationDate,
                    onValueChange = { expirationDate = it },
                    label = { Text("Expiration Date (yyyy-MM-dd)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = expirationTime,
                    onValueChange = { expirationTime = it },
                    label = { Text("Expiration Time (HH:mm)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }
        }

        // Image picker
        Text("Photos (${imageUris.size}/${CloudinaryRepository.MAX_PHOTOS})")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ThreeDButton(
                text = "Add Photos",
                onClick = { imagePicker.launch("image/*") }
            )
            if (imageUris.isNotEmpty()) {
                TextButton(onClick = { imageUris = emptyList() }) {
                    Text("Clear")
                }
            }
        }

        // Pickup location
        Text("Pickup Location")
        if (pickupLat != null && pickupLng != null) {
            Text("Lat: $pickupLat, Lng: $pickupLng", style = MaterialTheme.typography.bodySmall)
        }
        ThreeDButton(
            text = if (pickupLat == null) "Select Pickup Location" else "Change Location",
            onClick = { showMap = true }
        )

        // Status message
        if (statusMessage.isNotEmpty()) {
            Text(statusMessage, color = MaterialTheme.colorScheme.primary)
        }

        // Upload button
        ThreeDButton(
            text = "Upload Listing",
            onClick = {
                // Validate
                if (title.isBlank() || price.isBlank()) {
                    statusMessage = "Title and price are required"
                    return@ThreeDButton
                }
                if (isPerishable && (expirationDate.isBlank() || expirationTime.isBlank())) {
                    statusMessage = "Expiration date and time are required for perishable products"
                    return@ThreeDButton
                }
                if (pickupLat == null || pickupLng == null) {
                    statusMessage = "Pickup location is required"
                    return@ThreeDButton
                }
                isUploading = true
                statusMessage = "Uploading..."
                scope.launch {
                    try {
                        val cloudinaryRepo = CloudinaryRepository(context)
                        // 1. Compress images (max 1600px, JPEG 80%)
                        val compressedImages = imageUris.map { uri ->
                            cloudinaryRepo.compressImage(uri)
                        }
                        statusMessage = "Compressed ${compressedImages.size} images"

                        // 2. Upload to Cloudinary (calls Cloud Function for signing internally)
                        val uploadResults = compressedImages.map { bytes ->
                            cloudinaryRepo.uploadImage(bytes)
                        }
                        statusMessage = "Uploaded ${uploadResults.size} images"

                        // 3. Save listing to Firestore with Cloudinary URLs
                        val imageUrls = uploadResults.map { it.secureUrl }
                        val publicIds = uploadResults.map { it.publicId }

                        Listing(
                            sellerId = sellerId,
                            sellerName = sellerName,
                            title = title,
                            description = description,
                            price = price.toDoubleOrNull() ?: 0.0,
                            category = category,
                            quantity = quantity.toIntOrNull() ?: 1,
                            isPerishable = isPerishable,
                            expirationDate = if (isPerishable) expirationDate else null,
                            expirationTime = if (isPerishable) expirationTime else null,
                            latitude = pickupLat,
                            longitude = pickupLng,
                            pickupAddress = null,
                            imageUrls = imageUrls,
                            cloudinaryPublicIds = publicIds,
                            createdAt = System.currentTimeMillis(),
                            status = "active"
                        )
                        // Save to Firestore
                        // ListingRepository().createListing(listing)

                        statusMessage = "Product published successfully!"
                    } catch (e: Exception) {
                        statusMessage = "Error: ${e.message}"
                    } finally {
                        isUploading = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isUploading
        ) {
            if (isUploading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp))
            } else {
                Text("Publish Product")
            }
        }
    }

    // Map dialog
    if (showMap) {
        AlertDialog(
            onDismissRequest = { showMap = false },
            title = { Text("Select Pickup Location") },
            text = {
                Box(modifier = Modifier.height(400.dp)) {
                    PickupLocationSelector(
                        onLocationSelected = { lat, lng ->
                            pickupLat = lat
                            pickupLng = lng
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showMap = false }) { Text("Done") }
            }
        )
    }
}
