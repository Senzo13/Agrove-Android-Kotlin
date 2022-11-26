package fr.devid.plantR.ui.irrig

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import fr.devid.plantR.base.BaseFragment
import fr.devid.plantR.databinding.FragmentIrrigBinding
import fr.devid.plantR.ui.home.ProfileViewModel
import fr.devid.plantR.utils.DownlinkUtilsJava
import javax.inject.Inject


class Irrig : BaseFragment() {
        @Inject
        lateinit var viewModelFactory: ViewModelProvider.Factory
        private val profilViewModel: ProfileViewModel by activityViewModels { viewModelFactory }
        private lateinit var gardenerRef: DatabaseReference
        private lateinit var handleGardener: ValueEventListener
        private lateinit var binding : FragmentIrrigBinding
        private val PAGE: String = "****** FragmentIrrig ******"

        override fun onCreateView(
                inflater: LayoutInflater, container: ViewGroup?,
                savedInstanceState: Bundle?
        ): View? {
            binding = FragmentIrrigBinding.inflate(inflater, container, false)
            initView()
            return binding.root
        }

        private fun initView() {

            binding.ivBack.setOnClickListener {
                findNavController().popBackStack()
            }
            //Envoie d'une payload au capteur de la personne
            val thread = Thread(Runnable {
                try {
                   // DownlinkUtilsJava().addCommand()
                } catch (e: Exception) {
                    println(e.message)
                }
            })
            thread.start()
        }

        override fun onStop() {
            super.onStop()
            removeObserver()
        }

        private fun removeObserver() {

        }

        private fun hideLoading() {

        }
    fun runOnUiThread(runnable: Runnable) {
        if (isAdded) requireActivity().runOnUiThread(runnable)
    }
    }

