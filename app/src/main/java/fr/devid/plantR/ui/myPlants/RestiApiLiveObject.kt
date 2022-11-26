package fr.devid.plantR.ui.myPlants

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.squareup.moshi.Json
import fr.devid.plantR.Constants
import fr.devid.plantR.R
import fr.devid.plantR.api.RequestJSON
import fr.devid.plantR.api.SendCommandLiveObject
import fr.devid.plantR.base.BaseFragment
import fr.devid.plantR.databinding.FragmentOneBinding
import fr.devid.plantR.models.WeatherApp
import fr.devid.plantR.services.AlerterService
import fr.devid.plantR.services.FirebaseService
import fr.devid.plantR.services.Singleton
import fr.devid.plantR.ui.home.ProfileViewModel
import fr.devid.plantR.viewmodels.Event
import okhttp3.*
import okhttp3.RequestBody.Companion.create
import org.json.JSONObject
import java.io.IOException
import javax.inject.Inject


class RestiApiLiveObject() :  BaseFragment() {
//
//    @Inject
//    lateinit var viewModelFactory: ViewModelProvider.Factory
//    private val profilViewModel: ProfileViewModel by activityViewModels { viewModelFactory }
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        binding = FragmentOneBinding.inflate(inflater, container, false)
//        return binding.root
//    }


}