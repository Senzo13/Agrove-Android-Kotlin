package fr.devid.plantR.api

data class LoginDto(val email: String, val password: String)

data class LoginResponseDto(val token: String?)

data class SendCommandLiveObject(var data: String, var port : Int, var confirmed : Boolean)

data class SendCommandLiveObjectResponse(
    var data: String,
    var port : Int,
    var confirmed : Boolean,
    var commandStatus : String,
    var creationTs : String,
    var updateTs: String)

data class RegisterDto(val nom: String, val prenom: String, val email: String, val password: String,
                       val identificationCode: String? = null)

data class TokenDto(val token: String?)

data class AddressDto(
    val addressEntered: String,
    val streetNumber: String?,
    val route: String?,
    val postalCode: String,
    val city: String,
    val department: String,
    val region: String,
    val country: String,
    val latitude: String,
    val longitude: String
)
data class BlueObject(
    val tajine : String = "Tajinoru"
)
data class ProfileDto(
    val identificationCode: String?,
    val nom: String,
    val prenom: String,
    val email: String
)
