package fr.devid.plantR.services

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FirebasePath {
    val ref = Firebase.database.reference
    fun path(chemin: String): DatabaseReference {
        return Firebase.database.getReference(chemin)
    }
    fun child(chemin : String): DatabaseReference {
        return Firebase.database.reference.child(chemin)
    }
    /*
    fun getUser(uuid : String): User {

    }
    */
}