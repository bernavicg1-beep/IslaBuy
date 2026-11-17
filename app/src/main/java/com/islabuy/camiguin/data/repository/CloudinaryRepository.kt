package com.islabuy.camiguin.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class CloudinaryRepository(private val context: Context) {

    companion object {
        const val MAX_PHOTOS = 5
        const val MAX_DIMENSION = 1600
        const val JPEG_QUALITY = 80
        const val PRESET = "islabuy_products"
        const val FOLDER = "islabuy/products"
        const val CLOUD_NAME = "ubezsbu4"
    }

    /**
     * Compress image before upload: max 1600px dimension, JPEG quality 80.
     */
    suspend fun compressImage(uri: Uri): ByteArray = withContext(Dispatchers.IO) {
        val inputStream = context.contentResolver.openInputStream(uri)
        val original = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        val (width, height) = calculateDimensions(original.width, original.height)
        val scaled = Bitmap.createScaledBitmap(original, width, height, true)

        val output = ByteArrayOutputStream()
        scaled.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, output)
        output.toByteArray()
    }

    private fun calculateDimensions(w: Int, h: Int): Pair<Int, Int> {
        if (w <= MAX_DIMENSION && h <= MAX_DIMENSION) return Pair(w, h)
        val ratio = if (w > h) MAX_DIMENSION.toFloat() / w else MAX_DIMENSION.toFloat() / h
        return Pair((w * ratio).toInt(), (h * ratio).toInt())
    }

    /**
     * Unsigned upload flow (no backend signing needed):
     * 1. Upload compressed image bytes directly to Cloudinary.
     * 2. Return secure URL + public ID.
     *
     * Requires upload preset `camiguin_market_products` to be set to
     * "Unsigned" mode in Cloudinary dashboard.
     * No API secret needed — safe for client-side use.
     */
    suspend fun uploadImage(compressedBytes: ByteArray): CloudinaryUploadResult = withContext(Dispatchers.IO) {
        val url = "https://api.cloudinary.com/v1_1/$CLOUD_NAME/image/upload"
        val boundary = "----FormBoundary${System.currentTimeMillis()}"
        val connection = java.net.URL(url).openConnection() as java.net.HttpURLConnection
        connection.requestMethod = "POST"
        connection.doOutput = true
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")

        val output = connection.outputStream
        val writer = java.io.PrintWriter(java.io.OutputStreamWriter(output, "UTF-8"), true)

        fun addField(name: String, value: String) {
            writer.append("--$boundary\r\n")
            writer.append("Content-Disposition: form-data; name=\"$name\"\r\n\r\n")
            writer.append("$value\r\n")
            writer.flush()
        }

        addField("upload_preset", PRESET)
        addField("folder", FOLDER)

        writer.append("--$boundary\r\n")
        writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"image.jpg\"\r\n")
        writer.append("Content-Type: image/jpeg\r\n\r\n")
        writer.flush()
        output.write(compressedBytes)
        output.flush()
        writer.append("\r\n")
        writer.append("--$boundary--\r\n")
        writer.close()

        val responseCode = connection.responseCode
        if (responseCode == 200) {
            val response = connection.inputStream.bufferedReader().readText()
            val json = org.json.JSONObject(response)
            CloudinaryUploadResult(
                secureUrl = json.getString("secure_url"),
                publicId = json.getString("public_id"),
                resourceType = "image"
            )
        } else {
            val error = connection.errorStream?.bufferedReader()?.readText() ?: "Unknown error"
            throw Exception("Cloudinary upload failed ($responseCode): $error")
        }
    }

    data class CloudinaryUploadResult(
        val secureUrl: String,
        val publicId: String,
        val resourceType: String
    )
}
