package fr.devid.plantR.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import fr.devid.plantR.R
import fr.devid.plantR.base.BaseFragment
import fr.devid.plantR.databinding.FragmentAddGardenerJumelageBinding
import fr.devid.plantR.models.GardenerMetadata
import fr.devid.plantR.models.InitGardener
import fr.devid.plantR.services.AlerterService
import fr.devid.plantR.services.FirebaseService
import javax.inject.Inject

/**
 * LG
 */

class FragmentAddGardenerJumelage : BaseFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val profilViewModel: ProfileViewModel by activityViewModels { viewModelFactory }
    private lateinit var binding: FragmentAddGardenerJumelageBinding
    private lateinit var userRef: DatabaseReference
    private lateinit var handleRef: ValueEventListener
    private var userId : String? = null
    private val data: FragmentAddGardenerJumelageArgs by navArgs()
    private lateinit var setData: InitGardener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddGardenerJumelageBinding.inflate(inflater, container, false)
        initView()
        return binding.root
    }

    private fun initView() {
        binding.btNext.setOnClickListener {
            FirebaseService().firebase.currentUser?.uid?.let { uid ->
                findNavController().navigate(FragmentAddGardenerJumelageDirections.actionFragmentAddGardenerJumelageToFragmentAddGardenerJumelageAdress(data.guid, uid, data.type))
            }
        }
    }

    private fun getUserObserver() {
        println("guid : " + data.guid + "\n" + "Nbetage : " + data.etage)
        FirebaseService().firebase.currentUser?.uid?.let { uid ->
            userId = uid
            userRef = FirebaseService().getUserByIdAddToOwners(uid)
            handleRef = userRef.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) { }
                override fun onDataChange(snapshot: DataSnapshot) {
                    val value = snapshot.getValue<String>()
                        when(value) {
                            "ok" -> {
                                //If ok then the match is working, so we display text that says so
                                userRef.removeValue()
                                binding.tvCreaPotager.visibility=View.GONE
                                binding.pbLoadingSpinner.visibility = View.GONE
                                binding.blocMiddle.visibility = View.VISIBLE
                                binding.btNext.visibility = View.VISIBLE
                                //AFFICHER MES ELEMENTS
                                binding.tvBottom.visibility=View.VISIBLE
                                binding.tvMiddle.visibility=View.VISIBLE
                                val set  = InitGardener(false, data.type, data.dimension)
                                   setData = set
                                    if(::setData.isInitialized) {
                                        if(data.gardenerParent.isNotEmpty()) {
                                            FirebaseService().getGardenerById(data.guid).child("type").setValue(data.type)
                                            FirebaseService().getGardenerById(data.guid).child("dimension").setValue(data.dimension)
                                            FirebaseService().getGardenerById(data.guid).child("ispublic").setValue(false)
                                            FirebaseService().getGardenerById(data.guid).child("gardenerParent").setValue(data.gardenerParent)
                                            FirebaseService().getGardenerById(data.guid).child("rangs").setValue(data.rangs?:2)
                                        } else {
                                            FirebaseService().getGardenerById(data.guid).child("type").setValue(data.type)
                                            FirebaseService().getGardenerById(data.guid).child("dimension").setValue(data.dimension)
                                            FirebaseService().getGardenerById(data.guid).child("ispublic").setValue(false)
                                        }
                                    }
                            }
                            "ko" -> {
                                //If ko the user receives an alert indicating an error.
                                userRef.removeValue()
                                findNavController().popBackStack()
                                activity?.let { act ->
                                    AlerterService.showError(getString(R.string.alerter_problem_garden_create),act)
                                    //RAJOUTER UN POPUP QUI AFFICHE UNE ERREUR
                                }
                            }
                            else -> {

                            }
                        }
                }
            }) //Here we adding the user to owner
            userRef.setValue("${data.etage}|${data.guid}")
        }
    }

    override fun onResume() {
        super.onResume()
        getUserObserver()

    }

    override fun onStop() {
        super.onStop()
    }

}
