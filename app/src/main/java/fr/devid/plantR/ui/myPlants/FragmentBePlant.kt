package fr.devid.plantR.ui.myPlants

import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import fr.devid.plantR.R
import fr.devid.plantR.base.BaseFragment
import fr.devid.plantR.databinding.FragmentBePlantBinding
import fr.devid.plantR.manager.PagerManager
import fr.devid.plantR.models.*
import fr.devid.plantR.services.AlerterService
import fr.devid.plantR.services.FirebaseService
import fr.devid.plantR.ui.home.ProfileViewModel
import java.util.*
import javax.inject.Inject

class FragmentBePlant : BaseFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val profilViewModel: ProfileViewModel by activityViewModels { viewModelFactory }
    private lateinit var binding: FragmentBePlantBinding
    private val adapter by lazy { PagerManager(childFragmentManager) }
    private val data: FragmentBePlantArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBePlantBinding.inflate(inflater, container, false)
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

        setNuisiblesFragmentByLang()

        adapter.addFragmentPage(

            FragmentOneBePlant(
                data.keys,
                data.name,
                data.harvested,
                data.sowing,
                data.etagePosition,
                data.uid), getString(R.string.be_plant_in_progress))

        adapter.addFragmentPage(
            FragmentThreeBePlant(
                data.keys,
                data.name,
                data.harvested,
                data.sowing,
                data.etagePosition,
                data.uid
            ), getString(R.string.be_plant_helper))

        binding.tlBePlant.setupWithViewPager(binding.vpBePlant)
        binding.vpBePlant.offscreenPageLimit = 2; //before setAdapter
        binding.vpBePlant.adapter = adapter

    }

    private fun setNuisiblesFragmentByLang() {
        when(Locale.getDefault().language.toString()) {
            "fr" -> {
                adapter.addFragmentPage(
                        FragmentTwoBePlant(
                                data.keys,
                                data.name,
                                data.harvested,
                                data.sowing,
                                data.etagePosition,
                                data.uid
                        ), getString(R.string.be_plant_my_task))
            }
            "en" -> {

            }
            else -> {
                adapter.addFragmentPage(
                        FragmentTwoBePlant(
                                data.keys,
                                data.name,
                                data.harvested,
                                data.sowing,
                                data.etagePosition,
                                data.uid
                        ), getString(R.string.be_plant_my_task))
            }
        }
    }

    override fun onStop() {
        super.onStop()
        println("ONSTOP")
    }

    private fun initView() {
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.ivPopupOption.setOnClickListener {
            setPopupView()
        }

        binding.tvTitleNamePlant.text = data.name
        println("LE NOM DE LA PLANTE EST = " + data.name)
    }

    private fun setPopupView() {
        val wrapper = ContextThemeWrapper(binding.ivPopupOption.context, R.style.PopupMenuPlantR)
        val popup = PopupMenu(wrapper, binding.ivPopupOption)
        popup.inflate(R.menu.menu_item)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_delete -> {
                    val popupDeletePlant = PopupDeleteBePlant(requireContext()) { popup, bool, _ ->
                        if (bool) {
                            FirebaseService().getGardenerPlantsById(data.uid)
                                .child("${data.etagePosition[0]}-${data.etagePosition[2]}")
                                .removeValue()
                                .addOnSuccessListener {
                                    activity?.let {
                                        AlerterService.showGood(context?.getString(R.string.DELETE_PLANT)!!, it)
                                    }
                                }.addOnFailureListener {
                                    activity?.let {
                                        AlerterService.showError(
                                            context?.getString(R.string.ECHEC_DELETE_PLANT)!!,
                                            it
                                        )
                                    }
                                }
                            findNavController().popBackStack()
                            popup.dismiss()
                        }
                        else {
                            popup.dismiss()
                        }
                    }
                    popupDeletePlant.show()
                }
                R.id.action_modify -> {
                    findNavController().navigate(
                        FragmentBePlantDirections.actionFragmentBePlantToFragmentEditPlant(
                            data.uid,
                            data.etagePosition,
                            data.name
                        )
                    )
                }
            }
            true
        }
        popup.show()
    }





}