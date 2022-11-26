package fr.devid.plantR.services

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import java.io.ByteArrayOutputStream

object PicturesUtils {

    fun getBitmapFromIntent(uri: Uri, context: Context): Bitmap {
        return if (android.os.Build.VERSION.SDK_INT >= 28) {
            ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
        } else {
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }
    }

    fun getBase64FromUri(uri: Uri, context: Context): String {
        val bitmap = getBitmapFromIntent(uri, context)
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        val encoded = Base64.encodeToString(byteArray, Base64.DEFAULT)
        return encoded
    }
}