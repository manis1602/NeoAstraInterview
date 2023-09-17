package com.neoastra.neoastrainterview.utils

import android.graphics.Bitmap
import android.graphics.Matrix


fun Bitmap.horizontalFlip(): Bitmap {
    val matrix = Matrix()
    matrix.preScale(-1f, 1f)
    return Bitmap.createBitmap(this, 0, 0, this.width, this.height, matrix, true)
}

fun Bitmap.verticalFlip(): Bitmap {
    val matrix = Matrix()
    matrix.preScale(1f, -1f)
    return Bitmap.createBitmap(this, 0, 0, this.width, this.height, matrix, true)
}