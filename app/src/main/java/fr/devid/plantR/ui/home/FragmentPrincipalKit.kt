package fr.devid.plantR.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import fr.devid.plantR.base.BaseFragment
import fr.devid.plantR.databinding.FragmentAddGardenerJumelageBinding
import fr.devid.plantR.services.AlerterService
import fr.devid.plantR.services.FirebaseService
import javax.inject.Inject

/**
 * LG
 */

class FragmentPrincipalKit : BaseFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var binding: FragmentAddGardenerJumelageBinding
    private lateinit var userRef: DatabaseReference
    private lateinit var handleRef: ValueEventListener
    private var userId : String? = null
    private val data: FragmentAddGardenerJumelageArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddGardenerJumelageBinding.inflate(inflater, container, false)
        initView()
        return binding.root
    }

    private fun initView() {


        binding.btNext.setOnClickListener {
            FirebaseService().firebase.currentUser?.uid?.let { uid ->
                findNavController().navigate(FragmentAddGardenerJumelageDirections.actionFragmentAddGardenerJumelageToFragmentAddGardenerJumelageAdress(data.guid, uid, data.type))
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
