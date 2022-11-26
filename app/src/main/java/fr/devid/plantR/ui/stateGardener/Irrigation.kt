package fr.devid.plantR.ui.stateGardener

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import fr.devid.plantR.R
import fr.devid.plantR.base.BaseFragment
import fr.devid.plantR.databinding.FragmentAdjustWateringBinding
import fr.devid.plantR.models.Irrigation
import fr.devid.plantR.services.AlerterService
import fr.devid.plantR.services.FirebaseService
import fr.devid.plantR.ui.home.ProfileViewModel
import javax.inject.Inject

class Irrigation : BaseFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val profilViewModel: ProfileViewModel by activityViewModels {
        viewModelFactory
    }
    private lateinit var binding: FragmentAdjustWateringBinding
    private lateinit var irrigationRef: DatabaseReference
    private lateinit var handleIrrigation: ValueEventListener
    private lateinit var threshold: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdjustWateringBinding.inflate(inflater, container, false)
        initView()
        return binding.root
    }

    fun initView() {
        threshold = binding.etThreshold as TextView

        getDataFromFirebase()

        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btAdjustWatering.setOnClickListener {
            if (binding.btAdjustWatering.text == getString(R.string.tv_button_define)) {
                sendCommand()
            } else if (binding.btAdjustWatering.text == getString(R.string.tv_button_defined)) {
                findNavController().popBackStack()
            } else {
                sendCommand()
            }
        }
    }

    private fun sendCommand() {
        if (isInRange(1, 100, threshold.text.trimEnd().toString().toInt())) {
            profilViewModel.userService.gardenerId?.let { guid ->
                FirebaseService().getGardenerByIrrigation(guid).child("payload")
                    .setValue("61;135;138,0,0,1;130,${threshold.text},0,15;14,0,0,0,1;138;134;62;;")
                    .addOnCompleteListener {
                        if (it.isComplete) {
                            AlerterService.showGood(
                                getString(R.string.alerter_modif_threshold),
                                requireActivity()
                            )
                        }
                    }
            }
        } else {
            AlerterService.showError(
                getString(R.string.alerter_choice_number_irrig),
                requireActivity()
            )
        }
    }

    fun getDataFromFirebase() {
        profilViewModel.userService.gardenerId?.let { guid ->
            irrigationRef = FirebaseService().getGardenerByIrrigation(guid)
            handleIrrigation = irrigationRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.getValue(Irrigation::class.java)?.let {
                        if (it.received.request.value.data.contains("61;135;138,0,0,1;130")) {
                            val splited = it.received.request.value.data.split(',')
                            val numberShowing = splited[4]
                            threshold.text = numberShowing
                            setText(it)
                        }
                    }

                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }

    fun setText(irrigation: Irrigation) {
        val button = binding.btAdjustWatering
        val isProcessed = irrigation.received.status == "PROCESSED" || irrigation.received.status == "" || irrigation.received.status == null
        val isCanceled = irrigation.received.status == "CANCELED" || irrigation.received.status == "ERROR" || irrigation.received.status == "EXPIRED"
        binding.tvTitleIrrigation.text =
            if (isProcessed) getString(R.string.tv_title_adjust_watering) else if (isCanceled) getString(
                R.string.tv_title_echec_irrig
            ) else getString(R.string.tv_current_setting)
        binding.tvDescriptionIrrigation.text =
            if (isProcessed) getString(R.string.tv_body_adjust_watering) else if (isCanceled) getString(
                R.string.tv_description_echec_irrig
            ) else getString(R.string.tv_actualise_threshold)
        if (isProcessed) {
            button.text = getString(R.string.tv_button_define)
            binding.etThreshold.isFocusable = true
            binding.etThreshold.isEnabled = true
        } else if (isCanceled) {
            button.text = getString(R.string.tv_btn_echec_irrig)
        } else {
            button.text = getString(R.string.tv_button_defined)
            binding.etThreshold.inputType = 0
            binding.etThreshold.isFocusable = false
        }
    }

    private fun isInRange(a: Int, b: Int, c: Int): Boolean {
        return if (b > a) c in a..b else c in b..a
    }

    override fun onStop() {
        super.onStop()
        if (::irrigationRef.isInitialized && ::handleIrrigation.isInitialized) {
            irrigationRef.removeEventListener(handleIrrigation)
        }
    }
}