package com.example.trackme.extension

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import timber.log.Timber
import java.io.ByteArrayOutputStream

fun Bitmap.string(): String? {
    return try {
        val outPutStream = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.PNG, 100, outPutStream)
        val bytes = outPutStream.toByteArray()
        Base64.encodeToString(bytes, Base64.DEFAULT)
    } catch (e: Exception) {
        Timber.e(e, "Convert bitmap to string error")
        null
    }
}

fun String?.toBitmap(): Bitmap? {
    return try {
        val encodeByte = Base64.decode(this, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
    } catch (e: Exception) {
        null
    }
}