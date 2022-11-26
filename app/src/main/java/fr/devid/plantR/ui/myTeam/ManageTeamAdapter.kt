package fr.devid.plantR.ui.myTeam

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
import fr.devid.plantR.databinding.RvManageTeamBinding
import fr.devid.plantR.models.Metadata
import fr.devid.plantR.services.FirebaseService
import kotlinx.android.synthetic.main.rv_manage_team.view.*
import java.lang.Exception

class ManageTeamAdapter(var deleteOwner : (String, Int) -> Unit) : ListAdapter<String, ManageTeamAdapter.ManageTeamHolder>(UserDiffUtil()) {

    class ManageTeamHolder(val binding : RvManageTeamBinding) : RecyclerView.ViewHolder(binding.root)

    private lateinit var storageRef: StorageReference
    private lateinit var userRef : DatabaseReference
    private var name : String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManageTeamHolder {

        val view = RvManageTeamBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ManageTeamHolder(view).apply {
            itemView.iv_delete.setOnClickListener {
                getItem(position).let { uid ->
                        deleteOwner(uid, position)
                    }
                }
            }
        }


    override fun getItemCount(): Int {
        return super.getItemCount()
    }

    @SuppressLint("SetTextI18n")

    override fun onBindViewHolder(holder: ManageTeamHolder, position: Int) {

        val member: String = getItem(position)



        userRef = FirebaseService().getUserByidMetadata(member)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val usersMetadata = snapshot.getValue(Metadata::class.java)
                if (usersMetadata != null) {
                    holder.binding.tvMemberName.text = "${usersMetadata.lastName.capitalize()} ${usersMetadata.firstName.capitalize()}"
                    name =  "${usersMetadata.lastName.capitalize()} ${usersMetadata.firstName.capitalize()}"
                }
            }
        })
        try {
            setGardenersObserver(holder, member)
        } catch(e : Exception) {
            println("une erreur est arrivé")
        }
    }

    fun setGardenersObserver(holder: ManageTeamHolder, uid: String) {
        storageRef = FirebaseService().getStoragePictureByUserId(uid)
        storageRef.downloadUrl.addOnSuccessListener { data ->
            Glide.with(holder.binding.ivAvatarMember.context)
                .load(data.toString())
                .circleCrop()
                .into(holder.binding.ivAvatarMember)
        }.addOnFailureListener { excp ->
            println("ManageTeamAdapter ligne 83\nL'image profile.jpg n'existe pas, error : " + excp)
            Glide.with(holder.binding.ivAvatarMember.context)
                .load(R.drawable.user)
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


