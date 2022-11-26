package fr.devid.plantR.ui.myPlants

import android.os.Bundle
import android.view.*
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import fr.devid.plantR.Constants
import fr.devid.plantR.R
import fr.devid.plantR.base.BaseFragment
import fr.devid.plantR.databinding.FragmentWishlistBinding
import fr.devid.plantR.models.Plant
import fr.devid.plantR.models.PlantFavs
import fr.devid.plantR.models.PositionDataPlant
import fr.devid.plantR.services.AlerterService
import fr.devid.plantR.services.FirebaseService
import fr.devid.plantR.ui.home.ProfileViewModel
import javax.inject.Inject

/**
 * lG
 * **/

class FragmentWishList() : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val profilViewModel: ProfileViewModel by activityViewModels { viewModelFactory }
    private val PAGE = "***** FragmentWishList *****"
    private lateinit var binding: FragmentWishlistBinding
    private lateinit var favsRef: DatabaseReference
    private lateinit var handleFavs: ValueEventListener
    private lateinit var plantsRef: DatabaseReference
    private lateinit var handlePlants: ValueEventListener
    private lateinit var adapter: WishListAdapter
    private lateinit var gardenerRef: DatabaseReference
    private lateinit var handleGardenerRef: ValueEventListener
    private val data: FragmentWishListArgs by navArgs()
    private var plantsFavsList = ArrayList<String>()
    var listFavsPlants = ArrayList<FavsModels>()

    data class FavsModels(
        var key: String = "",
        var name: String = "",
        var period: PlantFavs? = null
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWishlistBinding.inflate(inflater, container, false)

        initView()
        return binding.root
    }

    override fun onStop() {
        super.onStop()
        plantsFavsList.clear()
        listFavsPlants.clear()
    }

    fun setFavorisObserver(gardenerId: String) {

        favsRef = FirebaseService().getFavs(gardenerId)
        handleFavs = favsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val favs = snapshot.getValue<HashMap<String, PlantFavs>>()
                plantsFavsList.clear()
                listFavsPlants.clear()
                if (favs != null) {
                    profilViewModel.userService.gardenerId?.let { GUID ->
                        setPlantsObserver(favs)
                    }
                } else {
                    setPlantsObserver(hashMapOf())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("$PAGE an error ${error.toException()}")
            }
        })
    }

    fun setPlantsObserver(plantsFavs: HashMap<String, PlantFavs>) {
        if (plantsFavs.isEmpty()) {
            adapter.submitList(emptyList())
        } else {
            plantsFavs.forEach { plantId ->
                println("MON PLANT ID : " + plantId)
                plantsRef = FirebaseService().getPlantsReferenceById(plantId.key)
                handlePlants = plantsRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val plant = snapshot.getValue(Plant::class.java)
                        if (plant != null) {
                            println(plant)
                            plant?.name?.let {
                                FavsModels(
                                    plantId.key,
                                    it, plantId.value
                                )
                            }?.let { listFavsPlants.add(it) }
                            binding.clNoneWishlist.visibility = View.GONE

                            adapter.submitList(listFavsPlants.toList())
                            println("MA LIST " + listFavsPlants)
                        } else {
                            binding.clNoneWishlist.visibility = View.VISIBLE

                            adapter.submitList(emptyList())
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        println("$PAGE an error ${error.toException()}")
                    }
                })
            }
        }
    }

    fun initView() {
        profilViewModel.userService.gardenerId?.let {
            getGardenerPlantName(it)

        }

        Constants.life = viewLifecycleOwner
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        if (plantsFavsList.count() > 0) {
            println("Ma liste de favs " + plantsFavsList)
        }

        if(profilViewModel.userService.typeGarden != null) {
            adapter = WishListAdapter((profilViewModel.userService.typeGarden!!)
                , {
                    AlerterService.showGood(
                        "${getString(R.string.alerter_not_slot)} $it !",
                        requireActivity()
                    )
                }, { plantName ->
                    AlerterService.showGood("$plantName ${getString(R.string.PLANT_ADD)}", requireActivity())
                })
        }


        binding.rvFavsPlants.adapter = adapter
        binding.rvFavsPlants.layoutManager = LinearLayoutManager(
            requireActivity(),
            LinearLayoutManager.VERTICAL,
            false
        )
    }

    var count = 0

    fun getGardenerPlantName(gardenerId: String) {
        gardenerRef = FirebaseService().getGardenerPlantsById(gardenerId)
        handleGardenerRef = gardenerRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val plant = snapshot.getValue<HashMap<String, PositionDataPlant>>()
                println("MA CLE OBTENUE " + plant?.keys)
                println("MA  VALUE " + plant?.values)

                adapter.stageListOfPlants = plant ?: hashMapOf()
                adapter.stage = data.stage

                profilViewModel.userService.gardenerId?.let { guid ->
                    setFavorisObserver(guid)
                    adapter.gardenerId = guid
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }


    override fun onResume() {
        super.onResume()

    }

}