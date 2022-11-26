package fr.devid.plantR.ui.myPlants

import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import fr.devid.plantR.Constants
import fr.devid.plantR.R
import fr.devid.plantR.base.BaseFragment
import fr.devid.plantR.databinding.FragmentMesPlantesBinding
import fr.devid.plantR.manager.PagerManager
import fr.devid.plantR.models.PlantFavs
import fr.devid.plantR.services.AlerterService
import fr.devid.plantR.services.FirebaseService
import fr.devid.plantR.services.Singleton
import fr.devid.plantR.ui.home.ProfileViewModel
import fr.devid.plantR.viewmodels.Event
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

class MesPlantesFragment : BaseFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val profilViewModel: ProfileViewModel by activityViewModels { viewModelFactory }
    private lateinit var binding: FragmentMesPlantesBinding
    private val adapter by lazy { PagerManager(childFragmentManager) }
    private lateinit var favsRef : DatabaseReference
    private lateinit var handleFavs : ValueEventListener
    private var tsLong = System.currentTimeMillis() / 1000
   val arrayContains = arrayListOf<String>()
    private lateinit var mSpinnerEtagesOrRangs: Spinner
    private var selectSpinnerEtagesOrRangs : Int = 0
    private lateinit var gardenerId : String
    private lateinit var type : String
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMesPlantesBinding.inflate(inflater, container, false)
        if(profilViewModel.userService.rangs != null) {
            println("salope")
            binding.clSpinner.visibility = View.VISIBLE
        mSpinnerEtagesOrRangs = binding.spinnerRangsEtages
        // We have here a layout which is stored in a variable in an arrayAdapter, this one is then used to store the name of the planters there.
        val adapterEtagesOrRangs = ArrayAdapter(requireActivity(), R.layout.view_drop_down_menu_spinner, mutableListOf<String>())
        mSpinnerEtagesOrRangs.adapter = adapterEtagesOrRangs
        mSpinnerEtagesOrRangs.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View,
                position: Int, id: Long
            ) {
                    if(position == 0 ) {
                        println("Selection 1: " + Constants.arrayRangsOrStages.get(0))
                        selectSpinnerEtagesOrRangs = 0
                        profilViewModel.userService.gardenerId?.let {
                            FirebaseService().getGardener(it) { result ->
                                profilViewModel.userService.typeGarden = result.type
                                profilViewModel.userService.stage = result.stage
                                setupTabLayout(result.stage, result.type)
                            }
                        }
                    }
                    if(position == 1) {
                        println("Selection 2: " + Constants.arrayRangsOrStages.get(1))
                        selectSpinnerEtagesOrRangs = 1
                        profilViewModel.userService.gardenerId?.let {
                            FirebaseService().getGardener(it) { result ->
                                profilViewModel.userService.typeGarden = result.type
                                profilViewModel.userService.stage = result.stage
                                setupTabLayoutWithRangs(result.stage, result.type)
                            }
                        }
                    }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        val adapterRangs = mSpinnerEtagesOrRangs.adapter as ArrayAdapter<String>
        adapterRangs.clear()
        val arrayStringRangs = Constants.arrayRangsOrStages
        adapterRangs.addAll(arrayStringRangs)
        println("Selection : " + Constants.arrayRangsOrStages.get(0))
        } else {
            binding.clSpinner.visibility = View.GONE
            profilViewModel.userService.gardenerId?.let {
                FirebaseService().getGardener(it) { result ->
                    println("Jpasse ici")
                    setupTabLayout(result.stage, result.type)
                }
            }
        }
        profilViewModel.userService.plantsOk = false
        retainInstance = true
        binding.addPlants.alpha = 0.2F
        AlerterService.showDialogLoaderPlant(requireContext(), true)
        Singleton.instance._plantsAddedCallBack.postValue(Event(false))
        initView()
        profilViewModel.userService.gardenerId?.let { guid ->
            gardenerId = guid
            FirebaseService().getGardener(gardenerId){
                type = it.type
            }
            checkFavsTrueOrFalse(guid)
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        println("MON STAGE EST : " + profilViewModel.userService.stage )
        println("OnRESUME")
        profilViewModel.currentPlantPageService.currentPlantModel = emptyList()
}

    private fun checkFavsTrueOrFalse(guid : String) {
            favsRef = FirebaseService().getFavs(guid)
            handleFavs = favsRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val checkFavs = snapshot.getValue<HashMap<String, PlantFavs>>()
                    if(checkFavs != null) {
                       val list = checkFavs.values.map {
                           it
                       }
                       setNotifFavs(list)
                    }
                }
                override fun onCancelled(error: DatabaseError) {

                }
            })

    }

    private fun setupTabLayoutWithRangs(etage: String, type : String) {
        when (etage) {
            "4" -> {
                if (type != null) {
                    when (type) {
                        "parcelle" -> {
                            binding.tlPlants.clearOnTabSelectedListeners()
                            adapter.rangs()
                            adapter.addFragmentPage(FragmentTwoRangs("4", true), getString(R.string.rangee_two))
                            adapter.addFragmentPage(FragmentOneRangs("5", true), getString(R.string.rangee_one))
                            binding.tlPlants.setupWithViewPager(binding.vpPlantRangs)
                            binding.vpPlant.visibility = View.GONE
                            binding.vpPlantRangs.visibility = View.VISIBLE
                            binding.vpPlantRangs.offscreenPageLimit = 1
                            binding.vpPlantRangs.adapter = adapter
                            binding.tlPlants.getTabAt(0)?.setIcon(R.drawable.rang1_2) // IL ME LE FAUDRA EN BLANC SI Y A UNE FEATURE
                            binding.tlPlants.getTabAt(1)?.setIcon(R.drawable.rang2_2) // IL ME LE FAUDRA EN BLANC SI Y A UNE FEATURE
                        }
                    }
                }
            }
        }
    }

    private fun setupTabLayout(etage: String, type: String) {
        println("Etage donnée : " + etage)
        when (etage) {
            "1" -> {
                if (type != null) {
            when(type) {
                "pot" -> {
                    adapter.resetAll()
                    profilViewModel.userService.gardenerName?.let {name ->
                    adapter.addFragmentPage(FragmentOne("0", false), "${name.toUpperCase()?:"Pot"}")
                    }
                    binding.tlPlants.setupWithViewPager(binding.vpPlant)
                    binding.vpPlant.offscreenPageLimit = 0; //before setAdapter
                    binding.vpPlant.adapter = adapter
                    binding.tlPlants.getTabAt(0)?.setIcon(R.drawable.mes_plantes_pot) // IL ME LE FAUDRA EN BLANC SI Y A UNE FEATURE
                }
                "capteur_pot" -> {
                    adapter.resetAll()
                    profilViewModel.userService.gardenerName?.let {name ->
                        adapter.addFragmentPage(FragmentOne("0", false), "${name.toUpperCase()?:"Kit pot"}")
                    }
                    binding.tlPlants.setupWithViewPager(binding.vpPlant)
                    binding.vpPlant.offscreenPageLimit = 0; //before setAdapter
                    binding.vpPlant.adapter = adapter
                    binding.tlPlants.getTabAt(0)?.setIcon(R.drawable.mes_plantes_kit_capteur_pot) // IL ME LE FAUDRA EN BLANC SI Y A UNE FEATURE
                }
                "jardiniere" -> {
                    println("Jardiniere simple type garden : " + type + " name :  jardiniere")
                    adapter.resetAll()
                    profilViewModel.userService.gardenerName?.let {name ->
                        adapter.addFragmentPage(FragmentOne("0", false), "${name.toUpperCase()?:"jardiniere"}")
                    }
                    binding.tlPlants.setupWithViewPager(binding.vpPlant)
                    binding.vpPlant.offscreenPageLimit = 0; //before setAdapter
                    binding.vpPlant.adapter = adapter
                    binding.tlPlants.getTabAt(0)?.setIcon(R.drawable.mes_plantes_jardiniere) // IL ME LE FAUDRA EN BLANC SI Y A UNE FEATURE
                  }
                "capteur_jardiniere" -> {
                    adapter.resetAll()
                    profilViewModel.userService.gardenerName?.let {name ->
                        adapter.addFragmentPage(FragmentOne("0", false), "${name.toUpperCase()?:"Jardinière"}")
                    }
                    binding.tlPlants.setupWithViewPager(binding.vpPlant)
                    binding.vpPlant.offscreenPageLimit = 0; //before setAdapter
                    binding.vpPlant.adapter = adapter
                    binding.tlPlants.getTabAt(0)?.setIcon(R.drawable.mes_plantes_kit_capteur_jardiniere) // IL ME LE FAUDRA EN BLANC SI Y A UNE FEATURE
                }
                "mural" -> {
                    adapter.resetAll()
                    profilViewModel.userService.gardenerName?.let {name ->
                        adapter.addFragmentPage(FragmentOne("0", false), "${name.toUpperCase()?:"Mural"}")
                    }
                    binding.tlPlants.setupWithViewPager(binding.vpPlant)
                    binding.vpPlant.offscreenPageLimit = 0; //before setAdapter
                    binding.vpPlant.adapter = adapter
                    binding.tlPlants.getTabAt(0)?.setIcon(R.drawable.mes_plantes_kit_mural) // IL ME LE FAUDRA EN BLANC SI Y A UNE FEATURE
                    }
                }
            }
        }
            "2" -> {
                if (type != null) {
                    when(type) {
                        "cle_en_main" -> {
                            adapter.resetAll()
                            adapter.addFragmentPage(FragmentTwo("0", false), getString(R.string.etage_two))
                            adapter.addFragmentPage(FragmentOne("1", false), getString(R.string.etage_one))
                            binding.tlPlants.setupWithViewPager(binding.vpPlant)
                            binding.vpPlant.offscreenPageLimit = 0; //before setAdapter
                            binding.vpPlant.adapter = adapter
                            binding.tlPlants.getTabAt(0)?.setIcon(R.drawable.etage2_2_white) // IL ME LE FAUDRA EN BLANC SI Y A UNE FEATURE
                            binding.tlPlants.getTabAt(1)?.setIcon(R.drawable.etage2_1_white) // IL ME LE FAUDRA EN BLANC SI Y A UNE FEATURE
                        }
                        "carre" -> {
                            adapter.resetAll()
                            adapter.addFragmentPage(FragmentTwo("0", false), getString(R.string.rangee_two))
                            adapter.addFragmentPage(FragmentOne("1", false), getString(R.string.rangee_one))
                            binding.tlPlants.setupWithViewPager(binding.vpPlant)
                            binding.vpPlant.offscreenPageLimit = 1; //before setAdapter
                            binding.vpPlant.adapter = adapter
                            binding.tlPlants.getTabAt(0)?.setIcon(R.drawable.rang1_2) // IL ME LE FAUDRA EN BLANC SI Y A UNE FEATURE
                            binding.tlPlants.getTabAt(1)?.setIcon(R.drawable.rang2_2) // IL ME LE FAUDRA EN BLANC SI Y A UNE FEATURE
                        }
                        "capteur_carre" -> {
                            adapter.resetAll()
                            adapter.addFragmentPage(FragmentTwo("0", false), getString(R.string.rangee_two))
                            adapter.addFragmentPage(FragmentOne("1", false), getString(R.string.rangee_one))
                            binding.tlPlants.setupWithViewPager(binding.vpPlant)
                            binding.vpPlant.offscreenPageLimit = 0; //before setAdapter
                            binding.vpPlant.adapter = adapter
                            binding.tlPlants.getTabAt(0)?.setIcon(R.drawable.rang1_2) // IL ME LE FAUDRA EN BLANC SI Y A UNE FEATURE
                            binding.tlPlants.getTabAt(1)?.setIcon(R.drawable.rang2_2) // IL ME LE FAUDRA EN BLANC SI Y A UNE FEATURE
                        }
                    }
                }
            }
            "3" -> {
                if (type != null) {
                    when (type) {
                        "carre" -> {
                            adapter.resetAll()
                            adapter.addFragmentPage(FragmentThree("0", false), getString(R.string.rangee_three))
                            adapter.addFragmentPage(FragmentTwo("1", false), getString(R.string.rangee_two))
                            adapter.addFragmentPage(FragmentOne("2", false), getString(R.string.rangee_one))
                            binding.tlPlants.setupWithViewPager(binding.vpPlant)
                            binding.vpPlant.offscreenPageLimit = 0; //before setAdapter
                            binding.vpPlant.adapter = adapter
                            binding.tlPlants.getTabAt(2)?.setIcon(R.drawable.rang3_3)
                            binding.tlPlants.getTabAt(1)?.setIcon(R.drawable.rang2_3)
                            binding.tlPlants.getTabAt(0)?.setIcon(R.drawable.rang1_3)
                        }
                        "capteur_carre" -> {
                            adapter.resetAll()
                            adapter.addFragmentPage(FragmentThree("0", false), getString(R.string.rangee_three))
                            adapter.addFragmentPage(FragmentTwo("1", false), getString(R.string.rangee_two))
                            adapter.addFragmentPage(FragmentOne("2", false), getString(R.string.rangee_one))
                            binding.tlPlants.setupWithViewPager(binding.vpPlant)
                            binding.vpPlant.offscreenPageLimit = 0; //before setAdapter
                            binding.vpPlant.adapter = adapter
                            binding.tlPlants.getTabAt(2)?.setIcon(R.drawable.rang3_3)
                            binding.tlPlants.getTabAt(1)?.setIcon(R.drawable.rang2_3)
                            binding.tlPlants.getTabAt(0)?.setIcon(R.drawable.rang1_3)
                        }
                        "cle_en_main" -> {
                            adapter.resetAll()
                            adapter.addFragmentPage(FragmentThree("0", false), getString(R.string.etage_three))
                            adapter.addFragmentPage(FragmentTwo("1", false), getString(R.string.etage_two))
                            adapter.addFragmentPage(FragmentOne("2", false), getString(R.string.etage_one))
                            binding.tlPlants.setupWithViewPager(binding.vpPlant)
                            binding.vpPlant.offscreenPageLimit = 2; //before setAdapter
                            binding.vpPlant.adapter = adapter
                            binding.tlPlants.getTabAt(2)?.setIcon(R.drawable.etage3_1_white)
                            binding.tlPlants.getTabAt(1)?.setIcon(R.drawable.etage3_2_white)
                            binding.tlPlants.getTabAt(0)?.setIcon(R.drawable.etage3_3_white)

                        }
                    }
                }
            }
            "4" -> {
                if (type != null) {
                    when (type) {
                        "carre" -> {
                            adapter.resetAll()
                            adapter.addFragmentPage(FragmentFourth("0", false), getString(R.string.rangee_fourth))
                            adapter.addFragmentPage(FragmentThree("1", false), getString(R.string.rangee_three))
                            adapter.addFragmentPage(FragmentTwo("2", false), getString(R.string.rangee_two))
                            adapter.addFragmentPage(FragmentOne("3", false), getString(R.string.rangee_one))
                            binding.tlPlants.setupWithViewPager(binding.vpPlant)
                            binding.vpPlant.offscreenPageLimit = 0; //before setAdapter
                            binding.vpPlant.adapter = adapter
                            binding.tlPlants.getTabAt(3)?.setIcon(R.drawable.rang4_4)
                            binding.tlPlants.getTabAt(2)?.setIcon(R.drawable.rang3_4)
                            binding.tlPlants.getTabAt(1)?.setIcon(R.drawable.rang2_4)
                            binding.tlPlants.getTabAt(0)?.setIcon(R.drawable.rang1_4)
                        }
                        "capteur_carre" -> {
                            adapter.resetAll()
                            adapter.addFragmentPage(FragmentFourth("0", false), getString(R.string.rangee_fourth))
                            adapter.addFragmentPage(FragmentThree("1", false), getString(R.string.rangee_three))
                            adapter.addFragmentPage(FragmentTwo("2", false), getString(R.string.rangee_two))
                            adapter.addFragmentPage(FragmentOne("3", false), getString(R.string.rangee_one))
                            binding.tlPlants.setupWithViewPager(binding.vpPlant)
                            binding.vpPlant.offscreenPageLimit = 0; //before setAdapter
                            binding.vpPlant.adapter = adapter
                            binding.tlPlants.getTabAt(3)?.setIcon(R.drawable.rang4_4)
                            binding.tlPlants.getTabAt(2)?.setIcon(R.drawable.rang3_4)
                            binding.tlPlants.getTabAt(1)?.setIcon(R.drawable.rang2_4)
                            binding.tlPlants.getTabAt(0)?.setIcon(R.drawable.rang1_4)
                        }
                        "cle_en_main" -> {
                            adapter.resetAll()
                            adapter.addFragmentPage(FragmentFourth("0", false), getString(R.string.etage_fourth))
                            adapter.addFragmentPage(FragmentThree("1", false), getString(R.string.etage_three))
                            adapter.addFragmentPage(FragmentTwo("2", false), getString(R.string.etage_two))
                            adapter.addFragmentPage(FragmentOne("3", false), getString(R.string.etage_one))
                            binding.tlPlants.setupWithViewPager(binding.vpPlant)
                            binding.vpPlant.offscreenPageLimit = 3; //before setAdapter
                            binding.vpPlant.adapter = adapter
                            binding.tlPlants.getTabAt(3)?.setIcon(R.drawable.etage4_1_white)
                            binding.tlPlants.getTabAt(2)?.setIcon(R.drawable.etage4_2_white)
                            binding.tlPlants.getTabAt(1)?.setIcon(R.drawable.etage4_3_white)
                            binding.tlPlants.getTabAt(0)?.setIcon(R.drawable.etage4_4_white)
                        }
                        "parcelle" -> {
                            binding.vpPlantRangs.visibility = View.GONE
                            binding.vpPlant.visibility = View.VISIBLE
                            binding.tlPlants.clearOnTabSelectedListeners()
                            binding.vpPlant.clearOnPageChangeListeners()
                            adapter.resetAll()
                            adapter.addFragmentPage(FragmentFourth("0", false), getString(R.string.etage_fourth))
                            adapter.addFragmentPage(FragmentThree("1", false), getString(R.string.etage_three))
                            adapter.addFragmentPage(FragmentTwo("2", false), getString(R.string.etage_two))
                            adapter.addFragmentPage(FragmentOne("3", false), getString(R.string.etage_one))
                            binding.tlPlants.setupWithViewPager(binding.vpPlant)
                            binding.vpPlant.offscreenPageLimit = 3; //before setAdapter
                            binding.vpPlant.adapter = adapter
                            binding.tlPlants.getTabAt(3)?.setIcon(R.drawable.etage4_1_white)
                            binding.tlPlants.getTabAt(2)?.setIcon(R.drawable.etage4_2_white)
                            binding.tlPlants.getTabAt(1)?.setIcon(R.drawable.etage4_3_white)
                            binding.tlPlants.getTabAt(0)?.setIcon(R.drawable.etage4_4_white)
                        }
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        FirebaseService().removeCallBack()
        println("ONSTOP")
    }

    private fun initView() {



        binding.ivFavs.setOnClickListener {
            val stage = profilViewModel.userService.stage!!
            findNavController().navigate(MesPlantesFragmentDirections.actionMesPlantesFragmentToFragmentWishList(stage))
        }

        runOnUiThread(Runnable {
        Singleton.instance._plantsAddedCallBack.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { plantsAdded ->
                if (plantsAdded) {
                    AlerterService.hideDialog()
                    binding.addPlants.alpha = 1F
                    binding.addPlants.setOnClickListener {
                        var rangs = false
                        if (profilViewModel.userService.stage != null) {
                            when (profilViewModel.userService.stage!!.toInt()) {
                                4 -> {
                                    var etage = binding.tlPlants.selectedTabPosition
                                    if (selectSpinnerEtagesOrRangs == 1) {
                                        println("LA VALEUR EST EGAL A 1")
                                        println("Select spinner egal : " + selectSpinnerEtagesOrRangs)
                                        println("J'ajoute donc un element de type rangs sa mere")
                                        println("Je print DONC LETAGE : " + etage)
                                        println("Je print DONC LETAGE : " + etage)
                                        etage += 4
                                        rangs = true
                                        println("Etage apres changement : " + etage)
                                    }
                                    println("numero de l etage actuelle " + etage)
                                    val sortOfPlantList =
                                        profilViewModel.currentPlantPageService.currentPlantModel.filter {
                                            println("Id de plante : ${it.id.first()}\nEtage : ${etage.toString()}")
                                            it.id.first().toString() == etage.toString()
                                        }

                                    println("*************** RECUP DE LA LISTE ***************\n" + sortOfPlantList)

                                    val popupAddPlant = profilViewModel.userService.dimension?.let { it1 ->
                                                PopupAddPlant(
                                                    sortOfPlantList,
                                                    etage,
                                                    it1,
                                                    type,
                                                    requireContext()
                                                ) { popup, bool, namePlant, etage, nbEtage ->
                                                    if (bool) {
                                                        popup.dismiss()
                                                        if (nbEtage >= 0) {
                                                            if (rangs) {
                                                                if (findNavController().currentDestination?.id == R.id.mesPlantesFragment) {
                                                                    findNavController().navigate(
                                                                        MesPlantesFragmentDirections.actionMesPlantesFragmentToFragmentHomePlants(
                                                                            etage,
                                                                            nbEtage,
                                                                            rangs,
                                                                            binding.tlPlants.selectedTabPosition
                                                                        )
                                                                    )
                                                                }
                                                            } else {
                                                                if (findNavController().currentDestination?.id == R.id.mesPlantesFragment) {
                                                                    findNavController().navigate(
                                                                        MesPlantesFragmentDirections.actionMesPlantesFragmentToFragmentHomePlants(
                                                                            etage,
                                                                            nbEtage,
                                                                            false
                                                                        )
                                                                    )
                                                                }
                                                            }

                                                        }
                                                    } else {
                                                        AlerterService.showError(
                                                            "${context?.getString(R.string.PLACE_ALREADY_USE)} $namePlant",
                                                            requireActivity()
                                                        )
                                                    }
                                                }

                                        }

                                    val width =
                                        (resources.displayMetrics.widthPixels * 0.90).toInt()
                                    val height =
                                        (resources.displayMetrics.heightPixels * 0.90).toInt()
                                    if (popupAddPlant != null) {
                                        popupAddPlant.window?.setLayout(width, height)
                                    }
                                    if (popupAddPlant != null) {
                                        popupAddPlant.show()
                                    }
                                }
                                3 -> {
                                    var etage = binding.tlPlants.selectedTabPosition
                                    println("numero de l etage actuelle " + etage)
                                    val sortOfPlantList =
                                        profilViewModel.currentPlantPageService.currentPlantModel.filter {
                                            println("Id de plante : ${it.id.first()}\nEtage : ${etage.toString()}")
                                            it.id.first().toString() == etage.toString()
                                        }

                                    println("*************** RECUP DE LA LISTE ***************\n" + sortOfPlantList)

                                    val popupAddPlant =
                                        profilViewModel.userService.dimension?.let { it1 ->
                                                PopupAddPlant(
                                                    sortOfPlantList,
                                                    etage,
                                                    it1,
                                                    type,
                                                    requireContext()
                                                ) { popup, bool, namePlant, etage, nbEtage ->
                                                    if (bool) {
                                                        popup.dismiss()
                                                        if (nbEtage >= 0) {
                                                            if (rangs) {
                                                                if (findNavController().currentDestination?.id == R.id.mesPlantesFragment) {
                                                                    findNavController().navigate(
                                                                        MesPlantesFragmentDirections.actionMesPlantesFragmentToFragmentHomePlants(
                                                                            etage,
                                                                            nbEtage,
                                                                            rangs,
                                                                            binding.tlPlants.selectedTabPosition
                                                                        )
                                                                    )
                                                                }
                                                            } else {
                                                                if (findNavController().currentDestination?.id == R.id.mesPlantesFragment) {
                                                                    findNavController().navigate(
                                                                        MesPlantesFragmentDirections.actionMesPlantesFragmentToFragmentHomePlants(
                                                                            etage,
                                                                            nbEtage,
                                                                            false
                                                                        )
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    } else {
                                                        AlerterService.showError(
                                                            "${context?.getString(R.string.PLACE_ALREADY_USE)} $namePlant",
                                                            requireActivity()
                                                        )
                                                    }
                                                }

                                        }

                                    val width =
                                        (resources.displayMetrics.widthPixels * 0.90).toInt()
                                    val height =
                                        (resources.displayMetrics.heightPixels * 0.90).toInt()
                                    if (popupAddPlant != null) {
                                        popupAddPlant.window?.setLayout(width, height)
                                    }
                                    if (popupAddPlant != null) {
                                        popupAddPlant.show()
                                    }
                                }
                                2 -> {
                                    var etage = binding.tlPlants.selectedTabPosition
                                    println("numero de l etage actuelle $etage")
                                    val sortOfPlantList =
                                        profilViewModel.currentPlantPageService.currentPlantModel.filter {
                                            println("Id de plante : ${it.id.first()}\nEtage : ${etage.toString()}")
                                            it.id.first().toString() == etage.toString()
                                        }

                                    println("*************** RECUP DE LA LISTE ***************\n$ sortOfPlantList")

                                    val popupAddPlant =
                                        profilViewModel.userService.dimension?.let { it1 ->
                                                PopupAddPlant(
                                                    sortOfPlantList,
                                                    etage,
                                                    it1,
                                                    type,
                                                    requireContext()
                                                ) { popup, bool, namePlant, etage, nbEtage ->
                                                    if (bool) {
                                                        popup.dismiss()
                                                        if (nbEtage >= 0) {
                                                            if (rangs) {
                                                                if (findNavController().currentDestination?.id == R.id.mesPlantesFragment) {
                                                                    findNavController().navigate(
                                                                        MesPlantesFragmentDirections.actionMesPlantesFragmentToFragmentHomePlants(
                                                                            etage,
                                                                            nbEtage,
                                                                            rangs,
                                                                            binding.tlPlants.selectedTabPosition
                                                                        )
                                                                    )
                                                                }
                                                            } else {
                                                                if (findNavController().currentDestination?.id == R.id.mesPlantesFragment) {
                                                                    findNavController().navigate(
                                                                        MesPlantesFragmentDirections.actionMesPlantesFragmentToFragmentHomePlants(
                                                                            etage,
                                                                            nbEtage,
                                                                            false
                                                                        )
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    } else {
                                                        AlerterService.showError(
                                                            "${context?.getString(R.string.PLACE_ALREADY_USE)} $namePlant",
                                                            requireActivity()
                                                        )
                                                    }
                                                }

                                        }

                                    val width =
                                        (resources.displayMetrics.widthPixels * 0.90).toInt()
                                    val height =
                                        (resources.displayMetrics.heightPixels * 0.90).toInt()
                                    if (popupAddPlant != null) {
                                        popupAddPlant.window?.setLayout(width, height)
                                    }
                                    if (popupAddPlant != null) {
                                        popupAddPlant.show()
                                    }
                                }
                                1 -> {
                                    var etage = binding.tlPlants.selectedTabPosition
                                    println("numero de l etage actuelle " + etage)
                                    val sortOfPlantList =
                                        profilViewModel.currentPlantPageService.currentPlantModel.filter {
                                            println("Id de plante : ${it.id.first()}\nEtage : ${etage.toString()}")
                                            it.id.first().toString() == etage.toString()
                                        }

                                    println("*************** RECUP DE LA LISTE ***************\n" + sortOfPlantList)

                                    val popupAddPlant =
                                        profilViewModel.userService.dimension?.let { it1 ->

                                                PopupAddPlant(
                                                    sortOfPlantList,
                                                    etage,
                                                    it1,
                                                    type,
                                                    requireContext()
                                                ) { popup, bool, namePlant, etage, nbEtage ->
                                                    if (bool) {
                                                        popup.dismiss()
                                                        if (nbEtage >= 0) {
                                                            if (rangs) {
                                                                if (findNavController().currentDestination?.id == R.id.mesPlantesFragment) {
                                                                    findNavController().navigate(
                                                                        MesPlantesFragmentDirections.actionMesPlantesFragmentToFragmentHomePlants(
                                                                            etage,
                                                                            nbEtage,
                                                                            rangs,
                                                                            binding.tlPlants.selectedTabPosition
                                                                        )
                                                                    )
                                                                }
                                                            } else {
                                                                if (findNavController().currentDestination?.id == R.id.mesPlantesFragment) {
                                                                    findNavController().navigate(
                                                                        MesPlantesFragmentDirections.actionMesPlantesFragmentToFragmentHomePlants(
                                                                            etage,
                                                                            nbEtage,
                                                                            false
                                                                        )
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    } else {
                                                        AlerterService.showError(
                                                            "${context?.getString(R.string.PLACE_ALREADY_USE)} $namePlant",
                                                            requireActivity()
                                                        )
                                                    }
                                                }

                                        }

                                    val width =
                                        (resources.displayMetrics.widthPixels * 0.90).toInt()
                                    val height =
                                        (resources.displayMetrics.heightPixels * 0.90).toInt()
                                    if (popupAddPlant != null) {
                                        popupAddPlant.window?.setLayout(width, height)
                                    }
                                    if (popupAddPlant != null) {
                                        popupAddPlant.show()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        })


        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun runOnUiThread(runnable: Runnable) {
        if (isAdded) requireActivity().runOnUiThread(runnable)
    }

    private fun setNotifFavs(favs : List<PlantFavs>) {
        favs.forEach {
            var startSowingFavs = it.sowingPeriod?.startMonth
            var endSowingFavs = it.sowingPeriod?.endMonth
            var startPlantingFavs = it.plantingPeriod?.startMonth
            var endPlantingFavs = it.plantingPeriod?.endMonth

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
            if(arrayContains.count() >= 1 ) {
                binding.ivNotification.visibility = View.VISIBLE
            } else {
                binding.ivNotification.visibility = View.GONE
            }
        }
    }
    
    fun getMonth(timestamp: Long): Int {
        val calendar = Calendar.getInstance(Locale.FRANCE)
        calendar.timeInMillis = timestamp * 1000L
        val month = DateFormat.format("MM", calendar).toString().toInt()
        return month
    }
}