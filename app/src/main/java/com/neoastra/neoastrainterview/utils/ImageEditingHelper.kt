package com.neoastra.neoastrainterview.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import androidx.exifinterface.media.ExifInterface
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import java.io.IOException

object ImageEditingHelper {
    fun getImagePath(context: Context, imageUri: Uri): String {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(imageUri, projection, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndex(MediaStore.Images.Media.DATA)
                Log.d("PHOTO_PICKER", "Column Index is - ${it.getString(columnIndex)}")
                return it.getString(columnIndex) ?: ""
            }
        }

        return ""
    }


    fun getExifInformation(imagePath: String): String {
        return try {
            val exifInfo = ExifInterface(imagePath)
            val exifString = StringBuilder()
            exifString.append("Image Information:\n")
            exifString.append("-------------\n")
            exifString.append("File Path: $imagePath\n")
            exifString.append("-------------\n")
            exifString.append("Make: ${exifInfo.getAttribute(ExifInterface.TAG_MAKE)}\n")
            exifString.append("Model: ${exifInfo.getAttribute(ExifInterface.TAG_MODEL)}\n")
            exifString.append("Date Time: ${exifInfo.getAttribute(ExifInterface.TAG_DATETIME)}\n")
            exifString.toString()
        } catch (exception: IOException) {
            exception.printStackTrace()
            "No Image Info Found"
        }
    }

    fun saveBitmapToFile(context: Context, bitmap: Bitmap, fileName: String): Boolean {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        val resolver = context.contentResolver

        val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        return try {
            imageUri?.let { uri ->
                val outputStream = resolver.openOutputStream(uri)
                outputStream?.use { stream ->
                    if (!bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)) {
                        throw IOException("Failed to save image")
                    }
                }
                values.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(uri, values, null, null)
                true
            } ?: false
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }
}
