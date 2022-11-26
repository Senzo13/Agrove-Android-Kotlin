package fr.devid.plantR.ui.myPlants

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import fr.devid.plantR.R
import fr.devid.plantR.databinding.RvAuxiliairesBinding
import fr.devid.plantR.databinding.RvToolsBinding
import fr.devid.plantR.models.Auxiliaire
import fr.devid.plantR.services.FirebaseService

class AuxiliaireAdapter() : ListAdapter<Auxiliairage, AuxiliaireAdapter.ToolsHolder>(UserDiffUtil()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToolsHolder {

        val view = RvAuxiliairesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ToolsHolder(view)
    }

    class ToolsHolder(val binding: RvAuxiliairesBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: ToolsHolder, position: Int) {
        val auxiliaire: Auxiliairage = getItem(position)
        holder.binding.tvNameAuxiliaire.text = auxiliaire.auxiliaire?.name

        val storageRef = auxiliaire.auxiliaire?.image?.let {
            FirebaseService().getStorageAuxiliairesId(it)
        }

        storageRef?.downloadUrl?.addOnSuccessListener {
            Glide.with(holder.itemView.context)
                .load(it.toString())
                .circleCrop()
                .into(holder.binding.ivAuxiliaire)
        }?.addOnFailureListener {
            Glide.with(holder.itemView.context)
                .load(holder.itemView.resources.getDrawable(R.drawable.tasksplant, null))
                .circleCrop()
                .into(holder.binding.ivAuxiliaire)
        }
    }

    internal class UserDiffUtil : DiffUtil.ItemCallback<Auxiliairage>() {
        override fun areItemsTheSame(
            oldItem: Auxiliairage,
            newItem: Auxiliairage
        ): Boolean {
            println("areItemsTheSame $oldItem est $newItem")
            return oldItem == newItem
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: Auxiliairage,
            newItem: Auxiliairage
        ): Boolean {
            println("areContentsTheSame$oldItem est $newItem")
            return oldItem == newItem
        }
    }
}