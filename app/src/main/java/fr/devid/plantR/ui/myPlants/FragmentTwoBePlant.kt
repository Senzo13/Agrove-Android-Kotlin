package fr.devid.plantR.ui.myPlants

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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
import fr.devid.plantR.R
import fr.devid.plantR.base.BaseFragment
import fr.devid.plantR.databinding.FragmentBePlantTwoBinding
import fr.devid.plantR.models.*
import fr.devid.plantR.services.FirebaseService
import fr.devid.plantR.ui.home.ProfileViewModel
import kotlinx.android.synthetic.main.fragment_profile.*
import java.lang.Exception
import javax.inject.Inject
import kotlin.collections.HashMap

/**
 * lG
 * **/
data class NuisiblesModels(
    var key : String = "",
    var nuisible : Nuisible? = null
)

class FragmentTwoBePlant(
    private val keys: String,
    private val name: String,
    private val dateHarvested: Int,
    private val dateSowing: Int,
    private val etagePlantPosition: String,
    private val gardenerId: String
) : BaseFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val profilViewModel: ProfileViewModel by activityViewModels { viewModelFactory }
    private val PAGE = "***** FragmentTwoBePlant *****"
    private lateinit var handleGardener : ValueEventListener
    private lateinit var binding: FragmentBePlantTwoBinding
    private lateinit var adapterNuisible : NuisiblesAdapter
    private lateinit var gardenerRef : DatabaseReference
    private lateinit var adapter : TasksAdapter
    private lateinit var nuisibleRef : DatabaseReference
    private lateinit var handleNuisible : ValueEventListener
    private lateinit var nuisiblePlantRef : DatabaseReference
    private lateinit var handlePlantNuisible : ValueEventListener
    private lateinit var arrayNuisibles : ArrayList<NuisiblesModels>
    var soinsNuisible : Soins = Soins()
    var arrayListAuxiliairesMap = listOf<String>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBePlantTwoBinding.inflate(inflater, container, false)
        profilViewModel.userService.arrayTestAuxiliaire.clear()
        getNuisiblesChecker()
        view()
        return binding.root
    }

    override fun onStop() {
        super.onStop()
        removeListener()

    }

    //CACHER CL MALADIE NUISIBLE
    //AFFICHER SCROLLVIEW NUISIBLES
    override fun onResume() {
        super.onResume()
        //ON LE REND VISIBLE SUR LE ONRESUME CAR IL REPASSE EN GONE LORSQUE L'ON CLIQUE SUR NUISIBLE
        binding.tvTips.visibility = View.VISIBLE
        binding.viewUnderline.visibility = View.VISIBLE
        binding.clTips.visibility = View.VISIBLE
        binding.tvTips.text = "Un problème avec votre ${name} ?\nDécouvrez ici des solutions 100% naturelles !"
        println("******* DANS MON ONRESUME DE MISSIONS $etagePlantPosition")
        //setGardenerObserver(gardenerId, etagePlantPosition)
    }

//    override fun setUserVisibleHint(visible: Boolean) {
//        super.setUserVisibleHint(visible)
//        if (visible) {
//            val popupStart =
//                PopupSoins(name, requireContext()) { popup, bool ->
//                    if (bool) {
//                        popup.dismiss()
//                        print("L'utilisateur a surement un problème avec sa plante !"+"\n")
//                    } else {
//                        popup.dismiss()
//                        print("L'utilisateur a cliqué sur je m'informe !"+"\n")
//                    }
//                }
//            popupStart.show()
//        }
//    }

    private fun getNuisiblesChecker() {
        println("Ma keys : " + keys)
        arrayNuisibles = arrayListOf()

        nuisiblePlantRef = FirebaseService().getPlantsReferenceById(keys).child("nuisibles")
        handlePlantNuisible = nuisiblePlantRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                println(PAGE + "onCancelled " + error.toException())
            }

            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                var nuisiblesCheck = snapshot.getValue<HashMap<String, Boolean>>()
                if (nuisiblesCheck != null) {
                    var listNuisibles = nuisiblesCheck.filter { it.value }.map { it.key }
                    println("Liste de mes nuisibles vrai" + listNuisibles)
                    getNuisibles(listNuisibles)
                }

            }
        })
    }

    private fun getNuisibles(list : List<String>) {
        arrayListAuxiliairesMap = listOf()

        list.forEach {
        nuisibleRef = FirebaseService().getNuisiblesReferenceId(it)
        handleNuisible = nuisibleRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                println(PAGE + "onCancelled " + error.toException())
            }

            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                var nuisibles = snapshot.getValue<Nuisible>()
                nuisibles.let { ns ->
                    soinsNuisible = ns?.soinsnuisibles!!
                    arrayListAuxiliairesMap = nuisibles!!.auxiliaires.listeauxiliaire.map { it.key }
                }
                println("Je recupere cela : " +  nuisibles)
                nuisibles.let { ns ->
                    arrayNuisibles.add(NuisiblesModels(it, ns))
                }
            }
        })
        }
    }

    private fun view() {

        binding.linearMaladies.setOnClickListener {
            println("Linear maladies")
        }

        binding.linearNuisibles.setOnClickListener {
            println("Linear nuisibles")

            binding.tvTips.visibility = View.GONE
            binding.viewUnderline.visibility = View.GONE
            binding.clTips.visibility = View.GONE

            adapterNuisible = NuisiblesAdapter{keyNuisible, nameNuisible, image, description ->
                        if (findNavController().currentDestination?.id == R.id.fragmentBePlant) {
                            println("GO PAGE MAN")
                            findNavController().navigate(FragmentBePlantDirections.actionFragmentBePlantToFragmentTipsTasksNuisibles(keys, keyNuisible, nameNuisible, image, description))
                        }
                    }

            binding.rvNuisibles.adapter = adapterNuisible

            activity?.let { act ->
                binding.rvNuisibles.layoutManager = LinearLayoutManager(act, LinearLayoutManager.VERTICAL, false)
            }

            if(::arrayNuisibles.isInitialized) {
                println("Cb de arraynuisible je possede : " + arrayNuisibles.count())
                if(arrayNuisibles.count() >= 1) {
                    println("Initialized Nuisibles")
                    adapterNuisible.submitList(arrayNuisibles.toList()) // OU LE MAP
                    binding.clMaladiesNuisibles.visibility = View.GONE
                    binding.scrollviewNuisibles.visibility = View.VISIBLE
                }
            }
        }
    }


    private fun removeListener() {
        if (::nuisibleRef.isInitialized && ::nuisibleRef.isInitialized) {
            nuisibleRef.removeEventListener(handleNuisible)
        }
        if (::nuisiblePlantRef.isInitialized && ::handlePlantNuisible.isInitialized) {
            nuisiblePlantRef.removeEventListener(handlePlantNuisible)
        }
    }

}

//
//    private fun setGardenerObserver(guid : String, position: String) {
//        println("**** gUid :$guid\nposition : $position ****")
//        gardenerRef = FirebaseService().getGardenerPlantsById(guid).child(position)
//        handleGardener = gardenerRef.addValueEventListener(object :ValueEventListener {
//            override fun onCancelled(error: DatabaseError) {
//                println("${PAGE} onCancelled ${error.toException()}")
//            }
//
//            override fun onDataChange(snapshot: DataSnapshot) {
//                println("MON SNAPSHOT  : $snapshot")
//                val gardenerPlants = snapshot.getValue<PositionDataPlant>()
//                gardenerPlants.let {
//                    val plantTasks = arrayListOf<PlantTasks>()
//                        it?.tasks?.forEachIndexed { index, tasks ->
//                            plantTasks.add(PlantTasks(index, it.plantID, it.plantName, tasks))
//                        }
//                    adapter = TasksAdapter { id, title, description, position, guid, listPosition, taskId ->
//                        println("GO PAGE MAN")
//                        if (findNavController().currentDestination?.id == R.id.fragmentBePlant) {
//                           findNavController().navigate(FragmentBePlantDirections.actionFragmentBePlantToFragmentTipsTasks(id,title,description, position, guid, listPosition, taskId))
//                        }
//                    }
//
//                    adapter.setPosition = position
//                    adapter.guid = guid
//                    adapter.plantId = keys
//                    binding.rvPlants.adapter = adapter
//                    binding.rvPlants.layoutManager = LinearLayoutManager(
//                        requireActivity().let { it },
//                        LinearLayoutManager.VERTICAL, false)
//                    println("nb element dans la liste " + plantTasks.count())
//                            val arrayFilter = plantTasks.filter { data ->
//                                val datePlant = data.tasks.date.roundToLong() * 1000
//                                val test = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
//                                val testConvert = test.toInstant(ZoneOffset.MIN).toEpochMilli()
//                                println("Affichage de la nouvelle valeur : ${testConvert}")
//                                val currentDate = Date().time
//                                println("********************** DATE ******************************")
//                                println("Date de la plante : $datePlant\nCurrentDate : ${currentDate}")
//                                !data.tasks.done && datePlant <= testConvert
//                            }
//
//                            if(arrayFilter.count() > 0) {
//                                binding.clTips.visibility = View.VISIBLE
//                                binding.tvTips.visibility = View.VISIBLE
//                                binding.tvTips.text = context?.getString(R.string.TASK_ATM)
//                            } else {
//                                binding.clTips.visibility = View.VISIBLE
//                                binding.tvTips.visibility = View.VISIBLE
//                                binding.tvTips.text = context?.getString(R.string.TASK_WEEK)
//                            }
//                            adapter.submitList(arrayFilter)
//                        }
//            }
//        })
//  }







