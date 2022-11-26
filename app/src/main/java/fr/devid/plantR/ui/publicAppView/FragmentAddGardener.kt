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
import fr.devid.plantR.databinding.FragmentChoiceGardenerTypeGardenerBinding
import fr.devid.plantR.ui.home.ProfileViewModel
import javax.inject.Inject

/**
 * LG
 */

class FragmentAddGardener : BaseFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val profilViewModel: ProfileViewModel by activityViewModels { viewModelFactory }
    private lateinit var binding: FragmentChoiceGardenerTypeGardenerBinding
    private val data: FragmentAddGardenerArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChoiceGardenerTypeGardenerBinding.inflate(inflater, container, false)
        initView()
        return binding.root
    }

    private fun initView() {

        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.clPetiteJardiniere.setOnClickListener {
            if(data.type == "Classic") {
                profilViewModel.userService.stateForAddGardener = false
                findNavController().navigate(FragmentAddGardenerDirections.actionFragmentAddGardenerToHomeFragment(-1, "Classic", 0, "jardiniere"))
            } else {
              findNavController().navigate(FragmentAddGardenerDirections.actionFragmentAddGardenerToHomeFragment(profilViewModel.userService.kitCapteur, "capteur_jardiniere", 0 , data.type))
            }
        }

        binding.clMoyenneJardiniere.setOnClickListener {
            if(data.type == "Classic") {
                profilViewModel.userService.stateForAddGardener = false
                findNavController().navigate(FragmentAddGardenerDirections.actionFragmentAddGardenerToHomeFragment(-1, "Classic", 1, "jardiniere"))
                } else {
                findNavController().navigate(FragmentAddGardenerDirections.actionFragmentAddGardenerToHomeFragment(profilViewModel.userService.kitCapteur, "capteur_jardiniere",1, data.type))
            }

        }

        binding.clGrandeJardiniere.setOnClickListener {
            if(data.type == "Classic") {
                profilViewModel.userService.stateForAddGardener = false
                findNavController().navigate(FragmentAddGardenerDirections.actionFragmentAddGardenerToHomeFragment(-1, "Classic", 2, "jardiniere"))
            } else {
                println("***********************")
                println("type  : " + data.type)
               findNavController().navigate(FragmentAddGardenerDirections.actionFragmentAddGardenerToHomeFragment(profilViewModel.userService.kitCapteur, "capteur_jardiniere", 2 , data.type))
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
