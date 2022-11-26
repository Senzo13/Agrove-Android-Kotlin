package fr.devid.plantR.ui.myPlants

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.storage.StorageReference
import fr.devid.plantR.R
import fr.devid.plantR.base.BaseFragment
import fr.devid.plantR.databinding.FragmentAddPlantesRenameBinding
import fr.devid.plantR.models.Plant
import fr.devid.plantR.models.PlantFavs
import fr.devid.plantR.models.PlantsToAdd
import fr.devid.plantR.models.PlantsToAddPreviousDate
import fr.devid.plantR.services.AlerterService
import fr.devid.plantR.services.FirebaseService
import fr.devid.plantR.ui.home.ProfileViewModel
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject

/**
 * LG
 */

class AddPlantesRenameFragment : BaseFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val profilViewModel: ProfileViewModel by activityViewModels { viewModelFactory }
    private lateinit var binding: FragmentAddPlantesRenameBinding
    private val data: AddPlantesRenameFragmentArgs by navArgs()
    private lateinit var storageRef: StorageReference
    private var path: String = "plant.jpg"
    lateinit var datePicker: DatePickerHelper
    private var tsLong = System.currentTimeMillis() / 1000
    private lateinit var favsRef: DatabaseReference
    var newFavs: HashMap<String, PlantFavs> = HashMap()
    val arrayContains = arrayListOf<String>()
    private lateinit var periodRef : DatabaseReference
    private lateinit var handlePeriod : ValueEventListener
    var checkDifferent : Map<String, PlantFavs> = emptyMap()
    private val currentDate = Date().time / 1000
    private val minDate = 31536000.toLong()
    private lateinit var choiceMonthString : String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddPlantesRenameBinding.inflate(inflater, container, false)
        initView()
        return binding.root
    }

    fun getMonth(timestamp: Long): Int {
        val calendar = Calendar.getInstance(Locale.FRANCE)
        calendar.timeInMillis = timestamp * 1000L
        val month = DateFormat.format("MM", calendar).toString().toInt()
        return month
    }

    private fun setPlantPeriod(gardenerId: String) {
        periodRef = FirebaseService().getPlantsReferenceById(data.id)
        handlePeriod = periodRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val plantFavs = snapshot.getValue(Plant::class.java)
                if(plantFavs != null) {
                    val plant = PlantFavs(plantFavs.sowingPeriod, plantFavs.plantingPeriod)
                    setFavorisObserver(gardenerId, plant)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun initView() {

        val getOfDateSowing = "${data.sowing}"
        val arraySplitSowing = getOfDateSowing.split("|")
        val getOfDatePlanting = "${data.planting}"
        val arraySplitPlanting = getOfDatePlanting.split("|")
        val startSowing = arraySplitSowing[0].toInt()
        val endSowing = arraySplitSowing[1].toInt()
        val startPlanting = arraySplitPlanting[0].toInt()
        val endPlanting = arraySplitPlanting[1].toInt()

        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.tvNamePlants.text = data.name.capitalize()
        binding.tvDescriptionPlants.text = data.description
        binding.etNameOfPlantsToRename.hint = data.name.capitalize()
        binding.tvSaisonPlant.visibility = View.GONE

        storageRef = FirebaseService().getStoragePlantsReference()
            .child(data.id).child(path)
        storageRef.downloadUrl.addOnSuccessListener {
            Glide.with(binding.ivPlants.context)
                .load(it.toString())
                .into(binding.ivPlants)
        }

        binding.ivBtnInformations.setOnClickListener {
            findNavController().navigate(
                AddPlantesRenameFragmentDirections.actionAddPlantesRenameFragmentToFragmentInformations(
                    data.id
                )
            )
        }

        // BOUTON POUR AJOUTER UNE PLANTE AGROVE
        binding.btAddPlants.setOnClickListener {

            val popupSowingOrPlanting = PopupSowingPlanting(
                requireContext(),
                data.name.capitalize()
            ) { popup, bool, choice ->

                if (choice == 0) { // SEMER
                    println("on entre dans semer")
                    lookingForSowing(startSowing, endSowing, tsLong, data.name)
                    popup.dismiss()
                }
                else if(choice == 1){ // PLANTER
                    println("on entre dans planter")
                    lookingForPlanting(startPlanting, endPlanting, tsLong, data.name)
                    popup.dismiss()
                } else {
                    popup.dismiss()
                }
            }
            popupSowingOrPlanting.show()
        }

    }


    fun setPopupFavs() {
        profilViewModel.userService.gardenerId?.let { guid ->
            setPlantPeriod(guid)
        }
    }

    private fun setPlantToSemisInFirebase() {
        println("Je rentre dans l'ajout d'une plante")
        profilViewModel.userService.gardenerId.let { guid ->
            val addPlants = PlantsToAdd(data.id, binding.etNameOfPlantsToRename.text.toString())
            FirebaseService().getGardenerById(guid!!).child("plantsToAdd")
                .child(data.etage + "-" + data.nbEtage).setValue(addPlants.toMap())
                .addOnSuccessListener {
                    activity?.let {
                        findNavController().popBackStack(R.id.mesPlantesFragment, false)
                    }
                }.addOnFailureListener {
                    activity?.let {
                        AlerterService.showError(context?.getString(R.string.ECHEC_PLANT_ADD)!!, it)
                    }
                }
        }
    }

    private fun setAddToPlantInFirebase() {
        println("Je rentre dans l'ajout d'une plante")
        profilViewModel.userService.gardenerId.let { guid ->
            val addPlants = PlantsToAdd(data.id, binding.etNameOfPlantsToRename.text.toString())
            FirebaseService().getGardenerById(guid!!).child("plantingToAdd")
                .child(data.etage + "-" + data.nbEtage).setValue(addPlants.toMap())
                .addOnSuccessListener {
                    println("Ajout de la plante réussie")
                    activity?.let {
                        findNavController().popBackStack(R.id.mesPlantesFragment, false)
                    }
                }.addOnFailureListener {
             println("Probleme dans l'ajout de la plante")
                }
        }
    }

    private fun lookingForPlanting(start: Int, end: Int, current: Long, name: String) {
        arrayContains.clear()

        if (start != -1 && end != -1 && getMonth(current) != -1) {

            if (start ?: -1 >= 0 && end ?: -1 >= 0 && checkBetweenStartAndEnd(
                    start ?: -1,
                    end ?: -1,
                    getMonth(current!!)
                )
            ) {
                arrayContains.add("SEMER")
            }

            if (start ?: -1 >= 0 && end ?: -1 >= 0 && checkBetweenStartAndEnd(
                    start ?: -1,
                    end ?: -1,
                    getMonth(current!!)
                )
            ) {
                arrayContains.add("PLANTER")
            }
            when (arrayContains.count()) {
                2 -> {
                    val title = "${getString(R.string.tv_trad_util_1)} $name ${getString(R.string.PLANT_ADD)}"
                    println(title)
                    val popup = PopupAddPlantSuccess(title,requireContext()) { dialog, bool, choice ->
                        setAddToPlantInFirebase()
                        if(choice == 0 ) {
                            dialog.dismiss()
                        } else {
                            dialog.dismiss()
                        }
                    }
                    popup.show()
                }
                1 -> {
                    val title =
                        if (arrayContains.contains("PLANTER")) "${getString(R.string.tv_trad_util_1)} $name ${getString(R.string.PLANT_ADD)} " else ""
                    println(title)
                    val popup = PopupAddPlantSuccess(title,requireContext()) { dialog, bool, choice ->
                        setAddToPlantInFirebase()
                        if(choice == 0 ) {
                            dialog.dismiss()
                        } else {
                            dialog.dismiss()
                        }
                    }
                    popup.show()
                }
                else -> { //AVOIR LE CAS DU POPUP QUI AFFICHE EGALEMENT L AJOUT DANS FAVORI
                    val result = "${getString(R.string.plant_add_trad)} ${data.name} ${getString(R.string.plant_add_trad_2)} ${nbMonthBeforeStartPeriod(getMonth(tsLong), start)} ${getString(R.string.plant_add_trad_3)}"
                    val popup = PopupPlanting(result, requireContext(), data.name) { dialog, bool, choice ->

                        if (choice == 0) {
                            setPopupFavs()
                            dialog.dismiss()
                        } else if(choice == 1) {
                            //AlerterService.showError("Mince, cette fonctionnalité est en construction !", requireActivity())
                            showDatePickerDialog()
                            dialog.dismiss()
                        } else {
                            dialog.dismiss()
                        }

                    }
                    popup.show()
                }
            }
        }
    }

    private fun lookingForSowing(start: Int, end: Int, current: Long, name: String) {
        arrayContains.clear()

        if (start != -1 && end != -1 && getMonth(current) != -1) {

            if (start ?: -1 >= 0 && end ?: -1 >= 0 && checkBetweenStartAndEnd(
                    start ?: -1,
                    end ?: -1,
                    getMonth(current)
                )
            ) {
                arrayContains.add("SEMER")
            }

            if (start ?: -1 >= 0 && end ?: -1 >= 0 && checkBetweenStartAndEnd(
                    start ?: -1,
                    end ?: -1,
                    getMonth(current)
                )
            ) {
                arrayContains.add("PLANTER")
            }
            when (arrayContains.count()) {
                2 -> {
                    val title = "${getString(R.string.tv_trad_util_1)} $name ${getString(R.string.PLANT_ADD)}"
                    println(title)
                    val popup = PopupAddPlantSuccess(title,requireContext()) { dialog, bool, choice ->
                        setPlantToSemisInFirebase()
                        if(choice == 0 ) {
                            dialog.dismiss()
                        } else {
                            dialog.dismiss()
                        }
                    }
                    popup.show()
                }
                1 -> {
                    val title = if (arrayContains.contains("SEMER")) "${getString(R.string.tv_trad_util_1)} $name ${getString(R.string.PLANT_ADD)}" else ""
                    println(title)
                    val popup = PopupAddPlantSuccess(title,requireContext()) { dialog, bool, choice ->
                        setPlantToSemisInFirebase()
                        if(choice == 0 ) {
                            dialog.dismiss()
                        } else {
                            dialog.dismiss()
                        }
                    }
                    popup.show()
                }
                else -> { //AVOIR LE CAS DU POPUP QUI AFFICHE EGALEMENT L AJOUT DANS FAVORI
                    val result = "${getString(R.string.plant_add_trad_4)} ${data.name} ${getString(R.string.plant_add_trad_2)} ${nbMonthBeforeStartPeriod(getMonth(tsLong), start)} ${getString(R.string.plant_add_trad_3)}"

                    val popup = PopupSeeding(result, requireContext(), data.name) { dialog, bool, choice ->
                            if (choice == 0) {
                                setPopupFavs()
                                dialog.dismiss()
                            } else if(choice == 1) {
                               // setPlantToSemisInFirebase()
                                   // AlerterService.showError("Mince, cette fonctionnalité est en construction !", requireActivity())
                                showDatePickerDialog()
                                dialog.dismiss()
                            } else {
                                dialog.dismiss()
                            }

                        }
                    popup.show()
                }
            }
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


    fun setFavorisObserver(gardenerId: String, plantFavs: PlantFavs) {

        favsRef = FirebaseService().getFavs(gardenerId)
        favsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val favs = snapshot.getValue<HashMap<String, PlantFavs>>()
                if(favs != null) {
                    checkDifferent = favs.filter { favs -> favs.key.equals(data.id, ignoreCase = true) }
                }
                    if (checkDifferent.isEmpty()) {
                        activity?.let { act ->

                            println("Vous pouvez ajouter ce favoris")
                            favs?.forEach { element ->
                                newFavs.put(element.key, element.value)
                            }
                            newFavs.put(data.id, plantFavs)
                            FirebaseService().getFavs(gardenerId).setValue(newFavs)
                            activity?.let { act ->
                                AlerterService.showGoodPlant(
                                    getString(R.string.tv_trad_util_1) + " ${data.name} ${getString(R.string.add_wishlist)}",
                                    act)
                                findNavController().popBackStack(R.id.mesPlantesFragment, false)
                            }
                        }
                    } else {
                        activity?.let { act ->
                            AlerterService.showError("${getString(R.string.alerter_already_garden_1)} ${data.name} " + getString(R.string.alerter_already_garden_2), act)
                        }
                    }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun showDatePickerDialog() {
        val cal = Calendar.getInstance()
        val d = cal.get(Calendar.DAY_OF_MONTH)
        val m = cal.get(Calendar.MONTH)
        val y = cal.get(Calendar.YEAR)
        datePicker = DatePickerHelper(requireContext(), false)
        val newMinDate = currentDate - minDate
        println("Ma currentDate : " + currentDate * 1000)
        println("Ma minDate : " + newMinDate * 1000)
        datePicker.setMinDate(newMinDate * 1000)
        datePicker.setMaxDate(currentDate * 1000)
        datePicker.showDialog(d, m, y, object : DatePickerHelper.Callback {
            override fun onDateSelected(dayofMonth: Int, month: Int, year: Int) {
                val dayStr = if (dayofMonth < 10) "0${dayofMonth}" else "${dayofMonth}"
                val mon = month + 1
                val monthStr = if (mon < 10) "0${mon}" else "${mon}"
                println("La date selectionnée est ${dayStr}-${monthStr}-${year}")
                val dateString = "${dayStr}-${monthStr}-${year}" //Date parsing
                val l = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                val unix = l.atStartOfDay(ZoneId.systemDefault()).toInstant().epochSecond
                val newDate = unix * 1000
                println("Timestamp convertit : " + newDate)
                println("Ma date timestamp : " + Date().time)
                val monthChoiceFormated = monthStr.toCharArray()
                if(monthChoiceFormated[0].toString() == "0") {
                    println("Le mois choisis est : " + monthChoiceFormated[1].toString())
                    choiceMonthString = monthChoiceFormated[1].toString()
                } else {
                    println("Le mois  choisis est : " + monthStr)
                    choiceMonthString = monthStr
                }
                profilViewModel.userService.gardenerId.let { guid ->
                    val dateSplited = dayStr.toCharArray()
                    val dateMonthSplited = monthStr.toCharArray()

                    if(dateSplited[0].toString() == "0") {

                        if(::choiceMonthString.isInitialized) {
                            println("Le mois selectionné est le ${choiceMonthString}, le mois actuel est ${profilViewModel.userService.currentMonth} et le jour actuel est ${profilViewModel.userService.currentDay}")
                        }
                        if(choiceMonthString.toString() == profilViewModel.userService.currentMonth.toString()) {
                            profilViewModel.userService.currentDay?.let {

                                val newTest = -dateSplited[1].toString().toDouble() + it.toDouble()
                                val addPlants = PlantsToAddPreviousDate(data.id, binding.etNameOfPlantsToRename.text.toString(), newTest)

                                FirebaseService().getGardenerById(guid!!)
                                    .child("plantingToAddPreviousDate")
                                    .child(data.etage + "-" + data.nbEtage).setValue(addPlants)
                                    .addOnSuccessListener {
                                        activity?.let {
                                            AlerterService.showGood(context?.getString(R.string.SUCCESS_PLANTING_ADD)!!, it)
                                            findNavController().popBackStack(
                                                R.id.mesPlantesFragment,
                                                false
                                            )
                                        }
                                    }.addOnFailureListener {
                                        activity?.let {
                                            AlerterService.showError(
                                                context?.getString(R.string.ECHEC_PLANT_ADD)!!,
                                                it
                                            )
                                        }
                                    }
                            }
                        } else if(choiceMonthString < profilViewModel.userService.currentMonth.toString()) {
                                val diffBetweenMonth = profilViewModel.userService.currentMonth?.toInt()?.minus(choiceMonthString.toInt())
                                val nbdeJour = profilViewModel.userService.currentDay?.toInt()?.let { diffBetweenMonth?.toInt()?.times(30)?.plus(it)
                                    ?.minus(dateSplited[1].toString().toInt()) }
                                val newTest = nbdeJour!!.toDouble()!!
                            val addPlants = PlantsToAddPreviousDate(
                                data.id,
                                binding.etNameOfPlantsToRename.text.toString(),
                                newTest
                            )
                            FirebaseService().getGardenerById(guid!!)
                                .child("plantingToAddPreviousDate")
                                .child(data.etage + "-" + data.nbEtage).setValue(addPlants)
                                .addOnSuccessListener {
                                    activity?.let {
                                        AlerterService.showGood(
                                            context?.getString(R.string.SUCCESS_PLANTING_ADD)!!,
                                            it
                                        )
                                        findNavController().popBackStack(
                                            R.id.mesPlantesFragment,
                                            false
                                        )
                                    }
                                }.addOnFailureListener {
                                    activity?.let {
                                        AlerterService.showError(
                                            context?.getString(R.string.ECHEC_PLANT_ADD)!!,
                                            it
                                        )
                                    }
                                }
                        } else {
                            val diffBetweenMonth = profilViewModel.userService.currentMonth?.toInt()?.minus(choiceMonthString.toInt())
                            val nbdeJour = profilViewModel.userService.currentDay?.toInt()?.let { diffBetweenMonth?.toInt()?.times(30)?.plus(it)
                                ?.minus(dateSplited[1].toString().toInt()) }
                            val newTest = nbdeJour!!.toDouble()!!
                            val addPlants = PlantsToAddPreviousDate(
                                data.id,
                                binding.etNameOfPlantsToRename.text.toString(),
                                newTest
                            )
                            FirebaseService().getGardenerById(guid!!)
                                .child("plantingToAddPreviousDate")
                                .child(data.etage + "-" + data.nbEtage).setValue(addPlants)
                                .addOnSuccessListener {
                                    activity?.let {
                                        AlerterService.showGood(
                                            context?.getString(R.string.SUCCESS_PLANTING_ADD)!!,
                                            it
                                        )
                                        findNavController().popBackStack(
                                            R.id.mesPlantesFragment,
                                            false
                                        )
                                    }
                                }.addOnFailureListener {
                                    activity?.let {
                                        AlerterService.showError(
                                            context?.getString(R.string.ECHEC_PLANT_ADD)!!,
                                            it
                                        )
                                    }
                                }
                        }


                    } else {
                        println("Nous avons pas de 0 car : " + dayStr)

                        if(::choiceMonthString.isInitialized) {
                            println("Le mois selectionné est le ${choiceMonthString}, le mois actuel est ${profilViewModel.userService.currentMonth} et le jour actuel est ${profilViewModel.userService.currentDay}")
                        }
                        if(choiceMonthString.toString() == profilViewModel.userService.currentMonth.toString()) {
                            profilViewModel.userService.currentDay?.let {
                                val newTest = - dayStr.toString().toDouble() + it.toDouble()


                                val addPlants = PlantsToAddPreviousDate(
                                    data.id,
                                    binding.etNameOfPlantsToRename.text.toString(),
                                    newTest
                                )
                                FirebaseService().getGardenerById(guid!!)
                                    .child("plantingToAddPreviousDate")
                                    .child(data.etage + "-" + data.nbEtage).setValue(addPlants)
                                    .addOnSuccessListener {
                                        activity?.let {
                                            AlerterService.showGood(
                                                context?.getString(R.string.SUCCESS_PLANTING_ADD)!!,
                                                it
                                            )
                                            findNavController().popBackStack(
                                                R.id.mesPlantesFragment,
                                                false
                                            )
                                        }
                                    }.addOnFailureListener {
                                        activity?.let {
                                            AlerterService.showError(
                                                context?.getString(R.string.ECHEC_PLANT_ADD)!!,
                                                it
                                            )
                                        }
                                    }
                            }
                        } else if(choiceMonthString < profilViewModel.userService.currentMonth.toString()) {
                            val diffBetweenMonth = profilViewModel.userService.currentMonth?.toInt()?.minus(choiceMonthString.toInt())
                            val nbdeJour = profilViewModel.userService.currentDay?.toInt()?.let { diffBetweenMonth?.toInt()?.times(30)?.plus(it)
                                ?.minus(dayStr.toInt()) }
                            val newTest = nbdeJour!!.toDouble()!!
                            val addPlants = PlantsToAddPreviousDate(
                                data.id,
                                binding.etNameOfPlantsToRename.text.toString(),
                                newTest
                            )
                            FirebaseService().getGardenerById(guid!!)
                                .child("plantingToAddPreviousDate")
                                .child(data.etage + "-" + data.nbEtage).setValue(addPlants)
                                .addOnSuccessListener {
                                    activity?.let {
                                        AlerterService.showGood(
                                            context?.getString(R.string.SUCCESS_PLANTING_ADD)!!,
                                            it
                                        )
                                        findNavController().popBackStack(
                                            R.id.mesPlantesFragment,
                                            false
                                        )
                                    }
                                }.addOnFailureListener {
                                    activity?.let {
                                        AlerterService.showError(
                                            context?.getString(R.string.ECHEC_PLANT_ADD)!!,
                                            it
                                        )
                                    }
                                }
                        } else {
                            val diffBetweenMonth = profilViewModel.userService.currentMonth?.toInt()?.minus(choiceMonthString.toInt()) // Mois actuel - Mois choisis
                            val nbdeJour = profilViewModel.userService.currentDay?.toInt()?.let { diffBetweenMonth?.toInt()?.times(30)?.plus(it) // résultat diffBetweenMonth * 30 + le jour actuel - le jour choisis
                                ?.minus(dayStr.toString().toInt()) }
                            val newTest = nbdeJour!!.toDouble()!!
                            val addPlants = PlantsToAddPreviousDate(
                                data.id,
                                binding.etNameOfPlantsToRename.text.toString(),
                                newTest
                            )
                            FirebaseService().getGardenerById(guid!!)
                                .child("plantingToAddPreviousDate")
                                .child(data.etage + "-" + data.nbEtage).setValue(addPlants)
                                .addOnSuccessListener {
                                    activity?.let {
                                        AlerterService.showGood(
                                            context?.getString(R.string.SUCCESS_PLANTING_ADD)!!,
                                            it
                                        )
                                        findNavController().popBackStack(
                                            R.id.mesPlantesFragment,
                                            false
                                        )
                                    }
                                }.addOnFailureListener {
                                    activity?.let {
                                        AlerterService.showError(
                                            context?.getString(R.string.ECHEC_PLANT_ADD)!!,
                                            it
                                        )
                                    }
                                }
                        }
                    }
                }
                //    setAddToPlantInFirebase()
            }
        })
        datePicker.dialog.getButton(DatePickerDialog.BUTTON_POSITIVE).stateListAnimator = null
        datePicker.dialog.getButton(DatePickerDialog.BUTTON_POSITIVE)
            .setBackgroundColor(resources.getColor(R.color.fui_transparent, null))
        datePicker.dialog.getButton(DatePickerDialog.BUTTON_POSITIVE)
            .setTextColor(resources.getColor(R.color.green_plantr, null))
        datePicker.dialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).stateListAnimator = null
        datePicker.dialog.getButton(DatePickerDialog.BUTTON_NEGATIVE)
            .setBackgroundColor(resources.getColor(R.color.fui_transparent, null))
        datePicker.dialog.getButton(DatePickerDialog.BUTTON_NEGATIVE)
            .setTextColor(resources.getColor(R.color.green_plantr, null))
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
    }

}
