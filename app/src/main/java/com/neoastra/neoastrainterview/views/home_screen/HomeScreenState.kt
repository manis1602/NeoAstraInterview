package com.neoastra.neoastrainterview.views.home_screen

import android.graphics.Bitmap
import android.net.Uri
import com.neoastra.neoastrainterview.model.AspectRatio
import com.neoastra.neoastrainterview.model.EditControl

data class HomeScreenState(
    val selectedImageUri: Uri? = null,
    val selectedImagePath: String = "",
    val selectedImageExifInfo: String = "",
    val selectedEditControl: EditControl = EditControl.NONE,
    val selectedBitmap: Bitmap? = null,
    val flipHorizontal: Boolean = false,
    val flipVertical: Boolean = false,
    val editedBitmap: Bitmap? = null,
    val cropRatio: AspectRatio = AspectRatio.RATIO_NONE
)
