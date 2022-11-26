package fr.devid.plantR.ui.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import fr.devid.plantR.models.Metadata
import fr.devid.plantR.R
import fr.devid.plantR.base.BaseFragment
import fr.devid.plantR.databinding.FragmentRegisterBinding
import fr.devid.plantR.services.AlerterService
import fr.devid.plantR.services.FirebasePath
import fr.devid.plantR.services.FirebaseService
import javax.inject.Inject

class RegisterFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private var database = FirebasePath()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentRegisterBinding.inflate(inflater, container, false)
        bindUi(binding)
        return binding.root
    }

    private fun bindUi(binding: FragmentRegisterBinding) {
        binding.checkBox.isChecked = false

        binding.ivArrowBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.clInfos.setOnClickListener {
            findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToPolicyFragment2())
        }

        binding.clCheckbox.setOnClickListener {
            binding.checkBox.isChecked = binding.checkBox.isChecked == false
        }
        binding.btRegister.setOnClickListener {
            //The registration layout information is retrieved here and then processed.

            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val nom = binding.etNom.text.toString().trim()
            val prenom = binding.etPrenom.text.toString().trim()
            val confirmPassword = binding.etPasswordConfirm.text.toString().trim()

            if (email.isEmpty() || password.isEmpty() || nom.isEmpty() || prenom.isEmpty() || confirmPassword.isEmpty()) {
                AlerterService.showError(getString(R.string.fill_all_fields), requireActivity())
            } else if (password != confirmPassword) {
                AlerterService.showError(getString(R.string.different_password), requireActivity())
            }
            else if (password.count() <= 5){
                AlerterService.showError(getString(R.string.WEAK_PASSWORD), requireActivity())
            }
            else if(!binding.checkBox.isChecked) {
                AlerterService.showError(getString(R.string.cgu_alert), requireActivity())
            } else {

                FirebaseService().firebase.createUserWithEmailAndPassword(email, password)
                    .addOnFailureListener {
                        println("It failure $it")
                        println("message : ${it.localizedMessage}\n${it.message}")
                        // Here I retrieve the localized message, and I process the errors they return in order to manage the different cases.
                        when (it.localizedMessage) {
                            "The email address is badly formatted." -> {
                                AlerterService.showError(getString(R.string.INVALID_EMAIL), requireActivity())
                            }
                            "The email address is already in use by another account." ->  {
                                AlerterService.showError(getString(R.string.EMAIL_ALREADY_USE), requireActivity())
                            }
                        }
                    }
                    .addOnSuccessListener {
                        println("It success $it")
                        val key = FirebaseService().firebase.currentUser?.uid
                        val users = Metadata(prenom, nom)
                        val postValues = users.toMap()
                        val childUpdates = HashMap<String, Any>()
                        childUpdates["/users/${key}/metadata/"] = postValues
                        database.ref.updateChildren(childUpdates)
                        findNavController().popBackStack(R.id.splashFragment, false)
                        if (key != null) {
                           setCurrentGardener(key)
                        }
                    }
            }
        }
    }

    private fun setCurrentGardener(uid : String) {
         FirebaseService().getUserCurrentGardener(uid).setValue("")
    }
}
