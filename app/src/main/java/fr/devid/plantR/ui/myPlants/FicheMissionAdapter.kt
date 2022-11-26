package fr.devid.plantR.ui.myPlants

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import fr.devid.plantR.R
import fr.devid.plantR.databinding.RvFicheMissionsBinding
import fr.devid.plantR.services.FirebaseService

class FicheMissionAdapter() : ListAdapter<StepsList, FicheMissionAdapter.FicheHolder>(UserDiffUtil()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):  FicheHolder {

        val view = RvFicheMissionsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return  FicheHolder(view)
    }

    class FicheHolder(val binding: RvFicheMissionsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder:  FicheHolder, position: Int) {
        val fiche: StepsList = getItem(position)


        println("JE SUIS DANS ADAPTER")
            holder.binding.tvTitleFicheMissions.text = fiche.title.capitalize()
        println("le titre de l'adapter est : " +  fiche.title)
            holder.binding.tvFicheDescription.text = fiche.description.capitalize()


        if(fiche.images != null) {
            println("Nom de mon image : ${fiche.images}.jpg")
            holder.binding.ivFicheMissions.visibility = View.VISIBLE
            val storageRef = FirebaseService().getSteps(fiche.id).child("${fiche.images}.jpg")
            storageRef.downloadUrl.addOnSuccessListener {
                Glide.with(holder.itemView.context)
                    .load(it.toString())
                    .into(holder.binding.ivFicheMissions)
            }
            storageRef.downloadUrl.addOnFailureListener {
                holder.binding.ivFicheMissions.visibility = View.GONE
                }
        } else {
            holder.binding.ivFicheMissions.visibility = View.GONE
        }

    }

    internal class UserDiffUtil : DiffUtil.ItemCallback<StepsList>() {
        override fun areItemsTheSame(
            oldItem: StepsList,
            newItem: StepsList
        ): Boolean {
            println("areItemsTheSame $oldItem est $newItem")
            return oldItem == newItem
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: StepsList,
            newItem: StepsList
        ): Boolean {
            println("areContentsTheSame$oldItem est $newItem")
            return oldItem == newItem
        }
    }
}