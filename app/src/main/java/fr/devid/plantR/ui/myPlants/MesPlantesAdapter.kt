package fr.devid.plantR.ui.myPlants

import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
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
import fr.devid.plantR.databinding.RvPlantsBinding
import fr.devid.plantR.models.*
import fr.devid.plantR.services.CurrentPlantPageService
import fr.devid.plantR.services.FirebaseService
import fr.devid.plantR.services.Singleton
import fr.devid.plantR.services.StringRef
import fr.devid.plantR.viewmodels.Event


data class PlantModel(
    var id: String = "",
    var positionDataPlant: PositionDataPlant? = null
)

class MesPlantesAdapter(
    val actionToPage: (String, String, Int, Int, String, String) -> Unit,
    val updateCallBack: (String, String, String) -> Unit,
    val actionToDelete: (String,String) -> Unit) :
    ListAdapter<PlantModel, MesPlantesAdapter.MesPlantesHolder>(UserDiffUtil()) {

    lateinit var currentEtage: String
    lateinit var gardenerId: String
    private val PAGE: String = "***** MesPlantesAdapter *****"
    private lateinit var gardenersPlantsRef: DatabaseReference
    private lateinit var gardenersPlantsData: ValueEventListener
    private lateinit var storageRef: StorageReference

    var listesDePlantes = hashMapOf<String, Plant>()
    var i = "0"
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MesPlantesHolder {
        val view = RvPlantsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MesPlantesHolder(view)
    }

    fun removeListener() {
        if (::gardenersPlantsRef.isInitialized && ::gardenersPlantsData.isInitialized)
            gardenersPlantsRef.removeEventListener(gardenersPlantsData)
    }

    class MesPlantesHolder(val binding: RvPlantsBinding) : RecyclerView.ViewHolder(binding.root)

    fun setGardenerPlantsObserver(gardenerId: String, currentPlantPageService: CurrentPlantPageService) {
        println("Nous somme dans le current etage numero  : " + currentEtage)
        gardenersPlantsRef = FirebaseService().getGardenerById(gardenerId)
        gardenersPlantsData = gardenersPlantsRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val plantsGardeners = dataSnapshot.getValue(GardenerPlant::class.java)
                plantsGardeners?.let {
                    val totalList = it.plants.map {
                        d -> SortOfPlant(d.key,d.value)
                    }
                    val plantAdapter = it.plants.filter {
                        it.key.first().toString() == currentEtage
                    }.map { data ->
                        PlantModel(data.key, data.value)
                    }
                    this@MesPlantesAdapter.submitList(plantAdapter){
                        //SERVICE
                        Singleton.instance._plantsAddedCallBack.postValue(Event(true))
                        println("RETOUR DE LA CURRENT LISTE " + totalList)
                        currentPlantPageService.currentPlantModel = totalList
                        currentPlantPageService.gardenerId = gardenerId
                    }

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("${PAGE} onCancelled ${databaseError.toException()}")
            }
        })
    }



    override fun onBindViewHolder(holder: MesPlantesHolder, position: Int) {

        FirebaseService().firebase.currentUser?.uid?.let { uid ->
            val userRef = FirebaseService().getUserByid(uid)
            val handleUser = userRef.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val users = snapshot.getValue(User::class.java)
                    gardenerId = users?.currentGardener!!
                }

            })
        }

        val plant: PlantModel = getItem(position)
        holder.itemView.setOnClickListener {
            if (position == RecyclerView.NO_POSITION) return@setOnClickListener
            getItem(position)?.let { key ->
                println("LE NUMERO DE LETAGE DE LA PLANTE " + key.id)
                if (key.positionDataPlant?.plantName.isNullOrEmpty()) {
                    listesDePlantes.get(key.positionDataPlant?.plantID!!)?.name?.let { name ->
                        actionToPage(key.positionDataPlant?.plantID!!, name, key.positionDataPlant?.dateHarvested!!, key.positionDataPlant?.dateSowing!!, "${key.id}", gardenerId.let { it })
                    }
                    if(listesDePlantes.get(key.positionDataPlant?.plantID!!)?.name == null) {
                        gardenerId.let { guid ->
                            currentEtage.let { etage ->
                                val plantDelete = "${etage}-${plant.id[2]}"
                                println("MON ID LORS DU DELETE : ${etage}-${plant.id[2]}")
                                FirebaseService().getGardenerPlantsById(gardenerId).child(plantDelete).removeValue()
                                notifyDataSetChanged()
                            }
                        }
                    } else {
                        println("La plante nest pas supprimé")
                    }
                } else {
                    key.positionDataPlant?.plantName?.let { name2 ->
                        actionToPage(key.positionDataPlant?.plantID!!, name2, key.positionDataPlant?.dateHarvested!!, key.positionDataPlant?.dateSowing!!, "${key.id}", gardenerId.let { it })
                    }
                }
            }
        }



        holder.binding.tvNamePlants.text = if (plant.positionDataPlant?.plantName.isNullOrEmpty()) {
           listesDePlantes.get(plant.positionDataPlant?.plantID)?.name
        } else {
                plant.positionDataPlant?.plantName
            }


        holder.binding.tvPositionPlant.text = (when (plant.id[2].toString()) {
            "0" -> holder.itemView.context.getString(R.string.PLACE_ONE)
            "1" -> holder.itemView.context.getString(R.string.PLACE_TWO)
            "2" -> holder.itemView.context.getString(R.string.PLACE_THREE)
            "3" -> holder.itemView.context.getString(R.string.PLACE_FOURTH)
            else -> "Emplacement ?"
        }).toString()


        plant.positionDataPlant?.plantID?.let { setPlantsPictureObserver(holder, it) }

        holder.binding.ivMenuOption.setOnClickListener {
            val wrapper = ContextThemeWrapper(
                holder.binding.ivMenuOption.context,
                fr.devid.plantR.R.style.PopupMenuPlantR
            )
            val popup = PopupMenu(wrapper, holder.binding.ivMenuOption)
            popup.inflate(fr.devid.plantR.R.menu.menu_item)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    fr.devid.plantR.R.id.action_delete -> {
                        gardenerId.let { guid ->
                            currentEtage.let { etage ->
                                val plantDelete = "${etage}-${plant.id[2]}"
                                println("MON ID LORS DU DELETE : ${etage}-${plant.id[2]}")
                                actionToDelete(plantDelete, guid)
                                notifyDataSetChanged()
                            }
                        }
                    }
                    fr.devid.plantR.R.id.action_modify -> {
                        updateCallBack(gardenerId, getItem(position).id,
                            if (getItem(position).positionDataPlant?.plantName.isNullOrEmpty())
                            {
                                listesDePlantes[getItem(position).positionDataPlant?.plantID!!]?.name
                            } else {
                                getItem(position).positionDataPlant?.plantName
                            }.toString()
                        )
                    }
                }
                true
            }
            popup.show()
        }
    }

    private fun setPlantsPictureObserver(holder: MesPlantesHolder, plantId: String) {

        storageRef = FirebaseService().getStoragePlantsReference()
            .child(plantId).child(StringRef.plantJpg)
        storageRef.downloadUrl.addOnSuccessListener {
            Glide.with(holder.binding.ivPlants.context)
                .load(it.toString())
                .circleCrop()
                .into(holder.binding.ivPlants)
        }
    }

    internal class UserDiffUtil : DiffUtil.ItemCallback<PlantModel>() {
        override fun areItemsTheSame(oldItem: PlantModel, newItem: PlantModel): Boolean {
            println("areItemsTheSame $oldItem est $newItem")
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PlantModel, newItem: PlantModel): Boolean {
            return oldItem == newItem
        }
    }
}

