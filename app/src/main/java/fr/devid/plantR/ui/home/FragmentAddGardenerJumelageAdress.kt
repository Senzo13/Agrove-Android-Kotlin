package fr.devid.plantR.ui.home

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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.gson.GsonBuilder
import com.tapadoo.alerter.Alerter
import fr.devid.plantR.Constants
import fr.devid.plantR.R
import fr.devid.plantR.base.BaseFragment
import fr.devid.plantR.databinding.FragmentAddGardenerJumelageGardenerAdressBinding
import fr.devid.plantR.models.GardenerMetadata
import fr.devid.plantR.models.WeatherApp
import fr.devid.plantR.services.AlerterService
import fr.devid.plantR.services.FirebaseService
import fr.devid.plantR.utils.PaysUtils
import okhttp3.*
import java.io.IOException
import java.util.*
import javax.inject.Inject

/**
 * LG
 */

class FragmentAddGardenerJumelageAdress : BaseFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val profilViewModel: ProfileViewModel by activityViewModels { viewModelFactory }
    private val PAGE = "**** FragmentAddGardenerJumelageAdress ****"
    private lateinit var binding: FragmentAddGardenerJumelageGardenerAdressBinding
    private val data: FragmentAddGardenerJumelageAdressArgs by navArgs()
    private lateinit var gardenerRef: DatabaseReference
    private lateinit var handleGardenerRef: ValueEventListener
    private lateinit var geocoder: Geocoder
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var setData: GardenerMetadata
    private lateinit var mSpinnerEmplacement: Spinner
    private lateinit var mSpinnerEnsoleillement: Spinner
    private lateinit var mSpinnerOrientation: Spinner
    private var selectSpinnerEnsoleillement : Int = 0
    private var selectSpinnerOrientation : Int = 0
    private var selectSpinnerEmplacement : Int = 0
    private lateinit var mSpinnerPays: Spinner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddGardenerJumelageGardenerAdressBinding.inflate(inflater, container, false)
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

        val adapterEnso = mSpinnerEnsoleillement.adapter as ArrayAdapter<String>
        adapterEnso.clear()
        val arrayString = Constants.arrayEnsoleillement
        val newArrayStringEnso = ArrayList<String>()
        arrayString.forEach {
            newArrayStringEnso.add(getString(it))
        }

        adapterEnso.addAll(newArrayStringEnso)
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


        if(data.type == "parcelle") {
            binding.clSpinnerOrientation.visibility = View.VISIBLE
            binding.tvOrientation.visibility = View.VISIBLE
        }

        binding.ivBack.setOnClickListener {
          //
        }

//        binding.ivLocalisation.setOnClickListener {
//            //This code allows the gps localization of the user, at the time of the click
//            binding.ivLocalisation.visibility = View.INVISIBLE
//            binding.pbLocalisationLoading.visibility = View.VISIBLE
//            geocoder = Geocoder(requireActivity(), Locale.getDefault())
//            fusedLocationClient =
//                LocationServices.getFusedLocationProviderClient(requireActivity().applicationContext)
//            if (ActivityCompat.checkSelfPermission(
//                    requireActivity(),
//                    Manifest.permission.ACCESS_FINE_LOCATION
//                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission (requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                return@setOnClickListener
//            }
//            fusedLocationClient.lastLocation
//                .addOnSuccessListener { location: Location? ->
//                    if (location != null) {
//
//                        val objet = geocoder.getFromLocation(location.latitude, location.longitude, 1)
//                        val ville = objet.getOrNull(0)
//                        if (ville != null) {
//                            val city = "${ville.locality}"
//                            profilViewModel.userService.cityUser = city
//                            binding.etVille.setText(city.capitalize())
//                        } else {
//                            AlerterService.showError(
//                                "${context?.getString(R.string.ADDRESS_NOT_FOUND)}",
//                                requireActivity()
//                            )
//                        }
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

        binding.btFinish.setOnClickListener {
            if(binding.etNom.text.isEmpty()) {
                AlerterService.showError(getString(R.string.alerter_name_ur_garden), requireActivity())
            } else {

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
                            //Here we retrieve the user's information, if the function code setGardenerDataObserver() in the onResume has been working properly before.
                            //Indeed, we keep in memory in a variable the metadata of our use.
                            if (::setData.isInitialized) {
                                setData.name = binding.etNom.text.toString().trimEnd()
                                setData.countryCode = PaysUtils(requireContext()).getCodeByName(binding.spinnerPays.selectedItem.toString())
                                println("LE COUNTRY CODE : " + setData.countryCode)
                                setData.zipCode = binding.etPostalCode.text.toString()
                                setData.emplacement = selectSpinnerEmplacement
                                setData.ensoleillement = selectSpinnerEnsoleillement
                                setData.orientation = selectSpinnerOrientation

                                if(profilViewModel.userService.cityUser != null) {
                                    setData.city = profilViewModel.userService.cityUser!!.trimEnd()
                                }
                                FirebaseService().getGardenerMetadataById(data.guid).setValue(setData)
                                    .addOnSuccessListener {
                                        println("$PAGE + L'ajout des metadata de la jardnière à bien réussis ! ")
                                        findNavController().navigate(FragmentAddGardenerJumelageAdressDirections.actionFragmentAddGardenerJumelageAdressToHomeFragment())
                                    }.addOnFailureListener {
                                        println("$PAGE L'ajout des metadata de la jardinière n'a pas fonctionné !")
                                    }
                            }
                        } else {
                            AlerterService.showError(getString(R.string.ERROR_API_ZIP), requireActivity())
                        }
                        }})


            }
        }
    }

        private fun setGardenerDataObserver() {
            //Function code that retrieves information from the user's metadata
            gardenerRef = FirebaseService().getGardenerMetadataById(data.guid)
            handleGardenerRef = gardenerRef.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    println("${PAGE} onCancelled " + error.toException())
                }
                override fun onDataChange(snapshot: DataSnapshot) {
                    var gardenerMetadata = snapshot.getValue(GardenerMetadata::class.java)
                    if(gardenerMetadata != null) {
                        setData = gardenerMetadata
                    }
                }
            })
        }

        override fun onResume() {
            setGardenerDataObserver()
            super.onResume()
        }

        override fun onStop() {
            removeListener()
            super.onStop()
        }

        fun removeListener() {
        if (::gardenerRef.isInitialized && ::handleGardenerRef.isInitialized)
            gardenerRef.removeEventListener(handleGardenerRef)
    }
}
