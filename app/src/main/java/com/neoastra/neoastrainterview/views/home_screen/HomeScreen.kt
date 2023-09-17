package com.neoastra.neoastrainterview.views.home_screen

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neoastra.neoastrainterview.R
import com.neoastra.neoastrainterview.model.AspectRatio
import com.neoastra.neoastrainterview.model.EditControl
import com.neoastra.neoastrainterview.utils.horizontalFlip
import com.neoastra.neoastrainterview.utils.verticalFlip
import com.neoastra.neoastrainterview.viewmodels.HomeScreenViewModel
import com.neoastra.neoastrainterview.utils.ImageEditingHelper.getExifInformation
import com.neoastra.neoastrainterview.utils.ImageEditingHelper.getImagePath
import com.neoastra.neoastrainterview.utils.ImageEditingHelper.saveBitmapToFile
import java.io.IOException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val context: Context = LocalContext.current

    val homeScreenViewModel: HomeScreenViewModel = viewModel()
    val homeScreenState by homeScreenViewModel.homeScreenState

    val permissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
    )
    var isEditingImage by remember {
        mutableStateOf(false)
    }


    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            data?.data?.let { imageUri ->
                try {
                    val imagePath: String = getImagePath(context = context, imageUri = imageUri)
                    val imageExifInfo = getExifInformation(imagePath = imagePath)

                    val inputStream = context.contentResolver.openInputStream(imageUri)
                    val imageBitmap = BitmapFactory.decodeStream(inputStream)
                    inputStream?.close()
                    homeScreenViewModel.onEvent(
                        HomeScreenEvent.OnImageSelected(
                            imageUri = imageUri,
                            imagePath = imagePath,
                            exifInfo = imageExifInfo,
                            imageBitmap = imageBitmap
                        )
                    )
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissionMap ->
            val allPermissionsGranted = permissionMap.values.reduce { acc, next -> acc && next }
            if (!allPermissionsGranted) {
                Toast.makeText(context, "Storage Permission Required!!", Toast.LENGTH_SHORT).show()
            } else {
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                imagePicker.launch(intent)
            }
        }
    )


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.neoastra))
                },
                actions = {
                    IconButton(onClick = {
                        if (permissions.all {
                                ContextCompat.checkSelfPermission(
                                    context,
                                    it
                                ) == PackageManager.PERMISSION_GRANTED
                            }) {
                            val intent = Intent(
                                Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                            )
                            imagePicker.launch(intent)
                        } else {
                            permissionLauncher.launch(permissions)
                        }

                    }) {
                        Icon(
                            imageVector = Icons.Filled.FileOpen,
                            contentDescription = stringResource(R.string.file_open_icon)
                        )
                    }

                    IconButton(onClick = {
                        val fileName =
                            homeScreenState.selectedImageUri?.lastPathSegment + "_edited.png"
                        homeScreenState.editedBitmap?.let { bitmap ->
                            if (saveBitmapToFile(context, bitmap, fileName)) {
                                Toast.makeText(
                                    context,
                                    "Image saved as $fileName",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Image not saved!!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Save,
                            contentDescription = stringResource(R.string.save_file_icon)
                        )
                    }
                }
            )
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(9f)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                homeScreenState.editedBitmap?.let { bitmap ->
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = stringResource(id = R.string.selected_image_view)
                    )
                }
            }
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(onClick = {
                    homeScreenViewModel.onEvent(
                        HomeScreenEvent.UpdateSelectedEditControl(
                            selectedEditControl = EditControl.IMAGE_INFO
                        )
                    )
                }) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = stringResource(R.string.info_icon)
                    )
                }
                IconButton(onClick = {
                    homeScreenViewModel.onEvent(
                        HomeScreenEvent.UpdateSelectedEditControl(
                            selectedEditControl = EditControl.IMAGE_CROP
                        )
                    )
                }) {
                    Icon(
                        imageVector = Icons.Filled.Crop,
                        contentDescription = stringResource(R.string.crop_main_icon)
                    )
                }
                IconButton(onClick = {
                    homeScreenViewModel.onEvent(
                        HomeScreenEvent.UpdateSelectedEditControl(
                            selectedEditControl = EditControl.IMAGE_FLIP
                        )
                    )
                }) {
                    Icon(
                        imageVector = Icons.Filled.Flip,
                        contentDescription = stringResource(R.string.flip_icon)
                    )
                }
            }

            AnimatedVisibility(visible = homeScreenState.selectedEditControl != EditControl.NONE) {
                when (homeScreenState.selectedEditControl) {
                    EditControl.IMAGE_INFO -> {
                        Text(text = homeScreenState.selectedImageExifInfo)
                    }

                    EditControl.IMAGE_CROP -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(onClick = {
                                homeScreenViewModel.onEvent(
                                    HomeScreenEvent.CropBitmap(
                                        aspectRatio = AspectRatio.RATIO_4_3
                                    )
                                )
                            }) {
                                Text(text = "4:3")
                            }
                            TextButton(onClick = {
                                homeScreenViewModel.onEvent(
                                    HomeScreenEvent.CropBitmap(
                                        aspectRatio = AspectRatio.RATIO_3_2
                                    )
                                )
                            }) {
                                Text(text = "3:2")
                            }
                            TextButton(onClick = {
                                homeScreenViewModel.onEvent(
                                    HomeScreenEvent.CropBitmap(
                                        aspectRatio = AspectRatio.RATIO_16_9
                                    )
                                )
                            }) {
                                Text(text = "16:9")
                            }
                            TextButton(onClick = {
                                homeScreenViewModel.onEvent(
                                    HomeScreenEvent.CropBitmap(
                                        aspectRatio = AspectRatio.RATIO_1_1
                                    )
                                )
                            }) {
                                Text(text = "1:1")
                            }

                            TextButton(
                                enabled = homeScreenState.cropRatio != AspectRatio.RATIO_NONE,
                                onClick = {
                                    homeScreenViewModel.onEvent(HomeScreenEvent.ResetCropRatio)
                                }) {
                                Text(text = "Original")
                            }
                        }
                    }

                    EditControl.IMAGE_FLIP -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = {
                                isEditingImage = true
                                homeScreenState.editedBitmap?.let { bitmap ->
                                    val editedBitmap = Bitmap.createBitmap(bitmap)
                                    val flippedBitmap = editedBitmap.verticalFlip()
                                    homeScreenViewModel.onEvent(
                                        HomeScreenEvent.UpdateEditedBitmap(
                                            editedBitmap = flippedBitmap
                                        )
                                    )

                                }
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.flip_vertical),
                                    contentDescription = stringResource(
                                        R.string.flip_vertical_icon
                                    )
                                )

                            }

                            IconButton(onClick = {
                                isEditingImage = true
                                homeScreenState.editedBitmap?.let { bitmap ->
                                    val editedBitmap = Bitmap.createBitmap(bitmap)
                                    val flippedBitmap = editedBitmap.horizontalFlip()
                                    homeScreenViewModel.onEvent(
                                        HomeScreenEvent.UpdateEditedBitmap(
                                            editedBitmap = flippedBitmap
                                        )
                                    )
                                }
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.flip_horizontal),
                                    contentDescription = stringResource(
                                        R.string.flip_horizontal_icon
                                    )
                                )
                            }
                        }
                    }

                    EditControl.NONE -> {

                    }
                }
            }

        }

    }
}



