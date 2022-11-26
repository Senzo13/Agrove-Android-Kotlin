package fr.devid.plantR.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import fr.devid.plantR.R
import fr.devid.plantR.base.BaseFragment
import fr.devid.plantR.databinding.FragmentBugReportBinding
import fr.devid.plantR.manager.PagerManager
import fr.devid.plantR.ui.register.RegisterViewModel
import javax.inject.Inject

/**
 * LG
 */

class FragmentReportBug : BaseFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val profilViewModel: ProfileViewModel by activityViewModels { viewModelFactory }
    private val registerViewModel: RegisterViewModel by activityViewModels { viewModelFactory }
    private lateinit var binding: FragmentBugReportBinding
    private val adapter by lazy { PagerManager(childFragmentManager) }
    // private val data: FragmentGardenerSubscribeArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBugReportBinding.inflate(inflater, container, false)
        initView()
        return binding.root
    }

    private fun initView() {
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
        adapter.resetAll()
        adapter.addFragmentPage(FragmentCurrentReport("0"), getString(R.string.menu_1))
        adapter.addFragmentPage(FragmentClosedReport("1"), getString(R.string.menu_2))
        binding.tlMenuTabLayout.setupWithViewPager(binding.vpBugView)
        binding.vpBugView.offscreenPageLimit = 1; //before setAdapter
        binding.vpBugView.adapter = adapter
        //binding.tlMenuTabLayout.getTabAt(0)?.setIcon(R.drawable.mes_plantes_kit_mural)
    }

}
