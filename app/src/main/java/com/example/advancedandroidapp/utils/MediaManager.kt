package com.example.advancedandroidapp.utils

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val permissionManager: PermissionManager
) {
    private val contentResolver: ContentResolver = context.contentResolver

    fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "IMG_${timeStamp}_"
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(imageFileName, ".jpg", storageDir)
    }

    fun saveImageToGallery(bitmap: Bitmap, title: String): Uri? {
        if (!permissionManager.hasStoragePermissions()) {
            throw SecurityException("Storage permission not granted")
        }

        val imageCollection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val imageDetails = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "$title.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.IS_PENDING, 1)
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }
        }

        return contentResolver.insert(imageCollection, imageDetails)?.also { uri ->
            contentResolver.openOutputStream(uri)?.use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                imageDetails.clear()
                imageDetails.put(MediaStore.Images.Media.IS_PENDING, 0)
                contentResolver.update(uri, imageDetails, null, null)
            }
        }
    }

    fun getImageFromUri(uri: Uri): Bitmap? {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            null
        }
    }

    fun compressImage(bitmap: Bitmap, quality: Int = 80): Bitmap {
        val outputStream = java.io.ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        val byteArray = outputStream.toByteArray()
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

    fun getMimeType(uri: Uri): String? {
        return if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            contentResolver.getType(uri)
        } else {
            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase())
        }
    }

    fun copyUriToFile(uri: Uri, file: File) {
        contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }
    }

    fun deleteFile(uri: Uri): Boolean {
        return try {
            contentResolver.delete(uri, null, null) > 0
        } catch (e: Exception) {
            false
        }
    }

    fun getFileSize(uri: Uri): Long {
        return contentResolver.openFileDescriptor(uri, "r")?.use {
            it.statSize
        } ?: 0
    }

    fun isImageFile(uri: Uri): Boolean {
        val mimeType = getMimeType(uri)
        return mimeType?.startsWith("image/") == true
    }

    fun getFileName(uri: Uri): String? {
        if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        return cursor.getString(nameIndex)
                    }
                }
            }
        }
        return uri.lastPathSegment
    }

    companion object {
        private const val MAX_IMAGE_DIMENSION = 1920
        private const val COMPRESSION_QUALITY = 80
    }
}

sealed class MediaResult {
    data class Success(val uri: Uri) : MediaResult()
    data class Error(val message: String) : MediaResult()
    object MissingPermission : MediaResult()
}

data class MediaInfo(
    val uri: Uri,
    val mimeType: String?,
    val size: Long,
    val name: String?
)

interface MediaRepository {
    suspend fun saveImage(bitmap: Bitmap, title: String): MediaResult
    suspend fun loadImage(uri: Uri): Bitmap?
    suspend fun deleteMedia(uri: Uri): Boolean
    fun getMediaInfo(uri: Uri): MediaInfo
}
