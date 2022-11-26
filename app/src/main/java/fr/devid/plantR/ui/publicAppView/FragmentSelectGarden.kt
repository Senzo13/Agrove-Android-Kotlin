package fr.devid.plantR.ui.publicAppView

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import fr.devid.plantR.base.BaseFragment
import fr.devid.plantR.databinding.FragmentSelectTypeOfGardenerBinding
import fr.devid.plantR.ui.home.ProfileViewModel
import javax.inject.Inject

/**
 * LG
 */

class FragmentSelectGarden : BaseFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val profilViewModel: ProfileViewModel by activityViewModels { viewModelFactory }
   private lateinit var binding: FragmentSelectTypeOfGardenerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSelectTypeOfGardenerBinding.inflate(inflater, container, false)
        initView()
        return binding.root
    }

    private fun initView() {
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.clMenu1.setOnClickListener {
            profilViewModel.userService.data_kit_already = false
            findNavController().navigate(FragmentSelectGardenDirections.actionFragmentSelectGardenToFragmentAddSquareGarden("capteur_carre"))
        }

        binding.clMenu2.setOnClickListener {
            profilViewModel.userService.data_kit_already = false
            findNavController().navigate(FragmentSelectGardenDirections.actionFragmentSelectGardenToFragmentAddPotGarden("capteur_pot"))
        }

        binding.clMenu3.setOnClickListener {
            profilViewModel.userService.data_kit_already = false
            findNavController().navigate(FragmentSelectGardenDirections.actionFragmentSelectGardenToFragmentAddGardener("capteur_jardiniere"))
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
    }

}
