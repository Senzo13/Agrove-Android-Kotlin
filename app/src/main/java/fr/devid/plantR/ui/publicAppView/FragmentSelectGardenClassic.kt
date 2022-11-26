package fr.devid.plantR.ui.publicAppView

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import fr.devid.plantR.base.BaseFragment
import fr.devid.plantR.databinding.FragmentSelectTypeOfGardenerClassicBinding
import javax.inject.Inject

/**
 * LG
 */

class FragmentSelectGardenClassic : BaseFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var binding: FragmentSelectTypeOfGardenerClassicBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSelectTypeOfGardenerClassicBinding.inflate(inflater, container, false)
        initView()
        return binding.root
    }

    private fun initView() {
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.clMenu1.setOnClickListener { // POTAGER
            findNavController().navigate(FragmentSelectGardenClassicDirections.actionFragmentSelectGardenClassicToFragmentAddSquareGarden("Classic")) // Forcément de type classic donc pas d'argument
        }
        binding.clMenu2.setOnClickListener { // POT
            findNavController().navigate(FragmentSelectGardenClassicDirections.actionFragmentSelectGardenClassicToFragmentAddPotGarden("Classic"))
        }
        binding.clMenu3.setOnClickListener { // JARDINIERE
            findNavController().navigate(FragmentSelectGardenClassicDirections.actionFragmentSelectGardenClassicToFragmentAddGardener("Classic"))
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
    }

}
