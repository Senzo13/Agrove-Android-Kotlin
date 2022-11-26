package fr.devid.plantR.utils

import android.content.Context
import java.io.IOException
import java.io.InputStream

class JsonUtils {
    fun read_json(context: Context, nameFile: String) : String? {
        var input : InputStream? = null
        val jsonString : String

        try {
            input = context.assets.open(nameFile)

            val size = input.available()

            val buffer = ByteArray(size)

            input.read(buffer)
            jsonString = String(buffer)
            return jsonString
        } catch (e : IOException) {
            e.printStackTrace()
        } finally {
            input?.close()
        }
        return null
    }
}