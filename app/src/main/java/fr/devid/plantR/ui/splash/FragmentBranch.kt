package fr.devid.plantR.ui.splash

import android.os.Bundle
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
import com.google.firebase.database.ktx.getValue
import fr.devid.plantR.base.BaseFragment
import fr.devid.plantR.databinding.FragmentBranchBinding
import fr.devid.plantR.services.FirebaseService
import fr.devid.plantR.ui.login.LoginViewModel
import javax.inject.Inject

class FragmentBranch : BaseFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val loginViewModel: LoginViewModel by activityViewModels { viewModelFactory }
    private lateinit var binding: FragmentBranchBinding
    private val data: FragmentBranchArgs by navArgs()
    private lateinit var userRef : DatabaseReference
    private lateinit var handleUserRef : ValueEventListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBranchBinding.inflate(inflater, container, false)
        initView()
        return binding.root
    }

    private fun initView() {
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        val user = FirebaseService().firebase.currentUser
        if (user != null) {
            userRef = FirebaseService().getUserByIdAddToOwners(user.uid)
            handleUserRef = userRef.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) { }
                override fun onDataChange(snapshot: DataSnapshot) {
                    val value = snapshot.getValue<String>()
                    when(value) {
                        "ok" -> {
                            //This is the information of the popup that manages the addition of a person in the team of his gardener.
                            binding.tvGardenerName.text = data.gardenerName
                            binding.pbLoadingSpinner.visibility = View.GONE
                            binding.cvBranch.visibility = View.VISIBLE
                            val idGardener = data.gardenerId.substring(2)
                            println("Gardener ID test $idGardener")
                            FirebaseService().getUserCurrentGardener(user.uid).setValue(idGardener)
                            removeEvent()
                        }
                        "ko" -> {
                            userRef.removeValue()
                            findNavController().popBackStack()
                        }
                        else -> {
                            binding.cvBranch.visibility = View.GONE
                            removeEvent()
                        }
                    }
                }
            })
            FirebaseService().getUserByIdAddToOwners(user.uid).setValue(data.gardenerId)
        } else {
            findNavController().popBackStack()
        }
    }

    private fun removeEvent() {
        if(::userRef.isInitialized) {
            userRef.removeValue() //Remove event KO
        }
    }
    override fun onStop() {
        super.onStop()
        //We switch everything to void/null to make sure it doesn't interfere again and removeEvent the handler.
        loginViewModel.sharedPreferencesService.isBranch = false
        loginViewModel.sharedPreferencesService.type = null
        loginViewModel.sharedPreferencesService.gardenerName = null
        loginViewModel.sharedPreferencesService.gardenerId = null
        if (::userRef.isInitialized && ::handleUserRef.isInitialized)
            userRef.removeEventListener(handleUserRef)
    }
}
