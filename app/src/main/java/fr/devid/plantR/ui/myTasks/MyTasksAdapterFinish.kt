package fr.devid.plantR.ui.myTasks

import android.annotation.SuppressLint
import android.text.format.DateFormat
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
import kotlinx.android.synthetic.main.rv_plants_tasks.view.*
import java.util.*

class MyTasksAdapterFinish(private val actionToPage: (String, String, String, String, String, Int, String, String, String) -> Unit)  :
    ListAdapter<TaskCalendar, MyTasksAdapterFinish.TasksHolder>(UserDiffUtil()) {

    lateinit var guid : String
    lateinit var plantRef : DatabaseReference
    lateinit var handlePlant : ValueEventListener
    private lateinit var userRef : DatabaseReference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksHolder {

        val view = RvPlantsTasksBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TasksHolder(view).apply {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position == RecyclerView.NO_POSITION) return@setOnClickListener
                getItem(position)?.let { plant ->
                    plant.tasks.doneBy?.userId?.let { it1 ->
                        actionToPage(plant.plantId, plant.tasks.title,plant.tasks.description,plant.key,guid,plant.position,itemView.tv_nameUser.text.toString(),"${itemView.tv_date_realized.text}",
                            it1
                        )
                    }
                }
            }
        }
    }

    class TasksHolder(val binding: RvPlantsTasksBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: TasksHolder, position: Int) {
        val plant: TaskCalendar = getItem(position)
            plant.tasks.doneBy?.userId?.let { setUserObserver(it, holder) } //Je lance la récupération de l'userId
           //holder.binding.tvDescTasks.text = plant.tasks.description

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

            getPlantNameObserver(plant.plantId, holder)
            holder.binding.tvDateRealized.text = plant.tasks.doneBy?.date?.toLong()?.let {
                holder.binding.tvNameUser.visibility = View.VISIBLE
                holder.binding.tvDateRealized.visibility = View.VISIBLE
                getDate(it)
            }

            val storageRef = FirebaseService().getStoragePlantsReference().child(plant.plantId).child("plant.jpg")

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

    private fun setUserObserver(userId : String, holder: TasksHolder) {
        userRef = FirebaseService().getUserByidMetadata(userId)
        userRef.addValueEventListener(object :ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var metadata = snapshot.getValue(Metadata::class.java)
                if (metadata != null) {
                    holder.binding.tvNameUser.text = "${metadata.lastName.capitalize()} ${metadata.firstName.capitalize()}"
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })


    }

    private fun getPlantNameObserver(plantId : String, holder : TasksHolder) {
        plantRef = FirebaseService().getPlantsReferenceById(plantId)
        handlePlant = plantRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var plant = snapshot.getValue(Plant::class.java)
                if(plant != null) {
                    holder.binding.tvNamePlants.text = plant.name?.capitalize()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
    fun getDate(timestamp: Long): String {
        val calendar = Calendar.getInstance(Locale.FRENCH)
        calendar.timeInMillis = timestamp * 1000L
        val date = DateFormat.format("dd/MM/yyyy", calendar).toString()
        val words = date.split(" ")
        var newStr = ""
        words.forEach {
            newStr += it.capitalize() + " "
        }
        return newStr.trimEnd()
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