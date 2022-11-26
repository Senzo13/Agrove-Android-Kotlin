package fr.devid.plantR.ui.subscribe

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import fr.devid.plantR.base.BaseFragment
import fr.devid.plantR.databinding.FragmentOneBinding
import fr.devid.plantR.models.Plant
import fr.devid.plantR.models.User
import fr.devid.plantR.services.AlerterService
import fr.devid.plantR.services.FirebaseService
import fr.devid.plantR.ui.home.ProfileViewModel
import javax.inject.Inject


/**
 * lG
 * **/

class FragmentTwoSubscribeRangs(val gardenerTeamId : String, var keyFragment : Boolean):  BaseFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val profilViewModel: ProfileViewModel by activityViewModels { viewModelFactory }
    private val PAGE = "***** FragmentTwo *****"
    private val fragment = keyFragment
    private lateinit var binding : FragmentOneBinding
    private lateinit var adapter : MesPlantesAdapter
    private lateinit var userRef : DatabaseReference
    private lateinit var handleUser : ValueEventListener
    private lateinit var plantRef : DatabaseReference
    private lateinit var handlePlant : ValueEventListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOneBinding.inflate(inflater,container,false)
        FirebaseService().firebase.currentUser?.uid?.let {uid ->
            userRef = FirebaseService().getUserByid(uid)
            handleUser = userRef.addValueEventListener(object : ValueEventListener{
                override fun onCancelled(error: DatabaseError) {
                    println("${PAGE} onCancelled ${error.toException()}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val userData = snapshot.getValue(User::class.java)
                    userData?.currentGardener?.let {
                        setObserverGetPlants(gardenerTeamId)//Observer de plants
                    }
                }
            })
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        profilViewModel.currentPlantPageService.currentPlantModel = emptyList()

    }
    override fun onStop() {
        super.onStop()
        removeListener()
    }

    private fun removeListener() {
        adapter.removeListener()
        if (::plantRef.isInitialized && ::handlePlant.isInitialized)
            plantRef.removeEventListener(handlePlant)
        if (::userRef.isInitialized && ::handleUser.isInitialized)
            userRef.removeEventListener(handleUser)
    }

    private fun setObserverGetPlants(gardenerId : String) {
        plantRef = FirebaseService().getPlantsReference()
        handlePlant = plantRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                println("${PAGE} onCancelled ${error.toException()}")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                println(snapshot.value)
                val plants = snapshot.getValue<HashMap<String, Plant>>()
                plants?.let {
                    println(it)
                    val listesDePlantes = it //Je stock mes plantes dans une listes
                    adapter = MesPlantesAdapter{ keys, name, harvested, sowing, pos, uid ->
                        findNavController() .navigate(MesPlantesSubscribeFragmentDirections.actionMesPlantesSubscribeFragmentToFragmentBePlantSubscribe(keys, name, harvested, sowing, pos, uid))
                    }

                    adapter.currentEtage = "4" //J'envoie mon numéro du fragment, et ma gardenerId
                    adapter.listesDePlantes = listesDePlantes
                    profilViewModel.currentPlantPageService.currentPlantModel = emptyList() // RESET
                    adapter.setGardenerPlantsObserver(gardenerId, profilViewModel.currentPlantPageService)
                    binding.rvOne.adapter = adapter
                    binding.rvOne.layoutManager = LinearLayoutManager(
                        requireActivity(),
                        LinearLayoutManager.VERTICAL,
                        false
                    )
                }
            }
        })
    }

}
