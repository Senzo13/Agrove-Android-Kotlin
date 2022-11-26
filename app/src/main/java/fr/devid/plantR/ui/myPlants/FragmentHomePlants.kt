package fr.devid.plantR.ui.myPlants

import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import fr.devid.plantR.R
import fr.devid.plantR.base.BaseFragment
import fr.devid.plantR.databinding.FragmentHomePlantsBinding
import fr.devid.plantR.models.*
import fr.devid.plantR.services.FirebaseService
import fr.devid.plantR.ui.home.ProfileViewModel
import fr.devid.plantR.ui.splash.hideKeyboard
import kotlinx.android.synthetic.main.fragment_home_plants.*
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


/**
 * lG
 * **/

data class CategorieModels(var filtre: Categories)

class FragmentHomePlants() : BaseFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val profilViewModel: ProfileViewModel by activityViewModels { viewModelFactory }
    private val PAGE = "***** FragmentHomePlants *****"
    private val data: FragmentHomePlantsArgs by navArgs()
    private lateinit var binding: FragmentHomePlantsBinding
    private lateinit var userRef: DatabaseReference
    private lateinit var handleUser: ValueEventListener
    private lateinit var gardenerRef: DatabaseReference
    private lateinit var categoriesRef: DatabaseReference
    private lateinit var handleCategories: ValueEventListener
    private lateinit var handleGardener: ValueEventListener
    private var currentUser: String = ""
    private var listPlantOfStage: List<String> = emptyList()
    private lateinit var plantRef: DatabaseReference
    private lateinit var handlePlant: ValueEventListener
    private lateinit var adapterPlants: PlantsAdapter
    private lateinit var adapterCardsPlants: MesCardsPlantesAdapter
    private var tsLong = System.currentTimeMillis() / 1000
    private var ts = tsLong.toString()
    private var checkKeyboard: Boolean = false
    private lateinit var mSpinner: Spinner
    private var categorieArray = arrayListOf<HomeCategories>()
    private var categorieList : HashMap<String, Categorie>? = HashMap()
    private var pokos : MutableLiveData<String> = MutableLiveData("Empty")

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomePlantsBinding.inflate(inflater, container, false)
        mSpinner = binding.spinner
        val adapter = ArrayAdapter(
                requireActivity(),
                R.layout.view_drop_down_menu,
                mutableListOf<String>())
        mSpinner.adapter = adapter
        mSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                    parent: AdapterView<*>, view: View,
                    position: Int, id: Long) {
                categorieList?.let {
                    if(position == 0) {
                        println("Vous avez selectionné Empty")
                    }
                    checkKeyboard = true
                    pokos.value = it.keys.elementAt(position)
                    profilViewModel.userService.categorieIsSelected = it.keys.elementAt(position)
                    when(Locale.getDefault().language.toString()) {
                        "fr" -> {
                            when(it.values.elementAt(position).name.toLowerCase()) {
                            "toute les plantes" -> {
                                binding.tvSuggestPlantFilter.text = "> Toute les plantes"
                            } else -> {
                                binding.tvSuggestPlantFilter.text = "> ${it.values.elementAt(position).name}"
                            }
                            }
                        }
                        "en" -> {
                            when(it.values.elementAt(position).name.toLowerCase()) {
                                "fruits" -> {
                                    binding.tvSuggestPlantFilter.text = "> Fruits"
                                }
                                "dépolluantes" -> {
                                    binding.tvSuggestPlantFilter.text = "> Depolluting"
                                }
                                "aromates" -> {
                                    binding.tvSuggestPlantFilter.text = "> Herbs"
                                }
                                "légumes" -> {
                                    binding.tvSuggestPlantFilter.text = "> Vegetables"
                                }
                                "fleurs" -> {
                                    binding.tvSuggestPlantFilter.text = "> Flowers"
                                }
                                "médicinales" -> {
                                    binding.tvSuggestPlantFilter.text = "> Medicinal"
                                }
                                "mellifères" -> {
                                    binding.tvSuggestPlantFilter.text = "> Melliferous"
                                }
                                "all plants" -> {
                                    binding.tvSuggestPlantFilter.text = "> All Plants"
                                }
                            }
                        }
                        else -> {
                            when(it.values.elementAt(position).name.toLowerCase()) {
                                "fruits" -> {
                                    binding.tvSuggestPlantFilter.text = "> Fruits"
                                }
                                "dépolluantes" -> {
                                    binding.tvSuggestPlantFilter.text = "> Depolluting"
                                }
                                "aromates" -> {
                                    binding.tvSuggestPlantFilter.text = "> Herbs"
                                }
                                "légumes" -> {
                                    binding.tvSuggestPlantFilter.text = "> Vegetables"
                                }
                                "fleurs" -> {
                                    binding.tvSuggestPlantFilter.text = "> Flowers"
                                }
                                "médicinales" -> {
                                    binding.tvSuggestPlantFilter.text = "> Medicinal"
                                }
                                "mellifères" -> {
                                    binding.tvSuggestPlantFilter.text = "> Melliferous"
                                }
                                 "all plants" -> {
                                    binding.tvSuggestPlantFilter.text = "> All Plants"
                                }
                            }
                        }

                    }
                    println("categorie selectionné " + profilViewModel.userService.categorieIsSelected)
                    adapterPlants.setGardenerPlantsObserver(profilViewModel.userService.categorieIsSelected, "")
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        initView()
        parseGardenersList()
        return binding.root
    }

    override fun onStop() {
        super.onStop()
        removeListener()
        if (::adapterPlants.isInitialized) {
            adapterPlants.removeListener()
        }
    }

    private fun removeListener() {
        if (::userRef.isInitialized && ::handleUser.isInitialized)
            userRef.removeEventListener(handleUser)
        if (::gardenerRef.isInitialized && ::handleGardener.isInitialized)
            gardenerRef.removeEventListener(handleGardener)
        if (::plantRef.isInitialized && ::handlePlant.isInitialized)
            plantRef.removeEventListener(handlePlant)
    }

    private fun initView() {
        if(data.rangs) {
            println("DES RANGS SONT LA")
            when(profilViewModel.userService.stage) {
                "1" -> {
                    binding.tvNumberFullOrEmpty.text = listPlantOfStage.count().toString() + "/4"
                    val circularProgressBar = binding.ivProgressBar
                    circularProgressBar.progressMax = 4F
                    circularProgressBar.setProgressWithAnimation(
                        listPlantOfStage.count().toFloat(), 1000
                    )
                }
                "2" -> {
                    binding.tvNumberFullOrEmpty.text = listPlantOfStage.count().toString() + "/4"
                    val circularProgressBar = binding.ivProgressBar
                    circularProgressBar.progressMax = 4F
                    circularProgressBar.setProgressWithAnimation(
                        listPlantOfStage.count().toFloat(), 1000
                    )
                }
                "3" -> {
                    binding.tvNumberFullOrEmpty.text = listPlantOfStage.count().toString() + "/4"
                    val circularProgressBar = binding.ivProgressBar
                    circularProgressBar.progressMax = 4F
                    circularProgressBar.setProgressWithAnimation(
                        listPlantOfStage.count().toFloat(), 1000
                    )
                }
                "4" -> {
                    binding.tvNumberFullOrEmpty.text = listPlantOfStage.count().toString() + "/4"
                    val circularProgressBar = binding.ivProgressBar
                    circularProgressBar.progressMax = 4F
                    circularProgressBar.setProgressWithAnimation(
                        listPlantOfStage.count().toFloat(), 1000
                    )
                }
            }
        }

        binding.etSearchView.clearFocus()
        binding.etSearchView.setHintTextColor(resources.getColor(R.color.extra_light_black_plantr))
        setObserver()
        setUserObserver()
        println("timeStamp actuelle " + ts)
        getMonth(tsLong) //Donne la date du jour avec le timeStamp
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onResume() {
        super.onResume()
        setObserver()
    }

    private fun parseGardenersList() {
        categorieList?.clear()
        categorieArray.clear()
            categoriesRef = FirebaseService().getCategorie()
            categoriesRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val categorieData = dataSnapshot.getValue<HashMap<String, Categorie>>()
                    categorieData?.let { metadata ->
                        categorieArray.add(HomeCategories("Empty", getString(R.string.tv_all_plants)))
                        metadata.forEach {
                            categorieList!!.put("Empty", Categorie( getString(R.string.tv_all_plants)))
                            when(Locale.getDefault().language.toString()) {
                                "fr" -> {
                                    categorieArray.add(HomeCategories(it.key, it.value.name))
                                }
                                "en" -> {
                                    when(it.value.name.toLowerCase()) {
                                        "fruits" -> {
                                            categorieArray.add(HomeCategories(it.key, "Fruits"))
                                        }
                                        "dépolluantes" -> {
                                            categorieArray.add(HomeCategories(it.key, "Depolluting"))
                                        }
                                        "aromates" -> {
                                            categorieArray.add(HomeCategories(it.key, "Herbs"))
                                        }
                                        "légumes" -> {
                                            categorieArray.add(HomeCategories(it.key, "Vegetables"))
                                        }
                                        "fleurs" -> {
                                            categorieArray.add(HomeCategories(it.key, "Flowers"))
                                        }
                                        "médicinales" -> {
                                            categorieArray.add(HomeCategories(it.key, "Medicinal"))
                                        }
                                        "mellifères" -> {
                                            categorieArray.add(HomeCategories(it.key, "Melliferous"))
                                        }
                                    }
                                }
                                else -> {
                                    when(it.value.name.toLowerCase()) {
                                        "fruits" -> {
                                            categorieArray.add(HomeCategories(it.key, "Fruits"))
                                        }
                                        "dépolluantes" -> {
                                            categorieArray.add(HomeCategories(it.key, "Depolluting"))
                                        }
                                        "aromates" -> {
                                            categorieArray.add(HomeCategories(it.key, "Herbs"))
                                        }
                                        "légumes" -> {
                                            categorieArray.add(HomeCategories(it.key, "Vegetables"))
                                        }
                                        "fleurs" -> {
                                            categorieArray.add(HomeCategories(it.key, "Flowers"))
                                        }
                                        "médicinales" -> {
                                            categorieArray.add(HomeCategories(it.key, "Medicinal"))
                                        }
                                        "mellifères" -> {
                                            categorieArray.add(HomeCategories(it.key, "Melliferous"))
                                        }
                                    }
                                }

                            }
                            categorieList!!.put(it.key, Categorie(it.value.name))
                        }
                        val adapter = mSpinner.adapter as ArrayAdapter<String>
                        adapter.clear()
                        val arrayToList = categorieArray.map { it.name }
                        adapter.addAll(arrayToList)
                        println("categorieList " + categorieList)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
        }

    fun getMonth(timestamp: Long): Int {
        val calendar = Calendar.getInstance(Locale.FRANCE)
        calendar.timeInMillis = timestamp * 1000L
        val month = DateFormat.format("MM", calendar).toString().toInt()
        return month
    }

    private fun setView(currentPage: Int, currentGardener: String, stage: String) {
        println("Je recupere un bool : " + checkBetweenStartAndEnd(1,12,10))
        if(data.rangs) {
            if (stage == "4") {
                when (currentPage) {
                    0 -> {
                        setGardenerObserver("0", currentGardener)
                        binding.tvNumeroEtage.text = getString(R.string.tv_numero_rank_1)
                        binding.ivEtage.setImageResource(R.drawable.rang1_2)
                        binding.ivEtage!!.setColorFilter(
                            ContextCompat.getColor(requireContext(), R.color.green_plantr), android.graphics.PorterDuff.Mode.MULTIPLY);
                    }
                    1 -> {
                        setGardenerObserver("1", currentGardener)
                        binding.tvNumeroEtage.text =  getString(R.string.tv_numero_rank_2)
                        binding.ivEtage.setImageResource(R.drawable.rang2_2)
                        binding.ivEtage!!.setColorFilter(ContextCompat.getColor(requireContext(), R.color.green_plantr), android.graphics.PorterDuff.Mode.MULTIPLY);
                    }
                    2 -> {
                        setGardenerObserver("2", currentGardener)
                        binding.tvNumeroEtage.text =  getString(R.string.tv_numero_rank_2)
                        binding.ivEtage.setImageResource(R.drawable.rang2_2)
                        binding.ivEtage!!.setColorFilter(
                            ContextCompat.getColor(requireContext(), R.color.green_plantr), android.graphics.PorterDuff.Mode.MULTIPLY);
                    }
                    3 -> {
                        setGardenerObserver("3", currentGardener)
                        binding.tvNumeroEtage.text = getString(R.string.tv_numero_rank_1)
                        binding.ivEtage.setImageResource(R.drawable.rang1_2)
                        binding.ivEtage!!.setColorFilter(
                            ContextCompat.getColor(requireContext(), R.color.green_plantr), android.graphics.PorterDuff.Mode.MULTIPLY);
                    }
                    4 -> {
                        setGardenerObserver("4", currentGardener)
                        binding.tvNumeroEtage.text = getString(R.string.tv_numero_rank_2)
                        binding.tvEtage.text = getString(R.string.tv_rank)
                        binding.ivEtage.setImageResource(R.drawable.rang1_2)
                        binding.ivEtage!!.setColorFilter(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.green_plantr
                            ), android.graphics.PorterDuff.Mode.MULTIPLY
                        );
                    }
                    5 -> {
                        setGardenerObserver("5", currentGardener)
                        binding.tvNumeroEtage.text = getString(R.string.tv_numero_rank_1)
                        binding.tvEtage.text = getString(R.string.tv_rank)
                        binding.ivEtage.setImageResource(R.drawable.rang2_2)
                        binding.ivEtage!!.setColorFilter(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.green_plantr
                            ), android.graphics.PorterDuff.Mode.MULTIPLY
                        );
                    }
                    6 -> {
                        setGardenerObserver("6", currentGardener)
                        binding.tvNumeroEtage.text = getString(R.string.tv_numero_rank_1)
                        binding.ivEtage.setImageResource(R.drawable.rang1_2)
                        binding.ivEtage!!.setColorFilter(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.green_plantr
                            ), android.graphics.PorterDuff.Mode.MULTIPLY
                        );
                    }
                }
            }
        }
        when(profilViewModel.userService.typeGarden) {
            "pot" -> {
                when(profilViewModel.userService.dimension) {
                    0 -> {
                        when (currentPage) {
                            0 -> {
                                setGardenerObserver("0", currentGardener)
                                binding.tvNumeroEtage.text = getString(R.string.small)
                                binding.tvEtage.text = getString(R.string.name_menu_classic_2)
                                binding.ivEtage.setImageResource(R.drawable.mon_potager_pot_petit)
                            }
                        }
                    }
                    1 -> {
                        when (currentPage) {
                            0 -> {
                                setGardenerObserver("0", currentGardener)
                                binding.tvNumeroEtage.text = getString(R.string.medium)
                                binding.tvEtage.text = getString(R.string.name_menu_classic_2)
                                binding.ivEtage.setImageResource(R.drawable.mon_potager_pot_moyen)
                            }
                        }
                    }
                    2 -> {
                        when (currentPage) {
                            0 -> {
                                setGardenerObserver("0", currentGardener)
                                binding.tvNumeroEtage.text = getString(R.string.big)
                                binding.tvEtage.text = getString(R.string.name_menu_classic_2)
                                binding.ivEtage.setImageResource(R.drawable.mon_potager_pot_grand)
                            }
                        }
                    }
                }
            }
            "capteur_pot" -> {
                when(profilViewModel.userService.dimension) {
                    0 -> {
                        when (currentPage) {
                            0 -> {
                                setGardenerObserver("0", currentGardener)
                                binding.tvNumeroEtage.text = getString(R.string.small)
                                binding.tvEtage.text =  getString(R.string.name_menu_classic_2)
                                binding.ivEtage.setImageResource(R.drawable.kit_capteur_pot_petit)
                            }
                        }
                    }
                    1 -> {
                        when (currentPage) {
                            0 -> {
                                setGardenerObserver("0", currentGardener)
                                binding.tvNumeroEtage.text =  getString(R.string.medium)
                                binding.tvEtage.text = getString(R.string.name_menu_classic_2)
                                binding.ivEtage.setImageResource(R.drawable.kit_capteur_pot_moyen)
                            }
                        }
                    }
                    2 -> {
                        when (currentPage) {
                            0 -> {
                                setGardenerObserver("0", currentGardener)
                                binding.tvNumeroEtage.text =  getString(R.string.big)
                                binding.tvEtage.text = getString(R.string.name_menu_classic_2)
                                binding.ivEtage.setImageResource(R.drawable.kit_capteur_pot_grand)
                            }
                        }
                    }
                }
            }

            "capteur_jardiniere" -> {
                when(profilViewModel.userService.dimension) {
                    0 -> {
                        when (currentPage) {
                            0 -> {
                                setGardenerObserver("0", currentGardener)
                                binding.tvNumeroEtage.text = getString(R.string.small)
                                binding.tvEtage.text = getString(R.string.name_menu_classic_3)
                                binding.ivEtage.setImageResource(R.drawable.kit_capteur_jardiniere_petit)
                            }
                        }
                    }
                    1 -> {
                        when (currentPage) {
                            0 -> {
                                setGardenerObserver("0", currentGardener)
                                binding.tvNumeroEtage.text = getString(R.string.medium)
                                binding.tvEtage.text = getString(R.string.name_menu_classic_3)
                                binding.ivEtage.setImageResource(R.drawable.kit_capteur_jardiniere_moyen)
                            }
                        }
                    }
                    2 -> {
                        when (currentPage) {
                            0 -> {
                                setGardenerObserver("0", currentGardener)
                                binding.tvNumeroEtage.text = getString(R.string.big)
                                binding.tvEtage.text = getString(R.string.name_menu_classic_3)
                                binding.ivEtage.setImageResource(R.drawable.kit_capteur_jardiniere_grand)
                            }
                        }
                    }
                }

            }
            "mural" -> {
                when(profilViewModel.userService.dimension) {
                    2 -> {
                        when (currentPage) {
                            0 -> {
                                setGardenerObserver("0", currentGardener)
                                binding.tvNumeroEtage.text = "Kit"
                                binding.tvEtage.text = "Mural"
                                binding.ivEtage.setImageResource(R.drawable.mes_plantes_kit_mural)
                                binding.ivEtage.setColorFilter(ContextCompat.getColor(requireContext(), R.color.green_plantr), android.graphics.PorterDuff.Mode.MULTIPLY);
                            }
                        }
                    }
                }
            }

            "jardiniere" -> {
                when(profilViewModel.userService.dimension) {
                    0 -> {
                        when (currentPage) {
                            0 -> {
                                setGardenerObserver("0", currentGardener)
                                binding.tvNumeroEtage.text = getString(R.string.small)
                                binding.tvEtage.text = getString(R.string.name_menu_classic_3)
                                binding.ivEtage.setImageResource(R.drawable.mon_potager_jardiniere_grand)
                            }
                        }
                    }
                    1 -> {
                        when (currentPage) {
                            0 -> {
                                setGardenerObserver("0", currentGardener)
                                binding.tvNumeroEtage.text =getString(R.string.medium)
                                binding.tvEtage.text = getString(R.string.name_menu_classic_3)
                                binding.ivEtage.setImageResource(R.drawable.mon_potager_jardiniere_grand)
                            }
                        }
                    }
                    2 -> {
                        when (currentPage) {
                            0 -> {
                                setGardenerObserver("0", currentGardener)
                                binding.tvNumeroEtage.text = getString(R.string.big)
                                binding.tvEtage.text = getString(R.string.name_menu_classic_3)
                                binding.ivEtage.setImageResource(R.drawable.mon_potager_jardiniere_grand)
                            }
                        }
                    }
                }

            }
            "carre" -> {
                when(profilViewModel.userService.dimension) {
                    0 ->  {
                        when (currentPage) {
                            0 -> {
                                setGardenerObserver("0", currentGardener)
                                binding.tvNumeroEtage.text = getString(R.string.tv_numero_2)
                                binding.tvEtage.text = getString(R.string.tv_rank)
                                binding.ivEtage.setImageResource(R.drawable.rang1_2)
                                binding.ivEtage.setColorFilter(ContextCompat.getColor(requireContext(), R.color.green_plantr), android.graphics.PorterDuff.Mode.MULTIPLY);
                            }
                            1 -> {
                                setGardenerObserver("1", currentGardener)
                                binding.tvNumeroEtage.text = getString(R.string.tv_numero_1)
                                binding.tvEtage.text = getString(R.string.tv_rank)
                                binding.ivEtage.setImageResource(R.drawable.rang2_2)
                                binding.ivEtage.setColorFilter(ContextCompat.getColor(requireContext(), R.color.green_plantr), android.graphics.PorterDuff.Mode.MULTIPLY);
                            }
                        }
                    }
                    1 -> {
                        when (currentPage) {
                            0 -> {
                                setGardenerObserver("0", currentGardener)
                                binding.tvNumeroEtage.text =getString(R.string.tv_numero_3)
                                binding.tvEtage.text = getString(R.string.tv_rank)
                                binding.ivEtage.setImageResource(R.drawable.rang1_3)
                                binding.ivEtage.setColorFilter(ContextCompat.getColor(requireContext(), R.color.green_plantr), android.graphics.PorterDuff.Mode.MULTIPLY);
                            }
                            1 -> {
                                setGardenerObserver("1", currentGardener)
                                binding.tvNumeroEtage.text = getString(R.string.tv_numero_2)
                                binding.tvEtage.text = getString(R.string.tv_rank)
                                binding.ivEtage.setImageResource(R.drawable.rang2_3)
                                binding.ivEtage.setColorFilter(ContextCompat.getColor(requireContext(), R.color.green_plantr), android.graphics.PorterDuff.Mode.MULTIPLY);
                            }
                            2 -> {
                                setGardenerObserver("2", currentGardener)
                                binding.tvNumeroEtage.text = getString(R.string.tv_numero_1)
                                binding.tvEtage.text = getString(R.string.tv_rank)
                                binding.ivEtage.setImageResource(R.drawable.rang3_3)
                                binding.ivEtage.setColorFilter(ContextCompat.getColor(requireContext(), R.color.green_plantr), android.graphics.PorterDuff.Mode.MULTIPLY);
                            }
                        }
                    }
                    2 -> {
                        when (currentPage) {
                            0 -> {
                                setGardenerObserver("0", currentGardener)
                                binding.tvNumeroEtage.text =getString(R.string.tv_numero_4)
                                binding.tvEtage.text = getString(R.string.tv_rank)
                                binding.ivEtage.setImageResource(R.drawable.rang1_4)
                                binding.ivEtage.setColorFilter(ContextCompat.getColor(requireContext(), R.color.green_plantr), android.graphics.PorterDuff.Mode.MULTIPLY);

                            }
                            1 -> {
                                setGardenerObserver("1", currentGardener)
                                binding.tvNumeroEtage.text = getString(R.string.tv_numero_3)
                                binding.tvEtage.text = getString(R.string.tv_rank)
                                binding.ivEtage.setImageResource(R.drawable.rang2_4)
                                binding.ivEtage.setColorFilter(ContextCompat.getColor(requireContext(), R.color.green_plantr), android.graphics.PorterDuff.Mode.MULTIPLY);

                            }
                            2 -> {
                                setGardenerObserver("2", currentGardener)
                                binding.tvNumeroEtage.text = getString(R.string.tv_numero_2)
                                binding.tvEtage.text = getString(R.string.tv_rank)
                                binding.ivEtage.setImageResource(R.drawable.rang3_4)
                                binding.ivEtage.setColorFilter(ContextCompat.getColor(requireContext(), R.color.green_plantr), android.graphics.PorterDuff.Mode.MULTIPLY);

                            }
                            3 -> {
                                setGardenerObserver("3", currentGardener)
                                binding.tvNumeroEtage.text = getString(R.string.tv_numero_1)
                                binding.tvEtage.text = getString(R.string.tv_rank)
                                binding.ivEtage.setImageResource(R.drawable.rang4_4)
                                binding.ivEtage.setColorFilter(ContextCompat.getColor(requireContext(), R.color.green_plantr), android.graphics.PorterDuff.Mode.MULTIPLY);
                            }
                        }
                    }

                }
            }
            "capteur_carre" -> {
                when(profilViewModel.userService.dimension) {
                    0 ->  {
                        when (currentPage) {
                            0 -> {
                                setGardenerObserver("0", currentGardener)
                                binding.tvNumeroEtage.text = getString(R.string.tv_numero_2)
                                binding.tvEtage.text = getString(R.string.tv_rank)
                                binding.ivEtage.setImageResource(R.drawable.rang1_2)
                                binding.ivEtage.setColorFilter(ContextCompat.getColor(requireContext(), R.color.green_plantr), android.graphics.PorterDuff.Mode.MULTIPLY);
                            }
                            1 -> {
                                setGardenerObserver("1", currentGardener)
                                binding.tvNumeroEtage.text = getString(R.string.tv_numero_1)
                                binding.tvEtage.text = getString(R.string.tv_rank)
                                binding.ivEtage.setImageResource(R.drawable.rang2_2)
                                binding.ivEtage.setColorFilter(ContextCompat.getColor(requireContext(), R.color.green_plantr), android.graphics.PorterDuff.Mode.MULTIPLY);
                            }
                        }
                    }
                    1 -> {
                        when (currentPage) {
                            0 -> {
                                setGardenerObserver("0", currentGardener)
                                binding.tvNumeroEtage.text =getString(R.string.tv_numero_3)
                                binding.tvEtage.text = getString(R.string.tv_rank)
                                binding.ivEtage.setImageResource(R.drawable.rang1_3)
                                binding.ivEtage.setColorFilter(ContextCompat.getColor(requireContext(), R.color.green_plantr), android.graphics.PorterDuff.Mode.MULTIPLY);
                            }
                            1 -> {
                                setGardenerObserver("1", currentGardener)
                                binding.tvNumeroEtage.text = getString(R.string.tv_numero_2)
                                binding.tvEtage.text = getString(R.string.tv_rank)
                                binding.ivEtage.setImageResource(R.drawable.rang2_3)
                                binding.ivEtage.setColorFilter(ContextCompat.getColor(requireContext(), R.color.green_plantr), android.graphics.PorterDuff.Mode.MULTIPLY);
                            }
                            2 -> {
                                setGardenerObserver("2", currentGardener)
                                binding.tvNumeroEtage.text = getString(R.string.tv_numero_1)
                                binding.tvEtage.text = getString(R.string.tv_rank)
                                binding.ivEtage.setImageResource(R.drawable.rang3_3)
                                binding.ivEtage.setColorFilter(ContextCompat.getColor(requireContext(), R.color.green_plantr), android.graphics.PorterDuff.Mode.MULTIPLY);
                            }
                        }
                    }
                    2 -> {
                        when (currentPage) {
                            0 -> {
                                setGardenerObserver("0", currentGardener)
                                binding.tvNumeroEtage.text =getString(R.string.tv_numero_4)
                                binding.tvEtage.text = getString(R.string.tv_rank)
                                binding.ivEtage.setImageResource(R.drawable.rang1_4)
                                binding.ivEtage.setColorFilter(ContextCompat.getColor(requireContext(), R.color.green_plantr), android.graphics.PorterDuff.Mode.MULTIPLY);

                            }
                            1 -> {
                                setGardenerObserver("1", currentGardener)
                                binding.tvNumeroEtage.text = getString(R.string.tv_numero_3)
                                binding.tvEtage.text = getString(R.string.tv_rank)
                                binding.ivEtage.setImageResource(R.drawable.rang2_4)
                                binding.ivEtage.setColorFilter(ContextCompat.getColor(requireContext(), R.color.green_plantr), android.graphics.PorterDuff.Mode.MULTIPLY);

                            }
                            2 -> {
                                setGardenerObserver("2", currentGardener)
                                binding.tvNumeroEtage.text = getString(R.string.tv_numero_2)
                                binding.tvEtage.text = getString(R.string.tv_rank)
                                binding.ivEtage.setImageResource(R.drawable.rang3_4)
                                binding.ivEtage.setColorFilter(ContextCompat.getColor(requireContext(), R.color.green_plantr), android.graphics.PorterDuff.Mode.MULTIPLY);

                            }
                            3 -> {
                                setGardenerObserver("3", currentGardener)
                                binding.tvNumeroEtage.text = getString(R.string.tv_numero_1)
                                binding.tvEtage.text = getString(R.string.tv_rank)
                                binding.ivEtage.setImageResource(R.drawable.rang4_4)
                                binding.ivEtage.setColorFilter(ContextCompat.getColor(requireContext(), R.color.green_plantr), android.graphics.PorterDuff.Mode.MULTIPLY);
                            }
                        }
                    }

                }
            }
            "cle_en_main" -> {
                if (stage == "4") {
                    when (currentPage) {
                        3 -> {
                            setGardenerObserver("3", currentGardener)
                            binding.tvNumeroEtage.text = getString(R.string.tv_numero_1)
                            binding.ivEtage.setImageResource(R.drawable.floor1_green)
                        }
                        2 -> {
                            setGardenerObserver("2", currentGardener)
                            binding.tvNumeroEtage.text = getString(R.string.tv_numero_2)
                            binding.ivEtage.setImageResource(R.drawable.floor2_green)
                        }
                        1 -> {
                            setGardenerObserver("1", currentGardener)
                            binding.tvNumeroEtage.text = getString(R.string.tv_numero_3)
                            binding.ivEtage.setImageResource(R.drawable.floor3_green)
                        }
                        0 -> {
                            setGardenerObserver("0", currentGardener)
                            binding.tvNumeroEtage.text = getString(R.string.tv_numero_4)
                            binding.ivEtage.setImageResource(R.drawable.floor4_green)
                        }
                    }
                }
                if (stage == "3") {
                    when (currentPage) {
                        2 -> {
                            setGardenerObserver("2", currentGardener)
                            binding.tvNumeroEtage.text = getString(R.string.tv_numero_1)
                            binding.ivEtage.setImageResource(R.drawable.floor1_green)

                        }
                        1 -> {
                            setGardenerObserver("1", currentGardener)
                            binding.tvNumeroEtage.text = getString(R.string.tv_numero_2)
                            binding.ivEtage.setImageResource(R.drawable.floor2_green)

                        }
                        0 -> {
                            setGardenerObserver("1", currentGardener)
                            binding.tvNumeroEtage.text = getString(R.string.tv_numero_3)
                            binding.ivEtage.setImageResource(R.drawable.floor3_green)
                        }
                    }
                }
                if (stage == "2") {
                    when (currentPage) {
                        1 -> {
                            setGardenerObserver("1", currentGardener)
                            binding.tvNumeroEtage.text = getString(R.string.tv_numero_1)
                            binding.ivEtage.setImageResource(R.drawable.floor1_green)
                        }
                        0 -> {
                            setGardenerObserver("0", currentGardener)
                            binding.tvNumeroEtage.text = getString(R.string.tv_numero_2)
                            binding.ivEtage.setImageResource(R.drawable.floor2_green)

                        }
                    }
                }
                if (stage == "1") {
                    when (currentPage) {
                        0 -> {
                            setGardenerObserver("0", currentGardener)
                            binding.tvNumeroEtage.text = getString(R.string.tv_numero_1)
                            binding.ivEtage.setImageResource(R.drawable.floor1_green)
                        }
                    }
                }
            }
            "parcelle" -> {
                if (stage == "4") {
                    when (currentPage) {
                        3 -> {
                            setGardenerObserver("3", currentGardener)
                            binding.tvNumeroEtage.text = getString(R.string.tv_numero_1)
                            binding.ivEtage.setImageResource(R.drawable.floor1_green)
                        }
                        2 -> {
                            setGardenerObserver("2", currentGardener)
                            binding.tvNumeroEtage.text = getString(R.string.tv_numero_2)
                            binding.ivEtage.setImageResource(R.drawable.floor2_green)
                        }
                        1 -> {
                            setGardenerObserver("1", currentGardener)
                            binding.tvNumeroEtage.text = getString(R.string.tv_numero_3)
                            binding.ivEtage.setImageResource(R.drawable.floor3_green)
                        }
                        0 -> {
                            setGardenerObserver("0", currentGardener)
                            binding.tvNumeroEtage.text = getString(R.string.tv_numero_4)
                            binding.ivEtage.setImageResource(R.drawable.floor4_green)
                        }
                    }
                }
                if (stage == "3") {
                    when (currentPage) {
                        2 -> {
                            setGardenerObserver("2", currentGardener)
                            binding.tvNumeroEtage.text = getString(R.string.tv_numero_1)
                            binding.ivEtage.setImageResource(R.drawable.floor1_green)

                        }
                        1 -> {
                            setGardenerObserver("1", currentGardener)
                            binding.tvNumeroEtage.text = getString(R.string.tv_numero_2)
                            binding.ivEtage.setImageResource(R.drawable.floor2_green)

                        }
                        0 -> {
                            setGardenerObserver("1", currentGardener)
                            binding.tvNumeroEtage.text = getString(R.string.tv_numero_3)
                            binding.ivEtage.setImageResource(R.drawable.floor3_green)
                        }
                    }
                }
                if (stage == "2") {
                    when (currentPage) {
                        1 -> {
                            setGardenerObserver("1", currentGardener)
                            binding.tvNumeroEtage.text = getString(R.string.tv_numero_1)
                            binding.ivEtage.setImageResource(R.drawable.floor1_green)
                        }
                        0 -> {
                            setGardenerObserver("0", currentGardener)
                            binding.tvNumeroEtage.text =getString(R.string.tv_numero_2)
                            binding.ivEtage.setImageResource(R.drawable.floor2_green)

                        }
                    }
                }
                if (stage == "1") {
                    when (currentPage) {
                        0 -> {
                            setGardenerObserver("0", currentGardener)
                            binding.tvNumeroEtage.text = getString(R.string.tv_numero_1)
                            binding.ivEtage.setImageResource(R.drawable.floor1_green)
                        }
                    }
                }
            }
        }


    }

    private fun setUserObserver() {
        FirebaseService().firebase.currentUser?.uid?.let { uid ->
            userRef = FirebaseService().getUserByid(uid)
            handleUser = userRef.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    println("${PAGE} error ${error.toException()}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)?.let {
                        currentUser = it.currentGardener
                        if (profilViewModel.userService.stage != null) {
                            setView(
                                    data.etage, it.currentGardener,
                                    profilViewModel.userService.stage!!
                            )
                        }
                    }

                    println("currentUSER VAUT ACTUELLEMENT : " + currentUser)
                }
            })
        }
    }

    private fun setGardenerObserver(currentEtage: String, currentGardener: String) {
        gardenerRef = FirebaseService().getGardenerById(currentGardener)
        handleGardener = gardenerRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val plants = snapshot.getValue(GardenerPlant::class.java)?.let {
                    if(data.rangs) {
                        listPlantOfStage = it.plants.keys.filter {
                            val number = data.selectedRangsNumber +4
                            it.first().toString() == number.toString()
                        }
                    } else {
                        listPlantOfStage = it.plants.keys.filter {
                            it.first().toString() == currentEtage
                        }
                    }

                    println("NB DE PLANTE DANS MA JARDINIERE : " + listPlantOfStage.count().toString())
                    when(profilViewModel.userService.typeGarden) {
                        "carre" -> {
                            when(profilViewModel.userService.dimension) {
                                0 -> {
                                    binding.tvNumberFullOrEmpty.text = listPlantOfStage.count().toString() + "/2"
                                    val circularProgressBar = binding.ivProgressBar
                                    circularProgressBar.progressMax = 2F
                                    circularProgressBar.setProgressWithAnimation(
                                        listPlantOfStage.count().toFloat(), 1000
                                    )
                                }
                                1 -> {
                                    binding.tvNumberFullOrEmpty.text = listPlantOfStage.count().toString() + "/3"
                                    val circularProgressBar = binding.ivProgressBar
                                    circularProgressBar.progressMax = 3F
                                    circularProgressBar.setProgressWithAnimation(
                                        listPlantOfStage.count().toFloat(), 1000
                                    )
                                }
                                2 -> {
                                    binding.tvNumberFullOrEmpty.text = listPlantOfStage.count().toString() + "/4"
                                    val circularProgressBar = binding.ivProgressBar
                                    circularProgressBar.progressMax = 4F
                                    circularProgressBar.setProgressWithAnimation(
                                        listPlantOfStage.count().toFloat(), 1000
                                    )
                                }
                            }
                        }
                        "capteur_carre" -> {
                            when(profilViewModel.userService.dimension) {
                                0 -> {
                                    binding.tvNumberFullOrEmpty.text = listPlantOfStage.count().toString() + "/2"
                                    val circularProgressBar = binding.ivProgressBar
                                    circularProgressBar.progressMax = 2F
                                    circularProgressBar.setProgressWithAnimation(
                                        listPlantOfStage.count().toFloat(), 1000
                                    )
                                }
                                1 -> {
                                    binding.tvNumberFullOrEmpty.text = listPlantOfStage.count().toString() + "/3"
                                    val circularProgressBar = binding.ivProgressBar
                                    circularProgressBar.progressMax = 3F
                                    circularProgressBar.setProgressWithAnimation(
                                        listPlantOfStage.count().toFloat(), 1000
                                    )
                                }
                                2 -> {
                                    binding.tvNumberFullOrEmpty.text = listPlantOfStage.count().toString() + "/4"
                                    val circularProgressBar = binding.ivProgressBar
                                    circularProgressBar.progressMax = 4F
                                    circularProgressBar.setProgressWithAnimation(
                                        listPlantOfStage.count().toFloat(), 1000
                                    )
                                }
                            }
                        }
                        "pot" -> {
                            when(profilViewModel.userService.dimension) {
                                0 -> {
                                    binding.tvNumberFullOrEmpty.text = listPlantOfStage.count().toString() + "/1"
                                    val circularProgressBar = binding.ivProgressBar
                                    circularProgressBar.progressMax = 1F
                                    circularProgressBar.setProgressWithAnimation(
                                        listPlantOfStage.count().toFloat(), 1000
                                    )
                                }
                                1 -> {
                                    binding.tvNumberFullOrEmpty.text = listPlantOfStage.count().toString() + "/1"
                                    val circularProgressBar = binding.ivProgressBar
                                    circularProgressBar.progressMax = 1F
                                    circularProgressBar.setProgressWithAnimation(
                                        listPlantOfStage.count().toFloat(), 1000
                                    )
                                }
                                2 -> {
                                    binding.tvNumberFullOrEmpty.text = listPlantOfStage.count().toString() + "/1"
                                    val circularProgressBar = binding.ivProgressBar
                                    circularProgressBar.progressMax = 1F
                                    circularProgressBar.setProgressWithAnimation(
                                        listPlantOfStage.count().toFloat(), 1000
                                    )
                                }
                            }
                        }
                        "jardiniere" -> {
                            when(profilViewModel.userService.dimension) {
                                0 -> {
                                    binding.tvNumberFullOrEmpty.text = listPlantOfStage.count().toString() + "/2"
                                    val circularProgressBar = binding.ivProgressBar
                                    circularProgressBar.progressMax = 2F
                                    circularProgressBar.setProgressWithAnimation(
                                        listPlantOfStage.count().toFloat(), 1000
                                    )
                                }
                                1 -> {
                                    binding.tvNumberFullOrEmpty.text = listPlantOfStage.count().toString() + "/2"
                                    val circularProgressBar = binding.ivProgressBar
                                    circularProgressBar.progressMax = 2F
                                    circularProgressBar.setProgressWithAnimation(
                                        listPlantOfStage.count().toFloat(), 1000
                                    )
                                }
                                2 -> {
                                    binding.tvNumberFullOrEmpty.text = listPlantOfStage.count().toString() + "/4"
                                    val circularProgressBar = binding.ivProgressBar
                                    circularProgressBar.progressMax = 4F
                                    circularProgressBar.setProgressWithAnimation(
                                        listPlantOfStage.count().toFloat(), 1000
                                    )
                                }
                                else -> {

                                }
                            }
                        }
                        "mural" -> {
                            when(profilViewModel.userService.dimension) {
                                0 -> {
                                    binding.tvNumberFullOrEmpty.text = listPlantOfStage.count().toString() + "/2"
                                    val circularProgressBar = binding.ivProgressBar
                                    circularProgressBar.progressMax = 2F
                                    circularProgressBar.setProgressWithAnimation(
                                        listPlantOfStage.count().toFloat(), 1000
                                    )
                                }
                                1 -> {
                                    binding.tvNumberFullOrEmpty.text = listPlantOfStage.count().toString() + "/2"
                                    val circularProgressBar = binding.ivProgressBar
                                    circularProgressBar.progressMax = 2F
                                    circularProgressBar.setProgressWithAnimation(
                                        listPlantOfStage.count().toFloat(), 1000
                                    )
                                }
                                2 -> {
                                    binding.tvNumberFullOrEmpty.text = listPlantOfStage.count().toString() + "/4"
                                    val circularProgressBar = binding.ivProgressBar
                                    circularProgressBar.progressMax = 4F
                                    circularProgressBar.setProgressWithAnimation(
                                        listPlantOfStage.count().toFloat(), 1000
                                    )
                                }
                                else -> {

                                }
                            }
                        }
                        "cle_en_main" -> {
                            when(profilViewModel.userService.stage) {
                                "1" -> {
                                    binding.tvNumberFullOrEmpty.text = listPlantOfStage.count().toString() + "/4"
                                    val circularProgressBar = binding.ivProgressBar
                                    circularProgressBar.progressMax = 4F
                                    circularProgressBar.setProgressWithAnimation(
                                        listPlantOfStage.count().toFloat(), 1000
                                    )
                                }
                                "2" -> {
                                    binding.tvNumberFullOrEmpty.text = listPlantOfStage.count().toString() + "/4"
                                    val circularProgressBar = binding.ivProgressBar
                                    circularProgressBar.progressMax = 4F
                                    circularProgressBar.setProgressWithAnimation(
                                        listPlantOfStage.count().toFloat(), 1000
                                    )
                                }
                                "3" -> {
                                    binding.tvNumberFullOrEmpty.text = listPlantOfStage.count().toString() + "/4"
                                    val circularProgressBar = binding.ivProgressBar
                                    circularProgressBar.progressMax = 4F
                                    circularProgressBar.setProgressWithAnimation(
                                        listPlantOfStage.count().toFloat(), 1000
                                    )
                                }
                                "4" -> {
                                    binding.tvNumberFullOrEmpty.text = listPlantOfStage.count().toString() + "/4"
                                    val circularProgressBar = binding.ivProgressBar
                                    circularProgressBar.progressMax = 4F
                                    circularProgressBar.setProgressWithAnimation(listPlantOfStage.count().toFloat(), 1000)
                                }
                            }
                        }

                        "parcelle" -> {
                            when(profilViewModel.userService.stage) {
                                "1" -> {
                                    binding.tvNumberFullOrEmpty.text = listPlantOfStage.count().toString() + "/4"
                                    val circularProgressBar = binding.ivProgressBar
                                    circularProgressBar.progressMax = 4F
                                    circularProgressBar.setProgressWithAnimation(
                                        listPlantOfStage.count().toFloat(), 1000
                                    )
                                }
                                "2" -> {
                                    binding.tvNumberFullOrEmpty.text = listPlantOfStage.count().toString() + "/4"
                                    val circularProgressBar = binding.ivProgressBar
                                    circularProgressBar.progressMax = 4F
                                    circularProgressBar.setProgressWithAnimation(
                                        listPlantOfStage.count().toFloat(), 1000
                                    )
                                }
                                "3" -> {
                                    binding.tvNumberFullOrEmpty.text = listPlantOfStage.count().toString() + "/4"
                                    val circularProgressBar = binding.ivProgressBar
                                    circularProgressBar.progressMax = 4F
                                    circularProgressBar.setProgressWithAnimation(
                                        listPlantOfStage.count().toFloat(), 1000
                                    )
                                }
                                "4" -> {
                                    binding.tvNumberFullOrEmpty.text = listPlantOfStage.count().toString() + "/4"
                                    val circularProgressBar = binding.ivProgressBar
                                    circularProgressBar.progressMax = 4F
                                    circularProgressBar.setProgressWithAnimation(
                                        listPlantOfStage.count().toFloat(), 1000
                                    )
                                }
                            }
                        }
                    }


                }
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun searchView(Adapter: PlantsAdapter) {

        binding.etSearchView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                println("AFTER")

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                println("BEFORE")

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                println("CHANGED")
                if (s.toString().count() > 0) {
                    binding.clSearchView.visibility = View.VISIBLE
                    binding.scrollViewSuggest.visibility = View.GONE
                    Adapter.setGardenerPlantsObserver(profilViewModel.userService.categorieIsSelected, s.toString())
                }
            }
        })

        binding.etSearchView.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus) {
                binding.clSearchView.visibility = View.VISIBLE
                binding.scrollViewSuggest.visibility = View.GONE

                Adapter.setGardenerPlantsObserver(profilViewModel.userService.categorieIsSelected, "")
                } else {
                    binding.clSearchView.visibility = View.GONE
                binding.scrollViewSuggest.visibility = View.VISIBLE
                    v.hideKeyboard()
                    binding.etSearchView.setText("")
                }
        }

        binding.searchView.setEndIconOnClickListener {
            binding.etSearchView.clearFocus()
        }
    } // END SEARCH VIEW

    private fun setObserver() {
        plantRef = FirebaseService().getPlantsReference()
        handlePlant = plantRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                println(PAGE + "onCancelled " + error.toException())
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(snapshot: DataSnapshot) {
                val plants = snapshot.getValue<HashMap<String, Plant>>()
                if(plants != null) {
                    println("Langue : " + Locale.getDefault().language.toString())
                    val plantsFilterJap = plants.filter { plantJap ->  plantJap.key == "parsley" || plantJap.key == "coriander" || plantJap.key == "basil" || plantJap.key == "bittermelon" || plantJap.key == "cherrytomato" || plantJap.key == "radish" }
                    val plantsFilterFr = plants.filter { plantFr ->  plantFr.key != "parsley" && plantFr.key != "coriander" && plantFr.key != "basil" && plantFr.key != "bittermelon" && plantFr.key != "cherrytomato" && plantFr.key != "radish" }
                    when(Locale.getDefault().language.toString()) {
                    "en" -> {
                        setPlantToCards(plantsFilterJap)
                    }
                    "fr" -> {
                        setPlantToCards(plantsFilterFr)
                    }
                    else -> {
                        setPlantToCards(plantsFilterJap)
                    }
                }
                }
            }
        })
    }


@RequiresApi(Build.VERSION_CODES.O)
private fun setPlantToCards(plants : Map<String, Plant>) {
    plants.let {
        //↓ ADAPTER FOR CARDS PLANTS SUGGEST ↓//
        val listesPlantsFilter = ArrayList<PlantModels>()

        it.forEach { e ->
            val plantModel = PlantModels(e.key, e.value)
            val arrayContains = arrayListOf<String>()

            if (plantModel.plants != null) {
                if (plantModel.plants!!.sowingPeriod?.startMonth ?: -1 >= 0 && plantModel.plants!!.sowingPeriod?.endMonth ?: -1 >= 0 && checkBetweenStartAndEnd(
                        plantModel.plants!!.sowingPeriod?.startMonth ?: -1,
                        plantModel.plants!!.sowingPeriod?.endMonth ?: -1,
                        getMonth(tsLong)
                    )
                ) {
                    arrayContains.add("SEMER")
                }

                if (plantModel.plants!!.plantingPeriod?.startMonth ?: -1 >= 0 && plantModel.plants!!.plantingPeriod?.endMonth ?: -1 >= 0 && checkBetweenStartAndEnd(
                        plantModel.plants!!.plantingPeriod?.startMonth ?: -1,
                        plantModel.plants!!.plantingPeriod?.endMonth ?: -1,
                        getMonth(tsLong)
                    )
                ) {
                    arrayContains.add("PLANTER")
                }
                when (arrayContains.count()) {
                    2 -> {
                        plantModel.period = "BOTH"
                        listesPlantsFilter.add(plantModel)
                    }
                    1 -> {
                        plantModel.period =
                            if (arrayContains.contains("SEMER")) "SEMER" else "PLANTER"
                        listesPlantsFilter.add(plantModel)
                    }
                    else -> {
                    }
                }
            }
        }

        adapterPlants = PlantsAdapter { id, name, description, sowings, planting, period ->
            requireView().hideKeyboard()
            val plantingRef = "${planting.startMonth}|${planting.endMonth}"
            val sowingRef = "${sowings.startMonth}|${sowings.endMonth}"

            findNavController().navigate(
                FragmentHomePlantsDirections.actionFragmentHomePlantsToAddPlantesRenameFragment(
                    id,
                    name,
                    description,
                    data.etage.toString(),
                    data.nbEtage.toString(),
                    plantingRef,
                    sowingRef,
                    period
                )
            )
        }

        adapterPlants.setGardenerPlantsObserver("", "")
        searchView(adapterPlants)
        adapterPlants.listesDePlantes = it
        binding.rvPlants.adapter = adapterPlants
        activity?.let { act ->
            binding.rvPlants.layoutManager =
                LinearLayoutManager(act, LinearLayoutManager.VERTICAL, false)
        }

        adapterCardsPlants = MesCardsPlantesAdapter({ keys, period ->
            checkKeyboard = false
            findNavController().navigate(
                FragmentHomePlantsDirections.actionFragmentHomePlantsToFragmentInformations(keys)
            )
        }, { id, name, description, saison, planting, sowings, period ->
            val plantingRef = "${planting.startMonth}|${planting.endMonth}"
            val sowingRef = "${sowings.startMonth}|${sowings.endMonth}"
            checkKeyboard = false
            findNavController().navigate(
                FragmentHomePlantsDirections.actionFragmentHomePlantsToAddPlantesRenameFragment(
                    id,
                    name,
                    description,
                    data.etage.toString(),
                    data.nbEtage.toString(), plantingRef, sowingRef, period
                )
            )
        })

        binding.rvCardPlants.adapter = adapterCardsPlants
        activity?.let { act ->
            binding.rvCardPlants.layoutManager =
                LinearLayoutManager(act, LinearLayoutManager.HORIZONTAL, false)
        }

        //HERE WE OBSERVE IF POKOS CHANGE FOR SUBMIT A NEW LIST

        pokos.observe(viewLifecycleOwner, androidx.lifecycle.Observer { obs ->
            if (obs != "Empty") {
                val newList = listesPlantsFilter.filter { o ->
                    o.plants?.filtre?.keys?.firstOrNull { a ->
                        a.toLowerCase().contains(obs.toLowerCase())
                    } != null
                }
                adapterCardsPlants.submitList(newList.toList())
            } else {
                adapterCardsPlants.submitList(listesPlantsFilter.toList())
            }
        })
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


