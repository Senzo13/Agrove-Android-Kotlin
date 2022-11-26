package fr.devid.plantR.ui.myPlants

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.storage.StorageReference
import fr.devid.plantR.Constants
import fr.devid.plantR.R
import fr.devid.plantR.databinding.RvPlantsFilterBinding
import fr.devid.plantR.models.*
import fr.devid.plantR.services.FirebaseService
import fr.devid.plantR.services.StringRef
import fr.devid.plantR.ui.home.ProfileViewModel
import kotlinx.android.synthetic.main.rv_plants_filter.view.*
import java.util.*
import kotlin.collections.HashMap

data class PlantModels(
    var id : String = "",
    var plants : Plant? = null,
    var period : String = ""
)

class PlantsAdapter(private val actionToPage: (String, String, String, SowingPeriod, PlantingPeriod, String) -> Unit) : ListAdapter<PlantModels, PlantsAdapter.PlantsHolder>(UserDiffUtil()) {

    private val PAGE: String = "***** PlantsAdapter *****"
    private lateinit var storageRef: StorageReference
    private lateinit var plantRef: DatabaseReference
    private lateinit var handleRef : ValueEventListener
    private var tsLong = System.currentTimeMillis() / 1000
    var listesDePlantes = mapOf<String, Plant>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantsHolder {
        val view = RvPlantsFilterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlantsHolder(view).apply {
            itemView.listePlants.setOnClickListener {
                val position = adapterPosition
                if (position == RecyclerView.NO_POSITION) return@setOnClickListener
                getItem(position)?.let { key ->
                    key.plants?.let { data ->
                        data?.name?.let { it1 ->
                            data?.description?.let { it2 ->
                                actionToPage(key.id,
                                    it1,
                                    it2, key.plants!!.sowingPeriod!!, key.plants!!.plantingPeriod!!, "null")
                            }
                        }
                    }
                }
            }
        }
    }
    class PlantsHolder(val binding : RvPlantsFilterBinding) : RecyclerView.ViewHolder(binding.root)
    fun removeListener() {
        if (::plantRef.isInitialized && ::handleRef.isInitialized)
            plantRef.removeEventListener(handleRef)
    }

    override fun onBindViewHolder(holder: PlantsHolder, position: Int) {

        val plant: PlantModels = getItem(position)
        holder.binding.tvNamePlants.text = plant.plants?.name?.capitalize()
        //holder.binding.tvSaisonPlant.text = "Été"
        var sowingStart = plant.plants?.sowingPeriod?.startMonth
        var sowingEnd = plant.plants?.sowingPeriod?.endMonth

        setPlantsPictureObserver(holder, plant.id)

        holder.binding.tvDescription.text = plant.plants?.description
//        when(sowingStart) {
//            1 -> holder.binding.tvSaisonPlant.text = "Saison : Hiver"
//            2 -> holder.binding.tvSaisonPlant.text = "Saison : Hiver"
//            3 -> holder.binding.tvSaisonPlant.text = "Saison : Printemps"
//            4 -> holder.binding.tvSaisonPlant.text = "Saison : Printemps"
//            5 -> holder.binding.tvSaisonPlant.text = "Saison : Printemps"
//            6 -> holder.binding.tvSaisonPlant.text = "Saison : Été"
//            7 -> holder.binding.tvSaisonPlant.text = "Saison : Été"
//            8 -> holder.binding.tvSaisonPlant.text = "Saison : Été"
//            9 -> holder.binding.tvSaisonPlant.text = "Saison : Automne"
//            10 -> holder.binding.tvSaisonPlant.text = "Saison : Automne"
//            11 -> holder.binding.tvSaisonPlant.text = "Saison : Automne"
//            12 -> holder.binding.tvSaisonPlant.text = "Saison : Hiver"
//        }
//        when(sowingEnd) {
//            1 -> holder.binding.tvSaisonPlant2.text = "/ Hiver"
//            2 -> holder.binding.tvSaisonPlant2.text = "/ Hiver"
//            3 -> holder.binding.tvSaisonPlant2.text = "/ Printemps"
//            4 -> holder.binding.tvSaisonPlant2.text = "/ Printemps"
//            5 -> holder.binding.tvSaisonPlant2.text = "/ Printemps"
//            6 -> holder.binding.tvSaisonPlant2.text = "/ Été"
//            7 -> holder.binding.tvSaisonPlant2.text = "/ Été"
//            8 -> holder.binding.tvSaisonPlant2.text = "/ Été"
//            9 -> holder.binding.tvSaisonPlant2.text = "/ Automne"
//            10 -> holder.binding.tvSaisonPlant2.text = "/ Automne"
//            11 -> holder.binding.tvSaisonPlant2.text = "/ Automne"
//            12 -> holder.binding.tvSaisonPlant2.text = "/ Hiver"
//        }
    }


    fun setGardenerPlantsObserver(categorie: String?, search : String) {
        plantRef = FirebaseService().getPlantsReference()
        handleRef = plantRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val plantsGardeners = dataSnapshot.getValue<HashMap<String, Plant>>()?.let {
                    val arrayFr = it.map { data ->
                        PlantModels(data.key, data.value)
                    }

                    val arrayJap = arrayFr.filter { plantJap ->
                        plantJap.plants?.name?.toLowerCase() == "parsley" ||  plantJap.plants?.name?.toLowerCase() == "coriander" ||  plantJap.plants?.name?.toLowerCase() == "basil" ||  plantJap.plants?.name?.toLowerCase() == "bitter melon" || plantJap.plants?.name?.toLowerCase() == "cherry tomato" || plantJap.plants?.name?.toLowerCase() == "radish"
                    }

                    val arrayFrNoJapPlants = arrayFr.filter { plantFr ->
                        plantFr.plants?.name?.toLowerCase() != "parsley" && plantFr.plants?.name?.toLowerCase() != "coriander" &&  plantFr.plants?.name?.toLowerCase() != "basil"&&  plantFr.plants?.name?.toLowerCase() != "bitter melon" && plantFr.plants?.name?.toLowerCase() != "cherry tomato" && plantFr.plants?.name?.toLowerCase() != "radish"
                    }

                    when(Locale.getDefault().language.toString()) {
                        "fr" -> {
                            showPlantsByLang(arrayFrNoJapPlants, categorie, search)
                        }
                        "en" -> {
                            showPlantsByLang(arrayJap, categorie, search)
                        }
                        else -> {
                            showPlantsByLang(arrayJap, categorie, search)
                        }
                    }
                }
                }

            override fun onCancelled(databaseError: DatabaseError) {
                println("${PAGE} onCancelled ${databaseError.toException()}")
            }
        })
    }

    private fun showPlantsByLang(array : List<PlantModels>, categorie : String?, search : String) {

        if (categorie == "Empty" && search.isEmpty()) {
            println("JE SUIS DANS LE VIDE")
            println("Ma categorie : " + categorie)
            this@PlantsAdapter.submitList(array.sortedBy { it.plants?.name })
        }
        if (categorie != "Empty" && search.isEmpty()) {
            println("Ma categorie : " + categorie)
            this@PlantsAdapter.submitList(array.filter { it.plants?.filtre?.keys!!.firstOrNull { piti -> piti.toLowerCase().contains(categorie?.toLowerCase()!!) } != null}.sortedBy { it.plants?.name })
        }

        if(search.isNotEmpty() && categorie == "Empty") {
            println("$categorie est ma catégorie, notre recherche est : $search")
            this@PlantsAdapter.submitList(array.filter { plantFr ->
                plantFr.plants!!.name.toLowerCase().contains(search.toLowerCase())
            }.sortedBy { it.plants?.name })

        }

        if(search.isNotEmpty() && categorie != "Empty") {
            println("$categorie est ma catégorie, notre recherche est : $search")
            this@PlantsAdapter.submitList( array.filter { it.plants?.filtre?.keys!!.firstOrNull { piti -> piti.toLowerCase().contains(categorie?.toLowerCase()!!) } != null}.sortedBy { it.plants?.name })
        }
    }

    private fun setPlantsPictureObserver(holder: PlantsHolder, plantId: String) {
        storageRef = FirebaseService().getStoragePlantsReference()
            .child(plantId).child(StringRef.plantJpg)
        storageRef.downloadUrl.addOnSuccessListener {
            Glide.with(holder.binding.ivPlants.context)
                .load(it.toString())
                .circleCrop()
                .into(holder.binding.ivPlants)
        }
    }

    internal class UserDiffUtil : DiffUtil.ItemCallback<PlantModels>() {

        override fun areItemsTheSame(oldItem: PlantModels, newItem: PlantModels): Boolean {
                return oldItem.id == newItem.id
        }
        override fun areContentsTheSame(oldItem: PlantModels, newItem: PlantModels): Boolean {
            return oldItem == newItem
        }

    }

}

