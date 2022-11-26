package fr.devid.plantR.ui.publicAppView

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import fr.devid.plantR.base.BaseFragment
import fr.devid.plantR.databinding.FragmentChoiceGardenerTypeBinding
import fr.devid.plantR.ui.home.ProfileViewModel
import javax.inject.Inject

/**
 * LG
 */

class FragmentChoiceGardenType : BaseFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val profilViewModel: ProfileViewModel by activityViewModels { viewModelFactory }
    private lateinit var binding: FragmentChoiceGardenerTypeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChoiceGardenerTypeBinding.inflate(inflater, container, false)
        initView()
        return binding.root
    }

    private fun initView() {
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.clKitParcelle.setOnClickListener {
            profilViewModel.userService.data_kit_already = false
            findNavController().navigate(FragmentChoiceGardenTypeDirections.actionFragmentChoiceGardenTypeToHomeFragment(profilViewModel.userService.kitParcelle, "parcelle", 2))
        }
        binding.clKitMural.setOnClickListener {
            profilViewModel.userService.data_kit_already = false
            findNavController().navigate(FragmentChoiceGardenTypeDirections.actionFragmentChoiceGardenTypeToHomeFragment(profilViewModel.userService.kitMural, "mural", 2))
        }
        binding.clKitCapteur.setOnClickListener {
            profilViewModel.userService.data_kit_already = false
            findNavController().navigate(FragmentChoiceGardenTypeDirections.actionFragmentChoiceGardenTypeToFragmentSelectGarden())
        }

        binding.clKitCleeEnMain.setOnClickListener {
            profilViewModel.userService.data_kit_already = false
            findNavController().navigate(FragmentChoiceGardenTypeDirections.actionFragmentChoiceGardenTypeToHomeFragment(profilViewModel.userService.kitCleEnMain,"cle_en_main", 2))
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
    }
}
