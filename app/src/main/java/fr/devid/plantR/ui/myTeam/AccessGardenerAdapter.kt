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
import fr.devid.plantR.databinding.RvManageTeamInWaitBinding
import fr.devid.plantR.models.Metadata
import fr.devid.plantR.models.SubscribeMember
import fr.devid.plantR.models.User
import fr.devid.plantR.services.FirebaseService
import java.lang.Exception
import java.util.*


class AccessGardenerAdapter() : ListAdapter<SubscribeMember, AccessGardenerAdapter.AccessGardenerHolder>(UserDiffUtil()) {

    class AccessGardenerHolder(val binding : RvManageTeamInWaitBinding) : RecyclerView.ViewHolder(binding.root)

    private lateinit var storageRef: StorageReference
    private var listsSubscribe: ArrayList<SubscribeMember> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccessGardenerHolder {

        val view = RvManageTeamInWaitBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        view.localLang = Locale.getDefault().language.toString()
        return AccessGardenerHolder(view)
    }

    override fun getItemCount(): Int {
        return super.getItemCount()
    }

    @SuppressLint("SetTextI18n")

    override fun onBindViewHolder(holder: AccessGardenerHolder, position: Int) {

        val member: SubscribeMember = getItem(position)
        println("Membre dans mon subscribe adapter : " + member.uid)
        holder.binding.tvNameSubscriber.text = "${member.metadata.lastName.capitalize()} ${member.metadata.firstName.capitalize()}"

            storageRef = FirebaseService().getStoragePictureByUserId(member.uid)
            storageRef.downloadUrl.addOnSuccessListener { data ->
                Glide.with(holder.binding.ivProfil.context)
                    .load(data.toString())
                    .circleCrop()
                    .into(holder.binding.ivProfil)
            }.addOnFailureListener {
                Glide.with(holder.binding.ivProfil.context)
                    .load(R.drawable.user)
                    .circleCrop()
                    .into(holder.binding.ivProfil)
            }



        holder.binding.btMenuValide.setOnClickListener {

            getItem(position)?.let {pos ->
                FirebaseService().getGardenerById(member.guid).child("addToOwnersSubscribe").child(member.uid).setValue(true)
                    FirebaseService().getGardenerById(member.guid).child("owners").child(member.uid).setValue(true)
                    FirebaseService().getGardenerById(member.guid).child("subscribemember").child(member.uid).removeValue()
                this@AccessGardenerAdapter.notifyItemRemoved(position)
            }
        }

        holder.binding.btMenuDelete.setOnClickListener {
            FirebaseService().getGardenerById(member.guid).child("subscribemember").child(member.uid).removeValue()
        }
    }

    internal class UserDiffUtil : DiffUtil.ItemCallback<SubscribeMember>() {

        override fun areItemsTheSame(
            oldItem: SubscribeMember,
            newItem: SubscribeMember
        ): Boolean {
            println("areItemsTheSame $oldItem est $newItem")
            return oldItem == newItem
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem:SubscribeMember,
            newItem:SubscribeMember
        ): Boolean {
            println("areContentsTheSame$oldItem est $newItem")
            return oldItem == newItem
        }
    }
}


