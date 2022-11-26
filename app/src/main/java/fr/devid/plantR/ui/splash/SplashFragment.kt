package fr.devid.plantR.ui.splash

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieAnimationView
import fr.devid.plantR.R
import fr.devid.plantR.base.BaseFragment
import fr.devid.plantR.services.AlerterService
import fr.devid.plantR.services.FirebaseService
import fr.devid.plantR.ui.login.LoginViewModel
import java.util.*
import javax.inject.Inject

class SplashFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val loginViewModel: LoginViewModel by activityViewModels { viewModelFactory }
    var handler: Handler = Handler()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        when(Locale.getDefault().language.toString()) {
            "fr" -> {
                val view = inflater.inflate(R.layout.fragment_splash, container, false)
                AlerterService.showDialogLoaderSplash(context, false)
                return view
            }
            "eng" -> {
                println("DANS LE ENG")
               // view.findViewById<LottieAnimationView>(R.id.lt_javrvis_splash_eng).visibility = View.VISIBLE
                val view = inflater.inflate(R.layout.fragment_splash_eng, container, false)
                AlerterService.showDialogLoaderSplashEng(context, false)
                return view
            }
            else -> {
                println("DANS LE ENG")
                val view = inflater.inflate(R.layout.fragment_splash_eng, container, false)
                AlerterService.showDialogLoaderSplashEng(context, false)
                return view
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(sendData)
    }

    private val sendData: Runnable = object : Runnable {
        override fun run() {
            try {
            // Connection check, if the user is different from empty then we allow navigation, otherwise we go back to the loginGraph
                val user = FirebaseService().firebase.currentUser
                if (user != null) {
                    findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToHomeMainNavigation())
                } else {
                    findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToLoginGraph())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        //Added a delay to display the splash screen longer
        handler = Handler(Looper.getMainLooper())
        handler.postDelayed(sendData, 2400)

    }

    override fun onStop() {
        super.onStop()
    }
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}
