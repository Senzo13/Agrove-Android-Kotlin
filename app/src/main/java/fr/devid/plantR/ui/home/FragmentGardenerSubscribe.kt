package fr.devid.plantR.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import fr.devid.plantR.base.BaseFragment
import fr.devid.plantR.databinding.FragmentGardenerSubscribeBinding
import fr.devid.plantR.services.FirebaseService
import javax.inject.Inject

/**
 * LG
 */

class FragmentGardenerSubscribe : BaseFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var binding: FragmentGardenerSubscribeBinding
    private val data: FragmentGardenerSubscribeArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGardenerSubscribeBinding.inflate(inflater, container, false)
        initView()
        return binding.root
    }

    private fun initView() {
        binding.btSubscribe.setOnClickListener {
            //we have here the particular case where we are going to subscribe to a planter
            FirebaseService().firebase.currentUser?.uid?.let { uid ->
                FirebaseService().getUserByid(uid).child("gardenersGuest").child(data.guid).setValue(true)
                findNavController().popBackStack()
            }

        }
    }

}
