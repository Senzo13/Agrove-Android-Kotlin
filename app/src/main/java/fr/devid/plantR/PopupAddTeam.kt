package fr.devid.plantR

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.storage.StorageReference
import fr.devid.plantR.R
import fr.devid.plantR.models.SortOfPlant
import fr.devid.plantR.services.FirebaseService
import fr.devid.plantR.services.StringRef
import kotlinx.android.synthetic.main.dialog_add_gardener.*
import kotlinx.android.synthetic.main.dialog_add_team.*
import kotlinx.android.synthetic.main.popup_add_plant.*

class PopupAddTeam(private val gardenerName : String, private val gardenerId : String, context: Context, private val callback: ((Dialog, Boolean, String) -> Unit)): Dialog(context) {
    private lateinit var userRef: DatabaseReference
    private lateinit var handleUserRef: ValueEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_add_team)
        setupView()
        val width = (context.resources.displayMetrics.widthPixels * 0.80).toInt()
        val defaultHeight = this.window?.attributes
        defaultHeight?.height?.toInt()?.let { this.window?.setLayout(width, it) }
    }


    private fun setupView() {


        val binding = this

        val user = FirebaseService().firebase.currentUser
        if (user != null) {
            println("/////////////////BRANCH IN")
            userRef = FirebaseService().getUserByIdAddToOwners(user.uid)
            handleUserRef = userRef.addValueEventListener(object : ValueEventListener {

                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {

                    val value = snapshot.getValue<String>()
                    when (value) {
                        "ok" -> {
                            println("/////////////////BRANCH OK")
                            userRef.removeValue()
                            val idGardener = gardenerId.substring(2)
                            println("Gardener ID test " + idGardener)
                            FirebaseService().getUserCurrentGardener(user.uid).setValue(idGardener)
                            binding.cl_loading.visibility = View.GONE
                            binding.dialog_content.visibility = View.VISIBLE
                        }
                        "ko" -> {
                            println("/////////////////BRANCH KO")
                            userRef.removeValue()
                        }
                        else -> {

                        }
                    }
                }
            })
            FirebaseService().getUserByIdAddToOwners(user.uid).setValue(gardenerId)

            this.tv_dialog_name_gardener.text = gardenerName

            this.setOnDismissListener {
                callback(this, false, "")
            }

            this.button_cancel_team.setOnClickListener {
                callback(this, false, "")
            }

        }
    }

    override fun onStop() {
        super.onStop()
        if(::userRef.isInitialized && ::handleUserRef.isInitialized) {
            userRef.removeEventListener(handleUserRef)
        }
    }
}