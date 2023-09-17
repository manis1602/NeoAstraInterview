package com.neoastra.neoastrainterview.views.home_screen

import android.graphics.Bitmap
import android.net.Uri
import com.neoastra.neoastrainterview.model.AspectRatio
import com.neoastra.neoastrainterview.model.EditControl

sealed class HomeScreenEvent{
    data class OnImageSelected(val imageUri: Uri, val imagePath: String, val exifInfo: String, val imageBitmap: Bitmap?): HomeScreenEvent()
    data class UpdateSelectedEditControl(val selectedEditControl: EditControl): HomeScreenEvent()
    data class UpdateSelectedBitmap(val selectedBitmap: Bitmap?): HomeScreenEvent()
    data class UpdateEditedBitmap(val editedBitmap: Bitmap?): HomeScreenEvent()
    data class UpdateFlipHorizontal(val flipStatus: Boolean): HomeScreenEvent()
    data class UpdateFlipVertical(val flipStatus: Boolean): HomeScreenEvent()
    object ResetCropRatio: HomeScreenEvent()
    data class UpdateCropRatioValue (val cropRatio: AspectRatio): HomeScreenEvent()
    data class CropBitmap(val aspectRatio: AspectRatio): HomeScreenEvent()
}
