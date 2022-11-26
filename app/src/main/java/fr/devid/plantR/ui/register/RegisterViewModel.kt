package fr.devid.plantR.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import fr.devid.plantR.R
import fr.devid.plantR.api.AppService
import fr.devid.plantR.api.RegisterDto
import fr.devid.plantR.models.NotificationContent
import fr.devid.plantR.models.NotificationData
import fr.devid.plantR.models.NotificationDto
import fr.devid.plantR.services.AuthenticationTokenInterceptor
import fr.devid.plantR.services.SharedPreferencesService
import fr.devid.plantR.viewmodels.Event
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class RegisterViewModel @Inject constructor(
    private val authenticationTokenInterceptor: AuthenticationTokenInterceptor,
    private val appService: AppService,
    var sharedPreferencesService: SharedPreferencesService

) : ViewModel() {

    enum class RegisterViewModelState {
        IDLE, LOADING, SUCCESS
    }

    var isProfessional: Boolean = false

    val state: LiveData<RegisterViewModelState>
        get() = _state
    val errorMessageEvent = MutableLiveData<Event<String>>()
    val errorMessageResourceEvent = MutableLiveData<Event<Int>>()

    private val _state = MutableLiveData(RegisterViewModelState.IDLE)

    fun sendNotification(topic : String, title : String, body : String, userId : String, data : NotificationData){
        val notification = NotificationDto("/topics/$topic", data)
        val key ="key=AAAAd_Geoag:APA91bEt_T6-Fg738UJRRLJOUiPd4AxToBuEuu4YDLXsa3RGiivLS5nyO7_ruDb08EJF2RWCHKKoFiAlia9yaJcTBuVoVUYOl1ouG4HAMj9t_AXOYpGXPTbWqxsj0vFm4JuS0fA80Gkt"
        viewModelScope.launch {
            val response = appService.sendNotification(key, notification)
            if(response.isSuccessful) {
                FirebaseMessaging.getInstance().subscribeToTopic(data.gardenerId)
            } else {
                FirebaseMessaging.getInstance().subscribeToTopic(data.gardenerId)
            }
            val body = response.body()
            Timber.d("Notification response = ${response.code()}")
            if (body == null) {
                return@launch
            }
        }
    }
    /*
    fun sendNotification(topic : String, title : String, body : String, userId : String, guid : String) {
        val notification = NotificationDto("/topics/$topic", NotificationContent(title, body), NotificationData(userId, title, body))
        val key ="key=AAAAd_Geoag:APA91bEt_T6-Fg738UJRRLJOUiPd4AxToBuEuu4YDLXsa3RGiivLS5nyO7_ruDb08EJF2RWCHKKoFiAlia9yaJcTBuVoVUYOl1ouG4HAMj9t_AXOYpGXPTbWqxsj0vFm4JuS0fA80Gkt"
        viewModelScope.launch {
            val response = appService.sendNotification(key, notification)
            if(response.isSuccessful) {
                FirebaseMessaging.getInstance().subscribeToTopic(guid)
            } else {
                FirebaseMessaging.getInstance().subscribeToTopic(guid)
            }
            val body = response.body()
            Timber.d("Notification response = ${response.code()}")
            if (body == null) {
                return@launch
            }
        }
    }
*/
    fun register(registerDto: RegisterDto, confirmationPassword: String) {
        if ( registerDto.nom.isBlank() || registerDto.prenom.isBlank() ||registerDto.email.isBlank() || registerDto.password.isBlank() ||
            ((registerDto.identificationCode.isNullOrBlank()))) {
            errorMessageResourceEvent.value = Event(R.string.fill_all_fields)
            return
        }
        if (registerDto.password != confirmationPassword) {
            errorMessageResourceEvent.value = Event(R.string.different_password)
            return
        }
        _state.value = RegisterViewModelState.LOADING
        viewModelScope.launch {
            val response = appService.register(registerDto)
            _state.value = RegisterViewModelState.IDLE
            val body = response?.body()
            Timber.d("Register response = ${response?.code()}")
            if (body == null) {
                errorMessageResourceEvent.value = Event(R.string.check_internet)
                return@launch
            }
            if (body.token != null) {
                authenticationTokenInterceptor.token = body.token
                _state.value = RegisterViewModelState.SUCCESS
                return@launch
            }

            errorMessageEvent.value = Event("Une erreur est survenue")
        }
    }
}