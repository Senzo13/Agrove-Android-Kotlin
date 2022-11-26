package fr.devid.plantR.ui.myPlants

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import fr.devid.plantR.R
import fr.devid.plantR.databinding.RvPlantsDiseaseBinding
import fr.devid.plantR.models.PlantTasks
import fr.devid.plantR.services.FirebaseService

class TasksAdapter(private val actionToPage: (String, String, String, String, String, Int, String) -> Unit)  :
    ListAdapter<PlantTasks, TasksAdapter.TasksHolder>(UserDiffUtil()) {

    lateinit var plantId : String
    lateinit var setPosition : String
    lateinit var guid : String

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksHolder {

        val view = RvPlantsDiseaseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TasksHolder(view)
    }

    class TasksHolder(val binding: RvPlantsDiseaseBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: TasksHolder, position: Int) {
        val plant: PlantTasks = getItem(position)
//
//        holder.binding.tvDisease.text = plant.tasks.title.toUpperCase()
//            //holder.binding.tvDescription.text = plant.tasks.description
//
//            val storageRef = FirebaseService().getStoragePlantsTasksReference(plantId, plant.tasks.title.toLowerCase())
//
//            storageRef.downloadUrl.addOnSuccessListener {
//                    Glide.with(holder.itemView.context)
//                        .load(it.toString())
//                        .circleCrop()
//                        .into(holder.binding.ivPlants)
//                }
//
//                .addOnFailureListener {
//                    Glide.with(holder.itemView.context)
//                        .load(holder.itemView.resources.getDrawable(R.drawable.tasksplant, null))
//                        .circleCrop()
//                        .into(holder.binding.ivPlants)
//                }
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

    internal class UserDiffUtil : DiffUtil.ItemCallback<PlantTasks>() {
        override fun areItemsTheSame(
            oldItem: PlantTasks,
            newItem: PlantTasks
        ): Boolean {
            println("areItemsTheSame $oldItem est $newItem")
            return oldItem == newItem
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: PlantTasks,
            newItem: PlantTasks
        ): Boolean {
            println("areContentsTheSame$oldItem est $newItem")
            return oldItem == newItem
        }
    }
}