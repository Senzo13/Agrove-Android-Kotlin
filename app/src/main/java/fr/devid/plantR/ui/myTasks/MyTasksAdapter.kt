package fr.devid.plantR.ui.myTasks

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import fr.devid.plantR.R
import fr.devid.plantR.databinding.RvPlantsTasksBinding
import fr.devid.plantR.models.*
import fr.devid.plantR.services.FirebaseService
import java.util.*

class MyTasksAdapter(private val actionToPage: (String, String, String, String, String, Int, String) -> Unit) :
    ListAdapter<TaskCalendar, MyTasksAdapter.TasksHolder>(UserDiffUtil()) {
    lateinit var plantRef : DatabaseReference
    lateinit var handlePlant : ValueEventListener
    lateinit var guid: String
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksHolder {

        val view = RvPlantsTasksBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TasksHolder(view).apply {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position == RecyclerView.NO_POSITION) return@setOnClickListener
                getItem(position)?.let { plant ->
                    plant.tasks.taskId?.let { taskId ->
                        actionToPage(
                            plant.plantId,
                            plant.tasks.title,
                            plant.tasks.description,
                            plant.key,
                            guid,
                            plant.position,
                            taskId
                        )
                    }
                }
            }
        }
    }

    class TasksHolder(val binding: RvPlantsTasksBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: TasksHolder, position: Int) {
        val plant: TaskCalendar = getItem(position)

        holder.binding.tvDateRealized.visibility = View.GONE
        holder.binding.tvNameUser.visibility = View.GONE
        println("A SAVOIR FRERE: " + plant.tasks.description)

        when(Locale.getDefault().language.toString()) {
                "fr" -> {
                    holder.binding.tvNameTasks.text = plant.tasks.title.toUpperCase()
                }
                "en" -> {
                    when(plant.tasks.title.toLowerCase()) {
                    "semer" -> {
                        holder.binding.tvNameTasks.text = "Sow"
                    }
                    "planter" -> {
                        holder.binding.tvNameTasks.text = "Plant"
                    }
                    else -> {
                        holder.binding.tvNameTasks.text = plant.tasks.title.toUpperCase()
                    }
                }
            }
            else -> {
                when(plant.tasks.title.toLowerCase()) {
                    "semer" -> {
                        holder.binding.tvNameTasks.text = "Sow"

                    }
                    "planter" -> {
                        holder.binding.tvNameTasks.text = "Plant"
                    }
                    else -> {
                        holder.binding.tvNameTasks.text = plant.tasks.title.toUpperCase()
                    }
                }
            }
        }

        if(plant.tasks.priority == 1) {
            holder.binding.ivPriority.visibility = View.VISIBLE
        } else if(plant.tasks.title.toLowerCase() == "récolter") {
            holder.binding.ivPriority.setImageDrawable(holder.binding.ivPriority.context.resources.getDrawable(R.drawable.recolte, null))
            holder.binding.ivPriority.visibility = View.VISIBLE
        } else {
            holder.binding.ivPriority.visibility = View.GONE
        }

        getPlantNameObserver(plant.plantId, holder)
        val storageRef =
            FirebaseService().getStoragePlantsReference().child(plant.plantId).child("plant.jpg")

        println(storageRef.path)
        storageRef.downloadUrl
            .addOnSuccessListener {
                Glide.with(holder.itemView.context)
                    .load(it.toString())
                    .circleCrop()
                    .into(holder.binding.ivPlants)
            }

            .addOnFailureListener {
                Glide.with(holder.itemView.context)
                    .load(holder.itemView.resources.getDrawable(R.drawable.tasksplant, null))
                    .circleCrop()
                    .into(holder.binding.ivPlants)
            }

    }

    private fun getPlantNameObserver(plantId : String, holder : TasksHolder) {
        plantRef = FirebaseService().getPlantsReferenceById(plantId)
        handlePlant = plantRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val plant = snapshot.getValue(Plant::class.java)
                if(plant != null) {
                    holder.binding.tvNamePlants.text = plant.name?.capitalize()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    internal class UserDiffUtil : DiffUtil.ItemCallback<TaskCalendar>() {

        override fun areItemsTheSame(
            oldItem: TaskCalendar,
            newItem: TaskCalendar
        ): Boolean {
            println("areItemsTheSame $oldItem est $newItem")
            return oldItem == newItem
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: TaskCalendar,
            newItem: TaskCalendar
        ): Boolean {
            println("areContentsTheSame$oldItem est $newItem")
            return oldItem == newItem
        }
    }
}