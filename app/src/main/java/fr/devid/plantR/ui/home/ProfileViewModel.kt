package fr.devid.plantR.ui.home

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.observe
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import fr.devid.plantR.api.AppService
import fr.devid.plantR.api.BlueObject
import fr.devid.plantR.models.Plant
import fr.devid.plantR.services.CurrentPlantPageService
import fr.devid.plantR.services.FirebaseService
import fr.devid.plantR.services.UserService
import fr.devid.plantR.viewmodels.Event
import fr.devid.plantR.viewmodels.EventObserver
import timber.log.Timber
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    private val appService: AppService,
    val userService : UserService,
    val currentPlantPageService : CurrentPlantPageService

) : ViewModel() {

    companion object {
        private const val PAGE = "ActivityViewModel"
    }
    private val firebaseService = FirebaseService()
    data class Profile(
        var firstName : String = "",
        var lastName : String = "",
        var email : String = ""
    )

    data class PlantModel(
        var key : String = "",
        var plant : Plant? = null
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

    val plants: LiveData<Event<ArrayList<PlantModel>>>
        get() = _plants

    var _plants = MutableLiveData<Event<ArrayList<PlantModel>>>()

    val _profile = MutableLiveData<Event<Profile>>()

    private val _bleobject = MutableLiveData<BlueObject>()

    var _gardenerList = MutableLiveData<Event<HashMap<String, Boolean>>>()

    var _firstName = MutableLiveData<Event<String>>()

    var _lastName = MutableLiveData<Event<String>>()


    fun getAllPlants()  {
        firebaseService.getPlantsReference().addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                println("*************** $PAGE **************")
                println("*************** Changement survenue dans la liste des plantes **************")
                snapshot.getValue<HashMap<String,Plant>>()?.let { e ->
                   val parseList : ArrayList<PlantModel> = arrayListOf()
                    e.map { element ->
                        parseList.add(PlantModel(element.key, element.value))
                   }
                    _plants.postValue(Event(parseList))
                    println("Mes plantes : $parseList")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("$PAGE $error")
            }
        })
    }

    fun getPlantByKey(lifecycleOwner: LifecycleOwner, key : String) : Plant? {
        println("*************** $PAGE **************")
        println("*************** Récupération d'une plante par sa key => $key **************")
        var plantValue : Plant? = null
        plants.observe(lifecycleOwner, EventObserver { e ->
           try {
               plantValue = e.first { element -> element.key.contains(key) }.plant
           } catch (e : NoSuchElementException) {
                println("************* La key choisis ne match pas avec celle du model *********")
           }
        })
        println("Data : " + plantValue)
        return plantValue
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