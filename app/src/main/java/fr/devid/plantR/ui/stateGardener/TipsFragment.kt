package fr.devid.plantR.ui.stateGardener

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import fr.devid.plantR.R
import fr.devid.plantR.base.BaseFragment
import fr.devid.plantR.databinding.FragmentConseilsBinding
import fr.devid.plantR.models.GardenerTips
import fr.devid.plantR.models.User
import fr.devid.plantR.services.FirebaseService
import fr.devid.plantR.services.Singleton
import fr.devid.plantR.ui.home.ProfileViewModel
import javax.inject.Inject

/**
 * LG
 */

class TipsFragment : BaseFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val profilViewModel: ProfileViewModel by activityViewModels { viewModelFactory }
    private val PAGE = "**** TipsFragment ****"
    private val data: TipsFragmentArgs by navArgs()
    private lateinit var binding : FragmentConseilsBinding
    private lateinit var userRef : DatabaseReference
    private lateinit var handleUser : ValueEventListener
    private var tipsName : String? = null
    private var tipsData : GardenerTips? = null
    private var gardenerId : String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentConseilsBinding.inflate(inflater,container,false)
        initView()
        return binding.root
    }

    private fun initView() {
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btSuccess.setOnClickListener {
            if(Singleton.instance.connectedState) {
                if(profilViewModel.userService.gardenerNameBle == profilViewModel.userService.gardenerName) {
                    when(data.name) {
                        "battery" -> {
                          Singleton.instance.batteryTips = true
                            findNavController().popBackStack(R.id.plantrFragment, false)
                        }
                        "capa" -> {
                            Singleton.instance.capaTips = true
                            findNavController().popBackStack(R.id.plantrFragment, false)
                        }
                    }
                }
            } else {
                setTips(data.gardenerID)
                requireActivity().let {
                    findNavController().popBackStack(R.id.plantrFragment, false)
                }
            }

        }

        binding.btEchec.setOnClickListener {
            findNavController().popBackStack()
        }

        if(Singleton.instance.connectedState) {
            setupViewBle(data.name)
        } else {
            setupView(data.name)
        }
    }

    private fun setTips(gardenerId : String) {
        when(data.name) {
            "humidity" ->  {
                FirebaseService().getGardenerTips(gardenerId).child("humidity").setValue(false)
            }
            "waterLevel" -> {
                FirebaseService().getGardenerTips(gardenerId).child("waterLevel").setValue(false)
            }
            "battery" -> {
                FirebaseService().getGardenerTips(gardenerId).child("battery").setValue(false)
            }
            "capa" -> {
                FirebaseService().getGardenerTips(gardenerId).child("capa").setValue(false)
            }
            "temperature" -> {
                FirebaseService().getGardenerTips(gardenerId).child("temperature").setValue(false)
            }
        }
    }
    private fun setupView(name : String) {
            when (name) {
                "battery" -> {
                    binding.tvTitle.text = getString(R.string.tv_title_alert_battery)
                    binding.tvMiddle.text = getString(R.string.tv_description_alert_battery)
//                    binding.tvEchec.text = getString(R.string.tv_button_problem_alert_battery)
//                    binding.tvSuccess.text = getString(R.string.tv_button_ok_alert_battery)
                    tipsName = "battery"
                }
                "capa" -> {
                    binding.tvTitle.text = getString(R.string.tv_title_alert_humidity)
                    binding.tvMiddle.text = getString(R.string.tv_description_alert_humidity)
//                    binding.tvEchec.text = getString(R.string.tv_button_problem_alert_humidity)
//                    binding.tvSuccess.text = getString(R.string.tv_button_ok_alert_humidity)
                    tipsName = "capa"
                }
                "waterLevel" -> {
                    binding.tvTitle.text = getString(R.string.tv_title_alert_water)
                    binding.tvMiddle.text = getString(R.string.tv_description_alert_water)
//                    binding.tvEchec.text = getString(R.string.tv_button_problem_alert_water)
//                    binding.tvSuccess.text = getString(R.string.tv_button_ok_alert_water)
                    tipsName = "waterLevel"
                }
                "temperature" -> {
                    binding.tvTitle.text = getString(R.string.tv_title_alert_temperature)
                    binding.tvMiddle.text = getString(R.string.tv_description_alert_temperature)
//                    binding.tvEchec.text = getString(R.string.tv_button_problem_alert_temperature)
//                    binding.tvSuccess.text = getString(R.string.tv_button_ok_alert_temperature)
                    tipsName = "temperature"
                }
        }
    }

    private fun setupViewBle(name : String) {
        when (name) {
            "battery" -> {
                binding.tvTitle.text = getString(R.string.tv_title_alert_battery)
                binding.tvMiddle.text = getString(R.string.tv_description_alert_battery)
//                    binding.tvEchec.text = getString(R.string.tv_button_problem_alert_battery)
//                    binding.tvSuccess.text = getString(R.string.tv_button_ok_alert_battery)
                tipsName = "battery"
            }
            "capa" -> {
                binding.tvTitle.text = getString(R.string.tv_title_alert_humidity)
                binding.tvMiddle.text = getString(R.string.tv_description_alert_humidity)
//                    binding.tvEchec.text = getString(R.string.tv_button_problem_alert_humidity)
//                    binding.tvSuccess.text = getString(R.string.tv_button_ok_alert_humidity)
                tipsName = "capa"
            }
            "waterLevel" -> {
                binding.tvTitle.text = getString(R.string.tv_title_alert_water)
                binding.tvMiddle.text = getString(R.string.tv_description_alert_water)
//                    binding.tvEchec.text = getString(R.string.tv_button_problem_alert_water)
//                    binding.tvSuccess.text = getString(R.string.tv_button_ok_alert_water)
                tipsName = "waterLevel"
            }
            "temperature" -> {
                binding.tvTitle.text = getString(R.string.tv_title_alert_temperature)
                binding.tvMiddle.text = getString(R.string.tv_description_alert_temperature)
//                    binding.tvEchec.text = getString(R.string.tv_button_problem_alert_temperature)
//                    binding.tvSuccess.text = getString(R.string.tv_button_ok_alert_temperature)
                tipsName = "temperature"
            }
        }
    }
    override fun onStop() {
        super.onStop()
        removeListener()
    }

    private fun removeListener() {
        if(::userRef.isInitialized && ::handleUser.isInitialized) {
            userRef.removeEventListener(handleUser)
        }
    }
}
