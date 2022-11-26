package fr.devid.plantR.ui.publicAppView

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import fr.devid.plantR.base.BaseFragment
import fr.devid.plantR.databinding.FragmentGardenerTypeBinding
import javax.inject.Inject

/**
 * LG
 */

class FragmentChoicePrincipalGardenType : BaseFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
   private lateinit var binding: FragmentGardenerTypeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGardenerTypeBinding.inflate(inflater, container, false)
        initView()
        return binding.root
    }

    private fun initView() {
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.clPotagerAgrove.setOnClickListener {
            findNavController().navigate(FragmentChoicePrincipalGardenTypeDirections.actionFragmentChoicePrincipalGardenTypeToFragmentChoiceGardenType())
        }

        binding.clPotagerClassic.setOnClickListener {
            findNavController().navigate(FragmentChoicePrincipalGardenTypeDirections.actionFragmentChoicePrincipalGardenTypeToFragmentSelectGardenClassic())
        }

    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
    }

}
