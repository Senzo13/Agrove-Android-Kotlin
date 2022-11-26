//package fr.devid.plantR.ui.home
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.lifecycle.ViewModelProvider
//import androidx.navigation.fragment.findNavController
//import androidx.navigation.fragment.navArgs
//import fr.devid.plantR.base.BaseFragment
//import fr.devid.plantR.databinding.FragmentAddGardenerJumelageGardenerNameBinding
//import javax.inject.Inject
//
///**
// * LG
// */
//
//class FragmentAddGardenerJumelageName : BaseFragment() {
//    @Inject
//    lateinit var viewModelFactory: ViewModelProvider.Factory
//    private lateinit var binding: FragmentAddGardenerJumelageGardenerNameBinding
//    private val data: FragmentAddGardenerJumelageNameArgs by navArgs()
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        binding = FragmentAddGardenerJumelageGardenerNameBinding.inflate(inflater, container, false)
//        initView()
//        return binding.root
//    }
//
//    private fun initView() {
//        binding.btLogin.setOnClickListener {
//            //Here, we add a name for our gardener
//            //then, we send the user back to home
//            binding.etNameOfPlantsToRename.text.toString().trimEnd()
//            var gardenerNameMetadata = binding.etNameOfPlantsToRename.text.toString().trimEnd()
//            if(gardenerNameMetadata.isNullOrEmpty()) {
//                findNavController().navigate(FragmentAddGardenerJumelageNameDirections.actionFragmentAddGardenerJumelageNameToFragmentAddGardenerJumelageAdress(data.guid,data.uid, "Ma jardinière"))
//            }
//            else {
//                findNavController().navigate(FragmentAddGardenerJumelageNameDirections.actionFragmentAddGardenerJumelageNameToFragmentAddGardenerJumelageAdress(data.guid,data.uid, gardenerNameMetadata))
//            }
//        }
//    }
//
//}
