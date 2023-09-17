package com.neoastra.neoastrainterview.viewmodels

import android.graphics.Bitmap
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.neoastra.neoastrainterview.model.AspectRatio
import com.neoastra.neoastrainterview.model.EditControl
import com.neoastra.neoastrainterview.views.home_screen.HomeScreenEvent
import com.neoastra.neoastrainterview.views.home_screen.HomeScreenState

class HomeScreenViewModel: ViewModel() {
    private var _homeScreenState: MutableState<HomeScreenState> = mutableStateOf(HomeScreenState())
    val homeScreenState: State<HomeScreenState> = _homeScreenState

    fun onEvent(homeScreenEvent: HomeScreenEvent){
        when(homeScreenEvent){
            is HomeScreenEvent.OnImageSelected -> {
                _homeScreenState.value = _homeScreenState.value.copy(
                    selectedImageUri = homeScreenEvent.imageUri,
                    selectedImagePath = homeScreenEvent.imagePath,
                    selectedImageExifInfo = homeScreenEvent.exifInfo,
                    selectedBitmap = homeScreenEvent.imageBitmap,
                    editedBitmap = homeScreenEvent.imageBitmap
                )
            }

            is HomeScreenEvent.UpdateSelectedEditControl -> {
                val bitmap = if (homeScreenEvent.selectedEditControl == EditControl.IMAGE_CROP) _homeScreenState.value.selectedBitmap else _homeScreenState.value.editedBitmap
                _homeScreenState.value = _homeScreenState.value.copy(
                    selectedEditControl = if (homeScreenEvent.selectedEditControl == _homeScreenState.value.selectedEditControl) EditControl.NONE else homeScreenEvent.selectedEditControl,
                    editedBitmap = bitmap
                )
            }

            is HomeScreenEvent.UpdateSelectedBitmap -> {
                _homeScreenState.value = _homeScreenState.value.copy(
                    selectedBitmap = homeScreenEvent.selectedBitmap,
                    editedBitmap = homeScreenEvent.selectedBitmap
                )
            }

            is HomeScreenEvent.UpdateEditedBitmap -> {
                _homeScreenState.value = _homeScreenState.value.copy(
                    editedBitmap = homeScreenEvent.editedBitmap
                )
            }

            is HomeScreenEvent.UpdateFlipHorizontal -> {
                _homeScreenState.value = _homeScreenState.value.copy(
                    flipHorizontal = homeScreenEvent.flipStatus
                )
            }
            is HomeScreenEvent.UpdateFlipVertical -> {
                _homeScreenState.value = _homeScreenState.value.copy(
                    flipVertical = homeScreenEvent.flipStatus
                )
            }

            HomeScreenEvent.ResetCropRatio -> {
                _homeScreenState.value = _homeScreenState.value.copy(
                    cropRatio = AspectRatio.RATIO_NONE,
                    editedBitmap = _homeScreenState.value.selectedBitmap
                )
            }

            is HomeScreenEvent.UpdateCropRatioValue -> {
                _homeScreenState.value = _homeScreenState.value.copy(
                    cropRatio = homeScreenEvent.cropRatio
                )
            }

            is HomeScreenEvent.CropBitmap -> {
                try {
                    val aspectRatio = when (homeScreenEvent.aspectRatio){
                        AspectRatio.RATIO_4_3 -> 4f/3f
                        AspectRatio.RATIO_3_2 -> 3f/2f
                        AspectRatio.RATIO_16_9 -> 16f/9f
                        AspectRatio.RATIO_1_1 -> 1f
                        AspectRatio.RATIO_NONE -> null
                    }
                    aspectRatio?.let {
                        val selectedBitmap = _homeScreenState.value.selectedBitmap!!
                        val width = selectedBitmap.width
                        val height = (width / aspectRatio).toInt()
                        val startY = (selectedBitmap.height - height) / 2
                        val croppedBitmap = Bitmap.createBitmap(selectedBitmap, 0, startY, width, height)
                        _homeScreenState.value = _homeScreenState.value.copy(
                            editedBitmap = croppedBitmap,
                            cropRatio = homeScreenEvent.aspectRatio
                        )
                    }
                }catch (e: Exception){
                    e.printStackTrace()
                }
            }
        }
    }
}