package fr.devid.plantR.ui.publicAppView
import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.GsonBuilder
import fr.devid.plantR.Constants
import fr.devid.plantR.R
import fr.devid.plantR.base.BaseFragment
import fr.devid.plantR.databinding.FragmentAddConfigPotagerAgroveBinding
import fr.devid.plantR.models.ClassicGardener
import fr.devid.plantR.models.GardenerMetadata
import fr.devid.plantR.models.WeatherApp
import fr.devid.plantR.services.AlerterService
import fr.devid.plantR.services.FirebaseService
import fr.devid.plantR.services.Singleton
import fr.devid.plantR.ui.home.ProfileViewModel
import fr.devid.plantR.utils.PaysUtils
import fr.devid.plantR.viewmodels.Event
import okhttp3.*
import java.io.IOException
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

/**
 * LG
 */

class FragmentAddPotager : BaseFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val profilViewModel: ProfileViewModel by activityViewModels { viewModelFactory }
    private lateinit var binding: FragmentAddConfigPotagerAgroveBinding
    private val data: FragmentAddPotagerArgs by navArgs()
    private lateinit var geocoder: Geocoder
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var city: String
    private lateinit var mSpinnerEmplacement: Spinner
    private lateinit var mSpinnerEnsoleillement: Spinner
    private lateinit var mSpinnerOrientation: Spinner
    private var selectSpinnerOrientation : Int = 0
    private lateinit var mSpinnerPays: Spinner
    private var selectSpinnerEnsoleillement : Int = 0
    private var selectSpinnerEmplacement : Int = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentAddConfigPotagerAgroveBinding.inflate(inflater, container, false)
        mSpinnerEmplacement = binding.spinnerEmplacement
        // We have here a layout which is stored in a variable in an arrayAdapter, this one is then used to store the name of the planters there.
        val adapter = ArrayAdapter(requireActivity(), R.layout.view_drop_down_menu_spinner, mutableListOf<String>())

        mSpinnerEmplacement.adapter = adapter
        mSpinnerEmplacement.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View,
                position: Int, id: Long
            ) {

                Constants.arrayEmplacement.let { emplacement ->
                    if(position == 0 ) {
                        println("Selection : " + Constants.arrayEmplacement.get(0))
                        selectSpinnerEmplacement = 0
                    }
                    if(position == 1) {
                        println("Selection : " + Constants.arrayEmplacement.get(1))
                        selectSpinnerEmplacement = 1
                    }
                    if(position == 2) {
                        println("Selection : " + Constants.arrayEmplacement.get(2))
                        selectSpinnerEmplacement = 2
                    }
                    if(position == 3) {
                        println("Selection : " + Constants.arrayEmplacement.get(3))
                        selectSpinnerEmplacement = 3
                    }
                    if(position == 4) {
                        println("Selection : " + Constants.arrayEmplacement.get(4))
                        selectSpinnerEmplacement = 4
                    }
                    if(position == 5) {
                        println("Selection : " + Constants.arrayEmplacement.get(5))
                        selectSpinnerEmplacement = 5
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        mSpinnerPays = binding.spinnerPays
        // We have here a layout which is stored in a variable in an arrayAdapter, this one is then used to store the name of the planters there.
        val adapterPays = ArrayAdapter(requireActivity(), R.layout.view_drop_down_menu_spinner, mutableListOf<String>())
        mSpinnerPays.adapter = adapterPays
        mSpinnerPays.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                    parent: AdapterView<*>, view: View,
                    position: Int, id: Long) {
                val test = parent.getItemAtPosition(position)
                println("LE ITEM QUE JE REGARDE : " + test)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        val adapterPaysList = mSpinnerPays.adapter as ArrayAdapter<String>
        adapterPaysList.clear()
        adapterPaysList.addAll(PaysUtils(requireContext()).getAllPaysName())


        mSpinnerEnsoleillement = binding.spinnerEnsoleillement
        // We have here a layout which is stored in a variable in an arrayAdapter, this one is then used to store the name of the planters there.
        val adapterEnsoleillement = ArrayAdapter(requireActivity(), R.layout.view_drop_down_menu_spinner, mutableListOf<String>())
        mSpinnerEnsoleillement.adapter = adapterEnsoleillement
        mSpinnerEnsoleillement.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View,
                position: Int, id: Long
            ) {
                Constants.arrayEnsoleillement.let { emplacement ->
                    if(position == 0 ) {
                        println("Selection : " + Constants.arrayEnsoleillement.get(0))
                        selectSpinnerEnsoleillement = 0
                    }
                    if(position == 1) {
                        println("Selection : " + Constants.arrayEnsoleillement.get(1))
                        selectSpinnerEnsoleillement = 1
                    }
                    if(position == 2) {
                        println("Selection : " + Constants.arrayEnsoleillement.get(2))
                        selectSpinnerEnsoleillement = 2
                    }
                    if(position == 3) {
                        println("Selection : " + Constants.arrayEnsoleillement.get(3))
                        selectSpinnerEnsoleillement = 3
                    }
                    if(position == 4) {
                        println("Selection : " + Constants.arrayEnsoleillement.get(4))
                        selectSpinnerEnsoleillement = 4
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }


        mSpinnerOrientation = binding.spinnerOrientation
        // We have here a layout which is stored in a variable in an arrayAdapter, this one is then used to store the name of the planters there.
        val adapterOrientation = ArrayAdapter(requireActivity(), R.layout.view_drop_down_menu_spinner, mutableListOf<String>())
        mSpinnerOrientation.adapter = adapterOrientation
        mSpinnerOrientation.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View,
                position: Int, id: Long
            ) {
                Constants.arrayOrientation.let { orientation ->
                    if(position == 0 ) {
                        println("Selection : " + Constants.arrayOrientation.get(0))
                        selectSpinnerOrientation = 0
                    }
                    if(position == 1) {
                        println("Selection : " + Constants.arrayOrientation.get(1))
                        selectSpinnerOrientation = 1
                    }
                    if(position == 2) {
                        println("Selection : " + Constants.arrayOrientation.get(2))
                        selectSpinnerOrientation = 2
                    }
                    if(position == 3) {
                        println("Selection : " + Constants.arrayOrientation.get(3))
                        selectSpinnerOrientation = 3
                    }
                    if(position == 4) {
                        println("Selection : " + Constants.arrayOrientation.get(4))
                        selectSpinnerOrientation = 4
                    }
                    if(position == 5) {
                        println("Selection : " + Constants.arrayOrientation.get(5))
                        selectSpinnerOrientation = 5
                    }
                    if(position == 6) {
                        println("Selection : " + Constants.arrayOrientation.get(6))
                        selectSpinnerOrientation = 6
                    }
                    if(position == 7) {
                        println("Selection : " + Constants.arrayOrientation.get(7))
                        selectSpinnerOrientation = 7
                    }
                    if(position == 8) {
                        println("Selection : " + Constants.arrayOrientation.get(8))
                        selectSpinnerOrientation = 8
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        val adapterEnso = mSpinnerEnsoleillement.adapter as ArrayAdapter<String>
        adapterEnso.clear()
        val arrayString = Constants.arrayEnsoleillement
        val newArrayString = ArrayList<String>()
        arrayString.forEach {
            newArrayString.add(getString(it))
        }
        adapterEnso.addAll(newArrayString)
        println("Selection : " + Constants.arrayEnsoleillement.get(0))


        val adapterEnpla = mSpinnerEmplacement.adapter as ArrayAdapter<String>
        adapterEnpla.clear()
        val arrayStringEmpla = Constants.arrayEmplacement
        val newArrayStringEmpla = ArrayList<String>()
        arrayStringEmpla.forEach {
            newArrayStringEmpla.add(getString(it))
        }

        adapterEnpla.addAll(newArrayStringEmpla)
        println("Selection : " + Constants.arrayEmplacement.get(0))

        val adapterOrienta = mSpinnerOrientation.adapter as ArrayAdapter<String>
        adapterOrienta.clear()
        val arrayStringOrientation = Constants.arrayOrientation
        val newArrayStringPlace = ArrayList<String>()
        arrayStringOrientation.forEach {
            newArrayStringPlace.add(getString(it))
        }
        adapterOrienta.addAll(newArrayStringPlace)
        println("Selection : " + Constants.arrayOrientation.get(0))


        initView()
        return binding.root

    }

    private fun initView() {

        if(data.typeGardener == "parcelle") {
            binding.clSpinnerOrientation.visibility = View.VISIBLE
            binding.tvOrientation.visibility = View.VISIBLE
        }

        binding.ivBack.setOnClickListener {
            print("RETOUR")
            profilViewModel.userService.stateForAddGardener = true
            findNavController().popBackStack()
        }

//        binding.ivLocalisation.setOnClickListener {
//            println("GEOLOC OUAIS")
//            //This code allows the gps localization of the user, at the time of the click
//            binding.ivLocalisation.visibility = View.INVISIBLE
//            binding.pbLocalisationLoading.visibility = View.VISIBLE
//            geocoder = Geocoder(requireActivity(), Locale.getDefault())
//            fusedLocationClient =
//                LocationServices.getFusedLocationProviderClient(requireActivity().applicationContext)
//            if (ActivityCompat.checkSelfPermission(
//                    requireActivity(),
//                    Manifest.permission.ACCESS_FINE_LOCATION
//                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                    requireActivity(),
//                    Manifest.permission.ACCESS_COARSE_LOCATION
//                ) != PackageManager.PERMISSION_GRANTED
//            ) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                return@setOnClickListener
//            }
//            fusedLocationClient.lastLocation
//                .addOnSuccessListener { location: Location? ->
//                    if (location != null) {
//                        try {
//                            val objet = geocoder.getFromLocation(location.latitude, location.longitude, 1)
//                            val ville = objet.getOrNull(0)
//                            if (ville != null) {
//                                city = "${ville.locality}"
//                                profilViewModel.userService.cityUser = city
//                                binding.etVille.setText(city.capitalize())
//                            } else {
//                                AlerterService.showError(
//                                    "${context?.getString(R.string.ADDRESS_NOT_FOUND)}",
//                                    requireActivity()
//                                )
//                            }
//
//                        } catch (e : IOException) {
//                            AlerterService.showError(
//                                "${context?.getString(R.string.ERROR)}",
//                                requireActivity()
//                            )
//                            println("Problème avec la géolocalisation ligne 270")
//                        }
//
//
//                    } else {
//                        AlerterService.showError(
//                            "${context?.getString(R.string.GPS_NOT_FOUND)}",
//                            requireActivity()
//                        )
//                    }
//                    binding.ivLocalisation.visibility = View.VISIBLE
//                    binding.pbLocalisationLoading.visibility = View.INVISIBLE
//                }
//            //End code of localization
//        }

        binding.btValid.setOnClickListener {



            if(binding.etNom.text.isNotEmpty()) {
                when (data.type) {
                    "pot" -> {
                        when (data.dimension) {
                            0 -> {
                                addGardener("pot", 0, "1")
                            }
                            1 -> {
                                addGardener("pot", 1, "1")
                            }
                            2 -> {
                                addGardener("pot", 2, "1")
                            }
                        }
                    }
                    "jardiniere" -> {
                        when (data.dimension) {
                            0 -> {
                                addGardener("jardiniere", 0, "1")
                            }
                            1 -> {
                                addGardener("jardiniere", 1, "1")
                            }
                            2 -> {
                                addGardener("jardiniere", 2, "1")
                            }
                        }
                    }
                    "carre" -> {
                        when (data.dimension) {
                            0 -> {
                                addGardener("carre", 0, "2")
                            }
                            1 -> {
                                addGardener("carre", 1, "3")
                            }
                            2 -> {
                                addGardener("carre", 2, "4")
                            }
                        }
                    }
                    "capteur_carre" -> {
                        when (data.dimension) {
                            0 -> {
                                addGardener("carre", 0, "2")
                            }
                            1 -> {
                                addGardener("carre", 1, "3")
                            }
                            2 -> {
                                addGardener("carre", 2, "4")
                            }
                        }
                    }
                    "cle_en_main" -> {
                        when (data.dimension) {
                            //ALWAYS 4 PLANTES
                            2 -> {
                                addGardener("cle_en_main", 2, "4")
                            }
                        }
                    }
                    "mural" -> {
                        when (data.dimension) {
                            //ALWAYS 4 PLANTES
                            2 -> {
                                addGardener("mural", 2, "4")
                            }
                        }
                    }
                }
            } else {
                AlerterService.showError("${getString(R.string.alerter_name_ur_garden)}", requireActivity())
            }




        }

    }


    fun addGardener(type: String, dimension: Int, stage : String) {

        val urlStr = "${Constants.ADRESSE_URL}zip=${binding.etPostalCode.text},${PaysUtils(requireContext()).getCodeByName(mSpinnerPays.selectedItem.toString())}&appid=${Constants.WeatherApiKey}"
        val client: OkHttpClient = OkHttpClient().newBuilder()
            .build()
        val request: Request = Request.Builder()
            .url(urlStr)
            .method("GET", null)
            .build()
        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                println("J'ai un problème avec la récupération : " + call)
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                val myGson = GsonBuilder().create()
                val myObject = myGson.fromJson(body, WeatherApp::class.java)
                if(myObject.cod == Constants.IS_SUCCESS_API_SEARCHED) {
                    if (type == "pot" || type == "carre" || type == "jardiniere") {
                        println("J'ARRIVE DANS L AJOUT DE LA JARDINIERE")
                        FirebaseService().firebase.currentUser?.uid?.let { userId ->
                            println("JE PASSE DANS LE CURRENT USER")
                            val hashMapUserId = HashMap<String, Boolean>()
                            hashMapUserId.put(userId ?: "", true)
                            println("MON USER ID : " + userId)
                            println("**************AFFICHAGE PRINT ADD POTAGER*****************" + "\nType de gardener : " + data.type + "\nTaille : " + data.dimension + "\n*****************FIN DE L AFFICHAGE*****************")
                            val rootRef = FirebaseDatabase.getInstance().reference
                            val gardenerRef = rootRef.child("gardeners")
                            val key = "Classic" + gardenerRef.push().key
                            println("AFFICHE DE LA KEY : " + key)
                            if (::city.isInitialized) {
                                val metadata = GardenerMetadata(
                                    "",
                                    PaysUtils(requireContext()).getCodeByName(binding.spinnerPays.selectedItem.toString()),
                                    binding.etPostalCode.text.toString(),
                                    "",
                                    selectSpinnerEmplacement,
                                    selectSpinnerEnsoleillement,
                                    HashMap(),
                                    binding.etNom.text.trimEnd().toString()
                                )
                                println("LE COUNTRY CODE ADD POTAGER : " + metadata.countryCode)
                                val newGardener = ClassicGardener(false, stage, metadata, hashMapUserId, type, dimension)

                                FirebaseService().getUserByid(userId).child("gardeners").child(key).setValue(true)
                                FirebaseService().getGardenerReference().child(key).setValue(newGardener).addOnCompleteListener {
                                    if(it.isSuccessful) {
                                        profilViewModel.userService.stateForAddGardener = true
                                        findNavController().popBackStack(R.id.homeFragment, false)
                                        AlerterService.showGood("Nouveau potager ajouté !", requireActivity())
                                    }
                                }.addOnFailureListener { AlerterService.showError("${getString(R.string.ERROR)}", requireActivity())
                                }
                            } else {
                                val metadata = GardenerMetadata(
                                    "",
                                    PaysUtils(requireContext()).getCodeByName(binding.spinnerPays.selectedItem.toString()),
                                    binding.etPostalCode.text.toString(),
                                    "",
                                    selectSpinnerEmplacement,
                                    selectSpinnerEnsoleillement,
                                    HashMap(),
                                    binding.etNom.text.trimEnd().toString()

                                )
                                val newGardener = ClassicGardener(false, stage, metadata, hashMapUserId, type, dimension)
                                FirebaseService().getUserByid(userId).child("gardeners").child(key).setValue(true)

                                FirebaseService().getGardenerReference().child(key).setValue(newGardener).addOnCompleteListener {
                                    if(it.isSuccessful) {
                                        profilViewModel.userService.stateForAddGardener = true
                                        findNavController().popBackStack(R.id.homeFragment, false)
                                        AlerterService.showGood("Nouveau potager ajouté !", requireActivity())
                                    }
                                }.addOnFailureListener {
                                    AlerterService.showError("${getString(R.string.ERROR)}", requireActivity())
                                }


                            }

                        }
                    } else {
                        FirebaseService().firebase.currentUser?.uid?.let { userId ->

                            if (data.gardenerId.isNotEmpty()) {

                                val hashMapUserId = HashMap<String, Boolean>()
                                hashMapUserId.put(userId ?: "", true)
                                val metadata = GardenerMetadata(
                                    "",
                                    mSpinnerPays.selectedItem.toString(),
                                    binding.etPostalCode.text.toString(),
                                    "",
                                    selectSpinnerEmplacement,
                                    selectSpinnerEnsoleillement,
                                    HashMap(),
                                    binding.etNom.text.trimEnd().toString()
                                )
                                val newGardener = ClassicGardener(false, stage, metadata, hashMapUserId, type, dimension)

                                FirebaseService().getGardenerById(data.gardenerId).setValue(newGardener).addOnCompleteListener {
                                    if(it.isSuccessful) {
                                        profilViewModel.userService.stateForAddGardener = true
                                        findNavController().popBackStack(R.id.homeFragment, false)
                                    }
                                }.addOnFailureListener {
                                    AlerterService.showError(
                                        "${getString(R.string.ERROR)}",
                                        requireActivity()
                                    )
                                    AlerterService.showGood("${getString(R.string.alerter_add_garden_success)}", requireActivity())
                                }
                            }

                        }
                    }
                } else {
                    AlerterService.showError(getString(R.string.ERROR_API_ZIP), requireActivity())
                }
            }
        })


    }


    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
    }

}
