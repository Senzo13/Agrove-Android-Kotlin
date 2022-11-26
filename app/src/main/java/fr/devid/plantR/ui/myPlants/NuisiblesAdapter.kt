package fr.devid.plantR.ui.myPlants

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import fr.devid.plantR.R
import fr.devid.plantR.databinding.RvNuisiblesBinding
import fr.devid.plantR.models.Nuisible
import fr.devid.plantR.models.NuisibleWithKey
import fr.devid.plantR.services.FirebaseService
import kotlinx.android.synthetic.main.rv_nuisibles.view.*
import kotlinx.android.synthetic.main.rv_plants_filter.view.*

class NuisiblesAdapter(private val actionToPage : (String, String, String, String) -> Unit) : ListAdapter<NuisiblesModels, NuisiblesAdapter.TasksHolder>(UserDiffUtil()) {

    lateinit var plantId : String
    lateinit var guid : String

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksHolder {

        val view = RvNuisiblesBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return TasksHolder(view).apply {
            itemView.listeNuisibles.setOnClickListener {
                val position = adapterPosition
                if (position == RecyclerView.NO_POSITION) return@setOnClickListener
                getItem(position)?.let { cable ->
                    if (cable != null) {
                        actionToPage(
                            cable.key,
                            cable.nuisible?.name!!,
                            cable.nuisible?.image!!,
                            cable.nuisible?.description!!
                        )


                       }
                    }
                }
            }
    }



    class TasksHolder(val binding: RvNuisiblesBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: TasksHolder, position: Int) {

        val nuisibles : NuisiblesModels = getItem(position)

        println("Nuisibles test : " + nuisibles)
        holder.binding.tvNameNuisibles.text = nuisibles.nuisible?.name

        println("Image : " + nuisibles.nuisible?.image)
            val storageRef = nuisibles.nuisible?.image?.let {
                FirebaseService().getStorageNuisiblesId(
                    it
                )
            }

        storageRef?.downloadUrl?.addOnSuccessListener {
            Glide.with(holder.itemView.context)
                .load(it.toString())
                .circleCrop()
                .into(holder.binding.ivNuisibles)
        }?.addOnFailureListener {
            Glide.with(holder.itemView.context)
                .load(holder.itemView.resources.getDrawable(R.drawable.tasksplant, null))
                .circleCrop()
                .into(holder.binding.ivNuisibles)
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

    internal class UserDiffUtil : DiffUtil.ItemCallback<NuisiblesModels>() {
        override fun areItemsTheSame(
            oldItem: NuisiblesModels,
            newItem: NuisiblesModels
        ): Boolean {
            println("areItemsTheSame $oldItem est $newItem")
            return oldItem.key == newItem.key
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: NuisiblesModels,
            newItem: NuisiblesModels
        ): Boolean {
            println("areContentsTheSame$oldItem est $newItem")
            return oldItem == newItem
        }
    }
}