package fr.devid.plantR.ui.profile

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.executor.GlideExecutor.UncaughtThrowableStrategy.LOG
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import fr.devid.plantR.Constants
import fr.devid.plantR.R
import fr.devid.plantR.base.BaseFragment
import fr.devid.plantR.databinding.FragmentProfileBinding
import fr.devid.plantR.models.*
import fr.devid.plantR.services.AlerterService
import fr.devid.plantR.services.FirebaseService
import fr.devid.plantR.services.PicturesUtils
import fr.devid.plantR.services.Singleton
import fr.devid.plantR.ui.home.ProfileViewModel
import fr.devid.plantR.utils.PaysUtils
import fr.devid.plantR.viewmodels.Event
import okhttp3.*
import okhttp3.Request
import java.io.IOException
import java.io.InputStream
import java.lang.IllegalStateException
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class ProfileFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val profilViewModel: ProfileViewModel by activityViewModels { viewModelFactory }
    private val data: ProfileFragmentArgs by navArgs()
    private lateinit var binding: FragmentProfileBinding
    private lateinit var handleUser: ValueEventListener
    private lateinit var handleGardener: ValueEventListener
    private lateinit var userRef: DatabaseReference
    private lateinit var gardenersRef: DatabaseReference
    private lateinit var gardenersRefSecond: DatabaseReference
    private lateinit var handleGardenersRefSecond: ValueEventListener
    private var currentGardener: String? = null
    private var imagesGardeners: HashMap<String, Boolean> = HashMap()
    private var oldGardener: GardenerMetadata? = null
    private lateinit var storageRef: StorageReference
    private var MY_PERMISSIONS_REQUEST_READ_LOCATION: Int = 41
    private val PERMISSION_REQUEST_CODE: Int = 101
    private val REQUEST_IMAGE_GALERY = 0
    private val PICTURE_RESULT = 2
    private var imageUri: Uri? = null
    private var gardenersArray = arrayListOf<HomeGardener>()
    private lateinit var mSpinner: Spinner
    private lateinit var gardenersNameRef: DatabaseReference
    private var gardenersList: HashMap<String, Boolean>? = null
    private var selectSpinnerEnsoleillement : Int = -1
    private var selectSpinnerEmplacement : Int = -1
    private lateinit var mSpinnerEmplacement: Spinner
    private lateinit var mSpinnerEnsoleillement: Spinner
    private lateinit var mSpinnerPays: Spinner
    private lateinit var ownerRef : DatabaseReference
    private lateinit var checkGardenerMetadata : DatabaseReference
    private lateinit var checkGardenerMetadataHandle : ValueEventListener

    private lateinit var mSpinnerOrientation: Spinner
    private var selectSpinnerOrientation : Int = -1
    private lateinit var pays : PaysUtils

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        pays = PaysUtils(requireContext())

        mSpinner = binding.spinner
        // We have here a layout which is stored in a variable in an arrayAdapter, this one is then used to store the name of the planters there.
        val adapter = ArrayAdapter(
            requireActivity(),
            R.layout.view_drop_down_menu_spinner,
            mutableListOf<String>()
        )

        mSpinner.adapter = adapter
        mSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View,
                position: Int, id: Long
            ) {
                FirebaseService().firebase.currentUser?.uid?.let { uid ->
                    gardenersList?.let { gardeners ->
                        FirebaseService().getUserCurrentGardener(uid).setValue(gardeners.keys.toList().elementAt(position))
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

        mSpinnerEmplacement = binding.spinnerEmplacement
        // We have here a layout which is stored in a variable in an arrayAdapter, this one is then used to store the name of the planters there.
        val adapterEmplacement = ArrayAdapter(requireActivity(), R.layout.view_drop_down_menu_spinner, mutableListOf<String>())

        mSpinnerEmplacement.adapter = adapterEmplacement
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
        adapterEnpla.addAll(Constants.arrayEmplacement.map { getString(it) })

        val adapterOrienta = mSpinnerOrientation.adapter as ArrayAdapter<String>
        adapterOrienta.clear()
        adapterOrienta.addAll(Constants.arrayOrientation.map { getString(it) })

        lifecycleScope.launchWhenResumed {
            initView()
        }
        if (requireActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSIONS_REQUEST_READ_LOCATION
            )
        }
        return binding.root
    }

    private fun sendEmail(recipient: String, subject: String, message: String) {
        /*ACTION_SEND action to launch an email client installed on your Android device.*/
        val mIntent = Intent(Intent.ACTION_SEND)
        /*To send an email you need to specify mailto: as URI using setData() method
        and data type will be to text/plain using setType() method*/
        mIntent.data = Uri.parse("mailto:")
        mIntent.type = "text/plain"
        // put recipient email in intent
        /* recipient is put as array because you may wanna send email to multiple emails
           so enter comma(,) separated emails, it will be stored in array*/
        mIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
        //put the Subject in the intent
        mIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        //put the message in the intent
        mIntent.putExtra(Intent.EXTRA_TEXT, message)

        try {
            //start email intent
            startActivity(Intent.createChooser(mIntent, "Choose Email Client..."))
        }
        catch (e: Exception){
            //if any thing goes wrong for example no email client application or any exception
            //get and show exception message
            AlerterService.showError(e.message!!, requireActivity())
        }

    }

    fun read_json(context : Context) : String? {
        var input : InputStream? = null
        val jsonString : String

        try {
            input = context.assets.open("pays.json")
            println("input : " + input)

            val size = input.available()

            val buffer = ByteArray(size)

            input.read(buffer)
            println("input buffer : " + input.read(buffer))
            jsonString = String(buffer)
            println("Pays : " + jsonString)
            return jsonString
        } catch (e : IOException) {
            e.printStackTrace()
        } finally {
            input?.close()
        }
        return null
    }

    fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun setCheckOwners(gardenerId: String, gardenerName : String) {
        ownerRef = FirebaseService().getGardenerOwnersById(gardenerId)
        ownerRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val ownersChecking = snapshot.getValue<HashMap<String, Boolean>>()
                if (ownersChecking != null) {
                    FirebaseService().firebase.currentUser?.uid.let { myUID ->
                        val teamList = ownersChecking.keys
                        if(teamList.count() == 1) {
                            println("Seul détenteur actuelle de la jardinière")
                            println("Pour potager connecter, on remove une partie du potager connecté")
                            println("Pour potager classic on supprime complètement la jardinière")
                            resetGardener(gardenerId, gardenerName)
                        }

                        if(teamList.count() >= 2) {
                            removeOwnersFromList(gardenerId, gardenerName)
                            println("Ici on doit pratiquer un retrait de la liste seulement")
                            println("Pour potager classic/connecté on retire de la liste du owner qui s'est enlevé")
                        }
                    }
                }
            }
        })
    }

    private fun resetGardener(gardenerId: String, gardenerName :String) {
        if(gardenerId.contains("Classic")) {
            FirebaseService().getGardenerById(gardenerId).removeValue().addOnCompleteListener {
                if(it.isSuccessful) {
                    println("Le potager "+gardenerName+" à bien était supprimé")
                    AlerterService.showGood(gardenerName+ " ${getString(R.string.SUCCESS_DELETE)}", requireActivity())
                    actualiseGardenerListAndRemove(gardenerId)
                }
            }
        } else {
            FirebaseService().getGardenerById(gardenerId).child("dimension").removeValue()
            FirebaseService().getGardenerById(gardenerId).child("climat").removeValue()
            FirebaseService().getGardenerById(gardenerId).child("owners").removeValue()
            FirebaseService().getGardenerById(gardenerId).child("ispublic").removeValue()
            FirebaseService().getGardenerById(gardenerId).child("type").removeValue()
            FirebaseService().getGardenerById(gardenerId).child("metadata").removeValue().addOnCompleteListener {
                if(it.isSuccessful) {
                    AlerterService.showGood(gardenerName +" ${getString(R.string.remove_from_ur_list)}", requireActivity())
                    actualiseGardenerListAndRemove(gardenerId)
                    println("Jardinière connecté réinitialiser")
                }
            }
        }
    }

    private fun actualiseGardenerListAndRemove(gardenerId : String) {
        val list = gardenersList?.filter { gardenerId != it.key }
        if(!list.isNullOrEmpty()) {
            FirebaseService().firebase.currentUser?.uid?.let { uid ->
                FirebaseService().getUserCurrentGardener(uid).setValue(list.keys.first())
            }
            binding.llMyLogement.visibility = View.VISIBLE
            binding.mbLeaveThisKitchen.visibility = View.VISIBLE
        } else {
            FirebaseService().firebase.currentUser?.uid?.let { uid ->
                FirebaseService().getUserCurrentGardener(uid).setValue("")
            }
            binding.llMyLogement.visibility = View.GONE
            binding.mbLeaveThisKitchen.visibility = View.GONE
        }
        gardenersList?.let { parseGardenersList(it) }
    }

    private fun removeOwnersFromList(gardenerId : String, gardenerName : String) {
        FirebaseService().firebase.currentUser?.uid?.let {  uid ->
            FirebaseService().getGardenerById(gardenerId).child("owners").child(uid).removeValue().addOnSuccessListener {
                AlerterService.showGood(gardenerName +" ${getString(R.string.remove_from_ur_list)}", requireActivity())
            }
            actualiseGardenerListAndRemove(gardenerId)
        }
    }



    private fun checkPostalCode(zip : String, countryCode : String) {

    }

    private fun initView() {
        setObserverGard()


        if(profilViewModel.userService.gardenerId?.isNotEmpty() == true) {
            binding.mbLeaveThisKitchen.visibility = View.VISIBLE
            binding.mbLeaveThisKitchen.setOnClickListener {
                val gardenersName = profilViewModel.userService.gardenerName?.let { name ->
                    val popupLeaveGardeners = PopupLeaveGardeners(
                        getString(R.string.tv_dialog_profil_1) + " ${name} " + getString(R.string.tv_dialog_profil_2),
                        requireContext()
                    ) { popup, bool, str ->
                        if (bool && str == "confirm") {
                            profilViewModel.userService.gardenerId?.let { guid ->
                                setCheckOwners(guid, name)//ON ENLEVE LA JARDINIERE
                                popup.dismiss()
                            }
                        } else {
                            popup.dismiss()
                        }
                    }
                    popupLeaveGardeners.show()
                }
            }
        } else {
            binding.mbLeaveThisKitchen.visibility = View.GONE
        }


        binding.clInfos.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToPolicyFragment())
        }

        if(data.visitor) {
            binding.llMyLogement.visibility = View.GONE
            binding.spinner.visibility = View.GONE
        //    binding.clShareId.visibility = View.GONE
        } else {
            binding.llMyLogement.visibility = View.VISIBLE
        }

        FirebaseService().firebase.currentUser?.uid?.let { uid ->
            storageRef = FirebaseService().getStoragePictureByUserId(uid)
            storageRef.downloadUrl.addOnSuccessListener {
                Glide.with(requireActivity())
                    .load(it.toString())
                    .circleCrop()
                    .into(binding.ivProfil)
            }.addOnFailureListener {
                Glide.with(binding.ivProfil.context)
                    .load(R.drawable.user)
                    .circleCrop()
                    .into(binding.ivProfil)
            }
        }

//        binding.tvShareGardener.setOnClickListener {
//            val recipient = ""
//            val subject = "Partage ID PLANTR"
//            val message = "Vous êtes invité à rejoindre "+binding.etDenomination.text.toString().let { it } +".\nLe code est le suivant : "+binding.tvIdPlantR.text.toString()
//            sendEmail(recipient, subject, message)
//        }

        binding.ivBtnAddImage.setOnClickListener {
            if (checkPersmission()) {
                showPictureDialog()
            } else {
                requestPermission()
            }

        }

        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.ivBtnDeco.setOnClickListener {
            var dialogLogOut = PopupLogOut(requireContext()) { popup, bool, str ->
                if(bool) {
                    popup.dismiss()
                    FirebaseService().firebase.signOut()
                    findNavController().popBackStack(R.id.splashFragment, false)
                } else {
                    popup.dismiss()
                }
            }
            dialogLogOut.show()

        }
//
//        binding.ivLocalisation.setOnClickListener {
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
//                ) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                return@setOnClickListener
//            }
//
//            fusedLocationClient.lastLocation
//                .addOnSuccessListener { location: Location? ->
//                    if (location != null) {
//
//                        val objet = geocoder.getFromLocation(
//                            location.latitude,
//                            location.longitude,
//                            1
//                        )
//                        val address = objet.getOrNull(0)
//                        if (address != null) {
//                            binding.etVille.setText("${address.locality}")
//                            profilViewModel.userService.cityUser = "${address.locality}"
//                           // binding.etAdresse.setText("${address.getAddressLine(0)}")
//                        } else {
//                            AlerterService.showError(
//                                "${getString(R.string.alerter_address_not_found)}",
//                                requireActivity())
//                        }
//                    } else {
//                        AlerterService.showError(
//                            "${getString(R.string.alerter_geoloc_location_fail)}",
//                            requireActivity()
//                        )
//                    }
//                    binding.ivLocalisation.visibility = View.VISIBLE
//                    binding.pbLocalisationLoading.visibility = View.INVISIBLE
//                }
//        }

        binding.ivInfoModifier.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val newPassword = binding.etPasswordConfirm.text.toString().trim()
            if (email.isNotEmpty() && password.isNotEmpty() && newPassword.isNotEmpty()) {
                if (password == newPassword) {
                    AlerterService.showError(
                        "${getString(R.string.alerter_password_different)}",
                        requireActivity()
                    )
                } else {
                    val cred = EmailAuthProvider.getCredential(email, password)
                    FirebaseService().firebase.currentUser?.reauthenticateAndRetrieveData(cred)
                        ?.addOnCompleteListener {
                            if (it.isSuccessful) {
                                FirebaseService().firebase.currentUser?.updatePassword(newPassword)
                                AlerterService.showGood(
                                        getString(R.string.MODIF_SUCCESS),
                                    requireActivity()
                                )
                            } else {
                                AlerterService.showError(
                                    it.exception?.localizedMessage
                                        ?: "${getString(R.string.alerter_old_password_not_good)}",
                                    requireActivity()
                                )
                            }
                        }
                }
            } else {
                AlerterService.showError(getString(R.string.fill_all_fields), requireActivity())
            }
        }



        binding.ivInfoGardenerModifier.setOnClickListener {
            val codePaysSelected = PaysUtils(requireContext()).getCodeByName(binding.spinnerPays.selectedItem.toString())

            FirebaseService().firebase.currentUser?.uid?.let { uid ->
                currentGardener?.let { gardenerId ->
                    if (oldGardener != null) {
                        oldGardener?.let {
                            imagesGardeners =
                                oldGardener?.images!!
                            val newGardeners = GardenerMetadata(
                                    binding.etAdresse.text.toString(),
                                    codePaysSelected,
                                    binding.etPostalCode.text.toString(),
                                    "",
                                    selectSpinnerEmplacement ?: 0,
                                    selectSpinnerEnsoleillement ?: 0,
                                    imagesGardeners,
                                    binding.etDenomination.text.toString(),
                                    selectSpinnerOrientation?:0
                            )

                            if (PaysUtils(requireContext()).getCodeByName(binding.spinnerPays.selectedItem.toString()) != it.countryCode || binding.etPostalCode.text.toString() != it.zipCode || binding.etAdresse.text.toString() != it.address || binding.etDenomination.text.toString() != it.name || selectSpinnerEmplacement != it.emplacement || selectSpinnerEnsoleillement != it.ensoleillement || selectSpinnerOrientation != it.orientation ) {

                                val urlStr = "${Constants.ADRESSE_URL}zip=${binding.etPostalCode.text.toString()},$codePaysSelected&appid=${Constants.WeatherApiKey}"
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
                                        Singleton.instance._apiCheckingOk.postValue(Event(true))
                                        FirebaseService().getGardenerMetadataById(gardenerId).setValue(newGardeners).addOnCompleteListener {
                                            if (!it.isSuccessful) {
                                                AlerterService.showError(
                                                        getString(R.string.MODIF_FAILED),
                                                        requireActivity()
                                                )
                                            }
                                            AlerterService.showGood(
                                                    getString(R.string.MODIF_SUCCESS),
                                                    requireActivity()
                                            )
                                            //   setObserverGard()
                                        }
                                    } else {
                                        AlerterService.showError(getString(R.string.ERROR_API_ZIP), requireActivity())
                                    }
                                }
                            })







                            } else {
                                AlerterService.showError(
                                    getString(R.string.SAME_VALUE_GARDENER),
                                    requireActivity()
                                )
                            }

                        }

                    }
                }
            }
        }

        /*binding.ivModifier.setOnClickListener {}*/
    }

    fun parseGardenersList(gardeners: HashMap<String, Boolean>) {
        if (gardeners.size < 1) {
            return
        }
        val gardenersList = gardeners.keys
        gardenersArray.clear()
        gardenersList.toList().forEach { gardenersListId -> //SHOW IN ORDER BY IT
            gardenersRef = FirebaseService().getGardenerMetadataById(gardenersListId)
           gardenersRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val gardenerMetadata = dataSnapshot.getValue(GardenerMetadata::class.java)
                    gardenerMetadata?.let { metadata ->
                        gardenersArray.add(HomeGardener(gardenersListId, metadata.name))
                        val adapter = mSpinner.adapter as ArrayAdapter<String>
                        adapter.clear()
                        println(metadata.name)
                        val arrayString = gardenersArray.map { it.name.capitalize() }
                        adapter.addAll(arrayString)
                        if (gardenersList.count() == gardenersArray.count()) {
                            setCurrentSpinnerSelectionGardener()
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    println("${LOG} onCancelled ${databaseError.toException()}")
                }
            })
        }
    }

    private fun setCurrentSpinnerSelectionGardener() {
        profilViewModel.userService.gardenerId?.let {
            val elements = gardenersArray.map { e -> e.id }
            val index = elements.indexOf(it)
            if (index != -1) {
                binding.spinner.setSelection(index)
                gardenersArray.clear() //BY LORENZO
            }
        }

        profilViewModel.userService.gardenerId?.let { guid ->
            checkGardenerMetadata = FirebaseService().getGardenerMetadataById(guid)
            checkGardenerMetadata.addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val checkGardenerMetadataSpinner = snapshot.getValue(GardenerMetadata::class.java)

                    if (checkGardenerMetadataSpinner != null) {
                        try {
                            if (checkGardenerMetadataSpinner.emplacement != -1 && checkGardenerMetadataSpinner.ensoleillement != -1 && checkGardenerMetadataSpinner.orientation != -1) {

                                println("IL SONT DIFFERENT DE -1")
                                binding.spinnerEmplacement.setSelection(checkGardenerMetadataSpinner.emplacement)
                                binding.spinnerOrientation.setSelection(checkGardenerMetadataSpinner.orientation)
                                binding.spinnerEnsoleillement.setSelection(checkGardenerMetadataSpinner.ensoleillement)
                            } else {
                                binding.spinnerEmplacement.setSelection(0)
                                binding.spinnerOrientation.setSelection(0)
                                binding.spinnerEnsoleillement.setSelection(0)
                            }
                            if (checkGardenerMetadataSpinner.countryCode.isNotEmpty()) {

                                println("HashMap test : " + PaysUtils(requireContext()).hashMapPaysIndexed)
                                binding.spinnerPays.setSelection(pays.getPositionByCode(checkGardenerMetadataSpinner.countryCode))
                            } else {
                                mSpinnerPays.setSelection(0)
                            }
                        } catch (error : IllegalStateException) {
                            print("CRASH CONTEXT")
                        }

                    }

                }

                override fun onCancelled(error: DatabaseError) {

                }
            })

        }
    }

    private fun setObserverGard() {
        FirebaseService().firebase.currentUser?.uid?.let { uid ->
            userRef = FirebaseService().getUserByid(uid)
            handleUser = userRef.addValueEventListener(object : ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user = dataSnapshot.getValue(User::class.java)
                    println(user?.Metadata?.firstName + "Prenom de l'utilisateur")
                    binding.tvFirstnameLastname.text =
                        user?.Metadata?.firstName + " " + user?.Metadata?.lastName
                    binding.etEmail.setText(FirebaseService().firebase.currentUser?.email)
                    user?.currentGardener?.let { gardenerId ->
                        user.gardeners.let { gardeners ->
                            currentGardener = gardenerId
                            profilViewModel.userService.gardenerId = gardenerId
                            gardenersList = gardeners

                        }

                    if (!gardenersList.isNullOrEmpty()) {
                        parseGardenersList(gardenersList!!)
                        setGardenersObserver(user.currentGardener)
                        //MODE VISITEUR ACTIVATE
                    } else {
                        binding.llMyLogement.visibility = View.GONE
                        binding.spinner.visibility = View.GONE
                    }

                    }

                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })
        }
    }


    private fun getCurrentGardenerName(guid: String) {
        gardenersNameRef = FirebaseService().getGardenerMetadataById(guid)
        gardenersNameRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val gardenerMetadata = dataSnapshot.getValue(GardenerMetadata::class.java)
                profilViewModel.userService.gardenerName = gardenerMetadata?.name
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // handle error
            }
        })
    }

    override fun onResume() {
        super.onResume()
        setObserver()
    }

    override fun onStop() {
        super.onStop()
        removeObserver()
    }

    private fun setObserver() {
        FirebaseService().firebase.currentUser?.uid?.let { uid ->
            userRef = FirebaseService().getUserByid(uid)
             handleUser = userRef.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user = dataSnapshot.getValue(User::class.java)
                    println(user?.Metadata?.firstName)

                    user?.currentGardener?.let {
                        currentGardener = it
                    }
                }
            })
        }
    }

    private fun setGardenersObserver(gardenerId: String) {
        gardenersRefSecond = FirebaseService().getGardenerMetadataById(gardenerId)
        handleGardenersRefSecond = gardenersRefSecond.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val gardenerMetadata = dataSnapshot.getValue(GardenerMetadata::class.java)
                profilViewModel.userService.gardenerName = gardenerMetadata?.name
                println(gardenerMetadata?.name)
                binding.etDenomination.setText(gardenerMetadata?.name)
               // binding.etVille.setText(gardenerMetadata?.city)
                //binding.etAdresse.setText(gardenerMetadata?.address)
                binding.etPostalCode.setText(gardenerMetadata?.zipCode)
                oldGardener = gardenerMetadata
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    private fun removeObserver() {
        //Ajout des remove event listener
//        if(::ownerRef.isInitialized && ::handleOwnerRef.isInitialized) {
//            ownerRef.removeEventListener(handleOwnerRef)
//        }
        if(::userRef.isInitialized && :: handleUser.isInitialized) {
            userRef.removeEventListener(handleUser)
        }


        removeGardener()
    }

    private fun removeGardener() {
        if (::gardenersRef.isInitialized && ::handleGardener.isInitialized)
            gardenersRef.removeEventListener(handleGardener)
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

    private fun showPictureDialog() {
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
        println("takeInGallery")
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Prendre une image"),
            REQUEST_IMAGE_GALERY
        )
    }

    private fun takePicture() {
        getPathFromActivityResult(requireContext(), false, null)
    }

    fun getPathFromActivityResult(context: Context, galery: Boolean, intentGalery: Intent?) {
        if (galery) {
            intentGalery?.data?.let {
                imageUri = it
                startActivityForResult(intentGalery, PICTURE_RESULT)
            }
        } else {
            val content = ContentValues()
            content.put(MediaStore.Images.Media.TITLE, "New Picture")
            content.put(MediaStore.Images.Media.DESCRIPTION, "From Camera")
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            imageUri = context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                content
            )
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            startActivityForResult(intent, PICTURE_RESULT)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        when (requestCode) {
            PICTURE_RESULT -> {
                if (resultCode == Activity.RESULT_OK) {
                    imageUri?.let { uri ->
                        uploadImgFirebase(uri)
                    }
                }
            }
            REQUEST_IMAGE_GALERY -> {
                if (resultCode == Activity.RESULT_OK) {
                    data?.let {
                        it.data?.let { uri ->
                            uploadImgFirebase(uri)
                        }
                    }
                }
            }
        }
    }

    private fun uploadImgFirebase(uri: Uri) {
        FirebaseService().firebase.currentUser?.uid?.let { uid ->
            FirebaseService().getStoragePictureByUserId(uid)
                .putFile(uri)
                .addOnSuccessListener {
                    val base64img = PicturesUtils.getBase64FromUri(uri, requireActivity())
                    Glide.with(requireContext())
                        .load(Base64.decode(base64img, Base64.DEFAULT))
                        .circleCrop()
                        .into(binding.ivProfil)
                    activity?.let { act ->
                        AlerterService.showGood(
                            getString(R.string.alerter_import_image),
                            act
                        )
                    }
                }
                .addOnFailureListener {
                    AlerterService.showError(getString(R.string.alerter_import_image_problem), requireActivity())
                } //Upload my img to Firebase
        }
    }
}



