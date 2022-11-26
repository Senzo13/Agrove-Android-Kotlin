package fr.devid.plantR.ui.about

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import fr.devid.plantR.base.BaseFragment
import fr.devid.plantR.ui.login.LoginViewModel
import javax.inject.Inject

class AboutFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val loginViewModel: LoginViewModel by activityViewModels { viewModelFactory }



    private fun bindUi() {
        val context = requireContext()
        val info = context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_ACTIVITIES)

    }
}
