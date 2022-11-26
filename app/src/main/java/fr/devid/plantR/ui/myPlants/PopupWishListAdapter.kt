package fr.devid.plantR.ui.myPlants

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView
import fr.devid.plantR.Constants
import fr.devid.plantR.R
import fr.devid.plantR.databinding.RvPlantsWishlistBinding
import fr.devid.plantR.models.PositionDataPlant
import fr.devid.plantR.services.FirebaseService
import fr.devid.plantR.services.StringRef

class PopupWishListAdapter(
    var stage: String,
    var alert: (String?) -> Unit,
    var infoStage: (String) -> Unit,
    var infoStageRemove: (String) -> Unit
) :
    ListAdapter<Map<String, PositionDataPlant>, PopupWishListAdapter.TasksHolder>(UserDiffUtil()) {

    lateinit var guid: String
    lateinit var storageRef: StorageReference
    var plantIsSelected = arrayListOf<String>()
    var arrayDataPlantIsSelected = arrayListOf<String>()
    var nbAdd: (Int) = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksHolder {

        val view =
            RvPlantsWishlistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TasksHolder(view)
    }

    class TasksHolder(val binding: RvPlantsWishlistBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: TasksHolder, position: Int) {

        val plantMap: Map<String, PositionDataPlant> = getItem(position)
        println(plantMap)
        val positionPlant = arrayListOf<PositionDataPlant?>()
        println("Position plant : " + positionPlant)
        when (nbAdd) {
            1 -> {
                holder.binding.ivPlant1.visibility = View.GONE
                holder.binding.ivPlant2.visibility = View.GONE
                holder.binding.ivPlant3.visibility = View.GONE

            }
            2 -> {
                holder.binding.ivPlant3.visibility = View.GONE
                holder.binding.ivPlant4.visibility = View.GONE
            }
            3 -> {
                holder.binding.ivPlant4.visibility = View.GONE
            }
            4 -> {

            }
        }
        for (i in 0..3) {
            println("$position-$i")
            val result = plantMap.get("$position-$i")
            positionPlant.add(result)
        }

        println("adapter " + positionPlant)
        positionPlant.forEachIndexed { index, dataPlant ->
            if (dataPlant != null) {

        println("index : " + index)
                println("dataPlant : " + dataPlant)
                when (index) {
                    0 -> {
                        setImagePlantPosition(dataPlant.plantID, holder.binding.ivPlant1)
                        holder.binding.ivPlant1.borderColor =
                            holder.binding.ivPlant1.resources.getColor(
                                R.color.fui_transparent,
                                null
                            )
                    }
                    1 -> {
                        setImagePlantPosition(dataPlant.plantID, holder.binding.ivPlant2)
                        holder.binding.ivPlant2.borderColor =
                            holder.binding.ivPlant2.resources.getColor(
                                R.color.fui_transparent,
                                null
                            )
                    }
                    2 -> {
                        setImagePlantPosition(dataPlant.plantID, holder.binding.ivPlant3)
                        holder.binding.ivPlant3.borderColor =
                            holder.binding.ivPlant3.resources.getColor(
                                R.color.fui_transparent,
                                null
                            )
                    }

                    3 -> {
                        setImagePlantPosition(dataPlant.plantID, holder.binding.ivPlant4)
                        holder.binding.ivPlant4.borderColor =
                            holder.binding.ivPlant4.resources.getColor(
                                R.color.fui_transparent,
                                null
                            )

                    }
                    else -> {
                        when (index) {
                            0 -> {
                                setImagePlantPosition(dataPlant.plantID, holder.binding.ivPlant1)
                                holder.binding.ivPlant1.borderColor =
                                    holder.binding.ivPlant1.resources.getColor(
                                        R.color.fui_transparent,
                                        null
                                    )
                            }
                        }
                    }
                }



            } else {
                when (index) {
                    0 -> {
                        holder.binding.ivPlant1.borderColor =
                            holder.binding.ivPlant1.resources.getColor(R.color.green_plantr, null)
                    }
                    1 -> {
                        holder.binding.ivPlant2.borderColor =
                            holder.binding.ivPlant2.resources.getColor(R.color.green_plantr, null)
                    }
                    2 -> {
                        holder.binding.ivPlant3.borderColor =
                            holder.binding.ivPlant3.resources.getColor(R.color.green_plantr, null)
                    }
                    3 -> {
                        holder.binding.ivPlant4.borderColor =
                            holder.binding.ivPlant4.resources.getColor(R.color.green_plantr, null)
                    }
                }
            }
        }

        holder.binding.ivPlant1.setOnClickListener {
            if (positionPlant.get(0)?.plantID != null) {
                alert(positionPlant.get(0)?.plantID)
                println("POSITION" + "$position-0")
            } else {
                if (holder.binding.ivPlant1.circleBackgroundColor != holder.binding.ivPlant1.resources.getColor(
                        R.color.green_plantr,
                        null
                    )
                ) {
                    holder.binding.ivPlant1.circleBackgroundColor =
                        holder.binding.ivPlant1.resources.getColor(R.color.green_plantr, null)
                    println("POSITION" + "$position-0")
                    arrayDataPlantIsSelected.add("$position-0")
                    infoStage("$position-0")
                } else {
                    holder.binding.ivPlant1.circleBackgroundColor =
                        holder.binding.ivPlant1.resources.getColor(R.color.fui_transparent, null)
                    arrayDataPlantIsSelected.remove("$position-0")
                    println("Suppression de l'element $position-0")
                    infoStageRemove("$position-0")
                }
            }
        }

        holder.binding.ivPlant2.setOnClickListener {
            if (positionPlant.get(1)?.plantID != null) {
                alert(positionPlant.get(1)?.plantID)
                println("POSITION" + "$position-1")
            } else {
                if (holder.binding.ivPlant2.circleBackgroundColor != holder.binding.ivPlant2.resources.getColor(
                        R.color.green_plantr,
                        null
                    )
                ) {
                    holder.binding.ivPlant2.circleBackgroundColor =
                        holder.binding.ivPlant2.resources.getColor(R.color.green_plantr, null)
                    println("POSITION" + "$position-1")
                    arrayDataPlantIsSelected.add("$position-1")
                    infoStage("$position-1")
                } else {
                    holder.binding.ivPlant2.circleBackgroundColor =
                        holder.binding.ivPlant2.resources.getColor(R.color.fui_transparent, null)
                    arrayDataPlantIsSelected.remove("$position-1")
                    println("Suppression de l'element $position-1")
                    infoStageRemove("$position-1")
                }
            }
        }

        holder.binding.ivPlant3.setOnClickListener {
            if (positionPlant.get(2)?.plantID != null) {
                alert(positionPlant.get(2)?.plantID)
                println("POSITION" + "$position-2")
            } else {
                if (holder.binding.ivPlant3.circleBackgroundColor != holder.binding.ivPlant3.resources.getColor(
                        R.color.green_plantr,
                        null
                    )
                ) {
                    holder.binding.ivPlant3.circleBackgroundColor =
                        holder.binding.ivPlant3.resources.getColor(R.color.green_plantr, null)
                    println("POSITION" + "$position-2")
                    arrayDataPlantIsSelected.add("$position-2")
                    Constants.arrayList.add("$position-2")
                    infoStage("$position-2")
                } else {
                    holder.binding.ivPlant3.circleBackgroundColor =
                        holder.binding.ivPlant3.resources.getColor(R.color.fui_transparent, null)
                    arrayDataPlantIsSelected.remove("$position-2")
                    println("Suppression de l'element $position-2")
                    infoStageRemove("$position-2")
                }
            }
        }

        holder.binding.ivPlant4.setOnClickListener {
            if (positionPlant.get(3)?.plantID != null) {
                alert(positionPlant.get(3)?.plantID)
                println("POSITION" + "$position-3")
            } else {
                if (holder.binding.ivPlant4.circleBackgroundColor != holder.binding.ivPlant4.resources.getColor(
                        R.color.green_plantr,
                        null
                    )
                ) {
                    holder.binding.ivPlant4.circleBackgroundColor =
                        holder.binding.ivPlant4.resources.getColor(R.color.green_plantr, null)
                    println("POSITION" + "$position-3")
                    arrayDataPlantIsSelected.add("$position-3")
                    infoStage("$position-3")
                } else {
                    holder.binding.ivPlant4.circleBackgroundColor =
                        holder.binding.ivPlant4.resources.getColor(R.color.fui_transparent, null)
                    arrayDataPlantIsSelected.remove("$position-3")
                    println("Suppression de l'element $position-3")
                    infoStageRemove("$position-3")
                    //DECOCHE ELEMENT
                }
            }
        }
    }

    private fun setImagePlantPosition(plantId: String, imageView: CircleImageView) {
        storageRef = FirebaseService().getStoragePlantsReference()
            .child(plantId).child(StringRef.plantJpg)
        storageRef.downloadUrl.addOnSuccessListener {
            Glide.with(imageView.context)
                .load(it.toString())
                .circleCrop()
                .into(imageView)
        }
    }

}

internal class UserDiffUtil : DiffUtil.ItemCallback<Map<String, PositionDataPlant>>() {
    override fun areItemsTheSame(
        oldItem: Map<String, PositionDataPlant>,
        newItem: Map<String, PositionDataPlant>
    ): Boolean {
        println("areItemsTheSame $oldItem est $newItem")
        return oldItem == newItem
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(
        oldItem: Map<String, PositionDataPlant>,
        newItem: Map<String, PositionDataPlant>
    ): Boolean {
        println("areContentsTheSame$oldItem est $newItem")
        return oldItem == newItem
    }
}