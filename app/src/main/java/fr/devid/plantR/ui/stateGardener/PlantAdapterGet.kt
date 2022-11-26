package fr.devid.plantR.ui.stateGardener

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Base64
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import fr.devid.plantR.R
import fr.devid.plantR.databinding.RvMyplantRBinding
import fr.devid.plantR.models.GardenerMetadata
import fr.devid.plantR.models.User
import fr.devid.plantR.services.AlerterService
import fr.devid.plantR.services.FirebaseService
import fr.devid.plantR.services.PicturesUtils
import kotlinx.android.synthetic.main.rv_myplant_r.view.*

class PlantAdapterGet(private val removeCallBack: (String) -> Unit) :
    ListAdapter<String, PlantAdapterGet.PlantHolder>(UserDiffUtil()) {
    private lateinit var userRef: DatabaseReference
    private lateinit var gardenersImagesRef: DatabaseReference
    private lateinit var handleGardenerImagesData: ValueEventListener

    private lateinit var handleUser: ValueEventListener
    private lateinit var storageRef: StorageReference
    var imageSend : Boolean? = null
    var act : Context? = null
    var uri : Uri? = null
    var gardenerId : String? = null
    lateinit var holders : PlantHolder

    class PlantHolder(val binding : RvMyplantRBinding) : RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantHolder {

        val view = RvMyplantRBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlantHolder(view).apply {

            itemView.iv_btn_delete.setOnClickListener {
                val position = adapterPosition
                if (position == RecyclerView.NO_POSITION) return@setOnClickListener
                println("Supprimé")
                removeCallBack(getItem(position))
                notifyDataSetChanged()
            }
            itemView.listePlants.setOnClickListener {
                val position = adapterPosition
                if (position == RecyclerView.NO_POSITION) return@setOnClickListener

            }
        }
    }

    override fun onBindViewHolder(holder: PlantHolder, position: Int) {
        val plant: String = getItem(position)

        if (position >= this.currentList.size - 1) {
            holder.binding.viewSeparate.visibility = View.GONE
        }

            println("L'image est bien inséré")
            Glide.with(holder.binding.rvImagesGardeners.context)
                .load(plant)
                .into(holder.binding.rvImagesGardeners)

        println("TEST OUAIS")
        gardenersImagesRef = Firebase.database.getReference("gardeners").child(gardenerId!!).child("metadata")
        handleGardenerImagesData = gardenersImagesRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val gardenerMetadata = dataSnapshot.getValue(GardenerMetadata::class.java)

                gardenerMetadata?.images.let {



                }
                println("J'envoie ça : " + gardenerMetadata?.images)
            }


            override fun onCancelled(databaseError: DatabaseError) {

            }
        })


        println("JE RECOIS SA : " + plant)
            setGardenersObserver(holder, plant , gardenerId!!)

}





    fun addImage(imageSend : Boolean, uri : Uri, act : Context) {
        if(imageSend == true) {
            println("Je passe la mec")
            if(::holders.isInitialized) {
                println("Initialize mec")
                val base64img = PicturesUtils.getBase64FromUri(uri!!, act!!)
                Glide.with(holders.binding.rvImagesGardeners.context)
                    .load(Base64.decode(base64img, Base64.DEFAULT))
                    .into(holders.binding.rvImagesGardeners)
            }

        }
    }

    fun setGardenersObserver(holder: PlantHolder, plant: String, gardenerId: String) {

            println("gardenerid : " + gardenerId)

            storageRef = FirebaseService().getStorageGardener(gardenerId)
                .child(plant + ".jpg")
            storageRef.downloadUrl.addOnSuccessListener {
                println("L'image est bien inséré")
                Glide.with(holder.binding.rvImagesGardeners.context)
                    .load(it.toString())
                    .into(holder.binding.rvImagesGardeners)
            }.addOnFailureListener {

            }

    }



    internal class UserDiffUtil : DiffUtil.ItemCallback<String>() {

        override fun areItemsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            println("areItemsTheSame $oldItem est $newItem")
            return oldItem == newItem
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            println("areContentsTheSame$oldItem est $newItem")
            return oldItem == newItem
        }
    }
}