package fr.devid.plantR.utils

import android.content.Context
import com.google.gson.Gson
import fr.devid.plantR.models.ListPaysModel
import fr.devid.plantR.models.Pays
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject

class DownlinkUtils(var context: Context) {

    init {
        var dataTest = "36313b3133353b3133382c302c302c313b3133302c35302c302c31353b3134302c302c302c313b3133383b3133343b36323b3b"
    }


    fun addCommand(message : String) {
        val data = JSONObject("{\r\n    \"data\":\"$message\",\r\n    \"port\":2,\r\n    \"confirmed\" : true\r\n}")
        val client = OkHttpClient().newBuilder()
                .build()
        val json = Gson().toJson(data)
        val body = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), json)
        val request: Request = Request.Builder()
                .url("https://liveobjects.orange-business.com/api/v0/vendors/lora/devices/C4AC590000CC8AC2/commands")
                .method("POST", body)
                .addHeader("x-api-key", "4855f5c1e92647d7989c31b1197b466f")
                .addHeader("Content-Type", "application/json")
                .build()

        var response: Response = client.newCall(request).execute()
    }

    fun getDeviceInfo() {

    }
}