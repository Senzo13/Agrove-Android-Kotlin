package fr.devid.plantR.ui.subscribe

import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import fr.devid.plantR.R
import fr.devid.plantR.base.BaseFragment
import fr.devid.plantR.databinding.FragmentBePlantBinding
import fr.devid.plantR.databinding.FragmentBePlantSubscribeBinding
import fr.devid.plantR.manager.PagerManager
import fr.devid.plantR.services.AlerterService
import fr.devid.plantR.services.FirebaseService

class FragmentBePlantSubscribe : BaseFragment() {

    private lateinit var binding: FragmentBePlantSubscribeBinding
    private val adapter by lazy { PagerManager(childFragmentManager) }
    private val data: FragmentBePlantSubscribeArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBePlantSubscribeBinding.inflate(inflater, container, false)
        initView()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        println("ONRESUME")
        setupTabLayout()
    }

    private fun setupTabLayout() {
        adapter.resetAll()
        adapter.addFragmentPage(
            FragmentOneBePlant(
                data.keys,
                data.name,
                data.harvested,
                data.sowing,
                data.etagePosition,
                data.uid
            ), getString(R.string.be_plant_in_progress)
        )
//        adapter.addFragmentPage(
//            FragmentTwoBePlant(
//                data.keys,
//                data.name,
//                data.harvested,
//                data.sowing,
//                data.etagePosition,
//                data.uid
//            ), getString(R.string.be_plant_my_task)
//        )
        adapter.addFragmentPage(
            FragmentThreeBePlant(
                data.keys,
                data.name,
                data.harvested,
                data.sowing,
                data.etagePosition,
                data.uid
            ), getString(R.string.be_plant_helper)
        )

        binding.tlBePlant.setupWithViewPager(binding.vpBePlant)
        binding.vpBePlant.offscreenPageLimit = 2; //before setAdapter
        binding.vpBePlant.adapter = adapter
    }

    override fun onStop() {
        super.onStop()
        println("ONSTOP")
    }

    private fun initView() {

        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.tvTitleNamePlant.text = data.name
    }
}