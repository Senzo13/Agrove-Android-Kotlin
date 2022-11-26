package fr.devid.plantR.ui.myPlants

import android.annotation.SuppressLint
import android.content.res.Resources
import android.text.format.DateFormat
import android.view.ContextThemeWrapper
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import fr.devid.plantR.R
import fr.devid.plantR.databinding.RvPlantsFavsBinding
import fr.devid.plantR.models.*
import fr.devid.plantR.services.FirebaseService
import fr.devid.plantR.services.StringRef
import kotlinx.android.synthetic.main.rv_plants_filter.view.listePlants
import java.util.*
import kotlin.collections.HashMap

class WishListAdapter(var typeGardener : String, var callBackPlantId: (String) -> Unit, var alert: (String) -> Unit) :
    ListAdapter<FragmentWishList.FavsModels, WishListAdapter.WishListHolder>(UserDiffUtil()) {
    private val PAGE: String = "***** WishList *****"
    var listesDePlantes = hashMapOf<String, Plant>()
    private var tsLong = System.currentTimeMillis() / 1000
    private var currentMonth = getMonth(tsLong)
    var gardenerId: String? = ""
    var stageListOfPlants = HashMap<String, PositionDataPlant>()
    var stage: String = ""
    var typeGarden = typeGardener
    val arrayContains = arrayListOf<String>()
    val plantez = "Plantez !"
    val semez = "Semez !"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WishListHolder {
        val view = RvPlantsFavsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WishListHolder(view).apply {
            arrayContains.clear()

            itemView.listePlants.setOnClickListener {
                val position = adapterPosition
                if (position == RecyclerView.NO_POSITION) return@setOnClickListener
            }
        }
    }

    fun checkBetweenStartAndEnd(start: Int, end: Int, current: Int): Boolean {
        if (end <= start) {
            return current >= start && current <= 12 || current >= 1 && current <= end
        } else {
            return current >= start && current <= end
        }
    }

    fun getMonth(timestamp: Long): Int {
        val calendar = Calendar.getInstance(Locale.FRANCE)
        calendar.timeInMillis = timestamp * 1000L
        val month = DateFormat.format("MM", calendar).toString().toInt()
        return month
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: WishListHolder, position: Int) {

        arrayContains.clear()

        val favs: FragmentWishList.FavsModels = getItem(position)
        holder.binding.tvNamePlants.text = favs.name
        println("MY CURRENT MONTH" + tsLong)

        val startSowingFavs = favs.period?.sowingPeriod?.startMonth
        val endSowingFavs = favs.period?.sowingPeriod?.endMonth
        val startPlantingFavs = favs.period?.plantingPeriod?.startMonth
        val endPlantingFavs = favs.period?.plantingPeriod?.endMonth

        if (startSowingFavs ?: -1 >= 0 && endSowingFavs ?: -1 >= 0 && checkBetweenStartAndEnd(
                startSowingFavs ?: -1,
                endSowingFavs ?: -1,
                getMonth(tsLong)
            )
        ) {
            arrayContains.add("SEMER")
        }

        if (startPlantingFavs ?: -1 >= 0 && endPlantingFavs ?: -1 >= 0 && checkBetweenStartAndEnd(
                startPlantingFavs ?: -1,
                endPlantingFavs ?: -1,
                getMonth(tsLong)
            )
        ) {
            arrayContains.add("PLANTER")
        }
        when (arrayContains.count()) {
            2 -> {
                holder.binding.tvPlanter.visibility = View.VISIBLE
                holder.binding.tvPlanter.text = holder.itemView.rootView.context.getString(R.string.wishlist_plant)
                holder.binding.tvSeed.visibility = View.VISIBLE
                holder.binding.tvSeed.text = holder.itemView.rootView.context.getString(R.string.wishlist_sow)
                holder.binding.ivPlanter.visibility = View.VISIBLE
                holder.binding.ivSeed.visibility = View.VISIBLE
                holder.binding.ivNotification.visibility = View.VISIBLE
            }
            1 -> {
                if (arrayContains.contains("SEMER")) {
                    //BON MOMENT POUR SEMER
                    holder.binding.tvSeed.visibility = View.VISIBLE
                    holder.binding.ivSeed.visibility = View.VISIBLE
                    holder.binding.tvSeed.text = holder.itemView.rootView.context.getString(R.string.wishlist_sow)
                    println("Affichage du semer : " + holder.binding.tvSeed.text)
                } else {
                    holder.binding.tvSeed.text = "${holder.itemView.rootView.context.getString(R.string.plant_add_trad_5)} ${if(nbMonthBeforeStartPeriod(currentMonth, startSowingFavs!!) == 0) { "${holder.itemView.rootView.context.getString(R.string.less_month)}"} else { nbMonthBeforeStartPeriod(currentMonth, startSowingFavs!!) }} ${holder.itemView.rootView.context.getString(R.string.plant_add_trad_3)}"
                }
                if (arrayContains.contains("PLANTER")) {
                    //BON MOMENT POUR SEMER
                    holder.binding.tvPlanter.visibility = View.VISIBLE
                    holder.binding.ivPlanter.visibility = View.VISIBLE
                    holder.binding.tvPlanter.text = holder.itemView.rootView.context.getString(R.string.wishlist_plant)
                    println("Affichage du planter : " + holder.binding.tvPlanter.text)
                } else {
                    holder.binding.tvPlanter.text = "${holder.itemView.rootView.context.getString(R.string.plant_add_trad_5)} ${if(nbMonthBeforeStartPeriod(currentMonth, startPlantingFavs!!) == 0) { "${holder.itemView.rootView.context.getString(R.string.less_month)}"} else { nbMonthBeforeStartPeriod(currentMonth, startPlantingFavs!!) }} ${holder.itemView.rootView.context.getString(R.string.plant_add_trad_3)}"
                }
            }
            else -> {
                holder.binding.tvSeed.visibility = View.VISIBLE
                holder.binding.tvPlanter.visibility = View.VISIBLE
                holder.binding.ivNotification.visibility = View.GONE
                holder.binding.ivPlanter.visibility = View.VISIBLE
                holder.binding.ivSeed.visibility = View.VISIBLE
                holder.binding.tvPlanter.text = "${holder.itemView.rootView.context.getString(R.string.plant_add_trad_5).capitalize()} ${if(nbMonthBeforeStartPeriod(currentMonth, startPlantingFavs!!) == 0) { "${holder.itemView.rootView.context.getString(R.string.less_month)}" } else { nbMonthBeforeStartPeriod(currentMonth, startPlantingFavs!!) }} ${holder.itemView.rootView.context.getString(R.string.plant_add_trad_3)}"
                holder.binding.tvSeed.text = "${holder.itemView.rootView.context.getString(R.string.plant_add_trad_5)} ${if(nbMonthBeforeStartPeriod(currentMonth, startSowingFavs!!) == 0) { "${holder.itemView.rootView.context.getString(R.string.less_month)}" } else { nbMonthBeforeStartPeriod(currentMonth, startSowingFavs!!) }} ${holder.itemView.rootView.context.getString(R.string.plant_add_trad_3)}"
            }
        }

        holder.itemView.setOnClickListener {
           addPlantFavsToPlants(holder, favs, startSowingFavs, startPlantingFavs)
        }

        holder.binding.ivMenuOption.setOnClickListener {

            val wrapper = ContextThemeWrapper(
                holder.binding.ivMenuOption.context,
                R.style.PopupMenuPlantR)

            val popup = PopupMenu(wrapper, holder.binding.ivMenuOption)
            popup.inflate(R.menu.menu_item_favs)

            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_add -> {
                        addPlantFavsToPlants(holder, favs, startSowingFavs, startPlantingFavs)
                    }// FIN DU BOUTON D AJOUT

                    R.id.action_delete -> {
                        println("$PAGE BOUTON DE DELETE ")
                        println("je supprime le ${favs.key}")
                        var popup = PopupDeleteFavs(
                            favs.name,
                            holder.binding.ivMenuOption.context
                        ) { dialog, bool, choice ->
                            if (choice == 1) { //LE DELETE
                                println("Mon itemCount : " + this@WishListAdapter.itemCount)
                                FirebaseService().getFavs(gardenerId!!).child(favs.key)
                                    .removeValue()
                                dialog.dismiss()
                                notifyDataSetChanged()
                                println("Suppression OK")
                                if (this@WishListAdapter.itemCount == 0) {
                                    this@WishListAdapter.submitList(emptyList())
                                }
                            } else if (choice == 0) {
                                dialog.dismiss()
                            } else {
                                dialog.dismiss()
                            }

                        }
                        popup.show()
                    }
                }
                true
            }
            popup.show()
        }

        setPlantsPictureObserver(holder, favs.key)
    }

    class WishListHolder(val binding: RvPlantsFavsBinding) : RecyclerView.ViewHolder(binding.root)


    private fun setAddAllPlants(listStage: List<String>, plantId: String, plantName: String, holder : WishListHolder) {
        gardenerId?.let { GUID ->
            listStage.forEach { stage ->
                val addPlants = PlantsToAdd(plantId, plantName)
                FirebaseService().getGardenerById(GUID).child("plantingToAdd")
                    .child(stage).setValue(addPlants.toMap())
                    .addOnSuccessListener {
                        println("${holder.itemView.rootView.context.getString(R.string.alerter_add_plant_wishlist)} " + stage)
                        gardenerId?.let { guid ->
                            FirebaseService().getFavs(guid).child(plantId).removeValue()
                        }
                    }.addOnFailureListener {
                        println("Probleme dans l'ajout de la plante")
                    }
            }
            alert(plantName)
        }
    }

    private fun setAddSemisAllPlants(listStage: List<String>, plantId: String, plantName: String, holder : WishListHolder) {
        gardenerId?.let { GUID ->
            listStage.forEach { stage ->
                val addPlants = PlantsToAdd(plantId, plantName)
                FirebaseService().getGardenerById(GUID).child("plantsToAdd")
                    .child(stage).setValue(addPlants.toMap())
                    .addOnSuccessListener {
                        gardenerId?.let { guid ->
                            FirebaseService().getFavs(guid).child(plantId).removeValue()
                        }
                        println("${holder.itemView.rootView.context.getString(R.string.alerter_add_plant_wishlist)} " + stage)
                    }.addOnFailureListener {
                        println("Probleme dans l'ajout de la plante")
                    }
            }
            alert(plantName)
        }
    }

    private fun setPlantsPictureObserver(holder: WishListHolder, plantId: String) {
        val storageRef = FirebaseService().getStoragePlantsReference()
            .child(plantId).child(StringRef.plantJpg)
        storageRef.downloadUrl.addOnSuccessListener {
            Glide.with(holder.binding.ivPlants.context)
                .load(it.toString())
                .circleCrop()
                .into(holder.binding.ivPlants)
        }
    }

    internal class UserDiffUtil : DiffUtil.ItemCallback<FragmentWishList.FavsModels>() {

        override fun areItemsTheSame(
            oldItem: FragmentWishList.FavsModels,
            newItem: FragmentWishList.FavsModels
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: FragmentWishList.FavsModels,
            newItem: FragmentWishList.FavsModels
        ): Boolean {
            return oldItem == newItem
        }
    }

    fun nbMonthBeforeStartPeriod(current: Int, startPeriod: Int) : Int{
        var hasEnd = false
        var index = current
        var nb = 0
        var boucleInf = 0
        while(!hasEnd) {
            if (index == startPeriod) {
                hasEnd = true
                break //-> voir si return nb
            } else {
                nb += 1
                index += 1
                if (index >= 13) {
                    index = 0
                    boucleInf += 1
                    if (boucleInf >= 3) {
                        return -1
                    }
                }
            }
        }
        return nb
    }

    fun addPlantFavsToPlants(holder : WishListHolder, favs : FragmentWishList.FavsModels, startSowingFavs : Int?, startPlantingFavs : Int? ) {

        println("Je rentre ici bro")
        println("Je compte le arrayContains : " + arrayContains.count())
        if (stage.isNotEmpty()) {
            val maxIndex = stage.toInt() - 1
            val arrayEtagePlant = arrayListOf<Map<String, PositionDataPlant>>()
            println("COUCOU " + stageListOfPlants)
            for (i in 0..maxIndex) {
                val list = stageListOfPlants.filter {
                    it.key[0].toString() == i.toString()
                }
                println("POKOS NEGRO" + list)
                arrayEtagePlant.add(list)
            }
            println("$PAGE BOUTON D AJOUT")
            println("Nous somme a l etage : " + stage)

            if (holder.binding.tvPlanter.text == holder.itemView.rootView.context.getString(R.string.wishlist_plant) && holder.binding.tvSeed.text == holder.itemView.rootView.context.getString(R.string.wishlist_sow)) {
                //LES 2 SONT BON
                val popup = PopupChoiceFavs(
                    favs.name,
                    "",
                    holder.binding.root.context
                ) { dialog, bool, choice ->
                    println("L'ajout pour planter ET semer")
                    if (choice == 0) {
                        // POUR SEMER
                        println("L'ajout pour semer à fonctionner")
                        val popup = PopupWishListAddPlant(
                            favs.key,
                            "${holder.itemView.context.getString(R.string.choice_semer).toLowerCase()}",
                            favs.name ?: "",
                            stage,
                            typeGarden,
                            arrayEtagePlant,
                            holder.binding.ivMenuOption.context
                        ) { dialog, bool, choice, plantId, etageChoisisPourAjout ->
                            if (choice == 0) {
                                dialog.dismiss()
                            }

                            if (choice == 1) {
                                println("Vous cliquer sur AJOUTER")
                                println(etageChoisisPourAjout)
                                setAddSemisAllPlants(
                                    etageChoisisPourAjout,
                                    favs.key, favs.name ?: "", holder)

                                dialog.dismiss()
                            }

                            if (choice == 2) {
                                callBackPlantId(plantId)
                            }

                        }
                        popup.show()
                        dialog.dismiss()
                    }
                    if (choice == 1) {
                        // POUR SEMER
                        println("JE PASSE ICI PD DU CUL")
                        println("L'ajout pour semer à fonctionnerx")
                        val popup = PopupWishListAddPlant(
                            favs.key,
                            "${holder.itemView.context.getString(R.string.choice_planter).toLowerCase()}",
                            favs.name ?: "",
                            stage,
                            typeGarden,
                            arrayEtagePlant,
                            holder.binding.ivMenuOption.context
                        ) { dialog, bool, choice, plantId, etageChoisisPourAjout ->
                            if (choice == 0) {
                                dialog.dismiss()
                            }

                            if (choice == 1) {
                                println("Vous cliquer sur AJOUTER")
                                println(etageChoisisPourAjout)
                                setAddAllPlants(etageChoisisPourAjout, favs.key, favs.name ?: "", holder)
                                dialog.dismiss()
                            }
                            if (choice == 2) {
                                callBackPlantId(plantId)
                            }
                        }
                        popup.show()
                        dialog.dismiss()
                    }
                    if (choice == 2) {
                        dialog.dismiss()
                    }
                    if (choice == -1) {
                        dialog.dismiss()
                    }
                }
                popup.show()
            }
            if (holder.binding.tvPlanter.text == holder.itemView.rootView.context.getString(R.string.wishlist_plant) && holder.binding.tvSeed.text != holder.itemView.rootView.context.getString(R.string.wishlist_sow)) {
                println("JE RENTRE DANS LE CAS DE PLANTER")
                //CAS PLANTER
                val popup = PopupChoiceFavs(
                    favs.name,
                    "",
                    holder.binding.root.context
                ) { dialog, bool, choice ->
                    if (choice == 0) {
                        //PEUT PAS SEMER
                        val result = "${holder.itemView.rootView.context.getString(R.string.plant_add_trad_5)} ${
                            nbMonthBeforeStartPeriod(
                                currentMonth,
                                startSowingFavs!!
                            )
                        } ${holder.itemView.rootView.context.getString(R.string.plant_add_trad_3)}"

                        val popup = PopupFavsWait(
                            "${holder.itemView.rootView.context.getString(R.string.plant_add_trad_8)} ${result.toLowerCase()}",
                            holder.binding.root.context
                        ) { dialog, bool, choice ->
                            if (choice == 0) {
                                dialog.dismiss()
                            }
                            if (choice == 1) {
                                dialog.dismiss()
                            }
                        }
                        popup.show()
                        dialog.dismiss()
                    }
                    if (choice == 1) {
                        // POUR PLANTER
                        println("L'ajout pour planter à fonctionner")
                        val popup = PopupWishListAddPlant(
                            favs.key,
                            "${holder.itemView.context.getString(R.string.choice_planter).toLowerCase()}",
                            favs.name ?: "",
                            stage,
                            typeGarden,
                            arrayEtagePlant,
                            holder.binding.ivMenuOption.context
                        ) { dialog, bool, choice, plantId, etageChoisisPourAjout ->
                            if (choice == 0) {
                                dialog.dismiss()
                            }

                            if (choice == 1) {
                                println("Vous cliquer sur AJOUTER")
                                println(etageChoisisPourAjout)
                                setAddAllPlants(
                                    etageChoisisPourAjout,
                                    favs.key,
                                    favs.name ?: "", holder)

                                dialog.dismiss()
                            }

                            if (choice == 2) {
                                callBackPlantId(plantId)
                            }
                        }
                        popup.show()

                        dialog.dismiss()
                    }

                    if (choice == 2) {
                        dialog.dismiss()
                    }
                    if (choice == -1) {
                        dialog.dismiss()
                    }
                }
                popup.show()

            }
            if (holder.binding.tvPlanter.text != holder.itemView.rootView.context.getString(R.string.wishlist_plant) && holder.binding.tvSeed.text == holder.itemView.rootView.context.getString(R.string.wishlist_sow)) {
                println("JE RENTRE DANS LE CAS DE SEMER")
                //CAS SEMER

                val popup = PopupChoiceFavs(
                    favs.name,
                    "",
                    holder.binding.root.context
                ) { dialog, bool, choice ->
                    if (choice == 0) {
                        // POUR SEMER
                        println("L'ajout pour semer à fonctionner")
                        var popup = PopupWishListAddPlant(
                            favs.key,
                            "${holder.itemView.context.getString(R.string.choice_semer).toLowerCase()}",
                            favs.name ?: "",
                            stage,
                            typeGarden,
                            arrayEtagePlant,
                            holder.binding.ivMenuOption.context
                        ) { dialog, bool, choice, plantId, etageChoisisPourAjout ->
                            if (choice == 0) {
                                dialog.dismiss()
                            }

                            if (choice == 1) {
                                println("Vous cliquer sur AJOUTER")
                                println(etageChoisisPourAjout)
                                setAddSemisAllPlants(
                                    etageChoisisPourAjout,
                                    favs.key,
                                    favs.name ?: ""
                                , holder)
                                dialog.dismiss()
                            }

                            if (choice == 2) {
                                callBackPlantId(plantId)
                            }
                        }
                        popup.show()
                        dialog.dismiss()
                    }
                    if (choice == 1) {
                        //PEUT PAS PLANTER
                        val result = "${holder.itemView.rootView.context.getString(R.string.plant_add_trad_5)} ${
                            nbMonthBeforeStartPeriod(
                                currentMonth,
                                startPlantingFavs!!
                            )
                        } ${holder.itemView.rootView.context.getString(R.string.plant_add_trad_3)}"
                        val popup = PopupFavsWait(
                            "${holder.itemView.rootView.context.getString(R.string.plant_add_trad_6)} ${result.toLowerCase()}",
                            holder.binding.root.context
                        ) { dialog, bool, choice ->
                            if (choice == 0) {
                                dialog.dismiss()
                            }
                            if (choice == 1) {
                                dialog.dismiss()
                            }
                        }
                        popup.show()
                        dialog.dismiss()
                    }
                    if (choice == 2) {
                        dialog.dismiss()
                    }
                    if (choice == -1) {
                        dialog.dismiss()
                    }
                }
                popup.show()
            }
            if (holder.binding.tvPlanter.text != holder.itemView.rootView.context.getString(R.string.wishlist_plant) && holder.binding.tvSeed.text != holder.itemView.rootView.context.getString(R.string.wishlist_sow)) {
                println("JE RENTRE DANS LE CAS OU AUCUN NE MARCHE")
                println("**** " + holder.binding.tvPlanter.text + " " + holder.binding.tvSeed.text + " ****")
                //OUVRIR LE POPUP QUI PROPOSE 2 CHOIX OU TU NE PEUX NI SEMER NI PLANTER
                val result = "V${holder.itemView.rootView.context.getString(R.string.plant_add_trad_4)} ${favs.name} ${holder.itemView.rootView.context.getString(R.string.plant_add_trad_5)} ${
                    nbMonthBeforeStartPeriod(
                        currentMonth,
                        startPlantingFavs!!
                    )
                } ${holder.itemView.rootView.context.getString(R.string.plant_add_trad_7)} ${
                    nbMonthBeforeStartPeriod(
                        currentMonth,
                        startSowingFavs!!
                    )
                } ${holder.itemView.rootView.context.getString(R.string.plant_add_trad_3)}"
                println(result)
                val popup = PopupFavsWait(
                    result,
                    holder.binding.root.context
                ) { dialog, bool, choice ->

                    if (choice == 0) {
                        dialog.dismiss()
                    }
                    if (choice == 1) {
                        dialog.dismiss()
                    }
                }
                popup.show()
            }
        }
    }


}


