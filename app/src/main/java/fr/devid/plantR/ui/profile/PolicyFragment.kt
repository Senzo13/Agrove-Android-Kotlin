package fr.devid.plantR.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import fr.devid.plantR.base.BaseFragment
import fr.devid.plantR.databinding.FragmentCguBinding
import javax.inject.Inject

class PolicyFragment : BaseFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var binding : FragmentCguBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCguBinding.inflate(inflater, container, false)
        initView()
        return binding.root
    }

    private fun initView() {
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

}
