package fr.devid.plantR.ui.subscribe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import fr.devid.plantR.Constants
import fr.devid.plantR.R
import fr.devid.plantR.base.BaseFragment
import fr.devid.plantR.databinding.FragmentMesPlantesSubscribeBinding
import fr.devid.plantR.manager.PagerManager
import fr.devid.plantR.models.GardenerStage
import fr.devid.plantR.services.FirebaseService
import fr.devid.plantR.ui.home.ProfileViewModel
import fr.devid.plantR.ui.myPlants.*
import javax.inject.Inject

class MesPlantesSubscribeFragment : BaseFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val profilViewModel: ProfileViewModel by activityViewModels { viewModelFactory }
    private lateinit var binding: FragmentMesPlantesSubscribeBinding
    private val data: MesPlantesSubscribeFragmentArgs by navArgs()
    private val adapter by lazy { PagerManager(childFragmentManager) }
    private lateinit var gardenerStageRef: DatabaseReference
    private lateinit var handleGardenerStageRef: ValueEventListener
    val arrayContains = arrayListOf<String>()
    private lateinit var mSpinnerEtagesOrRangs: Spinner
    private var selectSpinnerEtagesOrRangs : Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMesPlantesSubscribeBinding.inflate(inflater, container, false)

        if(data.gardenerRangs != null) {
            binding.clSpinner.visibility = View.VISIBLE
            mSpinnerEtagesOrRangs = binding.spinnerRangsEtages
            // We have here a layout which is stored in a variable in an arrayAdapter, this one is then used to store the name of the planters there.
            var adapterEtagesOrRangs = ArrayAdapter(requireActivity(), R.layout.view_drop_down_menu_spinner, mutableListOf<String>())
            mSpinnerEtagesOrRangs.adapter = adapterEtagesOrRangs
            mSpinnerEtagesOrRangs.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>, view: View,
                    position: Int, id: Long
                ) {
                    if(position == 0 ) {
                        println("Selection : " + Constants.arrayRangsOrStages.get(0))
                        selectSpinnerEtagesOrRangs = 0
                        setupTabLayout((data.gardenerStage),  data.gardenerType)
                    }
                    if(position == 1) {
                        println("Selection : " + Constants.arrayRangsOrStages.get(1))
                        selectSpinnerEtagesOrRangs = 1
                        setupTabLayoutWithRangs((data.gardenerStage),  data.gardenerType)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }

            var adapterRangs = mSpinnerEtagesOrRangs.adapter as ArrayAdapter<String>
            adapterRangs.clear()
            var arrayStringRangs = Constants.arrayRangsOrStages
            adapterRangs.addAll(arrayStringRangs)
            println("Selection : " + Constants.arrayRangsOrStages.get(0))
        } else {
            binding.clSpinner.visibility = View.GONE
            println("Le spinner est normalement invisible")
            if(data.gardenerStage != null) {
                setupTabLayout((data.gardenerStage), data.gardenerType)
            }
        }
        initView()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        println("ONRESUME")
        setGardenerObserver(data.garedenerId)
        profilViewModel.currentPlantPageService.currentPlantModel = emptyList()
    }

    private fun setGardenerObserver(guid : String) {
        gardenerStageRef = FirebaseService().getGardenerById(guid)
        handleGardenerStageRef = gardenerStageRef.addValueEventListener(object :
            ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                var gardenerDataStage = snapshot.getValue(GardenerStage::class.java)
                if (gardenerDataStage != null) {
                    setupTabLayout(gardenerDataStage.stage, "")

                }
            }
        })
    }
    private fun setupTabLayoutWithRangs(etage: String , type : String) {
        when (etage) {
            "4" -> {
                if (type != null) {
                    when (type) {
                        "parcelle" -> {
                            binding.tlPlants.clearOnTabSelectedListeners()
                            adapter.rangs()
                            adapter.addFragmentPage(FragmentTwoSubscribeRangs(data.garedenerId, true), getString(R.string.rangee_two))
                            adapter.addFragmentPage(FragmentOneSubscribeRangs(data.garedenerId, true), getString(R.string.rangee_one))
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


    private fun setupTabLayout(etage: String, type : String) {
        binding.tvPlantTeamUser.text = "${data.garderName}"
        println("******* TYPAGE ICI PRESENT : " + type)
        println("******* ETAGE ICI PRESENT : " + etage)
        when (etage) {
            "1" -> {
                if(type != null) {
                    when(type) {
                        "pot" -> {
                            adapter.resetAll()
                            profilViewModel.userService.gardenerName?.let {name ->
                                adapter.addFragmentPage(FragmentOneSubscribe(data.garedenerId, "0"), "${name.toUpperCase()}")
                            }
                            binding.tlPlants.setupWithViewPager(binding.vpPlant)
                            binding.vpPlant.offscreenPageLimit = 0; //before setAdapter
                            binding.vpPlant.adapter = adapter
                            binding.tlPlants.getTabAt(0)?.setIcon(R.drawable.mes_plantes_pot) // IL ME LE FAUDRA EN BLANC SI Y A UNE FEATURE
                        }
                        "capteur_pot" -> {
                            adapter.resetAll()
                            profilViewModel.userService.gardenerName?.let {name ->
                                adapter.addFragmentPage(FragmentOneSubscribe(data.garedenerId, "0"), "${name.toUpperCase()}")
                            }
                            binding.tlPlants.setupWithViewPager(binding.vpPlant)
                            binding.vpPlant.offscreenPageLimit = 0; //before setAdapter
                            binding.vpPlant.adapter = adapter
                            binding.tlPlants.getTabAt(0)?.setIcon(R.drawable.mes_plantes_kit_capteur_pot) // IL ME LE FAUDRA EN BLANC SI Y A UNE FEATURE
                        }
                        "jardiniere" -> {
                            adapter.resetAll()
                            profilViewModel.userService.gardenerName?.let {name ->
                                adapter.addFragmentPage(FragmentOneSubscribe(data.garedenerId, "0"), "${name.toUpperCase()}")
                            }
                            binding.tlPlants.setupWithViewPager(binding.vpPlant)
                            binding.vpPlant.offscreenPageLimit = 0; //before setAdapter
                            binding.vpPlant.adapter = adapter
                            binding.tlPlants.getTabAt(0)?.setIcon(R.drawable.mes_plantes_jardiniere) // IL ME LE FAUDRA EN BLANC SI Y A UNE FEATURE
                        }
                        "capteur_jardiniere" -> {
                            adapter.resetAll()
                            profilViewModel.userService.gardenerName?.let {name ->
                                adapter.addFragmentPage(FragmentOneSubscribe(data.garedenerId, "0"), "${name.toUpperCase()}")
                            }
                            binding.tlPlants.setupWithViewPager(binding.vpPlant)
                            binding.vpPlant.offscreenPageLimit = 0; //before setAdapter
                            binding.vpPlant.adapter = adapter
                            binding.tlPlants.getTabAt(0)?.setIcon(R.drawable.mes_plantes_kit_capteur_jardiniere) // IL ME LE FAUDRA EN BLANC SI Y A UNE FEATURE
                        }
                        "mural" -> {
                            adapter.resetAll()
                            adapter.addFragmentPage(FragmentOneSubscribe("0", "0"), getString(R.string.etage_one))
                            binding.tlPlants.setupWithViewPager(binding.vpPlant)
                            binding.vpPlant.offscreenPageLimit = 0; //before setAdapter
                            binding.vpPlant.adapter = adapter
                            binding.tlPlants.getTabAt(0)?.setIcon(R.drawable.mes_plantes_kit_mural) // IL ME LE FAUDRA EN BLANC SI Y A UNE FEATURE
                        }
                    }
                }
            }
            "2" -> {
                if(type != null) {
                    when(type) {
                        "cle_en_main" -> {
                            adapter.resetAll()
                            adapter.addFragmentPage(FragmentTwoSubscribe(data.garedenerId, "0"), getString(R.string.etage_two))
                            adapter.addFragmentPage(FragmentOneSubscribe(data.garedenerId, "1"), getString(R.string.etage_one))
                            binding.tlPlants.setupWithViewPager(binding.vpPlant)
                            binding.vpPlant.offscreenPageLimit = 0; //before setAdapter
                            binding.vpPlant.adapter = adapter
                            binding.tlPlants.getTabAt(0)?.setIcon(R.drawable.etage2_2_white) // IL ME LE FAUDRA EN BLANC SI Y A UNE FEATURE
                            binding.tlPlants.getTabAt(1)?.setIcon(R.drawable.etage2_1_white) // IL ME LE FAUDRA EN BLANC SI Y A UNE FEATURE
                        }
                        "carre" -> {
                            adapter.resetAll()
                            adapter.addFragmentPage(FragmentTwoSubscribe(data.garedenerId, "0"), getString(R.string.rangee_two))
                            adapter.addFragmentPage(FragmentOneSubscribe(data.garedenerId, "1"), getString(R.string.rangee_one))
                            binding.tlPlants.setupWithViewPager(binding.vpPlant)
                            binding.vpPlant.offscreenPageLimit = 1; //before setAdapter
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
                            adapter.addFragmentPage(FragmentThreeSubscribe(data.garedenerId, "0"), getString(R.string.rangee_three))
                            adapter.addFragmentPage(FragmentTwoSubscribe(data.garedenerId, "1"), getString(R.string.rangee_two))
                            adapter.addFragmentPage(FragmentOneSubscribe(data.garedenerId, "2"), getString(R.string.rangee_one))
                            binding.tlPlants.setupWithViewPager(binding.vpPlant)
                            binding.vpPlant.offscreenPageLimit = 0; //before setAdapter
                            binding.vpPlant.adapter = adapter
                            binding.tlPlants.getTabAt(2)?.setIcon(R.drawable.rang3_3)
                            binding.tlPlants.getTabAt(1)?.setIcon(R.drawable.rang2_3)
                            binding.tlPlants.getTabAt(0)?.setIcon(R.drawable.rang1_3)
                        }
                        "cle_en_main" -> {
                            adapter.resetAll()
                            adapter.addFragmentPage(FragmentThreeSubscribe(data.garedenerId, "0"), getString(R.string.etage_three))
                            adapter.addFragmentPage(FragmentTwoSubscribe(data.garedenerId, "1"), getString(R.string.etage_two))
                            adapter.addFragmentPage(FragmentOneSubscribe(data.garedenerId, "2"), getString(R.string.etage_one))
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
                            adapter.addFragmentPage(FragmentFourthSubscribe(data.garedenerId, "0"), getString(R.string.rangee_fourth))
                            adapter.addFragmentPage(FragmentThreeSubscribe(data.garedenerId, "1"), getString(R.string.rangee_three))
                            adapter.addFragmentPage(FragmentTwoSubscribe(data.garedenerId, "2"), getString(R.string.rangee_two))
                            adapter.addFragmentPage(FragmentOneSubscribe(data.garedenerId, "3"), getString(R.string.rangee_one))
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
                            adapter.addFragmentPage(FragmentFourthSubscribe(data.garedenerId, "0"), getString(R.string.etage_fourth))
                            adapter.addFragmentPage(FragmentThreeSubscribe(data.garedenerId, "1"), getString(R.string.etage_three))
                            adapter.addFragmentPage(FragmentTwoSubscribe(data.garedenerId, "2"), getString(R.string.etage_two))
                            adapter.addFragmentPage(FragmentOneSubscribe(data.garedenerId, "3"), getString(R.string.etage_one))
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
                            adapter.addFragmentPage(FragmentFourthSubscribe(data.garedenerId, "0"), getString(R.string.etage_fourth))
                            adapter.addFragmentPage(FragmentThreeSubscribe(data.garedenerId, "1"), getString(R.string.etage_three))
                            adapter.addFragmentPage(FragmentTwoSubscribe(data.garedenerId, "2"), getString(R.string.etage_two))
                            adapter.addFragmentPage(FragmentOneSubscribe(data.garedenerId, "3"), getString(R.string.etage_one))
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
        println("ONSTOP")
    }

    private fun initView() {

        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}