package fr.devid.plantR.ui.myTeam

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.database.*
import fr.devid.plantR.R
import fr.devid.plantR.base.BaseFragment
import fr.devid.plantR.databinding.FragmentReglageTeamBinding
import fr.devid.plantR.models.CheckIsPublic
import fr.devid.plantR.services.AlerterService
import fr.devid.plantR.services.FirebaseService
import fr.devid.plantR.ui.home.ProfileViewModel
import javax.inject.Inject

class FragmentReglageTeam : BaseFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val profilViewModel: ProfileViewModel by activityViewModels { viewModelFactory }
    private val PAGE: String = "****** FragmentReglageTeam ******"
    private val data : FragmentReglageTeamArgs by navArgs()
    private lateinit var binding: FragmentReglageTeamBinding
    private lateinit var isPublicValue: CheckIsPublic
    private lateinit var stateRef: DatabaseReference
    private lateinit var stateHandleRef: ValueEventListener

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReglageTeamBinding.inflate(inflater, container, false)
        publicObserver()
        initView()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
    }

    private fun initView() {

        // le isChecked du bouton switch dépends de la data envoyée par le fragment précédent //
        binding.mySwitch.isChecked = data.ispublic

        binding.mySwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked ) {
                println("Checked")
                publicState()
            } else {
                publicState()
            }
        }

        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun publicObserver() {
        println("JE RENTRE DANS PUBLIC OBSERVER")
        profilViewModel.userService.gardenerId?.let { guid ->
            stateRef = FirebaseService().getIsPublic(guid)
                stateHandleRef = stateRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val isPublicData = snapshot.getValue(CheckIsPublic::class.java)
                        if (isPublicData != null) {
                            println("J'actualise mon isPublicValue a " + isPublicData)
                            isPublicValue = isPublicData
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        println(PAGE + "Un problème est survenu lors de la récupération du isPublic dans la ligne 75\nRacine myTeam")
                    }
                })
        }
    }

    private fun publicState() {
        if (::isPublicValue.isInitialized) {
                    if (isPublicValue.ispublic) {
                        println("Vous passez votre jardinière en OFF")
                        isPublicValue.ispublic = false
                       // setPbLoading(isPublicValue.ispublic)
                        stateRef.child("ispublic").setValue(isPublicValue.ispublic)
                        AlerterService.showGood(resources.getString(R.string.GARDENER_LOCK), requireActivity())
                    } else if (!isPublicValue.ispublic) {
                        println("Vous passez votre jardinière en ON")
                        isPublicValue.ispublic = true
                     //   setPbLoading(isPublicValue.ispublic)
                        stateRef.child("ispublic").setValue(isPublicValue.ispublic)
                        AlerterService.showGood(resources.getString(R.string.GARDENER_UNLOCK), requireActivity())
                    }
        }
    }

    override fun onStop() {
        removeListener()
        super.onStop()
    }

    private fun removeListener() {

    }

}