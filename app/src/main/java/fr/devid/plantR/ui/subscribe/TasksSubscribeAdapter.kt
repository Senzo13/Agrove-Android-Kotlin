package fr.devid.plantR.ui.subscribe

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import fr.devid.plantR.R
import fr.devid.plantR.databinding.RvPlantsDiseaseBinding
import fr.devid.plantR.models.Tasks
import fr.devid.plantR.services.FirebaseService

class TasksSubscribeAdapter(private val actionToPage: (String, String, String, String, String, Int) -> Unit)  :
    ListAdapter<Tasks, TasksSubscribeAdapter.TasksHolder>(UserDiffUtil()) {

    lateinit var plantId : String
    lateinit var setPosition : String
    lateinit var guid : String
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksHolder {

        val view = RvPlantsDiseaseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TasksHolder(view)
    }

    class TasksHolder(val binding: RvPlantsDiseaseBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: TasksHolder, position: Int) {
        val plant: Tasks = getItem(position)

//
//            holder.binding.tvDisease.text = plant.title.toUpperCase()
//           // holder.binding.tvDescription.text = plant.description
//            val storageRef = FirebaseService().getStoragePlantsTasksReference(plantId, plant.title.toLowerCase())
//
//            println(storageRef.path)
//            storageRef.downloadUrl
//                .addOnSuccessListener {
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

    }


    internal class UserDiffUtil : DiffUtil.ItemCallback<Tasks>() {

        override fun areItemsTheSame(
            oldItem: Tasks,
            newItem: Tasks
        ): Boolean {
            println("areItemsTheSame $oldItem est $newItem")
            return oldItem == newItem
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: Tasks,
            newItem: Tasks
        ): Boolean {
            println("areContentsTheSame$oldItem est $newItem")
            return oldItem == newItem
        }
    }
}