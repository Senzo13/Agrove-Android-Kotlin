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
import androidx.appcompat.view.menu.MenuView
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

class PlantAdapter(private val removeCallBack: (String) -> Unit) :
    ListAdapter<String, PlantAdapter.PlantHolder>(UserDiffUtil()) {
    private lateinit var userRef: DatabaseReference
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

        FirebaseService().firebase.currentUser?.uid?.let { uid ->
            userRef = FirebaseService().getUserByid(uid)
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user = dataSnapshot.getValue(User::class.java)
                    user?.currentGardener?.let { gardenerId ->
                        if (gardenerId.isNotEmpty()) {
                            setGardenersObserver(holder, plant, gardenerId)
                        }
                    }
                }
            })
        }
    }



    fun setGardenersObserver(holder: PlantHolder, plant: String, gardenerId: String) {

        println("gardenerid : " + gardenerId)

        storageRef = FirebaseService().getStorageGardener(gardenerId).child(plant + ".jpg")
        storageRef.downloadUrl.addOnSuccessListener {
            Glide.with(holder.binding.rvImagesGardeners.context)
                    .load(it.toString())
                    .into(holder.binding.rvImagesGardeners)
            holder.binding.rvImagesGardeners.visibility = View.VISIBLE
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