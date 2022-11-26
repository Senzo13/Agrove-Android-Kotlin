package fr.devid.plantR.ui.login

import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import fr.devid.plantR.BuildConfig
import fr.devid.plantR.R
import fr.devid.plantR.base.BaseFragment
import fr.devid.plantR.base.onBackPressedCallBackNavControllerOrParent
import fr.devid.plantR.databinding.FragmentLoginBinding
import fr.devid.plantR.services.AlerterService
import fr.devid.plantR.services.FirebaseService
import fr.devid.plantR.services.Singleton
import fr.devid.plantR.ui.splash.ChangePasswordDialog
import java.lang.Exception
import javax.inject.Inject

class LoginFragment : BaseFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val loginViewModel: LoginViewModel by activityViewModels { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentLoginBinding.inflate(inflater, container, false)
        val navController = findNavController()
        bindUi(binding, navController)
        subscribeUi(binding, navController)
        Singleton.instance.hideLoadingScreenByLang()
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            onBackPressedCallBackNavControllerOrParent())
    }

    private fun bindUi(binding: FragmentLoginBinding, navController: NavController) {
        binding.etPassword.transformationMethod = PasswordTransformationMethod() // Hide password initially

        //It clickListener displays or hides the user's password.
        binding.ivVisibility.setOnClickListener {
            if (binding.etPassword.transformationMethod is PasswordTransformationMethod) {
                binding.ivVisibility.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_visibility_24, null)) // SHOW)
                binding.etPassword.transformationMethod = null
                binding.etPassword.setSelection(binding.etPassword.length())
            } else {
                binding.ivVisibility.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_visibility_off_24, null))
                binding.etPassword.transformationMethod = PasswordTransformationMethod()
                binding.etPassword.setSelection(binding.etPassword.length())
            }
        }

        if (BuildConfig.DEBUG) {
            //It allows if the project and in debug mode to use these connection information
          binding.etEmail.setText("compte_test@gmail.com")
          binding.etPassword.setText("password")
        }

        binding.btBle.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            if (email.isEmpty() || password.isEmpty()) {
                AlerterService.showError(getString(R.string.fill_all_fields), requireActivity())
            } else {
                FirebaseService().firebase.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        val user = FirebaseService().firebase.currentUser
                        if (it.isSuccessful) {
                            if (user != null) {
                                try {
                                    findNavController().popBackStack(R.id.splashFragment, false)
                                } catch(e : Exception) {
                                    println("Le fragment a connu une erreur de type not accociated with a fragment manager")
                                }
                            }
                        } else {
                            AlerterService.showError(
                                getString(R.string.WRONG_EMAIL_OR_PASSWORD),
                                requireActivity())
                        }
                    }
            }
        }

        binding.btRegister.setOnClickListener {
            //It allows navigation on the registration when clicking on the registration button.
            navController.navigate(
                LoginFragmentDirections.actionLoginFragmentToRegisterFragment(
                    false)
            )
        }

        binding.mbForgotPassword.setOnClickListener {
            // Typical use of a dialog(popup)
            val dialog = ChangePasswordDialog(requireContext()) { dialog, bool, string ->
                if (bool) {
                    //If we confirm the return of the email
                    FirebaseService().firebase.sendPasswordResetEmail(string).addOnSuccessListener {
                        activity?.let {
                            AlerterService.showGood("${context?.getString(R.string.SENDMAIL_CONFIRM)}", it)
                        }
                    }.addOnFailureListener {
                        activity?.let {
                            AlerterService.showError("${context?.getString(R.string.SENDMAIL_ERROR)}", it)
                        }
                    }
                    dialog.dismiss()
                }
                else {
                    if(string.isNotEmpty()) {
                        activity?.let { act ->
                            AlerterService.showError(string, act)
                        }
                    } else {
                        dialog.dismiss()
                    }
                }

            }
            dialog.show()
        }
    }

    private fun subscribeUi(binding: FragmentLoginBinding, navController: NavController) {
        loginViewModel.authenticationState.observe(viewLifecycleOwner, Observer {
            if (it == LoginViewModel.AuthenticationState.AUTHENTICATED) {
                navController.popBackStack()
            }
        })
        loginViewModel.isLoading.observe(viewLifecycleOwner, Observer {
            binding.isLoading = it
        })
        loginViewModel.loginState.observe(viewLifecycleOwner, Observer {
            val errorResource = when (it.getContentIfNotHandled() ?: return@Observer) {
                LoginViewModel.LoginState.FILL_FIELDS -> R.string.fill_all_fields
                LoginViewModel.LoginState.NO_INTERNET -> R.string.check_internet
                LoginViewModel.LoginState.NOT_ACTIVATED -> R.string.not_activated
                LoginViewModel.LoginState.WRONG_CREDENTIALS -> R.string.wrong_credentials
            }
            AlerterService.showError(getString(errorResource), requireActivity())
        })
    }
}