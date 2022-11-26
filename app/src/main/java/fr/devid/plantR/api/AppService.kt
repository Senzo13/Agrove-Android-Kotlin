package fr.devid.plantR.api

import fr.devid.plantR.models.NotificationDto
import fr.devid.plantR.services.AuthenticationTokenInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface AppService {

    @POST("login")
    suspend fun login(@Body loginDto: LoginDto): Response<LoginResponseDto>

    @Headers("Content-Type: application/json")
    @POST("commands")
    suspend fun sendCommand(@Body commands: SendCommandLiveObject): Call<SendCommandLiveObjectResponse>


    @POST("register")
    suspend fun register(@Body registerDto: RegisterDto): Response<TokenDto>

    @GET("profile")
    @Headers(AuthenticationTokenInterceptor.AUTHORIZATION_HEADER)
    suspend fun getProfile(): ProfileDto

    @POST("fcm/send")
    suspend fun sendNotification(@Header("Authorization") authorize : String, @Body notification : NotificationDto) : Response<Void>




}
