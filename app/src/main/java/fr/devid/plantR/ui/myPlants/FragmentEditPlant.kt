package fr.devid.plantR.ui.myPlants

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import fr.devid.plantR.R
import fr.devid.plantR.base.BaseFragment
import fr.devid.plantR.databinding.FragmentModifyPlantBinding
import fr.devid.plantR.manager.PagerManager
import fr.devid.plantR.models.PositionDataPlant
import fr.devid.plantR.services.AlerterService
import fr.devid.plantR.services.FirebaseService

class FragmentEditPlant : BaseFragment() {

    private lateinit var binding: FragmentModifyPlantBinding
    private val data: FragmentEditPlantArgs by navArgs()
    private val PAGE = "FragmentEditPlant"
    private lateinit var gardenerRef: DatabaseReference
    private lateinit var handleGardener: ValueEventListener
    private lateinit var gardenerUpdate: PositionDataPlant

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentModifyPlantBinding.inflate(inflater, container, false)
        initView()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        setGardenerPlantObserver(data.guid, data.etage)
        println("ONRESUME")
    }


    override fun onStop() {
        super.onStop()
        println("ONSTOP")
    }

    private fun initView() {

        binding.etNameOfGardener.hint = data.name
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btModify.setOnClickListener {
            if (binding.etNameOfGardener.text.isEmpty()) {
                activity?.let {
                    AlerterService.showError(context?.getString(R.string.fill_do_not_empty)!!, it)
                }
            } else {
                gardenerUpdate.plantName = binding.etNameOfGardener.text.toString()
                FirebaseService().getGardenerPlantsById(data.guid).child(data.etage)
                    .setValue(gardenerUpdate).addOnSuccessListener {
                        activity?.let {
                        AlerterService.showGood(context?.getString(R.string.MODIF_SUCCESS)!!, it)
                        }
                }.addOnFailureListener {
                            activity?.let {
                                AlerterService.showError(context?.getString(R.string.MODIF_FAILED)!!, it)
                            }
                }
                findNavController().popBackStack()
            }
        }

//        binding.btDelete.setOnClickListener { // FOR THE FEATURE
//            FirebaseService().getGardenerPlantsById(data.guid).child(data.etage).removeValue()
//                .addOnSuccessListener {
//                    activity?.let {
//
//                    AlerterService.showGood("La plante à bien était supprimé", it)
//                    }
//                }.addOnFailureListener {
//                    activity?.let {
//                    AlerterService.showError("La suppression de la plante à échoué", it)
//                    }
//
//            }
//            findNavController().popBackStack()
//        }
    }

    private fun setGardenerPlantObserver(guid: String, etage: String) {
        gardenerRef = FirebaseService().getGardenerPlantsById(guid).child(etage)
        handleGardener = gardenerRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                println("${PAGE} ayant l'erreur ${error.toException()}")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val gardenerPlants = snapshot.getValue(PositionDataPlant::class.java)
                if (gardenerPlants != null) {
                    gardenerUpdate = gardenerPlants
                }

            }

        })
    }
}