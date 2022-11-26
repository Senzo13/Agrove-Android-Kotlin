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
import fr.devid.plantR.databinding.FragmentChoiceGardenerTypePotagerClassicBinding
import fr.devid.plantR.ui.home.ProfileViewModel
import javax.inject.Inject

/**
 * LG
 */

class FragmentAddSquareGarden : BaseFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val profilViewModel: ProfileViewModel by activityViewModels { viewModelFactory }
    private lateinit var binding: FragmentChoiceGardenerTypePotagerClassicBinding
    private val data: FragmentAddSquareGardenArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            FragmentChoiceGardenerTypePotagerClassicBinding.inflate(inflater, container, false)
        initView()
        return binding.root
    }

    private fun initView() {
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.clPetitCarre.setOnClickListener {
            if (data.type == "Classic") {
                profilViewModel.userService.stateForAddGardener = false
                findNavController().navigate(FragmentAddSquareGardenDirections.actionFragmentAddSquareGardenToHomeFragment(-1, "Classic", 0, "carre"))
            } else {
                findNavController().navigate(FragmentAddSquareGardenDirections.actionFragmentAddSquareGardenToHomeFragment(profilViewModel.userService.kitCapteur, "capteur_carre", 0, data.type))
            }
        }

        binding.clMoyenCarre.setOnClickListener {
            if (data.type == "Classic") {
                profilViewModel.userService.stateForAddGardener = false
                findNavController().navigate(FragmentAddSquareGardenDirections.actionFragmentAddSquareGardenToHomeFragment(-1, "Classic", 1, "carre"))
            } else {
                findNavController().navigate(FragmentAddSquareGardenDirections.actionFragmentAddSquareGardenToHomeFragment(profilViewModel.userService.kitCapteur, "capteur_carre", 1, data.type))
            }
        }

        binding.clGrandCarre.setOnClickListener {
            if (data.type == "Classic") {
                profilViewModel.userService.stateForAddGardener = false
                findNavController().navigate(
                    FragmentAddSquareGardenDirections.actionFragmentAddSquareGardenToHomeFragment(-1, "Classic", 2, "carre"))
            } else {
                findNavController().navigate(FragmentAddSquareGardenDirections.actionFragmentAddSquareGardenToHomeFragment(profilViewModel.userService.kitCapteur, "capteur_carre", 2, data.type))
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
