package fr.devid.plantR.ui.subscribe

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference
import fr.devid.plantR.R
import fr.devid.plantR.databinding.RvGardenersGuestBinding
import fr.devid.plantR.models.GardenerMetadata
import fr.devid.plantR.models.GardenerStage
import fr.devid.plantR.services.FirebaseService

class SubscribeAdapter(val actionToPage : (String, String, String, Int, String) -> Unit,val callBackDelete : (String, String, Int) -> Unit) : ListAdapter<String, SubscribeAdapter.SubscribeHolder>(UserDiffUtil()) {

    class SubscribeHolder(val binding: RvGardenersGuestBinding) : RecyclerView.ViewHolder(binding.root)

    private lateinit var storageRef: StorageReference
    private lateinit var userRef: DatabaseReference
    private lateinit var gardenerRef: DatabaseReference
    private lateinit var handleGardenerRef: ValueEventListener
    private lateinit var handleUserRef: ValueEventListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubscribeHolder {

        val view = RvGardenersGuestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SubscribeHolder(view)
    }

    override fun getItemCount(): Int {
        return super.getItemCount()
    }

    @SuppressLint("SetTextI18n")

    override fun onBindViewHolder(holder: SubscribeHolder, position: Int) {

        val gardenersId: String = getItem(position)


        println("NB DE ITEM ICI " + this.itemCount)
        userRef = FirebaseService().getGardenerMetadataById(gardenersId)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val gardenersMetadata = snapshot.getValue(GardenerMetadata::class.java)
                if (gardenersMetadata != null) {
                    holder.binding.tvMemberName.text = "${gardenersMetadata.name}"

                    var listsGardeners = gardenersMetadata.images
                    if (!listsGardeners.isEmpty()) { // SI PAS NULL
                        if (listsGardeners.containsKey("logo")) { // SI CONTAINS LOGO
                            setGardenersObserver(holder, gardenersId, "logo")
                        } else {
                            setGardenersObserver(holder, gardenersId, listsGardeners.keys.first().toString())
                        }
                    } else { //SI RIEN
                        setGardenersObserver(holder, gardenersId, "null")
                    }
                } else {
                    notifyDataSetChanged()
                    holder.binding.tvMemberName.text = "Jardinière sans nom"
                    setGardenersObserver(holder, null, "null")
                }
            }
        })

        holder.binding.clTeamClick.setOnClickListener {

            if(!holder.binding.tvMemberName.text.isNullOrEmpty()) {
                setData(gardenersId , holder.binding.tvMemberName.text.toString())
            } else {
                setData(gardenersId, "Plantes de la team")
            }
        }


        holder.binding.ivDelete.setOnClickListener {
            getItem(position)?.let {gardenerId ->
                FirebaseService().firebase.currentUser?.uid?.let { uid ->
                    callBackDelete(uid,gardenerId, position)
                }
            }
        }
    }

    fun setData(gardenersId : String, name : String) {
        gardenerRef = FirebaseService().getGardenerById(gardenersId)
        handleGardenerRef = gardenerRef.addValueEventListener(object :
            ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                var gardenerDataStage = snapshot.getValue(GardenerStage::class.java)
                if (gardenerDataStage != null) {
                    var type = gardenerDataStage.type
                    var rangs = gardenerDataStage.rangs
                    var stage = gardenerDataStage.stage
                    if(rangs != null) {
                        actionToPage(gardenersId,name, type, rangs, stage)
                    }
                }
            }
        })
    }
    fun setGardenersObserver(holder: SubscribeHolder, guid: String?, images : String) {

        if(guid != null) {
            if(images != "null") {
                storageRef = FirebaseService().getStorageGardenerPicture(guid, images+".jpg")
                storageRef.downloadUrl.addOnSuccessListener { data ->
                    Glide.with(holder.binding.ivAvatarMember.context)
                        .load(data.toString())
                        .circleCrop()
                        .into(holder.binding.ivAvatarMember)
                }
                storageRef.downloadUrl.addOnFailureListener {
                    Glide.with(holder.binding.ivAvatarMember.context)
                        .load(R.drawable.gardenerbackground)
                        .circleCrop()
                        .into(holder.binding.ivAvatarMember)
                }
            } else {
                Glide.with(holder.binding.ivAvatarMember.context)
                    .load(R.mipmap.ic_launcher)
                    .circleCrop()
                    .into(holder.binding.ivAvatarMember)
            }
        } else {
            Glide.with(holder.binding.ivAvatarMember.context)
                .load(R.mipmap.ic_launcher)
                .circleCrop()
                .into(holder.binding.ivAvatarMember)
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


