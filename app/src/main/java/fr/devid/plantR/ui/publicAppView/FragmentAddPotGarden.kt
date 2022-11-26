package fr.devid.plantR.ui.publicAppView

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import fr.devid.plantR.base.BaseFragment
import fr.devid.plantR.databinding.FragmentChoiceGardenerTypePotClassicBinding
import fr.devid.plantR.ui.home.ProfileViewModel
import javax.inject.Inject

/**
 * LG
 */

class FragmentAddPotGarden : BaseFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val profilViewModel: ProfileViewModel by activityViewModels { viewModelFactory }
    private lateinit var binding: FragmentChoiceGardenerTypePotClassicBinding
    private val data: FragmentAddPotGardenArgs by navArgs()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChoiceGardenerTypePotClassicBinding.inflate(inflater, container, false)
        initView()
        return binding.root
    }

    private fun initView() {
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.clKitPetitPot.setOnClickListener {
            if(data.type == "Classic") {
                profilViewModel.userService.stateForAddGardener = false
                     findNavController().navigate(FragmentAddPotGardenDirections.actionFragmentAddPotGardenToHomeFragment(-1, "Classic", 0, "pot"))
            } else {
                findNavController().navigate(FragmentAddPotGardenDirections.actionFragmentAddPotGardenToHomeFragment(profilViewModel.userService.kitCapteur, "capteur_pot", 0, data.type))
            }
        }

        binding.clMoyenPot.setOnClickListener {
            if(data.type == "Classic") {
                profilViewModel.userService.stateForAddGardener = false
                findNavController().navigate(FragmentAddPotGardenDirections.actionFragmentAddPotGardenToHomeFragment(-1, "Classic", 1, "pot"))
            } else {
                   findNavController().navigate(FragmentAddPotGardenDirections.actionFragmentAddPotGardenToHomeFragment(profilViewModel.userService.kitCapteur, "capteur_pot", 1, data.type))
            }
        }

        binding.clPotGrand.setOnClickListener {
            if(data.type == "Classic") {
                profilViewModel.userService.stateForAddGardener = false
                findNavController().navigate(FragmentAddPotGardenDirections.actionFragmentAddPotGardenToHomeFragment(-1, "Classic", 2, "pot"))
            } else {
                   findNavController().navigate(FragmentAddPotGardenDirections.actionFragmentAddPotGardenToHomeFragment(profilViewModel.userService.kitCapteur, "capteur_pot", 2, data.type))
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
    }

}
