package fr.devid.plantR.ui.stateGardener
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.gson.GsonBuilder
import fr.devid.plantR.Constants
import fr.devid.plantR.R
import fr.devid.plantR.base.BaseFragment
import fr.devid.plantR.comm.Observer
import fr.devid.plantR.comm.ObserverManager
import fr.devid.plantR.data.BleDevice
import fr.devid.plantR.databinding.FragmentPlantrBinding
import fr.devid.plantR.models.*
import fr.devid.plantR.services.AlerterService
import fr.devid.plantR.services.FirebaseService
import fr.devid.plantR.services.Singleton
import fr.devid.plantR.ui.home.ProfileViewModel
import fr.devid.plantR.utils.DateFormatUtils
import fr.devid.plantR.utils.ProgressBarUtils
import okhttp3.*
import java.io.IOException
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap
import kotlin.collections.set
import kotlin.math.round

private lateinit var binding: FragmentPlantrBinding
private lateinit var userRef: DatabaseReference
private lateinit var handleUser: ValueEventListener
private lateinit var gardenersTipsRef: DatabaseReference
private lateinit var handleGardenersTips: ValueEventListener
private lateinit var gardenersStatsRef: DatabaseReference
private lateinit var handleGardenerStatsData: ValueEventListener
private lateinit var loraFailureRef: DatabaseReference
private lateinit var handleLoraFailure: ValueEventListener
private lateinit var gardenersParentRef: DatabaseReference
private lateinit var handleGardenersParent: ValueEventListener
private lateinit var gardenerStatsRefParentCheck: DatabaseReference
private lateinit var handleGardenerStatsRefParentCheck: ValueEventListener
private lateinit var gardenersClimatRef: DatabaseReference
private lateinit var handleGardenerClimatData: ValueEventListener
private lateinit var gardenersImagesRef: DatabaseReference
private lateinit var handleGardenerImagesData: ValueEventListener
private lateinit var userPictureRef: DatabaseReference
private lateinit var handleUserPictureRef: ValueEventListener
private lateinit var getCityRef : DatabaseReference
private lateinit var handleGetCityRef : ValueEventListener
var ratio = 4
private lateinit var gardenerStatsRefParent: DatabaseReference
private lateinit var handleGardenerStatsRefParent: ValueEventListener
private val PERMISSION_REQUEST_CODE: Int = 101
private val API_NETWORK_SUCCESS = 200

class PlantrFragment : BaseFragment(), Observer {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val profilViewModel: ProfileViewModel by activityViewModels { viewModelFactory }
    private lateinit var plantAdapter: PlantAdapter
    private var arrayTips: HashMap<String, Boolean> = HashMap()
    private var currentGardener: String? = null
    private lateinit var dataGardenerRef : DatabaseReference
    private lateinit var dataGardenerHandle : ValueEventListener
    private lateinit var dataGardenerType : DatabaseReference
    private lateinit var dataGardenerTypeHandle : ValueEventListener
    private var singleton: Singleton = Singleton.instance
    private var nameTipsGardener: Boolean? = null
    val handler = Handler()
    var test: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlantrBinding.inflate(inflater, container, false)

        //localLang
        binding.localLang = Locale.getDefault().language.toString()

        initView()

        ObserverManager.getInstance().addObserver(this)
        return binding.root
    }

    private fun getTypeGarden() {
        profilViewModel.userService.gardenerId.let { gardenerId ->
            dataGardenerType = FirebaseService().getGardenerById(gardenerId!!)
            dataGardenerTypeHandle = dataGardenerType.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var data =  snapshot.getValue(GardenerStage::class.java)
                    println("ICI CACA" + data)

                    if (data != null) {
                        if (data.type != null) {
                            if (data.type == "carre" || data.type == "pot" || data.type == "jardiniere") {
                                when (data.type) {
                                    "pot" -> {
                                        when (profilViewModel.userService.dimension) {
                                            0 -> {
                                                binding.ivBackgroundJardiniere.setImageDrawable(
                                                    resources.getDrawable(
                                                        R.drawable.mon_potager_pot_petit,
                                                        null
                                                    )
                                                )
                                                binding.ivBackgroundJardiniere.visibility = View.VISIBLE

                                            }
                                            1 -> {
                                                binding.ivBackgroundJardiniere.setImageDrawable(
                                                    resources.getDrawable(
                                                        R.drawable.mon_potager_pot_moyen,
                                                        null
                                                    )
                                                )
                                                binding.ivBackgroundJardiniere.visibility = View.VISIBLE

                                            }
                                            2 -> {
                                                binding.ivBackgroundJardiniere.setImageDrawable(
                                                    resources.getDrawable(
                                                        R.drawable.mon_potager_pot_grand,
                                                        null
                                                    )
                                                )
                                                binding.ivBackgroundJardiniere.visibility = View.VISIBLE

                                            }
                                        }
                                    }
                                    "jardiniere" -> {
                                        when (profilViewModel.userService.dimension) {
                                            0 -> {
                                                try {
                                                    binding.ivBackgroundJardiniere.setImageDrawable(
                                                        resources.getDrawable(
                                                            R.drawable.mon_potager_jardiniere_petit,
                                                            null
                                                        )
                                                    )
                                                    binding.ivBackgroundJardiniere.visibility = View.VISIBLE

                                                } catch (exception: IllegalStateException) {
                                                    println("Exception state ligne 949")
                                                }
                                            }
                                            1 -> {
                                                try {
                                                    binding.ivBackgroundJardiniere.setImageDrawable(
                                                        resources.getDrawable(
                                                            R.drawable.mon_potager_jardiniere_moyen,
                                                            null
                                                        )
                                                    )
                                                    binding.ivBackgroundJardiniere.visibility = View.VISIBLE

                                                } catch (exception: IllegalStateException) {
                                                    println("Exception state ligne 964")
                                                }

                                            }
                                            2 -> {
                                                try {
                                                    binding.ivBackgroundJardiniere.setImageDrawable(
                                                        resources.getDrawable(
                                                            R.drawable.mon_potager_jardiniere_grand,
                                                            null
                                                        )
                                                    )
                                                    binding.ivBackgroundJardiniere.visibility = View.VISIBLE

                                                } catch (exception: IllegalStateException) {
                                                    println("Exception state ligne 979")
                                                }

                                            }
                                        }
                                    }
                                    "carre" -> {
                                        when (profilViewModel.userService.dimension) {
                                            0 -> {
                                                try {
                                                    binding.ivBackgroundJardiniere.setImageDrawable(
                                                        resources.getDrawable(
                                                            R.drawable.petit_carre_potager,
                                                            null
                                                        )
                                                    )
                                                    binding.ivBackgroundJardiniere.visibility = View.VISIBLE
                                                }catch (exception: IllegalStateException) {
                                                    println("IllegalStateException ligne 997")
                                                }
                                            }
                                            1 -> {
                                                try {
                                                    binding.ivBackgroundJardiniere.setImageDrawable(
                                                        resources.getDrawable(
                                                            R.drawable.moyen_carre_potager,
                                                            null
                                                        )
                                                    )
                                                    binding.ivBackgroundJardiniere.visibility = View.VISIBLE
                                                } catch (exception: IllegalStateException) {
                                                    println("IllegalStateException ligne 1010")
                                                }
                                            }
                                            2 -> {
                                                try {
                                                    binding.ivBackgroundJardiniere.setImageDrawable(
                                                        resources.getDrawable(
                                                            R.drawable.grand_carre_potager,
                                                            null
                                                        )
                                                    )
                                                    binding.ivBackgroundJardiniere.visibility = View.VISIBLE
                                                } catch (exception: IllegalStateException) {
                                                    println("IllegalStateException ligne 1023")
                                                }
                                            }
                                        }
                                    }
                                }

                            } else {
                                when (data.type) {
                                    "parcelle" -> {
                                        try {
                                            binding.ivBackgroundJardiniere.setImageDrawable(
                                                resources.getDrawable(
                                                    R.drawable.kit_4_etages,
                                                    null
                                                )
                                            )
                                            binding.ivBackgroundJardiniere.visibility = View.VISIBLE

                                        } catch (exception: IllegalStateException) {
                                            println("Exception state ligne 839")
                                        }
                                    }
                                    "cle_en_main" -> {
                                        when (data.stage) {
                                            "2" -> {
                                                try {
                                                    binding.ivBackgroundJardiniere.setImageDrawable(
                                                        resources.getDrawable(
                                                            R.drawable.kit_2_etages,
                                                            null
                                                        )
                                                    )
                                                    binding.ivBackgroundJardiniere.visibility = View.VISIBLE

                                                } catch (exception: IllegalStateException) {
                                                    println("Exception state ligne 839")
                                                }
                                            }
                                            "3" -> {
                                                try {
                                                    binding.ivBackgroundJardiniere.setImageDrawable(
                                                        resources.getDrawable(
                                                            R.drawable.kit_3_etages,
                                                            null
                                                        )
                                                    )
                                                    binding.ivBackgroundJardiniere.visibility = View.VISIBLE

                                                } catch (exception: IllegalStateException) {
                                                    println("Exception state ligne 851")
                                                }
                                            }
                                            "4" -> {
                                                try {
                                                    binding.ivBackgroundJardiniere.setImageDrawable(
                                                        resources.getDrawable(
                                                            R.drawable.kit_4_etages,
                                                            null
                                                        )
                                                    )
                                                    binding.ivBackgroundJardiniere.visibility = View.VISIBLE

                                                } catch (exception: IllegalStateException) {
                                                    println("Exception state ligne 864")
                                                }
                                            }
                                        }
                                    }
                                    "capteur_pot" -> {
                                        when (profilViewModel.userService.dimension) {
                                            0 -> {
                                                try {
                                                    binding.ivBackgroundJardiniere.setImageDrawable(
                                                        resources.getDrawable(
                                                            R.drawable.kit_capteur_pot_petit, null
                                                        )
                                                    )
                                                    binding.ivBackgroundJardiniere.visibility = View.VISIBLE

                                                } catch (exception: IllegalStateException) {
                                                    println("Exception state ligne 874")
                                                }

                                            }
                                            1 -> {
                                                try {
                                                    binding.ivBackgroundJardiniere.setImageDrawable(
                                                        resources.getDrawable(
                                                            R.drawable.kit_capteur_pot_moyen,
                                                            null
                                                        )
                                                    )
                                                    binding.ivBackgroundJardiniere.visibility = View.VISIBLE

                                                } catch (exception: IllegalStateException) {
                                                    println("Exception state ligne 892")
                                                }

                                            }
                                            2 -> {
                                                try {
                                                    binding.ivBackgroundJardiniere.setImageDrawable(
                                                        resources.getDrawable(
                                                            R.drawable.kit_capteur_pot_grand,
                                                            null
                                                        )
                                                    )
                                                    binding.ivBackgroundJardiniere.visibility = View.VISIBLE

                                                } catch (exception: IllegalStateException) {
                                                    println("Exception state ligne 904")
                                                }

                                            }
                                        }
                                    }
                                    "capteur_carre" -> {
                                        when (profilViewModel.userService.dimension) {
                                            0 -> {
                                                try {
                                                    binding.ivBackgroundJardiniere.setImageDrawable(
                                                        resources.getDrawable(
                                                            R.drawable.capteurcarre_petit_vert, null
                                                        )
                                                    )
                                                    binding.ivBackgroundJardiniere.visibility = View.VISIBLE

                                                } catch (exception: IllegalStateException) {
                                                    println("Exception state ligne 874")
                                                }

                                            }
                                            1 -> {
                                                try {
                                                    binding.ivBackgroundJardiniere.setImageDrawable(
                                                        resources.getDrawable(
                                                            R.drawable.capteurcarre_moyen_vert, null
                                                        )
                                                    )
                                                    binding.ivBackgroundJardiniere.visibility = View.VISIBLE

                                                } catch (exception: IllegalStateException) {
                                                    println("Exception state ligne 892")
                                                }

                                            }
                                            2 -> {
                                                try {
                                                    binding.ivBackgroundJardiniere.setImageDrawable(
                                                        resources.getDrawable(
                                                            R.drawable.capteurcarre_grand_vert, null
                                                        )
                                                    )
                                                    binding.ivBackgroundJardiniere.visibility = View.VISIBLE

                                                } catch (exception: IllegalStateException) {
                                                    println("Exception state ligne 904")
                                                }

                                            }
                                        }
                                    }
                                    "capteur_jardiniere" -> {
                                        when (profilViewModel.userService.dimension) {
                                            0 -> {
                                                try {
                                                    binding.ivBackgroundJardiniere.setImageDrawable(
                                                        resources.getDrawable(
                                                            R.drawable.kit_capteur_jardiniere_petit,
                                                            null
                                                        )
                                                    )
                                                    binding.ivBackgroundJardiniere.visibility = View.VISIBLE

                                                } catch (exception: IllegalStateException) {
                                                    println("Exception state ligne 924")
                                                }

                                            }
                                            1 -> {
                                                try {
                                                    binding.ivBackgroundJardiniere.setImageDrawable(
                                                        resources.getDrawable(
                                                            R.drawable.kit_capteur_jardiniere_moyen,
                                                            null
                                                        )
                                                    )
                                                    binding.ivBackgroundJardiniere.visibility = View.VISIBLE

                                                } catch (exception: IllegalStateException) {
                                                    println("Exception state ligne 934")
                                                }

                                            }
                                            2 -> {
                                                try {
                                                    binding.ivBackgroundJardiniere.setImageDrawable(
                                                        resources.getDrawable(
                                                            R.drawable.kit_capteur_jardiniere_grand,
                                                            null
                                                        )
                                                    )
                                                    binding.ivBackgroundJardiniere.visibility = View.VISIBLE

                                                } catch (exception: IllegalStateException) {
                                                    println("Exception state ligne 948")
                                                }

                                            }
                                        }
                                    }
                                    "capteur_carre" -> {
                                        when (profilViewModel.userService.dimension) {
                                            0 -> {
                                                try {
                                                    binding.ivBackgroundJardiniere.setImageDrawable(
                                                        resources.getDrawable(
                                                            R.drawable.kit_capteur_jardiniere_petit,
                                                            null
                                                        )
                                                    )
                                                    binding.ivBackgroundJardiniere.visibility = View.VISIBLE

                                                } catch (exception: IllegalStateException) {
                                                    println("Exception state ligne 924")
                                                }

                                            }
                                            1 -> {
                                                try {
                                                    binding.ivBackgroundJardiniere.setImageDrawable(
                                                        resources.getDrawable(
                                                            R.drawable.kit_capteur_jardiniere_moyen,
                                                            null
                                                        )
                                                    )
                                                    binding.ivBackgroundJardiniere.visibility = View.VISIBLE

                                                } catch (exception: IllegalStateException) {
                                                    println("Exception state ligne 934")
                                                }

                                            }
                                            2 -> {
                                                try {
                                                    binding.ivBackgroundJardiniere.setImageDrawable(
                                                        resources.getDrawable(
                                                            R.drawable.kit_capteur_jardiniere_grand,
                                                            null
                                                        )
                                                    )
                                                    binding.ivBackgroundJardiniere.visibility = View.VISIBLE

                                                } catch (exception: IllegalStateException) {
                                                    println("Exception state ligne 948")
                                                }

                                            }
                                        }
                                    }
                                    "mural" -> {
                                        when (data.stage) {
                                            "1" -> {
                                                try {
                                                    binding.ivBackgroundJardiniere.setImageDrawable(
                                                        resources.getDrawable(
                                                            R.drawable.kit_mural,
                                                            null
                                                        )
                                                    )

                                                    binding.ivBackgroundJardiniere.visibility = View.VISIBLE

                                                } catch (exception: IllegalStateException) {
                                                    println("Exception state ligne 964")
                                                }

                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }
    }

    private fun setViewIfGeolocalisationCityName() {
            profilViewModel.userService.typeGarden?.let {type ->
                println("J'ai effectivement un type de jardiniere : " + type)
                    if(type != "cle_en_main" && type != "mural" && type != "parcelle" && type != "capteur_pot" && type != "capteur_carre" && type != "capteur_jardiniere") {
                        binding.cvGestion.visibility =View.GONE
                        binding.tvStateClimatTitle.visibility = View.GONE
                        setShowGeolocPopup()
                    } else {
                            when (type) {
                                "cle_en_main" -> {
                                    setViewVisibleCleEnMain()
                                }
                                "parcelle" -> {
                                    println("OKKK MEC")
                                    setViewVisibleParcelle()
                                }
                                "mural" -> {
                                    setViewVisibleKitMural()
                                }
                                "capteur_pot" -> {
                                    println("Capteur pot yesss")
                                    setViewVisibleCapteurPot()
                                }
                                "capteur_jardiniere" -> {
                                    setViewVisibleCapteurJardiniere()
                                }
                                "capteur_carre" -> {
                                    setViewVisibleCapteurCarre()
                                }
                            }

                    }

            }
    }

    private fun setShowGeolocPopup() {
        val popup = GeolocPopup(requireContext()) { dialog, bool, choice ->
            if(choice == "fermer") {
                dialog.dismiss()
            }
        }
        popup.show()
    }


    private fun setViewVisibleParcelle() {
        binding.cvGestion.visibility = View.VISIBLE
        binding.pbBatteryBar.visibility = View.VISIBLE
        binding.clBlocHideIfClassic.visibility = View.VISIBLE
        binding.tvDegree.visibility = View.VISIBLE

        println("GardenerId : " + profilViewModel.userService.gardenerId)
        getTypeGarden()

        profilViewModel.userService.gardenerId?.let { gardenerId ->
            println("Je vais entrer dans le setStats de la parcelle")
            checkGardenerParent(gardenerId)
        }
    }

    private fun setViewVisibleCleEnMain() {
        println("J'entre dans le setViewVisible")
        binding.cvGestion.visibility = View.VISIBLE
        binding.pbBatteryBar.visibility = View.VISIBLE
        binding.pbWaterBar.visibility = View.VISIBLE
        binding.tvStateWater.visibility = View.VISIBLE
        binding.tvTitleWater.visibility = View.VISIBLE
        binding.clBlocHideIfClassic.visibility = View.VISIBLE
        binding.tvDegree.visibility = View.VISIBLE
        println("Mon gardenerID : " + profilViewModel.userService.gardenerId)
        getTypeGarden()

        profilViewModel.userService.gardenerId?.let {gardenerId ->
            println("J'ai un gardenerId")
            setStatsGardeners(gardenerId, null, "cle_en_main")
        }
        binding.llLuminosity.visibility = View.VISIBLE
    }

    private fun setViewVisibleKitMural() {
        binding.cvGestion.visibility = View.VISIBLE
        binding.pbBatteryBar.visibility = View.VISIBLE
        binding.clBlocHideIfClassic.visibility = View.VISIBLE
        binding.clHumidityAnyType.visibility = View.VISIBLE
        getTypeGarden()
        profilViewModel.userService.gardenerId?.let {gardenerId ->
            setStatsGardeners(gardenerId, null, "mural")
        }
    }

    private fun setViewVisibleCapteurJardiniere() {
        binding.cvGestion.visibility = View.VISIBLE
        binding.pbBatteryBar.visibility = View.VISIBLE
        binding.clBlocHideIfClassic.visibility = View.VISIBLE
        binding.tvDegree.visibility = View.VISIBLE
        binding.tvTitleWater.visibility = View.GONE
        binding.cvWater.visibility = View.GONE //RENDRE INVISIBLE POUR LES CAPTEUR
        println("J'entre dans le setViewVisible des capteurs")
        getTypeGarden()
        profilViewModel.userService.gardenerId?.let {gardenerId ->
            setStatsGardeners(gardenerId, null, "capteur_jardiniere")
        }
    }

    private fun setViewVisibleCapteurCarre() {
        binding.cvGestion.visibility = View.VISIBLE
        binding.pbBatteryBar.visibility = View.VISIBLE
        binding.tvTitleWater.visibility = View.GONE
        binding.cvWater.visibility = View.GONE //RENDRE INVISIBLE POUR LES CAPTEUR
        binding.clBlocHideIfClassic.visibility = View.VISIBLE
        binding.tvDegree.visibility = View.VISIBLE
        binding.llLuminosity.visibility = View.VISIBLE
        getTypeGarden()
        println("J'entre dans le setViewVisible des capteurs")
        profilViewModel.userService.gardenerId?.let {gardenerId ->
            setStatsGardeners(gardenerId, null, "capteur_carre")
        }
    }

    private fun setViewVisibleCapteurPot() {
        binding.cvGestion.visibility = View.VISIBLE
        binding.pbBatteryBar.visibility = View.VISIBLE
        binding.tvTitleWater.visibility = View.GONE
        binding.cvWater.visibility = View.GONE //RENDRE INVISIBLE POUR LES CAPTEUR
        binding.clBlocHideIfClassic.visibility = View.VISIBLE
        binding.tvDegree.visibility = View.VISIBLE
        getTypeGarden()
        println("J'entre dans le setViewVisible des capteurs")
        profilViewModel.userService.gardenerId?.let {gardenerId ->
            setStatsGardeners(gardenerId, null, "capteur_pot")
        }
    }

    private fun setViewVisibleCapteur() {
        binding.cvGestion.visibility = View.VISIBLE
        binding.pbBatteryBar.visibility = View.VISIBLE
        binding.clBlocHideIfClassic.visibility = View.VISIBLE
        binding.tvDegree.visibility = View.VISIBLE
        println("J'entre dans le setViewVisible des capteurs")
        profilViewModel.userService.gardenerId?.let {gardenerId ->
            setStatsGardeners(gardenerId, null, "capteur")
        }
    }

    private fun getCityUser(gardenerId: String) {
        getCityRef = FirebaseService().getGardenerMetadataById(gardenerId)
        handleGetCityRef = getCityRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val userCity = snapshot.getValue(GardenerMetadata::class.java)
                if (userCity != null) {
                    println("J'suis dans le userCity")
                    if (userCity.city.isNotEmpty()) {
                        println("Set de la ville de l'utilisateur")
                        profilViewModel.userService.cityUser = userCity.city
                    }
                }
            }
        })

    }

    private fun apiRequestOk(myObject : WeatherApp, call : Call) {
        if(context != null) {
                   profilViewModel.userService.typeGarden?.let { type ->

               when (type) {
               "cle_en_main" -> {
                   setViewVisibleCleEnMain()
               }
               "parcelle" -> {
                   setViewVisibleParcelle()
               }
               "mural" -> {
                   setViewVisibleKitMural()
               }"capteur_pot" -> {
                       setViewVisibleCapteurPot()
                   }
                   "capteur_jardiniere" -> {
                       setViewVisibleCapteurJardiniere()
                   }
                   "capteur_carre" -> {
                       setViewVisibleCapteurCarre()
                   }
                   else -> {
                        setClassicMeteo(myObject.main.temp!!, myObject.main.humidity!!, myObject.main.pressure!!, myObject.name!!)
                   }
               }

       }
        }
    }

    fun apiBadRequest() {
        val t = profilViewModel.userService.typeGarden
        if(t != "cle_en_main" && t != "parcelle" && t != "capteur_pot" && t != "capteur_jardiniere" && t != "capteur_carre" && t != "mural") {
            binding.cvGestion.visibility = View.GONE
            binding.tvStateClimatTitle.visibility = View.GONE
        }
    }

    private fun weatherService(zipCode : String, countryCode : String)  {
        val urlStr = "${Constants.ADRESSE_URL}zip=$zipCode,$countryCode&appid=${Constants.WeatherApiKey}"
        val client: OkHttpClient = OkHttpClient().newBuilder()
            .build()
        val request: Request = Request.Builder()
            .url(urlStr)
            .method("GET", null)
            .build()
        println("URL : " + urlStr)
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("J'ai un problème avec la récupération : " + call)
                apiBadRequest()
            }

            override fun onResponse(call: Call, response: Response) {
                println("onResponse weatherService")
                val body = response.body?.string()
                val myGson = GsonBuilder().create()
                println("Le myGson : " + myGson)
                val myObject = myGson.fromJson(body, WeatherApp::class.java)
                println(myObject)
                if (myObject.cod == API_NETWORK_SUCCESS) {
                    runOnUiThread(Runnable {
                        apiRequestOk(myObject, call)
                    })
                } else {
                    runOnUiThread(Runnable {
                        println("Je passe ici la")
                        setViewIfGeolocalisationCityName()
                    })
                }
            }
        })
    }


    private fun getDataGardener() {
        profilViewModel.userService.gardenerId?.let { guid ->
            dataGardenerRef = FirebaseService().getGardenerById(guid)
            dataGardenerHandle = dataGardenerRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                   snapshot.getValue(GardenerStage::class.java)?.let {
                       profilViewModel.userService.typeGarden = it.type
                        if (it.type == "cle_en_main") {
                            binding.btAdjustWatering.visibility = View.VISIBLE
                        } else {
                            binding.btAdjustWatering.visibility = View.INVISIBLE
                        }
                   }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })

        }

    }

    private fun getLoraFailure() {
        profilViewModel.userService.gardenerId?.let { guid ->
            loraFailureRef = FirebaseService().getGardenerById(guid)
            handleLoraFailure = loraFailureRef.addValueEventListener(object : ValueEventListener {
                @SuppressLint("SetTextI18n")
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.getValue(GardenerData::class.java)?.let {
                        if(it.loraTs != "") {
                            println(isLoraFailure(it.loraTs))
                            FirebaseService().getGardenerById(guid).child("tips").child("loraTs").setValue(isLoraFailure(it.loraTs))
                            if (isLoraFailure(it.loraTs)) {
                                binding.clLoraFailure.visibility = View.VISIBLE
                                binding.tvDateFormat.text = " ${DateFormatUtils().setToDateFormat(it.loraTs, getString(R.string.tv_date_format_panne), getString(R.string.tv_current_setting_1))}"
                                binding.tvDateFormat.visibility = View.VISIBLE
                            } else {
                                binding.clLoraFailure.visibility = View.GONE
                            }
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) { }
            })
        }
    }

    private fun isLoraFailure(date : String) : Boolean {
        val current = System.currentTimeMillis() / 1000
        val result = current.toDouble() - date.toDouble()
        println("Current : " + current)
        println("Lora : " + date.toInt())
        println("resultat : " +result.toInt())
        return result >= 86400
    }

    private fun initView() {

        getLoraFailure()
        if(profilViewModel.userService.typeGarden != null) {
            if (profilViewModel.userService.typeGarden == "cle_en_main") {
                binding.btAdjustWatering.visibility = View.VISIBLE
            } else {
                binding.btAdjustWatering.visibility = View.INVISIBLE
            }
        } else {
            getDataGardener()
        }

        binding.btAdjustWatering.setOnClickListener {
            if (findNavController().currentDestination?.id == R.id.plantrFragment) {
                findNavController().navigate(PlantrFragmentDirections.actionPlantrFragmentToIrrigation())
            }
        }

        setObserver()

        binding.clInParent.setOnClickListener {
            AlerterService.showGoodCardGeneral(
                "${getString(R.string.alerter_garden)}",
                requireActivity()
            )
        }

        println("Je passe dans le onResume")
        plantAdapter = PlantAdapter { currentGardenerImages ->
            Firebase.database.getReference("gardeners")
                .child(profilViewModel.userService.gardenerId!!)
                .child("metadata").child("images")
                .child(currentGardenerImages)
                .removeValue { databaseError, _ ->
                    if (databaseError != null)
                        AlerterService.showError(
                            "V${getString(R.string.alerter_need_network)}",
                            requireActivity()
                        )
                }
        }

        binding.ivBtnTips.setOnClickListener {
            if (binding.ivNotification.visibility == View.VISIBLE) {
                println("BOUTON TIPS : " + "\n")
                println("ArrayTips : " + arrayTips.values)
                println("Connected : " + Singleton.instance.connectedState)
                println("profilviewmodel : " + profilViewModel.userService.gardenerName)
                println("profilviewmodelBle name : " + profilViewModel.userService.gardenerNameBle)
                if (arrayTips.containsValue(true)) {
                    if (Singleton.instance.connectedState) {
                        if (profilViewModel.userService.gardenerNameBle == profilViewModel.userService.gardenerName) {
                            arrayTips.map {
                                if (it.value) {
                                    val name = it.key
                                    if (findNavController().currentDestination?.id == R.id.plantrFragment) {
                                        findNavController().navigate(PlantrFragmentDirections.actionPlantrFragmentToTipsFragment(name, ""))
                                    }
                                }
                            }
                        }
                    } else {
                        profilViewModel.userService.gardenerId?.let { guid ->
                            arrayTips.map {
                                if (it.value) {
                                    val name = it.key
                                    when(profilViewModel.userService.typeGarden) {
                                        "parcelle" -> {
                                            checkGardenerRefParent(guid, name)
                                        }
                                        else -> {
                                            if (findNavController().currentDestination?.id == R.id.plantrFragment) {
                                                findNavController().navigate(
                                                    PlantrFragmentDirections.actionPlantrFragmentToTipsFragment(name, guid))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    println("des valeurs sont fausses")
                }
            } else {

                val popupScan = NotifPopup(requireContext()) { popup, bool, str ->
                    if (str == "fermer") {
                        popup.dismiss()
                    } else {
                        popup.dismiss()
                    }
                }
                popupScan.show()
            }
        }

        profilViewModel.userService.gardenerId = currentGardener
        binding.btnAddCamera.setOnClickListener {
            if (checkPersmission()) {
                showPictureDialog()
            } else {
                requestPermission()
            }
        }

        checkHiddenButton() //makes the buttons invisible or invisible

        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setClassicMeteo(temp: Double, humidity: Double, pressure: Double, name : String) {
        getTypeGarden()
        val tempKelvin = temp.minus(273.15)
        println("Temperature en Kelvin ${tempKelvin.toInt()}")
        println("Je set ma vue !")
        binding.ivNotification.visibility = View.GONE
        if(context != null) {
              binding.tvStateClimatTitle.text = "${getString(R.string.climat_non_detect)} $name"
        singleton.gardenerIdResaBle = null
        binding.circularProgressBar.visibility = View.GONE
        binding.tvStateClimatTitle.visibility = View.VISIBLE
        binding.cvGestion.visibility = View.VISIBLE
        binding.pbBarGeneralState.visibility = View.GONE
        println("Je rentre dans le cas false")
        binding.clParentRight.visibility = View.INVISIBLE
        binding.clBlocHideIfClassic.visibility = View.GONE
        binding.tvPourcentHumidity.text = "${humidity.toInt()}%"
        binding.tvPourcentHumidity.visibility = View.VISIBLE
        println("Temps : " + temp + "\n" + "Humidity : " + humidity)
        binding.tvPourcentPressure.text = pressure.toInt().toString() + " hPa"
        binding.tvDegree.text = "${tempKelvin.toInt()}°"
        } else {
            return
        }

    }

//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun setActivePlantrBle(
//    ) {
//        println("TypeGarden : " + profilViewModel.userService.typeGarden)
//        if (singleton.connectedState && profilViewModel.userService.typeGarden != "cle_en_main" && profilViewModel.userService.typeGarden != "mural") {
//            binding.tvStateClimatTitle.text =
//                context?.resources?.getString(R.string.climat_detect)?.toUpperCase()!!
//            binding.tvGardenerState.text = ""
//            binding.circularProgressBar.visibility = View.GONE
//            binding.pbHumidityBarLoading.visibility = View.VISIBLE
//            binding.tvStateHumidityC0.text = ""
//            binding.pbBatteryBarLoading.visibility = View.VISIBLE
//            binding.tvStateBattery.text = ""
//            binding.tvPourcentHumidity.text = ""
//            binding.tvDegree.text = ""
//            binding.pbHumidityC0.visibility = View.GONE
//            binding.pbBatteryBar.visibility = View.GONE
//
//            binding.clParentv.visibility = View.GONE
//            binding.pbHumidityC0.min = 520
//            binding.pbHumidityC0.max = 1200
//
//            Singleton.instance._humidityWind.observe(viewLifecycleOwner) {
//                it.getContentIfNotHandled()?.let { value ->
//                    if (value == -1F) {
//
//                    } else {
//                        binding.tvPourcentHumidity.text = value.toInt().toString() + "%"
//                        binding.tvPourcentHumidity.visibility = View.VISIBLE
//                    }
//                }
//            }
//
//            Singleton.instance._temp.observe(viewLifecycleOwner) {
//                it.getContentIfNotHandled()?.let { value ->
//                    if (value == -1F) {
//
//                    } else {
//                        binding.tvDegree.text = value.toInt().toString() + "°"
//                        binding.tvDegree.visibility = View.VISIBLE
//                    }
//                }
//            }
//            var result: Float = 0F
//
//            //Initialisation de la valeur de la progressBar Ble
//            Singleton.instance._capaProgress.observe(viewLifecycleOwner) {
//                it.getContentIfNotHandled()?.let { valueCapa ->
//                    println("Ma capa : " + valueCapa.toInt())
//                    GLOBAL_CAPA = valueCapa.toString()
//                    Singleton.instance._batteryLevel.observe(viewLifecycleOwner) {
//                        it.getContentIfNotHandled()?.let { value ->
//                            if (value == -1F) {
//                                binding.pbBatteryBarLoading.visibility = View.VISIBLE
//                            } else {
//                                GLOBAL_BATTERY = value.toString()
//                            }
//                        }
//                    }
//
//                    if (::GLOBAL_BATTERY.isInitialized) {
//                        result = GLOBAL_BATTERY.toFloat() + valueCapa.toFloat()
//                        println("Addition des deux : " + result)
//                        println("Moyenne pour pbGeneralState : " + result / 2)
//
//                        circularProgressBar.setProgressWithAnimation((result / 2), 1000)
//                        println("Ma value Capa pour la progressBar : " + valueCapa * 10)
//                        checkProgressGeneralBle(
//                            (result / 2), valueCapa * 10, binding.circularProgressBar
//                        )
//                        binding.circularProgressBar.visibility = View.VISIBLE
//                        binding.pbBatteryBarLoading.visibility = View.GONE
//                        binding.pbBatteryBar.setProgress(GLOBAL_BATTERY.toFloat().toInt(), true)
//                        checkProgressNumberBattery(
//                            "Batterie",
//                            GLOBAL_BATTERY.toFloat().toInt(),
//                            binding.pbBatteryBar,
//                            binding.tvStateBattery
//                        )
//                        binding.pbBatteryBar.visibility = View.VISIBLE
//                    }
//                    if (valueCapa.toFloat() == -1F) {
//                        binding.pbHumidityBarLoading.visibility = View.VISIBLE
//                        binding.tvStateHumidityC0.text = ""
//                    } else {
//                        binding.pbHumidityBarLoading.visibility = View.GONE
//                        val convertValue = valueCapa * 10
//                        checkProgressNumberHumidityBle(
//                            "Humidité", convertValue.toInt(), binding.pbHumidityC0,
//                            binding.tvStateHumidityC0
//                        )
//                        binding.pbHumidityC0.visibility = View.VISIBLE
//                    }
//
//
//                }
//            }
//        } else {
//
//            binding.pbBatteryBarLoading.visibility = View.GONE
//            binding.pbHumidityBarLoading.visibility = View.GONE
//        }
//
//        binding.clBlocHideIfClassic.visibility = View.VISIBLE
//        binding.cvWater.visibility = View.GONE //On rend invisible les bloc inutile
//        binding.clBlocHideIfClassic.visibility = View.VISIBLE
//        binding.cvHumidity.visibility = View.VISIBLE
//        binding.cvBattery.visibility = View.VISIBLE
//        binding.tvTitleHumidity.visibility = View.VISIBLE
//        binding.tvTitleBattery.visibility = View.VISIBLE
//        binding.tvTitleWater.visibility = View.GONE
//        binding.pbBarGeneralState.visibility = View.GONE
//        binding.tvDegree.visibility = View.VISIBLE
//        binding.tvPourcentHumidity.visibility = View.VISIBLE
//        binding.ivFloor1.visibility = View.GONE
//        binding.ivFloor1.setColorFilter(
//            ContextCompat.getColor(requireContext(), R.color.green_plantr),
//            android.graphics.PorterDuff.Mode.MULTIPLY
//        );
//
//        if (profilViewModel.userService.gardenerId != singleton.gardenerIdFORBLE) {
//            binding.circularProgressBar.visibility = View.GONE
//        }
//    }

    override fun onResume() {
        super.onResume()
        Singleton.instance.hideLoadingScreenByLang()
    }

    private fun checkHiddenButton() {
        // binding.pbLocalisationLoadingClHeaderMiddle.visibility = View.VISIBLE
        when (profilViewModel.userService.typeGarden) {
            "carre" -> {
                when (profilViewModel.userService.dimension) {
                    0 -> {
                        binding.tvGardenerState.visibility = View.VISIBLE
                        binding.pbBarGeneralState.visibility = View.GONE
                        binding.tvGardenerState.text = getString(R.string.classic_type_garden_carre_0)
                    }
                    1 -> {
                        binding.tvGardenerState.visibility = View.VISIBLE
                        binding.pbBarGeneralState.visibility = View.GONE
                        binding.tvGardenerState.text = getString(R.string.classic_type_garden_carre_1)
                    }
                    2 -> {
                        binding.tvGardenerState.visibility = View.VISIBLE
                        binding.pbBarGeneralState.visibility = View.GONE
                        binding.tvGardenerState.text = getString(R.string.classic_type_garden_carre_2)
                    }
                }

            }
            "jardiniere" -> {
                when (profilViewModel.userService.dimension) {
                    0 -> {
                        binding.tvGardenerState.visibility = View.VISIBLE
                        binding.pbBarGeneralState.visibility = View.GONE
                        binding.tvGardenerState.text = getString(R.string.classic_type_garden_jardiniere_0)
                    }
                    1 -> {
                        binding.tvGardenerState.visibility = View.VISIBLE
                        binding.pbBarGeneralState.visibility = View.GONE
                        binding.tvGardenerState.text = getString(R.string.classic_type_garden_jardiniere_1)
                    }
                    2 -> {
                        binding.tvGardenerState.visibility = View.VISIBLE
                        binding.pbBarGeneralState.visibility = View.GONE
                        binding.tvGardenerState.text = getString(R.string.classic_type_garden_jardiniere_2)
                    }
                }
            }
            "pot" -> {
                when (profilViewModel.userService.dimension) {
                    0 -> {
                        binding.pbBarGeneralState.visibility = View.GONE
                        binding.tvGardenerState.visibility = View.VISIBLE
                        binding.tvGardenerState.text = getString(R.string.classic_type_garden_pot_0)
                    }
                    1 -> {
                        binding.tvGardenerState.visibility = View.VISIBLE

                        binding.pbBarGeneralState.visibility = View.GONE
                        binding.tvGardenerState.text = getString(R.string.classic_type_garden_pot_1)
                    }
                    2 -> {
                        binding.tvGardenerState.visibility = View.VISIBLE

                        binding.pbBarGeneralState.visibility = View.GONE
                        binding.tvGardenerState.text = getString(R.string.classic_type_garden_pot_2)
                    }
                }
            }
            "cle_en_main" -> {
                binding.tvPourcentLuminosity.visibility = View.INVISIBLE
                binding.tvPourcentHumidity.visibility = View.INVISIBLE
                binding.tvDegree.visibility = View.INVISIBLE
            }
            "mural" -> {
                binding.tvPourcentLuminosity.visibility = View.INVISIBLE
                binding.tvPourcentHumidity.visibility = View.INVISIBLE
                binding.tvDegree.visibility = View.INVISIBLE
            }
        }
    }

    override fun onStop() {
        super.onStop()
        nameTipsGardener = null
        removeObserver()
    }

    private fun removeObserver() {
        if (::gardenersClimatRef.isInitialized && ::handleGardenerClimatData.isInitialized) {
            gardenersClimatRef.removeEventListener(handleGardenerClimatData)
        }
        if (::userRef.isInitialized && ::handleUser.isInitialized) {
            userRef.removeEventListener(handleUser)
        }
        if (::userPictureRef.isInitialized && ::handleUserPictureRef.isInitialized) {
            userPictureRef.removeEventListener(handleUserPictureRef)
        }
        if (::gardenersStatsRef.isInitialized && ::handleGardenerStatsData.isInitialized) {
            gardenersStatsRef.removeEventListener(handleGardenerStatsData)
        }

        if (::gardenersImagesRef.isInitialized && ::handleGardenerImagesData.isInitialized) {
            gardenersImagesRef.removeEventListener(handleGardenerImagesData)
        }
        if (::gardenersTipsRef.isInitialized && ::handleGardenersTips.isInitialized) {
            gardenersTipsRef.removeEventListener(handleGardenersTips)
        }
        if (::getCityRef.isInitialized && ::handleGetCityRef.isInitialized) {
            getCityRef.removeEventListener(handleGetCityRef)
        }

    }

    private fun setObserver() {
        FirebaseService().firebase.currentUser?.uid?.let { uid ->
            userRef = FirebaseService().getUserByid(uid)
            handleUser = userRef.addValueEventListener(object : ValueEventListener {

                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user = dataSnapshot.getValue(User::class.java)
                    user?.currentGardener?.let {
                        currentGardener = it
                        profilViewModel.userService.gardenerId = currentGardener
                        if (it.isNotEmpty()) {
                            getCityUser(it)
                            setGardenersObserver(it)
                            setGardenersTipsObserver(it)
                            println("GARDENER ID OBS : " + profilViewModel.userService.gardenerId)
                        }


                    }
                }
            })
        }
    }

    private fun setGardenersTipsObserver(gardenerId: String) {
        when (profilViewModel.userService.typeGarden) {
            "parcelle" -> {
                checkGardenerParentIfParcelle(gardenerId)
            }
            else -> {
                setGardenersTips(gardenerId)
            }
        }

    }


    private fun checkGardenerParentIfParcelle(gardenerId: String){
        gardenerStatsRefParent = FirebaseService().getGardenerById(gardenerId)
        handleGardenerStatsRefParent = gardenerStatsRefParent.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                println(error)
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                //Here we fetches the informations from the users
                val gardenerDataStage = snapshot.getValue(GardenerStage::class.java)
                if (gardenerDataStage != null) {
                    profilViewModel.userService.gardenerParent = null
                 //   if(gardenerDataStage.gardenerParent != null || gardenerDataStage.gardenerParent.isNotEmpty()) {
                        profilViewModel.userService.rangs = gardenerDataStage.rangs
                        gardenerDataStage.gardenerParent?.let { setGardenersTips(it) }
                    }
             //   }
            }
        })
    }

    private fun checkGardenerRefParent(gardenerId: String, name : String){
        gardenerStatsRefParentCheck = FirebaseService().getGardenerById(gardenerId)
        handleGardenerStatsRefParentCheck = gardenerStatsRefParentCheck.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                println(error)
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                //Here we fetches the informations from the users
                val gardenerDataStage = snapshot.getValue(GardenerStage::class.java)

                if (gardenerDataStage != null) {
                    profilViewModel.userService.gardenerParent = null
                    if (gardenerDataStage.gardenerParent != null) {
                        if (findNavController().currentDestination?.id == R.id.plantrFragment) {
                            findNavController().navigate(PlantrFragmentDirections.actionPlantrFragmentToTipsFragment(name,
                                gardenerDataStage.gardenerParent!!
                            ))
                        }
                    }
                }
            }
        })
    }

    private fun setGardenersTips(gardenerId: String) {
        gardenersTipsRef = Firebase.database.getReference("gardeners").child(gardenerId).child("tips")
        handleGardenersTips = gardenersTipsRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val tips = snapshot.getValue(GardenerTips::class.java)?.let {
                    println("DETECTION TIPS")
                    arrayTips["capa"] = it.capa
                    arrayTips["battery"] = it.battery
                    arrayTips["temperature"] = it.temperature
                    it.waterLevel.let { wt ->
                        arrayTips["waterLevel"] = wt
                    }
                    println("MON ARRAY DE TIPS" + arrayTips)
                    if (arrayTips.containsValue(true)) {
                        binding.ivNotification.visibility = View.GONE
                    } else {
                        binding.ivNotification.visibility = View.GONE
                    }
                }
            }

        })
    }

    private fun setGardenersObserver(gardenerId: String) {

        gardenersClimatRef = FirebaseService().getGardenerById(gardenerId)
        handleGardenerClimatData = gardenersClimatRef.addValueEventListener(object : ValueEventListener {

                @SuppressLint("SetTextI18n")
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val gardener = dataSnapshot.getValue(Gardener::class.java)
                    //binding.tvPourcentVent.setText(gardenerClimat?.pressure.toString() + "km/h")
                    if (gardener != null) {
                        println("JE SUIS ARRIVER LA")
                        binding.tvPourcentHumidity.text = gardener.stats.humidity.toString() + "%"
                        profilViewModel.userService.gardenerName = gardener.metadata.name.capitalize().trimEnd()
                        binding.tvTitleGardenerState.text = gardener.metadata.name.capitalize().trimEnd()
                        println("Gardener : " + gardener)

                        gardener.metadata.zipCode?.let { zipCode ->
                            profilViewModel.userService.typeGarden?.let { type ->
                                gardener.metadata.countryCode?.let { CD ->
                                    weatherService(zipCode, CD)
                                }
                            }
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })

        gardenersImagesRef = Firebase.database.getReference("gardeners").child(gardenerId).child("metadata")
        handleGardenerImagesData = gardenersImagesRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val gardenerMetadata = dataSnapshot.getValue(GardenerMetadata::class.java)?.let {
                    val images = it.images.keys.toList()

                    if (images.isEmpty()) {
                        plantAdapter.submitList(emptyList()) //Si rien, alors afficher aucune image
                        binding.ivBackgroundHidden.visibility = View.VISIBLE
                    } else {
                        binding.ivBackgroundHidden.visibility = View.GONE

                        binding.rvMyPlantR.adapter = plantAdapter
                        activity?.let { act ->
                            binding.rvMyPlantR.layoutManager = LinearLayoutManager(act, LinearLayoutManager.HORIZONTAL, false)
                        }
                        plantAdapter.submitList(images)
                        println("mes images " + images)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })





    }

    private fun checkGardenerParent(gardenerId: String) {
        gardenersParentRef = FirebaseService().getGardenerReference().child(gardenerId)
        handleGardenersParent =
            gardenersParentRef.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val gardener = snapshot.getValue(GardenerStage::class.java)
                    if (gardener != null) {
                        val newGuid = gardener.gardenerParent
                        println("Passage parcelle " + "\n")
                        println("New Guid : " + newGuid)
                        if (newGuid != null) {
                            setStatsGardeners(newGuid, gardenerId, "parcelle")
                        }
                    }
                }
            })
    }

    private fun setStatsGardeners(
        gardenerId: String,
        realGardenerIdForParcelle: String?,
        type: String
    ) {
        gardenersStatsRef = Firebase.database.getReference("gardeners").child(gardenerId).child("stats")
        handleGardenerStatsData = gardenersStatsRef.addValueEventListener(object : ValueEventListener {
                @RequiresApi(Build.VERSION_CODES.N)
                @SuppressLint("ResourceAsColor", "ObjectAnimatorBinding", "SetTextI18n")
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val gardenerStats = dataSnapshot.getValue(GardenerStats::class.java)
                    if (gardenerStats != null) {
                        val stats = ProgressBarUtils().calculate(type, gardenerStats, realGardenerIdForParcelle?.last()?.toInt()?:null)
                        ProgressBarUtils().setColorPv(requireContext(), stats!!, binding.circularProgressBar, binding.tvGardenerState)
                        binding.pbBarGeneralState.visibility = View.GONE
                        binding.tvGardenerState.visibility = View.VISIBLE
                            binding.tvStateClimatTitle.visibility = View.VISIBLE
                            binding.tvStateClimatTitle.text = context?.resources?.getString(R.string.climat_detect)?.toUpperCase()!!
                            binding.tvPourcentLuminosity.visibility = View.VISIBLE
                            val calcul = gardenerStats?.luminosity?.toInt()?.let { multiply(it, 8.8756) }
                            val calculConvertToKlx = calcul?.div(1000)?.round(2)
                            if (calcul != null) {
                                println("Lum avant : " + gardenerStats.luminosity?.toInt())
                                println("Lum après : " + calculConvertToKlx)
                                if (calculConvertToKlx != null) {
                                    binding.tvPourcentLuminosity.text =
                                        calculConvertToKlx.toString() + " klx"
                                }
                            }
                            binding.tvPourcentHumidity.text =
                                gardenerStats?.humidity?.toInt().toString() + "%"
                            binding.tvPourcentHumidity.visibility = View.VISIBLE
                            updateLumCalcul(gardenerStats)
                            binding.tvPourcentPressure.text =
                                gardenerStats?.pressure?.toInt().toString() + " hPa"
                            binding.tvDegree.visibility = View.VISIBLE
                            binding.tvDegree.text =
                                "${gardenerStats?.temperature?.toInt().toString()}°"

                        when (type) {
                            "parcelle" -> {
                                println("Parcelle genre j'ai " + realGardenerIdForParcelle!!.last())
                                when (realGardenerIdForParcelle!!.last().toString()) {
                                    "0" -> {
                                        println("J'entre dans le 0 car j'ai : " + realGardenerIdForParcelle!!.last())
                                        binding.clHumidityParcelle.visibility = View.VISIBLE
                                        binding.clHumidityAnyType.visibility = View.GONE
                                        if (gardenerStats.capacities?.c1 != null) {
                                            binding.pbHumidityParcelle.max = 100
                                            binding.pbHumidityBarLoadingParcelle.visibility =
                                                View.GONE
                                            binding.pbHumidityParcelle.setProgress(gardenerStats.capacities.c1!!.toInt())

                                            when (gardenerStats.capacities.c1!!.toInt()) {
                                                in 0..50 -> {
                                                    try {
                                                        binding.pbHumidityParcelle.progressDrawable =
                                                            requireActivity().getDrawable(
                                                                R.drawable.custom_general_seekbar_progress_red
                                                            )
                                                        binding.tvStateHumidityParcelle.text =
                                                            getString(R.string.tv_state_seche)
                                                    } catch (error: IllegalStateException) {
                                                        println("IllegalStateException")
                                                    }
                                                }
                                                in 50..70 -> {
                                                    try {
                                                        binding.pbHumidityParcelle.progressDrawable =
                                                            requireActivity().getDrawable(
                                                                R.drawable.custom_general_seekbar_progress_orange
                                                            )
                                                        binding.tvStateHumidityParcelle.text =
                                                            getString(R.string.tv_state_humid_m)
                                                    } catch (error: IllegalStateException) {
                                                        println("IllegalStateException")
                                                    }
                                                }
                                                in 70..95 -> {
                                                    try {
                                                        binding.pbHumidityParcelle.progressDrawable =
                                                            requireActivity().getDrawable(
                                                                R.drawable.custom_general_seekbar_progress_green
                                                            )
                                                        binding.tvStateHumidityParcelle.text =
                                                            getString(R.string.tv_state_humid_p)
                                                    } catch (error: IllegalStateException) {
                                                        println("IllegalStateException")
                                                    }
                                                }
                                                in 95..100 -> {
                                                    try {
                                                        binding.pbHumidityParcelle.progressDrawable =
                                                            requireActivity().getDrawable(
                                                                R.drawable.custom_general_seekbar_progress_orange
                                                            )
                                                        binding.tvStateHumidityParcelle.text =
                                                            getString(R.string.tv_state_too_humid)
                                                    } catch (error: IllegalStateException) {
                                                        println("IllegalStateException")
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    "1" -> {
                                        println("J'entre dans le 1 car j'ai : " + realGardenerIdForParcelle!!.last())
                                        binding.clHumidityParcelle.visibility = View.VISIBLE
                                        binding.clHumidityAnyType.visibility = View.GONE
                                        if (gardenerStats?.capacities?.c1 != null) {
                                            binding.pbHumidityParcelle.max = 100
                                            binding.pbHumidityBarLoadingParcelle.visibility =
                                                View.GONE
                                            binding.pbHumidityParcelle.setProgress(gardenerStats.capacities.c1!!.toInt())

                                            when (gardenerStats.capacities.c1!!.toInt()) {
                                                in 0..30 -> {
                                                    try {
                                                        binding.pbHumidityParcelle.progressDrawable =
                                                            requireActivity().getDrawable(
                                                                R.drawable.custom_general_seekbar_progress_red
                                                            )
                                                        binding.tvStateHumidityParcelle.text =
                                                            getString(R.string.tv_state_seche_small)
                                                    } catch (error: IllegalStateException) {
                                                        println("IllegalStateException")
                                                    }
                                                }
                                                in 30..50 -> {
                                                    try {
                                                        binding.pbHumidityParcelle.progressDrawable =
                                                            requireActivity().getDrawable(
                                                                R.drawable.custom_general_seekbar_progress_orange
                                                            )
                                                        binding.tvStateHumidityParcelle.text =
                                                            getString(R.string.tv_state_humid_m)
                                                    } catch (error: IllegalStateException) {
                                                        println("IllegalStateException")
                                                    }
                                                }
                                                in 50..70 -> {
                                                    try {
                                                        binding.pbHumidityParcelle.progressDrawable =
                                                            requireActivity().getDrawable(
                                                                R.drawable.custom_general_seekbar_progress_green
                                                            )
                                                        binding.tvStateHumidityParcelle.text =
                                                            getString(R.string.tv_state_humid)
                                                    } catch (error: IllegalStateException) {
                                                        println("IllegalStateException")
                                                    }
                                                }
                                                in 70..85 -> {
                                                    try {
                                                        binding.pbHumidityParcelle.progressDrawable =
                                                            requireActivity().getDrawable(
                                                                R.drawable.custom_general_seekbar_progress_green
                                                            )
                                                        binding.tvStateHumidityParcelle.text =
                                                            getString(R.string.tv_state_humid_p)
                                                    } catch (error: IllegalStateException) {
                                                        println("IllegalStateException")
                                                    }
                                                }
                                                in 85..100 -> {
                                                    try {
                                                        binding.pbHumidityParcelle.progressDrawable =
                                                            requireActivity().getDrawable(
                                                                R.drawable.custom_general_seekbar_progress_orange
                                                            )
                                                        binding.tvStateHumidityParcelle.text =
                                                            getString(R.string.tv_state_too_humid)
                                                    } catch (error: IllegalStateException) {
                                                        println("IllegalStateException")
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    "2" -> {
                                        println("J'entre dans le 2 car j'ai : " + realGardenerIdForParcelle!!.last())
                                        binding.clHumidityParcelle.visibility = View.VISIBLE
                                        binding.clHumidityAnyType.visibility = View.GONE
                                        if (gardenerStats?.capacities?.c2 != null) {
                                            binding.pbHumidityBarLoadingParcelle.visibility =
                                                View.GONE
                                            binding.pbHumidityParcelle.max = 100
                                            binding.pbHumidityParcelle.setProgress(gardenerStats.capacities.c2!!.toInt())

                                            when (gardenerStats.capacities.c2!!.toInt()) {
                                                in 0..30 -> {
                                                    try {
                                                        binding.pbHumidityParcelle.progressDrawable =
                                                            requireActivity().getDrawable(
                                                                R.drawable.custom_general_seekbar_progress_red
                                                            )
                                                        binding.tvStateHumidityParcelle.text =
                                                            getString(R.string.tv_state_seche_small)
                                                    } catch (error: IllegalStateException) {
                                                        println("IllegalStateException")
                                                    }
                                                }
                                                in 30..50 -> {
                                                    try {
                                                        binding.pbHumidityParcelle.progressDrawable =
                                                            requireActivity().getDrawable(
                                                                R.drawable.custom_general_seekbar_progress_orange
                                                            )
                                                        binding.tvStateHumidityParcelle.text =
                                                            getString(R.string.tv_state_humid_m)
                                                    } catch (error: IllegalStateException) {
                                                        println("IllegalStateException")
                                                    }
                                                }
                                                in 50..70 -> {
                                                    try {
                                                        binding.pbHumidityParcelle.progressDrawable =
                                                            requireActivity().getDrawable(
                                                                R.drawable.custom_general_seekbar_progress_green
                                                            )
                                                        binding.tvStateHumidityParcelle.text =
                                                            getString(R.string.tv_state_humid)
                                                    } catch (error: IllegalStateException) {
                                                        println("IllegalStateException")
                                                    }
                                                }
                                                in 70..85 -> {
                                                    try {
                                                        binding.pbHumidityParcelle.progressDrawable =
                                                            requireActivity().getDrawable(
                                                                R.drawable.custom_general_seekbar_progress_green
                                                            )
                                                        binding.tvStateHumidityParcelle.text =
                                                            getString(R.string.tv_state_humid_p)
                                                    } catch (error: IllegalStateException) {
                                                        println("IllegalStateException")
                                                    }
                                                }
                                                in 85..100 -> {
                                                    try {
                                                        binding.pbHumidityParcelle.progressDrawable =
                                                            requireActivity().getDrawable(
                                                                R.drawable.custom_general_seekbar_progress_orange
                                                            )
                                                        binding.tvStateHumidityParcelle.text =
                                                            getString(R.string.tv_state_too_humid)
                                                    } catch (error: IllegalStateException) {
                                                        println("IllegalStateException")
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    "3" -> {
                                        println("J'entre dans le 3 car j'ai : " + realGardenerIdForParcelle!!.last())
                                        binding.clHumidityParcelle.visibility = View.VISIBLE
                                        binding.clHumidityAnyType.visibility = View.GONE
                                        if (gardenerStats?.capacities?.c2 != null) {
                                            binding.pbHumidityBarLoadingParcelle.visibility =
                                                View.GONE
                                            binding.pbHumidityParcelle.max = 100
                                            binding.pbHumidityParcelle.setProgress(gardenerStats.capacities.c2!!.toInt())

                                            when (gardenerStats.capacities.c2!!.toInt()) {
                                                in 0..30 -> {
                                                    try {
                                                        binding.pbHumidityParcelle.progressDrawable =
                                                            requireActivity().getDrawable(
                                                                R.drawable.custom_general_seekbar_progress_red
                                                            )
                                                        binding.tvStateHumidityParcelle.text =
                                                            getString(R.string.tv_state_seche_small)
                                                    } catch (error: IllegalStateException) {
                                                        println("IllegalStateException")
                                                    }
                                                }
                                                in 30..50 -> {
                                                    try {
                                                        binding.pbHumidityParcelle.progressDrawable =
                                                            requireActivity().getDrawable(
                                                                R.drawable.custom_general_seekbar_progress_orange
                                                            )
                                                        binding.tvStateHumidityParcelle.text =
                                                            getString(R.string.tv_state_humid_m)
                                                    } catch (error: IllegalStateException) {
                                                        println("IllegalStateException")
                                                    }
                                                }
                                                in 50..70 -> {
                                                    try {
                                                        binding.pbHumidityParcelle.progressDrawable =
                                                            requireActivity().getDrawable(
                                                                R.drawable.custom_general_seekbar_progress_green
                                                            )
                                                        binding.tvStateHumidityParcelle.text =
                                                            getString(R.string.tv_state_humid)
                                                    } catch (error: IllegalStateException) {
                                                        println("IllegalStateException")
                                                    }
                                                }
                                                in 70..85 -> {
                                                    try {
                                                        binding.pbHumidityParcelle.progressDrawable =
                                                            requireActivity().getDrawable(
                                                                R.drawable.custom_general_seekbar_progress_green
                                                            )
                                                        binding.tvStateHumidityParcelle.text =
                                                            getString(R.string.tv_state_humid_p)
                                                    } catch (error: IllegalStateException) {
                                                        println("IllegalStateException")
                                                    }
                                                }
                                                in 85..100 -> {
                                                    try {
                                                        binding.pbHumidityParcelle.progressDrawable =
                                                            requireActivity().getDrawable(
                                                                R.drawable.custom_general_seekbar_progress_orange
                                                            )
                                                        binding.tvStateHumidityParcelle.text =
                                                            getString(R.string.tv_state_too_humid)
                                                    } catch (error: IllegalStateException) {
                                                        println("IllegalStateException")
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    "4" -> {
                                        println("J'entre dans le 3 car j'ai : " + realGardenerIdForParcelle!!.last())
                                        binding.clHumidityParcelle.visibility = View.VISIBLE
                                        binding.clHumidityAnyType.visibility = View.GONE
                                        if (gardenerStats?.capacities?.c2 != null) {
                                            binding.pbHumidityBarLoadingParcelle.visibility =
                                                View.GONE
                                            binding.pbHumidityParcelle.max = 100
                                            binding.pbHumidityParcelle.setProgress(gardenerStats.capacities.c2!!.toInt())

                                            when (gardenerStats.capacities.c2!!.toInt()) {
                                                in 0..30 -> {
                                                    try {
                                                        binding.pbHumidityParcelle.progressDrawable =
                                                            requireActivity().getDrawable(
                                                                R.drawable.custom_general_seekbar_progress_red
                                                            )
                                                        binding.tvStateHumidityParcelle.text =
                                                            getString(R.string.tv_state_seche_small)
                                                    } catch (error: IllegalStateException) {
                                                        println("IllegalStateException")
                                                    }
                                                }
                                                in 30..50 -> {
                                                    try {
                                                        binding.pbHumidityParcelle.progressDrawable =
                                                            requireActivity().getDrawable(
                                                                R.drawable.custom_general_seekbar_progress_orange
                                                            )
                                                        binding.tvStateHumidityParcelle.text =
                                                            getString(R.string.tv_state_humid_m)
                                                    } catch (error: IllegalStateException) {
                                                        println("IllegalStateException")
                                                    }
                                                }
                                                in 50..70 -> {
                                                    try {
                                                        binding.pbHumidityParcelle.progressDrawable =
                                                            requireActivity().getDrawable(
                                                                R.drawable.custom_general_seekbar_progress_green
                                                            )
                                                        binding.tvStateHumidityParcelle.text =
                                                            getString(R.string.tv_state_humid)
                                                    } catch (error: IllegalStateException) {
                                                        println("IllegalStateException")
                                                    }
                                                }
                                                in 70..85 -> {
                                                    try {
                                                        binding.pbHumidityParcelle.progressDrawable =
                                                            requireActivity().getDrawable(
                                                                R.drawable.custom_general_seekbar_progress_green
                                                            )
                                                        binding.tvStateHumidityParcelle.text =
                                                            getString(R.string.tv_state_humid_p)
                                                    } catch (error: IllegalStateException) {
                                                        println("IllegalStateException")
                                                    }
                                                }
                                                in 85..100 -> {
                                                    try {
                                                        binding.pbHumidityParcelle.progressDrawable =
                                                            requireActivity().getDrawable(
                                                                R.drawable.custom_general_seekbar_progress_orange
                                                            )
                                                        binding.tvStateHumidityParcelle.text =
                                                            getString(R.string.tv_state_too_humid)
                                                    } catch (error: IllegalStateException) {
                                                        println("IllegalStateException")
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    "5" -> {
                                        println("J'entre dans le 3 car j'ai : " + realGardenerIdForParcelle!!.last())
                                        binding.clHumidityParcelle.visibility = View.VISIBLE
                                        binding.clHumidityAnyType.visibility = View.GONE
                                        if (gardenerStats?.capacities?.c2 != null) {
                                            binding.pbHumidityBarLoadingParcelle.visibility =
                                                View.GONE
                                            binding.pbHumidityParcelle.max = 100
                                            binding.pbHumidityParcelle.setProgress(gardenerStats.capacities.c2!!.toInt())

                                            when (gardenerStats.capacities.c2!!.toInt()) {
                                                in 0..30 -> {
                                                    try {
                                                        binding.pbHumidityParcelle.progressDrawable =
                                                            requireActivity().getDrawable(
                                                                R.drawable.custom_general_seekbar_progress_red
                                                            )
                                                        binding.tvStateHumidityParcelle.text =
                                                            getString(R.string.tv_state_seche_small)
                                                    } catch (error: IllegalStateException) {
                                                        println("IllegalStateException")
                                                    }
                                                }
                                                in 30..50 -> {
                                                    try {
                                                        binding.pbHumidityParcelle.progressDrawable =
                                                            requireActivity().getDrawable(
                                                                R.drawable.custom_general_seekbar_progress_orange
                                                            )
                                                        binding.tvStateHumidityParcelle.text =
                                                            getString(R.string.tv_state_humid_m)
                                                    } catch (error: IllegalStateException) {
                                                        println("IllegalStateException")
                                                    }
                                                }
                                                in 50..70 -> {
                                                    try {
                                                        binding.pbHumidityParcelle.progressDrawable =
                                                            requireActivity().getDrawable(
                                                                R.drawable.custom_general_seekbar_progress_green
                                                            )
                                                        binding.tvStateHumidityParcelle.text =
                                                            getString(R.string.tv_state_humid)
                                                    } catch (error: IllegalStateException) {
                                                        println("IllegalStateException")
                                                    }
                                                }
                                                in 70..85 -> {
                                                    try {
                                                        binding.pbHumidityParcelle.progressDrawable =
                                                            requireActivity().getDrawable(
                                                                R.drawable.custom_general_seekbar_progress_green
                                                            )
                                                        binding.tvStateHumidityParcelle.text =
                                                            getString(R.string.tv_state_humid_p)
                                                    } catch (error: IllegalStateException) {
                                                        println("IllegalStateException")
                                                    }
                                                }
                                                in 85..100 -> {
                                                    try {
                                                        binding.pbHumidityParcelle.progressDrawable =
                                                            requireActivity().getDrawable(
                                                                R.drawable.custom_general_seekbar_progress_orange
                                                            )
                                                        binding.tvStateHumidityParcelle.text =
                                                            getString(R.string.tv_state_too_humid)
                                                    } catch (error: IllegalStateException) {
                                                        println("IllegalStateException")
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                            }

                            "cle_en_main" -> {
                                binding.clHumidityAnyType.visibility = View.VISIBLE
                                binding.cvWater.visibility = View.VISIBLE

                                val wt = gardenerStats?.waterLevel
                                val textViewWater = binding.tvStateWater
                                val water = "WaterLevel"
                                checkProgressNumber(water, wt, binding.pbWaterBar, textViewWater)

                            }
                            "mural" -> {
                                binding.clHumidityAnyType.visibility = View.VISIBLE
                            }
                            "capteur_jardiniere" -> {
                                binding.clHumidityAnyType.visibility = View.VISIBLE
                            }
                            "capteur_pot" -> {
                                binding.clHumidityAnyType.visibility = View.VISIBLE
                            }
                            "capteur_carre" -> {
                                binding.clHumidityAnyType.visibility = View.VISIBLE
                            }
                        }

                    }




                    val hs = gardenerStats?.soilMisture
                    val c0 = gardenerStats?.capacities?.c1
                    val c1 = gardenerStats?.capacities?.c2
                    val c2 = gardenerStats?.capacities?.c3
                    val c3 = gardenerStats?.capacities?.c4

                    val humidity = "Humidité"

                    val bt = gardenerStats?.battery

                    val batterie = "Batterie"

                    val textViewBattery = binding.tvStateBattery

                    checkProgressNumber(batterie, bt, binding.pbBatteryBar, textViewBattery)

                    binding.pbBatteryBarLoading.visibility = View.GONE


                    if (gardenerStats?.capacities?.c1 != null) {
                        println("Capacité 0 : " + gardenerStats.capacities.c1)
                        val textViewHumidity = binding.tvStateHumidityC0
                        textViewHumidity.visibility = View.VISIBLE
                        binding.pbHumidityC0.visibility = View.VISIBLE

                        binding.ivFloor1.setColorFilter(
                            ContextCompat.getColor(
                               context?:return,
                                R.color.green_plantr
                            ), android.graphics.PorterDuff.Mode.MULTIPLY
                        );
                        binding.ivFloor1.setImageResource(R.drawable.etage2_1)
                        binding.ivFloor1.setColorFilter(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.green_plantr
                            ), android.graphics.PorterDuff.Mode.MULTIPLY
                        );
                        binding.ivFloor1.visibility = View.GONE


                        println("le type : " + profilViewModel.userService.typeGarden)
                        checkProgressNumberHumidity(
                            humidity,
                            c0,
                            binding.pbHumidityC0,
                            textViewHumidity
                        )

                    }

                }


                override fun onCancelled(databaseError: DatabaseError) {
                    AlerterService.showError("${getString(R.string.ERROR)}", requireActivity())
                }


                @RequiresApi(Build.VERSION_CODES.N)
                @SuppressLint("ResourceAsColor")
                private fun checkProgressNumber(
                    name: String,
                    value: Int?,
                    bar: ProgressBar,
                    textView: TextView
                ) {

                        when (value) {
                            in 1..19 -> {
                                value?.let {
                                    bar.setProgress(it, true)
                                    activity?.let { act ->
                                        bar.progressDrawable =
                                            act.getDrawable(R.drawable.custom_general_seekbar_progress_red)
                                    }
                                    textView.text = getString(R.string.tv_state_bad)

                                    if (arrayTips["battery"] == true || arrayTips["capa"] == true || arrayTips["luminosity"] == true || arrayTips["humidity"] == true || arrayTips["waterLevel"] == true) {
                                        binding.ivNotification.visibility = View.GONE
                                    } else {
                                        arrayTips = HashMap()
                                        binding.ivNotification.visibility = View.GONE
                                    }
                                }
                            }
                            in 20..39 -> {
                                value?.let {
                                    bar.setProgress(it, true)
                                    activity?.let { act ->
                                        bar.progressDrawable =
                                            act.getDrawable(R.drawable.custom_general_seekbar_progress_orange)
                                    }
                                    textView.text = getString(R.string.tv_state_medium)
                                }
                            }
                            else -> {
                                value?.let {
                                    bar.setProgress(it, true)
                                    activity?.let { act2 ->
                                        bar.progressDrawable =
                                            act2.getDrawable(R.drawable.custom_general_seekbar_progress_green)
                                    }
                                    textView.text = getString(R.string.tv_state_good)
                                }
                            }
                        }
                    }


            })
    }

    override fun onDestroy() {
        super.onDestroy()
    }


    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("ResourceAsColor")
    private fun checkProgressNumberHumidity(
        name: String,
        value: Int?,
        bar: ProgressBar,
        textView: TextView
    ) {
        if(context != null) {
            when (value) {
            in 1..45 -> {
                value?.let {
                    bar.setProgress(it, true)
                    activity?.let { act ->
                        bar.progressDrawable =
                            act.getDrawable(R.drawable.custom_general_seekbar_progress_red)
                    }
                    if (arrayTips["battery"] == true || arrayTips["capa"] == true ||arrayTips["luminosity"] == true ||  arrayTips["waterLevel"] == true) {
                        binding.ivNotification.visibility = View.GONE
                    } else {
                        arrayTips = HashMap()
                        binding.ivNotification.visibility = View.GONE
                    }
                    if (name == "Batterie") {
                        textView.text = "Batterie très faible"

                    } else {
                        textView.text = getString(R.string.tv_state_seche)
                    }
                    binding.pbHumidityBarLoading.visibility = View.GONE
                }
            }
            in 45..65 -> {
                value?.let {
                    bar.setProgress(it, true)
                    activity?.let { act ->
                        bar.progressDrawable =
                            act.getDrawable(R.drawable.custom_general_seekbar_progress_orange)
                    }
                    if (name == "Batterie") {
                        textView.text = "Batterie faible"
                    } else {
                        textView.text = getString(R.string.tv_state_humid_m)
                    }
                    binding.pbHumidityBarLoading.visibility = View.GONE

                }
            }
            in 65..80 -> {
                value?.let {
                    bar.setProgress(it, true)
                    activity?.let { act ->
                        bar.progressDrawable =
                            act.getDrawable(R.drawable.custom_general_seekbar_progress_green)
                    }
                    if (name == "Batterie") {
                        textView.text = getString(R.string.tv_state_good)
                    } else {
                        textView.text = getString(R.string.tv_state_humid_p)
                    }
                    binding.pbHumidityBarLoading.visibility = View.GONE

                }
            }
            in 80..100 -> {
                value?.let {
                    bar.setProgress(it, true)
                    activity?.let { act ->
                        bar.progressDrawable =
                            act.getDrawable(R.drawable.custom_general_seekbar_progress_orange)
                    }

                    if (name == "Batterie") {
                        textView.text = getString(R.string.tv_state_good)
                    } else {
                        textView.text = getString(R.string.tv_state_too_humid)
                    }

                    binding.pbHumidityBarLoading.visibility = View.GONE

                }
            }

            else -> {
                value?.let {
                    bar.animate()
                    bar.setProgress(it, true)
                    activity?.let { act2 ->
                        bar.progressDrawable =
                            act2.getDrawable(R.drawable.custom_general_seekbar_progress_orange)
                    }

                    textView.text = getString(R.string.tv_state_too_humid)
                    binding.pbHumidityBarLoading.visibility = View.GONE
                }
            }
        }
        } else {
            return
        }

    }

    private fun checkPersmission(): Boolean {
        return (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                ) {
                    showPictureDialog()
                } else {
                    Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
            else -> {
            }
        }
    }

    fun multiply(x: Int, y: Double) = x * y  // 4

    fun multiplyInt(x: Int, y: Int) = x * y  // 4

    fun Double.round(decimals: Int): Double {
        var multiplier = 1.0
        repeat(decimals) { multiplier *= 10 }
        return round(this * multiplier) / multiplier
    }

    @SuppressLint("SetTextI18n")
    private fun updateLumCalcul(gardenerStats: GardenerStats?) {
       if(profilViewModel.userService.typeGarden == "cle_en_main") {
                val calcul = gardenerStats?.luminosity?.toInt()?.let { multiply(it, 8.8756) }
                val calculConvertToKlx = calcul?.div(1000)?.round(2)
                if (calcul != null) {
                    println("GROS CALCUL OUAIS OUAIS OUAIS")
                    println("Lum avant : " + gardenerStats.luminosity?.toInt())
                    println("Lum après : " + calculConvertToKlx)
                    if (calculConvertToKlx != null) {
                        binding.tvPourcentLuminosity.text = calculConvertToKlx.toString() + " klx"
                    }
                }
        }
    }

    private fun showPictureDialog() {
        println("DANS LE MENU")

        val pictureDialog = AlertDialog.Builder(context)
        pictureDialog.setTitle(getString(R.string.SELECT_DIALOG))
        val pictureDialogItems =
            arrayOf(getString(R.string.TAKE_IN_GALERY), getString(R.string.TAKE_PICTURE))
        pictureDialog.setItems(
            pictureDialogItems
        ) { _, which ->
            when (which) {
                0 -> takeInGallery()
                1 -> takePicture()
            }
        }
        pictureDialog.show()
    }

    private fun takeInGallery() {
        println("Je suis dans le takeGallery")
        ImagePicker.with(this).galleryOnly().galleryMimeTypes(arrayOf("image/*")).crop(300F,130F).start()
    }

    private fun takePicture() {
        println("takePicture")
        println("Je suis dans le takePicture")
        ImagePicker.with(this).cameraOnly().crop(300F,130F).start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == ImagePicker.REQUEST_CODE) {
            data?.let {
                uploadImgFirebase(data.data!!)
            }
        }
    }

    private fun uploadImgFirebase(uri: Uri) {
        println("Ah ouais j'upload la")
        FirebaseService().firebase.currentUser?.uid?.let { uid ->
            val key = Firebase.database.reference.push().key //Permet juste de créer une clée de push
            val list = HashMap<String, Boolean>()
            key?.let { keys ->
                list.put(keys, true)
                if (profilViewModel.userService.gardenerId != null) {
                    FirebaseService().getStorageGardener(profilViewModel.userService.gardenerId!!)
                        .child(keys + ".jpg").putFile(uri)
                        .addOnSuccessListener {
                            activity?.let { act ->
                                FirebaseService().getGardenerImages(profilViewModel.userService.gardenerId!!).setValue(list.toMap())
                                AlerterService.showGood(getString(R.string.alerter_import_image), act)
                            }
                        }
                        .addOnFailureListener {
                            activity?.let { act2 ->
                                AlerterService.showError(
                                    "${getString(R.string.alerter_import_image_problem)}",
                                    act2
                                )
                            }
                        }
                }
            }
        }
    }




    override fun disConnected(device: BleDevice?) {
        if (singleton.bleDevice != null) {
            if (device != null && device.key == singleton.bleDevice!!.key) {
                //finish();
            }
        }
    }


    private fun execTask() {
        println("ExecTask function...") // FAIRE UN CALL BACK QUI RENVOIE SUCCESS POUR LANCER LA SUITE ET ETC...
        if (singleton.connectedState && singleton.launchStateData == false) {
            if (singleton.bleDevice != null) {
                singleton.loadSensorData(singleton.mode)
            }
        }
    }



    private fun runOnUiThread(runnable: Runnable) {
        if (isAdded) requireActivity().runOnUiThread(runnable)
    }
}