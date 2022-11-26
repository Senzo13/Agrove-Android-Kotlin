package fr.devid.plantR.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import fr.devid.plantR.api.AppService
import fr.devid.plantR.api.BlueObject
import fr.devid.plantR.api.ProfileDto
import fr.devid.plantR.models.Plant
import fr.devid.plantR.services.CurrentPlantPageService
import fr.devid.plantR.services.FirebaseService
import fr.devid.plantR.services.UserService
import fr.devid.plantR.viewmodels.Event
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val appService: AppService,
    val userService : UserService,
    val firebaseService : FirebaseService,
    val currentPlantPageService : CurrentPlantPageService

) : ViewModel() {

    data class Profile(
        var firstName : String = "",
        var lastName : String = "",
        var email : String = ""
    )

    val profile: LiveData<Event<Profile>>
        get() = _profile

    val bleObject: LiveData<BlueObject>
        get() = _bleobject

    val firstName: LiveData<Event<String>>
        get() = _firstName

    val lastName: LiveData<Event<String>>
        get() = _lastName

    val gardenerList: LiveData<Event<HashMap<String, Boolean>>>
        get() = _gardenerList

    val _profile = MutableLiveData<Event<Profile>>()

    private val _bleobject = MutableLiveData<BlueObject>()
    var _gardenerList = MutableLiveData<Event<HashMap<String, Boolean>>>()

    var _firstName = MutableLiveData<Event<String>>()
    var _lastName = MutableLiveData<Event<String>>()

//    fun getAllPlants() : Plant? {
//        var listPlants : Plant? = null
//        v = this.firebaseService.getPlantsReference().addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                println("*************** Récupération de la liste des plantes du homeViewModel **************")
//                listPlants = snapshot.getValue(Plant::class.java)
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//        })
//        return listPlants
//    }

    fun getPlantById() {

    }

    fun refreshProfile(profile : Event<Profile>) {
        Timber.d("Refreshing profile...")
                _profile.postValue(profile)
    }

    override fun onCleared() {
        super.onCleared()
        Timber.d("ProfileViewModel onCleared")
    }
}