package fr.devid.plantR.ui.myPlants

import android.text.format.DateFormat
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import android.view.LayoutInflater
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference
import fr.devid.plantR.R
import fr.devid.plantR.databinding.RvCardPlantBinding
import fr.devid.plantR.models.*
import fr.devid.plantR.services.FirebaseService
import fr.devid.plantR.services.StringRef
import kotlinx.android.synthetic.main.rv_card_plant.view.*
import java.util.*

class MesCardsPlantesAdapter(private val actionToPage : (String, String) -> Unit, private val actionToAdd : (String, String, String,String, PlantingPeriod, SowingPeriod, String) -> Unit) : ListAdapter<PlantModels, MesCardsPlantesAdapter.MesCardsPlantesHolder>(UserDiffUtil()) {

    private lateinit var storageRef: StorageReference
    private lateinit var plantRef: DatabaseReference
    private lateinit var handleRef : ValueEventListener
    private var tsLong = System.currentTimeMillis() / 1000
    private var ts = tsLong.toString()
    var listesDePlantes = hashMapOf<String, Plant>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MesCardsPlantesHolder {
        val view = RvCardPlantBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MesCardsPlantesHolder(view).apply {
            itemView.cv_card.setOnClickListener {
                val position = adapterPosition
                if (position == RecyclerView.NO_POSITION) return@setOnClickListener
                getItem(position)?.let { key ->
                    actionToAdd(key.id, key.plants?.name.toString(), key.plants?.description.toString(), itemView.tv_saison_plant.text.toString(), key.plants!!.plantingPeriod!!,key.plants!!.sowingPeriod!!, key.period)
                }
            }
            itemView.iv_information_click.setOnClickListener {
                val position = adapterPosition
                if (position == RecyclerView.NO_POSITION) return@setOnClickListener
                getItem(position)?.let { key ->
                    actionToPage(key.id,key.period)

                }
            }
    }
    }

    class MesCardsPlantesHolder(val binding : RvCardPlantBinding) : RecyclerView.ViewHolder(binding.root)
    fun removeListener() {
        if (::plantRef.isInitialized && ::handleRef.isInitialized)
            plantRef.removeEventListener(handleRef)
    }

    fun getDate(timestamp: Long) : Int {
        val calendar = Calendar.getInstance(Locale.FRANCE)
        calendar.timeInMillis = timestamp * 1000L
        val date = DateFormat.format("MM",calendar).toString().toInt()
        return date
    }

    override fun onBindViewHolder(holder: MesCardsPlantesHolder, position: Int) {
        val plant: PlantModels = getItem(position)
        holder.binding.tvNamePlants.text = plant.plants?.name?.capitalize()

        if(plant.period == "BOTH" ) {
            holder.binding.tvSaisonPlant.text = holder.itemView.context.getString(R.string.SEMIS_OR_PLANT)
        }
        else if (plant.period == "SEMER"){
            holder.binding.tvSaisonPlant.text = holder.itemView.context.getString(R.string.SEMIS)
        } else {
            holder.binding.tvSaisonPlant.text = holder.itemView.context.getString(R.string.PLANT)
        }
//
//        when(getDate(tsLong)) {
//            1 -> holder.binding.tvSaisonPlant.text = "Hiver"
//            2 -> holder.binding.tvSaisonPlant.text = "Hiver"
//            3 -> holder.binding.tvSaisonPlant.text = "Printemps"
//            4 -> holder.binding.tvSaisonPlant.text = "Printemps"
//            5 -> holder.binding.tvSaisonPlant.text = "Printemps"
//            6 -> holder.binding.tvSaisonPlant.text = "Été"
//            7 -> holder.binding.tvSaisonPlant.text = "Été"
//            8 -> holder.binding.tvSaisonPlant.text = "Été"
//            9 -> holder.binding.tvSaisonPlant.text = "Automne"
//            10 -> holder.binding.tvSaisonPlant.text = "Automne"
//            11 -> holder.binding.tvSaisonPlant.text = "Automne"
//            12 -> holder.binding.tvSaisonPlant.text = "Hiver"
//        }

        holder.binding.tvDescriptionPlants.text = plant.plants?.description
        setPlantsPictureObserver(holder, plant.id)
    }


    private fun setPlantsPictureObserver(holder: MesCardsPlantesHolder, plantId: String) {

        storageRef = FirebaseService().getStoragePlantsReference()
            .child(plantId).child(StringRef.plantJpg)
        storageRef.downloadUrl.addOnSuccessListener {
            Glide.with(holder.binding.ivPlants.context)
                .load(it.toString())
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

