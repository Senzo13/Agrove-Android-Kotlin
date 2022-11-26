package fr.devid.plantR.services

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import fr.devid.plantR.models.Gardener
import fr.devid.plantR.models.GardenerData
import fr.devid.plantR.ui.myPlants.PlantModel
import javax.inject.Singleton

object StringRef {
    const val gardener = "gardeners"
    const val plantJpg = "plant.jpg"
}

@Singleton
class FirebaseService {

    var currentPlantModel : List<PlantModel> = emptyList()

    val firebase: FirebaseAuth
        get() {
            return FirebaseAuth.getInstance()
        }
    val storage: FirebaseStorage
        get() {
            return FirebaseStorage.getInstance()
        }

    lateinit var gardenerRef : DatabaseReference
    lateinit var handleGardener : ValueEventListener

    val gardeners = "gardeners"
    val gardenersGuest ="gardenersGuest"
    val owners = "owners"
    val metadata = "metadata"
    val users = "users"
    val picture = "profile.jpg"
    val stats = "stats"
    val friends = "friends"
    val rapport = "rapport"
    val plants = "plants"
    val associations = "associations"
    val images = "images"
    val tips = "tips"
    val tasks = "tasks"
    val favoris = "favoris"
    val filterTask = "filterTask"
    val categories = "categories"
    val addToOwners = "addToOwners"
    val ispublic = "ispublic"
    val irrigation = "irrigation"
    val etapes = "etapes"
    val outils = "outils"
    val nuisibles = "nuisibles"
    val auxiliaires = "auxiliaires"
    val auxiliaire = "auxiliaire"
    val listeauxiliaire = "listeauxiliaire"
    val subscribeMember = "subscribemember"

    val currentGardener = "currentGardener"

    // DATABASE

    fun getGardenerReference(): DatabaseReference {
        return Firebase.database.getReference(gardeners)
    }

    fun getGardenerById(id: String): DatabaseReference {
        return Firebase.database.getReference(gardeners).child(id)
    }

    fun getGardenerByIrrigation(id: String): DatabaseReference {
        return Firebase.database.getReference(gardeners).child(id).child("irrigation")
    }

    fun getGardenerOwnersById(id: String): DatabaseReference {
        return Firebase.database.getReference(gardeners).child(id).child(owners)
    }
    fun getGardenerPlantsById(id: String): DatabaseReference {
        return getGardenerById(id).child(plants)
    }
    fun getGardenerMetadataById(id: String) : DatabaseReference {
        return getGardenerById(id).child(metadata)
    }
    fun getIsPublic(id: String) : DatabaseReference {
        return getGardenerById(id)
    }
    fun getSubscribeMember(id: String) : DatabaseReference {
        return getGardenerById(id).child(subscribeMember)
    }
    fun getGardenerStatsById(id: String) : DatabaseReference {
        return getGardenerById(id).child(stats)
    }
    fun getGardenerFriendsById(id: String) : DatabaseReference {
        return getGardenerById(id).child(friends)
    }
    fun getGardenerImages(id: String): DatabaseReference {
        return getGardenerMetadataById(id).child(images)
    }
    fun getGardenerTips(id: String): DatabaseReference {
        return getGardenerById(id).child(tips)
    }

    // REPORT
    fun getRapportByUserId(id: String) : DatabaseReference {
        return Firebase.database.getReference(rapport).child(id)
    }
    fun getRapport() : DatabaseReference {
        return Firebase.database.getReference(rapport)
    }
    // USERS
    fun getUserByid(id: String) : DatabaseReference {
        return Firebase.database.getReference(users).child(id)
    }
    fun getUserByidMetadata(id: String) : DatabaseReference {
        return Firebase.database.getReference(users).child(id).child(metadata)
    }
    fun getGardenersGuestUserByid(id: String) : DatabaseReference {
        return Firebase.database.getReference(users).child(id).child(gardenersGuest)
    }
    fun getUserByIdAddToOwners(id: String) : DatabaseReference {
        return Firebase.database.getReference(users).child(id).child(metadata).child(addToOwners)
    }
    fun getUserCurrentGardener(id: String) : DatabaseReference {
        return Firebase.database.getReference(users).child(id).child("currentGardener")
    }

    // PLANTS
    fun getFavs(id: String) : DatabaseReference {
        return Firebase.database.getReference(gardeners).child(id).child(favoris)
    }
    fun getPlantsReference(): DatabaseReference {
        return Firebase.database.getReference(plants)
    }

    fun getPlantsReferenceById(id : String) : DatabaseReference {
        return Firebase.database.getReference(plants).child(id)
    }

    fun getNuisiblesReference() : DatabaseReference {
        return Firebase.database.getReference(nuisibles)
    }

    fun getNuisiblesReferenceId(id : String) : DatabaseReference {
        return Firebase.database.getReference(nuisibles).child(id)
    }

    fun getAuxiliaireFromNuisible(id : String) : DatabaseReference {
        return Firebase.database.getReference(nuisibles).child(id)
    }

    fun getAuxiliaire(id : String) : DatabaseReference {
        return Firebase.database.getReference(auxiliaire).child(id)
    }

    fun getFilterTask() : DatabaseReference {
        return Firebase.database.getReference(filterTask)
    }

    fun getAssociationById(id : String) : DatabaseReference {
        return Firebase.database.getReference(associations).child(id)
    }

    // CATEGORIE

    fun getCategorie() : DatabaseReference {
        return Firebase.database.getReference(categories)
    }

    // STORAGE

    fun getStorage(): StorageReference {
        return  FirebaseService().storage.reference
    }

    fun getStoragePictureByUserId(id: String): StorageReference {
        val path = getStorage().child(users).child(id).child(picture)
        println("**************")
        println(path.path)
        return path
    }
    fun getSteps(id: String) : StorageReference {
        return getStorage().child(plants).child(id).child(etapes)
    }
    fun getTools(id: String) : StorageReference {
        return getStorage().child(outils).child(id)
    }
    fun getStorageGardener(id: String) : StorageReference {
        return getStorage().child(gardeners).child(id)
    }
    fun getStorageGardenerPicture(id: String, picture : String) : StorageReference {
        return getStorage().child(gardeners).child(id).child(picture)
    }
    fun getStoragePictureByFriendId(id: String) : StorageReference {
        return getStorage().child(gardeners).child(id)
    }
    fun getStoragePlantsReference(): StorageReference {
        return getStorage().child(plants)
    }
    fun getStorageNuisiblesId(id : String): StorageReference {
        return getStorage().child(nuisibles).child(id+".jpg")
    }

    fun getStorageAuxiliairesId(id : String): StorageReference {
        return getStorage().child(auxiliaires).child(id+".jpg")
    }
    fun getStoragePlantsTasksReference(plantId : String, taskName: String): StorageReference {
        return getStorage().child(plants).child(plantId).child(tasks).child(taskName+".jpg")
    }

    fun getGardener(guid : String, callback: (gardener: GardenerData) -> Unit) {
        gardenerRef = this.getGardenerById(guid)
        handleGardener = gardenerRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                println("Je rentre avant le callback")
                p0.getValue(GardenerData::class.java)?.let { callback.invoke(it)}
            }
            override fun onCancelled(p0: DatabaseError) {
                println(p0)
            }
        })
    }

    fun removeCallBack() {
        if(::gardenerRef.isInitialized && ::handleGardener.isInitialized)
            gardenerRef.removeEventListener(handleGardener)
    }
}