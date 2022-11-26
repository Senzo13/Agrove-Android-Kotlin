package fr.devid.plantR.ui.myPlants

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import fr.devid.plantR.R
import fr.devid.plantR.databinding.RvFicheSoinsBinding
import fr.devid.plantR.databinding.RvNuisiblesBinding
import fr.devid.plantR.models.Nuisible
import fr.devid.plantR.models.NuisibleWithKey
import fr.devid.plantR.models.Soins
import fr.devid.plantR.services.FirebaseService
import kotlinx.android.synthetic.main.rv_nuisibles.view.*
import kotlinx.android.synthetic.main.rv_plants_filter.view.*

class SoinsAdapter() : ListAdapter<StepsListTraitement, SoinsAdapter.TasksHolder>(UserDiffUtil()) {

    lateinit var plantId : String
    lateinit var guid : String

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksHolder {

        val view = RvFicheSoinsBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return TasksHolder(view)

    }



    class TasksHolder(val binding: RvFicheSoinsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: TasksHolder, position: Int) {

        val soins : StepsListTraitement = getItem(position)

        holder.binding.tvTitleFicheSoins.text = soins.name
        holder.binding.tvFicheDescription.text = soins.description

        val storageRef = soins.images.let {
            FirebaseService().getStorageNuisiblesId(
                it?:""
            )
        }

        storageRef?.downloadUrl?.addOnSuccessListener {
            Glide.with(holder.itemView.context)
                .load(it.toString())
                .into(holder.binding.ivTraitement)
        }?.addOnFailureListener {
            Glide.with(holder.itemView.context)
                .load(holder.itemView.resources.getDrawable(R.drawable.tasksplant, null))
                .into(holder.binding.ivTraitement)
        }




//
//
//            println("le plant id est " + plantId)
//
//            holder.binding.listePlants.setOnClickListener {
//                if(!plant.tasks.done) {
//                    println("On va a la page")
//                    plant.tasks.taskId?.let { it1 -> actionToPage(plantId, plant.tasks.title, plant.tasks.description, setPosition.let {it}, guid.let { it }, plant.index.toInt(), it1) }
//                }
//            }
    }

    internal class UserDiffUtil : DiffUtil.ItemCallback<StepsListTraitement>() {
        override fun areItemsTheSame(
            oldItem: StepsListTraitement,
            newItem: StepsListTraitement
        ): Boolean {
            println("areItemsTheSame $oldItem est $newItem")
            return oldItem == newItem
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: StepsListTraitement,
            newItem: StepsListTraitement
        ): Boolean {
            println("areContentsTheSame$oldItem est $newItem")
            return oldItem == newItem
        }
    }
}