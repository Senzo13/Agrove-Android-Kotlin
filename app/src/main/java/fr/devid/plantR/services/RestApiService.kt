package fr.devid.plantR.services
import fr.devid.plantR.api.AppService
import fr.devid.plantR.api.SendCommandLiveObject
import fr.devid.plantR.api.SendCommandLiveObjectResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RestApiService {
    suspend fun sendCommands(sent: SendCommandLiveObject, onResult: (SendCommandLiveObjectResponse?) -> Unit){
        val retrofit = ServiceBuilder.buildService(AppService::class.java)
        retrofit.sendCommand(sent).enqueue(
            object : Callback<SendCommandLiveObjectResponse> {
                override fun onFailure(call: Call<SendCommandLiveObjectResponse>, t: Throwable) {
                    onResult(null)
                    }

                override fun onResponse(
                    call: Call<SendCommandLiveObjectResponse>,
                    response: Response<SendCommandLiveObjectResponse>
                ) {
                    val commandSend = response.body()
                    onResult(commandSend)
                }
            }
        )
    }
}

