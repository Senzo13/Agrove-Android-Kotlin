package fr.devid.plantR.ui.home

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.StorageReference
import com.google.gson.GsonBuilder
import com.google.zxing.client.android.Intents
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import com.journeyapps.barcodescanner.CaptureActivity
import fr.devid.plantR.AnyOrientationCaptureActivity
import fr.devid.plantR.R
import fr.devid.plantR.api.RequestJSON
import fr.devid.plantR.api.SendCommandLiveObject
import fr.devid.plantR.base.BaseFragment
import fr.devid.plantR.databinding.FragmentHomeBinding
import fr.devid.plantR.models.*
import fr.devid.plantR.services.AlerterService
import fr.devid.plantR.services.FirebaseService
import fr.devid.plantR.services.Singleton
import fr.devid.plantR.ui.myPlants.checkBetweenStartAndEnd
import fr.devid.plantR.ui.register.RegisterViewModel
import fr.devid.plantR.utils.NotificationClickUtils
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap


class HomeFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val profilViewModel: ProfileViewModel by activityViewModels { viewModelFactory }
    private val registerViewModel: RegisterViewModel by activityViewModels { viewModelFactory }
    private val LOG = "**** HomeFragment ****"
    private lateinit var userRef: DatabaseReference
    private lateinit var handleUser: ValueEventListener
    private lateinit var userRefCurrent: DatabaseReference
    private lateinit var handleUserCurrent: ValueEventListener
    private lateinit var handleGardener: ValueEventListener
    private lateinit var gardenersNameRef: DatabaseReference
    private lateinit var handleGardenerName: ValueEventListener
    private lateinit var getCityRef: DatabaseReference
    private lateinit var handleGetCityRef: ValueEventListener
    private lateinit var gardenersRef: DatabaseReference
    private lateinit var gardenerOwnerRef: DatabaseReference
    private lateinit var handleGardenerOwnerRef: ValueEventListener
    private lateinit var gardenerGuestRef: DatabaseReference
    private lateinit var binding: FragmentHomeBinding
    private var currentGardener: String? = null
    private var gardenersList: HashMap<String, Boolean>? = null
    private lateinit var storageRef: StorageReference
    private lateinit var handleGardenerStatsData: ValueEventListener
    private lateinit var gardenerStageRef: DatabaseReference
    private lateinit var handleGardenerStageRef: ValueEventListener
    private lateinit var gardenerStatsRefParent: DatabaseReference
    private lateinit var handleGardenerStatsRefParent: ValueEventListener
    private lateinit var gardenersStatsRef: DatabaseReference
    private lateinit var mSpinner: Spinner
    private var gardenersArray = arrayListOf<HomeGardener>()
    private var isPublic: Boolean = true
    private lateinit var favsRef: DatabaseReference
    private lateinit var handleFavs: ValueEventListener
    private var tsLong = System.currentTimeMillis() / 1000
    val arrayContains = arrayListOf<String>()
    private lateinit var gardenersTipsRef: DatabaseReference
    private lateinit var handleGardenersTips: ValueEventListener
    private var arrayTips: HashMap<String, Boolean> = HashMap()
    private lateinit var userSubscribeRef: DatabaseReference
    private lateinit var handleUserSubscribe: ValueEventListener
    private lateinit var currentDayString: String
    private lateinit var currentMonthString: String
    private lateinit var GLOBAL_CAPA: String
    private lateinit var GLOBAL_BATTERY: String
    private var countage = 0
    private val data: HomeFragmentArgs by navArgs()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        setObserver()
        initView()
        var isSpinnerTouched = false

        mSpinner = binding.spinner
        // We have here a layout which is stored in a variable in an arrayAdapter, this one is then used to store the name of the planters there.f
        val adapter = ArrayAdapter(
            requireActivity(),
            R.layout.view_drop_down_menu,
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
                        // do what you want
                        if (gardeners.count() > 0) {
                            FirebaseService().getUserCurrentGardener(uid)
                                .setValue(gardeners.keys.toList().elementAt(position))
                            getCityUserSpinner(gardeners.keys.toList().elementAt(position))
                        }

                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        return binding.root
    }


    fun responseApiSuccess(response: JSONObject) {
        Log.i("request-success", response.toString());
    }

    fun responseApiError(error: Exception) {
        Log.e("request-error", error.toString());
    }

    fun callAPI() {
        var queryObject: JSONObject = JSONObject();
        val myGson = GsonBuilder().create()
        val test = myGson.toJson(SendCommandLiveObject("data", 2, true))
        val myTest = JSONObject(test)
        println("Conversion class to json to jsonObject myTest " + myTest)

        try {
            RequestJSON.instance()
                .setURL("https://liveobjects.orange-business.com/api/v0/vendors/lora/devices/C4AC590000CC8AC2/commands")
                .setMethod(
                    "POST"
                ).setQuery(myTest).send(
                    requireActivity(),
                    this::responseApiSuccess,
                    this::responseApiError
                );
        } catch (error: Exception) {
            error.printStackTrace();
        }
    }

//    fun liveObjectsCommands() { // NEXT FEATURE
//        val okHttp = OkHttpClient()
//        AppModule.provideAppServiceLiveObjectsCommandsPost(okHttp, "C4AC590000CC8AC2")
//    }

    private fun initView() {
        callAPI()
        setDateService()
        scanHomePopupManageByType()
        setBtnNav()

        //     profilViewModel.getAllPlants()
        //  profilViewModel.getPlantByKey(viewLifecycleOwner, "ail")
    }

    private fun checkSubscribeMember(guid: String) {
        userSubscribeRef = FirebaseService().getSubscribeMember(guid)
        handleUserSubscribe = userSubscribeRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {
                val listSubscribeMember = snapshot.getValue<HashMap<String, Boolean>>()
                if (listSubscribeMember != null) {
                    val subscribeCount = listSubscribeMember.toList().count()
                    if (subscribeCount > 0) {
                        binding.ivNotificationTeam.visibility = View.VISIBLE
                    }
                } else {
                    binding.ivNotificationTeam.visibility = View.GONE
                }
            }
        })
    }

    private fun checkLevelBatteryBle(value: Int) {
        when (value) {
            in 1..19 -> {
                binding.pbBarStateGardenerGeneral.progressDrawable =
                    requireActivity().getDrawable(R.drawable.custom_general_seekbar_progress_red)
                binding.tvStateGeneralGardener.text =
                    "${context?.getString(R.string.STATE_LEVEL_3)}"

            }
            in 20..39 -> {
                binding.pbBarStateGardenerGeneral.progressDrawable =
                    requireActivity().getDrawable(R.drawable.custom_general_seekbar_progress_orange)
                binding.tvStateGeneralGardener.text =
                    "${context?.getString(R.string.STATE_LEVEL_2)}"

            }
        }
    }

    fun checkLevelCapaBle(value: Int) {
        when (value) {
            in 500 until 680 -> {
                binding.pbBarStateGardenerGeneral.progressDrawable =
                    requireActivity().getDrawable(R.drawable.custom_general_seekbar_progress_red)
                binding.tvStateGeneralGardener.text =
                    "${context?.getString(R.string.STATE_LEVEL_3)}"

            }

            in 680 until 780 -> {
                binding.pbBarStateGardenerGeneral.progressDrawable =
                    requireActivity().getDrawable(R.drawable.custom_general_seekbar_progress_orange)
                binding.tvStateGeneralGardener.text =
                    "${context?.getString(R.string.STATE_LEVEL_2)}"

            }
        }
    }

    fun checkProgressBle() {

        if (profilViewModel.userService.gardenerId == Singleton.instance.gardenerIdFORBLE) {
            var result: Float = 0F

            //Initialisation de la valeur de la progressBar Ble
            Singleton.instance._capaProgress.observe(viewLifecycleOwner) {
                if (profilViewModel.userService.gardenerNameBle == profilViewModel.userService.gardenerName) {

                    it.getContentIfNotHandled()?.let { valueCapa ->
                        println("Ma capa : " + valueCapa.toInt())
                        GLOBAL_CAPA = valueCapa.toString()
                        Singleton.instance._batteryLevel.observe(viewLifecycleOwner) {
                            it.getContentIfNotHandled()?.let { value ->
                                if (value == -1F) {
                                    //AJOUTER UN LOADING
                                } else {
                                    GLOBAL_BATTERY = value.toString()
                                }
                            }
                        }

                        if (::GLOBAL_BATTERY.isInitialized) {
                            result = GLOBAL_BATTERY.toFloat() + valueCapa.toFloat()
                            println("Addition des deux : " + result)
                            println("Moyenne pour pbGeneralState : " + result / 2)
                            binding.pbBarStateGardenerGeneral.progress = (result / 2).toInt()
                            println("Ma value Capa pour la progressBar : " + valueCapa * 10)
                            checkProgressNumber((result / 2), binding.pbBarStateGardenerGeneral)
                            binding.pbBarStateGardenerGeneral.visibility = View.VISIBLE
                            checkLevelBatteryBle(GLOBAL_BATTERY.toFloat().toInt())
                            checkLevelCapaBle(valueCapa.toFloat().toInt() * 10)
                        }
                    }
                }
            }
        }

    }

    private fun setGardenerObserverStage(guid: String) {
        setGardenerObserverStageAndType(guid)
    }

    private fun checkGardenerParentIfParcelle(gardenerId: String) {
        gardenerStatsRefParent = FirebaseService().getGardenerById(gardenerId)
        handleGardenerStatsRefParent = gardenerStatsRefParent.addValueEventListener(object :
            ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                println(error)
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                //Here we fetches the informations from the users
                val gardenerDataStage = snapshot.getValue(GardenerStage::class.java)
                if (gardenerDataStage != null) {
                    profilViewModel.userService.gardenerParent = null
                    if (gardenerDataStage.gardenerParent != null) {
                        profilViewModel.userService.rangs = gardenerDataStage.rangs
                        setGardenersTipsObserver(gardenerDataStage.gardenerParent!!)
                    }
                    if (gardenerDataStage.gardenerParent == null) {
                        setGardenersTipsObserver(gardenerId)
                    }
                }
            }
        })
    }

    private fun setGardenerObserverStageAndType(guid: String) {

        gardenerStageRef = FirebaseService().getGardenerById(guid)
        handleGardenerStageRef = gardenerStageRef.addValueEventListener(object :
            ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                println(error)
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                //Here we fetches the informations from the users
                val gardenerDataStage = snapshot.getValue(GardenerStage::class.java)
                println("JE SUIS DANS LE STAGE LA OK")
                if (gardenerDataStage != null) {
                    println("JE SUIS DANS LE STAGE LA OUI")
                    println("stage : " + gardenerDataStage)
                    profilViewModel.userService.stage = gardenerDataStage.stage
                    profilViewModel.userService.typeGarden = gardenerDataStage.type
                    profilViewModel.userService.rangs = null
                    profilViewModel.userService.gardenerParent = null
                    profilViewModel.userService.gardenerName = gardenerDataStage.metadata.name
                    if (gardenerDataStage.gardenerParent != null) {
                        profilViewModel.userService.rangs = gardenerDataStage.rangs
                        println("Mon rang est : " + gardenerDataStage.rangs)
                        profilViewModel.userService.gardenerParent == gardenerDataStage.gardenerParent
                        println("SET GARDENERPARENT")
                    }
                    println("MON TYPE EST ACTUELLEMENT CELUI LA : " + gardenerDataStage)
                    when (gardenerDataStage.type) {
                        "carre" -> {
                            binding.tvTitleGardenerState.visibility = View.GONE
                            binding.clParentStateGardener.visibility = View.GONE
                        }
                        "jardiniere" -> {
                            binding.tvTitleGardenerState.visibility = View.GONE
                            binding.clParentStateGardener.visibility = View.GONE
                        }
                        "pot" -> {
                            binding.tvTitleGardenerState.visibility = View.GONE
                            binding.clParentStateGardener.visibility = View.GONE
                        }
                        "capteur_pot" -> {
                            observerGardenersStats(guid)
                            binding.tvTitleGardenerState.visibility = View.VISIBLE
                            binding.clParentStateGardener.visibility = View.VISIBLE
                        }
                        "capteur_carre" -> {
                            observerGardenersStats(guid)
                            binding.tvTitleGardenerState.visibility = View.VISIBLE
                            binding.clParentStateGardener.visibility = View.VISIBLE
                        }
                        "capteur_jardiniere" -> {
                            observerGardenersStats(guid)
                            binding.tvTitleGardenerState.visibility = View.VISIBLE
                            binding.clParentStateGardener.visibility = View.VISIBLE
                        }
                        "capteur" -> {
                            observerGardenersStats(guid)
                            binding.tvTitleGardenerState.visibility = View.VISIBLE
                            binding.clParentStateGardener.visibility = View.VISIBLE
                        }
                        "cle_en_main" -> {
                            observerGardenersStats(guid)
                            binding.tvTitleGardenerState.visibility = View.VISIBLE
                            binding.clParentStateGardener.visibility = View.VISIBLE
                        }
                        "parcelle" -> {
                            gardenerDataStage.gardenerParent?.let {
                                observerGardenersStats(it)
                            }
                            binding.tvTitleGardenerState.visibility = View.VISIBLE
                            binding.clParentStateGardener.visibility = View.VISIBLE

                        }
                        "mural" -> {
                            observerGardenersStats(guid)
                            binding.tvTitleGardenerState.visibility = View.VISIBLE
                            binding.clParentStateGardener.visibility = View.VISIBLE
                        }
                    }

                    profilViewModel.userService.dimension = gardenerDataStage.dimension
                    isPublic = gardenerDataStage.ispublic
                    println("HOME IsPublic ?? : " + isPublic)
                    println("LA JE PASSE DAS LE BTN NEW")
                }
            }
        })

    }

    private fun setGardenersTipsObserver(gardenerId: String) {
        arrayTips.clear()
        binding.ivNotificationPlantr.visibility = View.GONE
        setTipsNotif(gardenerId)
        println("PARCELLE PARENT : " + gardenerId)
    }

    private fun setTipsNotif(gardenerId: String) {
        gardenersTipsRef =
            Firebase.database.getReference("gardeners").child(gardenerId).child("tips")
        handleGardenersTips = gardenersTipsRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val tips = snapshot.getValue(GardenerTips::class.java)
                if (tips != null) {
                    println("DETECTION TIPS")
                    arrayTips["battery"] = tips.battery
                    arrayTips["capa"] = tips.capa
                    arrayTips["temperature"] = tips.temperature
                    arrayTips["waterLevel"] = tips.waterLevel
                    println("MON ARRAY DE TIPS" + arrayTips)
                    if (arrayTips.containsValue(true)) {
                        binding.ivNotificationPlantr.visibility = View.GONE
                    }
                }
                checkSubscribeMember(gardenerId) // FOR SET A NOTIF SUBSCRIBE
            }
        })

    }

    override fun onStop() {
        super.onStop()
        FirebaseService().removeCallBack()
        arrayContains.clear()
        profilViewModel.userService.gardenerId = currentGardener
        println("ONSTOP GD " + profilViewModel.userService.gardenerId)
        Singleton.instance.gardenerId = currentGardener
        removeObserver()
        println("ONSTOP GD AFTER REMOVE " + profilViewModel.userService.gardenerId)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        getCityUser()
        arrayTips.clear()
        getCurrentUser()
        arrayContains.clear()
    }


    private fun checkFavsTrueOrFalse(guid: String) {

        arrayContains.clear()
        favsRef = FirebaseService().getFavs(guid)
        handleFavs = favsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val checkFavs = snapshot.getValue<HashMap<String, PlantFavs>>()
                val list = checkFavs?.values?.map {
                    it
                }
                setNotifFavs(list)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun getMonth(timestamp: Long): Int {
        val calendar = Calendar.getInstance(Locale.FRANCE)
        calendar.timeInMillis = timestamp * 1000L
        val month = DateFormat.format("MM", calendar).toString().toInt()
        return month
    }

    private fun setNotifFavs(favs: List<PlantFavs>?) {
        favs?.forEach {
            val startSowingFavs = it.sowingPeriod?.startMonth
            val endSowingFavs = it.sowingPeriod?.endMonth
            val startPlantingFavs = it.plantingPeriod?.startMonth
            val endPlantingFavs = it.plantingPeriod?.endMonth

            if (startSowingFavs ?: -1 >= 0 && endSowingFavs ?: -1 >= 0 && checkBetweenStartAndEnd(
                    startSowingFavs ?: -1,
                    endSowingFavs ?: -1,
                    getMonth(tsLong)
                )
            ) {
                arrayContains.add("SEMER")
            }

            if (startPlantingFavs ?: -1 >= 0 && endPlantingFavs ?: -1 >= 0 && checkBetweenStartAndEnd(
                    startPlantingFavs ?: -1,
                    endPlantingFavs ?: -1,
                    getMonth(tsLong)
                )
            ) {
                arrayContains.add("PLANTER")
            }
        }
        if (arrayContains.isNullOrEmpty()) {
            binding.ivNotification.visibility = View.GONE
        } else {
            binding.ivNotification.visibility = View.VISIBLE
        }
    }


    fun parseGardenersList(gardeners: HashMap<String, Boolean>) {
        val gardenersListed = gardeners.keys
        gardenersArray.clear()
        topicCars(gardeners.keys.toList())
        gardenersListed.toList().forEach { gardenersListId -> //SHOW IN ORDER BY IT
            gardenersRef = FirebaseService().getGardenerMetadataById(gardenersListId)
            handleGardener = gardenersRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val gardenerMetadata = dataSnapshot.getValue(GardenerMetadata::class.java)
                    gardenerMetadata?.let { metadata ->
                        gardenersArray.add(HomeGardener(gardenersListId, metadata.name))
                        val adapter = mSpinner.adapter as ArrayAdapter<String>
                        adapter.clear()
                        val arrayString = gardenersArray.map { it.name.capitalize() }
                        adapter.addAll(arrayString)
                        if (gardenersListed.count() == gardenersArray.count()) {
                            setCurrentGardener()
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    println("${LOG} onCancelled ${databaseError.toException()}")
                }
            })
        }
    }

    private fun topicCars(newTopic: List<String>) {
        unsubscribeTopic()
        subscribeTopic(newTopic)
    }

    private fun subscribeTopic(guidList: List<String>) {
        registerViewModel.sharedPreferencesService.oldTopics = ""
        guidList.forEach { guid ->
            FirebaseMessaging.getInstance().subscribeToTopic(guid).addOnCompleteListener { task ->
                registerViewModel.sharedPreferencesService.oldTopics += "$guid|"
                if (!task.isSuccessful) {
                    println("Le subscribe n'a pas fonctionné sur $guid")
                } else {
                    println("Le subscribe à fonctionné sur $guid")
                }
            }
        }
    }

    private fun getCurrentUser() {
        FirebaseService().firebase.currentUser?.uid?.let { uid ->
            userRefCurrent = FirebaseService().getUserByid(uid)
            handleUserCurrent = userRefCurrent.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user = dataSnapshot.getValue(User::class.java)
                    println(user?.Metadata?.firstName)
                    profilViewModel.userService.currentUsername =
                        "${user?.Metadata?.firstName} ${user?.Metadata?.lastName}"
                }
            })
        }
    }

    private fun unsubscribeTopic() {
        val arrayTest =
            registerViewModel.sharedPreferencesService.oldTopics?.split("|") ?: emptyList()
        println("La list de gardeners $arrayTest")

        arrayTest.forEach { guid ->
            FirebaseMessaging.getInstance().unsubscribeFromTopic(guid)
                .addOnCompleteListener { task ->

                    if (!task.isSuccessful) {
                        println("Le unsubscribe n'a pas fonctionné sur $guid")
                    } else {
                        println("Le unsubscribe à fonctionné sur $guid")
                        return@addOnCompleteListener
                    }
                }
        }
    }

    private fun setCurrentGardener() {
        //SANS CELA PLUS AUCUNE PAGE NE FONCTIONNE
        //LANCEMENT DES NOTIFICATIONS POUR ARRIERE PLAN, ET QUAND L'APP EST KILLED
        NotificationClickUtils().hasNotificationClick(requireActivity(), findNavController())
        NotificationClickUtils().hasNotificationClickTips(requireActivity(), findNavController(), profilViewModel)
        profilViewModel.userService.gardenerId?.let {

            setGardenerObserverStage(it)
            val elements = gardenersArray.map { e -> e.id }
            val index = elements.indexOf(it)
            if (index != -1) {
                binding.spinner.setSelection(index)
            }
            binding.clSpinner.visibility = View.VISIBLE
            getCityUser()

        }

    }

    private fun getCityUser() {
        profilViewModel.userService.gardenerId?.let { guid ->
            getCityRef = FirebaseService().getGardenerMetadataById(guid)
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
                            println("CITY CC: " + profilViewModel.userService.cityUser)

                        }
                    }
                }
            })
        }
    }

    private fun getCityUserSpinner(guid: String) {
        profilViewModel.userService.cityUser = null
        getCityRef = FirebaseService().getGardenerMetadataById(guid)
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
                        println("CITY CC: " + profilViewModel.userService.cityUser)

                    }
                }
                println("CITY CC: VIDE")

            }
        })

    }

    @SuppressLint("SimpleDateFormat")


    private fun setDateService() {
        val simpleDateFormatDay = SimpleDateFormat("dd")
        val currentDay: String = simpleDateFormatDay.format(Date())
        val simpleDateFormatMonth = SimpleDateFormat("MM")
        val currentMonth: String = simpleDateFormatMonth.format(Date())
        val currentDayFormated = currentDay.toCharArray()
        val currentMonthFormated = currentMonth.toCharArray()
        if (currentDayFormated[0].toString() == "0") {
            println("Le jour actuel est : " + currentDayFormated[1].toString())
            currentDayString = currentDayFormated[1].toString()
        } else {
            println("Le jour actuel est : " + currentDay)
            currentDayString = currentDay
        }
        if (currentMonthFormated[0].toString() == "0") {
            println("Le mois actuel est : " + currentMonthFormated[1].toString())
            currentMonthString = currentMonthFormated[1].toString()
        } else {
            println("Le mois actuel est : " + currentMonth)
            currentMonthString = currentMonth
        }
        if (::currentMonthString.isInitialized && ::currentDayString.isInitialized) {
            profilViewModel.userService.currentDay = currentDayString
            profilViewModel.userService.currentMonth = currentMonthString
        }
    }

    private fun setPopupAddGardener(description: String) {
        val popupAddGardener =
            PopupScanGardener(description, requireContext()) { popup, bool, str ->
                if (bool) {
                    popup.dismiss()
                    qrCode()
                } else {
                    popup.dismiss()
                }
            }
        popupAddGardener.show()
        profilViewModel.userService.data_kit_already = true
    }

    private fun scanHomePopupManageByType() {

        //J'ajoute une jardinière Classic donc je ne propose pas de popup
        if (data.typeGardener == "Classic" && !profilViewModel.userService.stateForAddGardener) {
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToFragmentAddPotager(
                    data.type,
                    data.typeGardener,
                    data.dimension
                )
            )
        } else {
            //J'ajoute un kit agrove donc je propose un popup avec la description associé
            if (data.kit != -1 && !profilViewModel.userService.data_kit_already) {
                when (data.kit) {
                    0 -> {
                        val description = getString(R.string.tv_title_public_add_kit_sensor_mural)
                        setPopupAddGardener(description)
                    }
                    1 -> {
                        val description = getString(R.string.tv_title_public_add_garden)
                        setPopupAddGardener(description)
                    }
                    2 -> {
                        when (data.type) {
                            "capteur_pot" -> {
                                val description =
                                    getString(R.string.tv_title_public_add_kit_capteur_pot)
                                setPopupAddGardener(description)
                            }
                            "capteur_jardiniere" -> {
                                val description =
                                    getString(R.string.tv_title_public_add_kit_capteur_jardiniere)
                                setPopupAddGardener(description)
                            }
                            "capteur_carre" -> {
                                val description =
                                    getString(R.string.tv_title_public_add_kit_capteur_carre)
                                setPopupAddGardener(description)
                            }
                        }
                    }
                    3 -> {
                        val description = getString(R.string.tv_title_public_add_parcelle)
                        setPopupAddGardener(description)
                    }
                }
            }
        }
    }


    private fun removeObserver() {
        removeGardener()
    }

    private fun removeGardener() {
        if (::userRef.isInitialized && ::handleUser.isInitialized) {
            userRef.removeEventListener(handleUser)
        }
        if (::gardenersRef.isInitialized && ::handleGardener.isInitialized) {
            gardenersRef.removeEventListener(handleGardener)
        }
        if (::gardenersStatsRef.isInitialized && ::handleGardenerStatsData.isInitialized) {
            gardenersStatsRef.removeEventListener(handleGardenerStatsData)
        }
        if (::gardenerStageRef.isInitialized && ::handleGardenerStageRef.isInitialized) {
            gardenerStageRef.removeEventListener(handleGardenerStageRef)
        }
        if (::gardenersTipsRef.isInitialized && ::handleGardenersTips.isInitialized) {
            gardenersTipsRef.removeEventListener(handleGardenersTips)
        }
        if (::gardenerStatsRefParent.isInitialized && ::handleGardenerStatsRefParent.isInitialized) {
            gardenerStatsRefParent.removeEventListener(handleGardenerStatsRefParent)
        }
        if (::getCityRef.isInitialized && ::handleGetCityRef.isInitialized) {
            getCityRef.removeEventListener(handleGetCityRef)
        }
        if (::userSubscribeRef.isInitialized && ::handleUserSubscribe.isInitialized) {
            userSubscribeRef.removeEventListener(handleUserSubscribe)
        }

        if (::gardenerOwnerRef.isInitialized && ::handleGardenerOwnerRef.isInitialized) {
            gardenerOwnerRef.removeEventListener(handleGardenerOwnerRef)
        }

        if (::userRefCurrent.isInitialized && ::handleUserCurrent.isInitialized) {
            userRefCurrent.removeEventListener(handleUserCurrent)
        }

        if (::gardenersNameRef.isInitialized && ::handleGardenerName.isInitialized) {
            gardenersNameRef.removeEventListener(handleUserCurrent)
        }

    }

    private fun setObserver() {
        FirebaseService().firebase.currentUser?.uid?.let { uid ->
            userRef = FirebaseService().getUserByid(uid)
            handleUser = userRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user = dataSnapshot.getValue(User::class.java)
                    countage += 1
                    println("SET OBSERVER EST ENTRER : " + countage + " fois")
                    if (user != null) {
                        if (user.gardeners.count() > 0) {
                            println("gardeners : " + user.gardeners)
                            setupViewOwners()
                            gardenersList = user.gardeners
                            parseGardenersList(gardenersList!!)
                            checkGardenerParentIfParcelle(user.currentGardener)
                            currentGardener = user.currentGardener
                            profilViewModel.userService.gardenerId = user.currentGardener
                            println("GardenerID MEC : " + profilViewModel.userService.gardenerId)
                            checkFavsTrueOrFalse(user.currentGardener)
                        } else {
                            setupViewVisitor()
                            println("0 gardeners détecté")
                        }
                    }
//                    if (user != null) {
//                        if (user.gardeners.count() < 1) {
//                            gardenersList?.clear()
//                            setupViewVisitor()
//                            println("Je compte : " + user.gardeners.size)
//                            println("Je passe dans le setObserver ")
//                            return
//                        }
//                        if (user.gardeners.count() >= 1) {
//                            println("SUPERIEUR OU EGAL A 1")
//                            println("VERIF : " + user.gardeners)
//                            if (user.currentGardener != null) {
//                                gardenersList = user.gardeners
//                                checkGardenerParentIfParcelle(user.currentGardener)
//                                currentGardener = user.currentGardener
//                                profilViewModel.userService.gardenerId = user.currentGardener
//                                checkFavsTrueOrFalse(user.currentGardener)
//
//                            }
//                        }
//                        parseGardenersList(user.gardeners)
//                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // handle error
                }
            })
        }

        FirebaseService().firebase.currentUser?.uid?.let { uid ->
            storageRef = FirebaseService().getStoragePictureByUserId(uid)
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                Glide.with(requireContext())
                    .load(uri.toString())
                    .circleCrop()
                    .into(binding.ivHomeProfileUser)
            }.addOnFailureListener {
                Glide.with(binding.ivHomeProfileUser.context)
                    .load(R.drawable.user)
                    .circleCrop()
                    .into(binding.ivHomeProfileUser)
            }
        }


    }

    private fun setupViewVisitor() {
        binding.clSpinner.visibility = View.GONE
        binding.ivNotificationTeam.visibility = View.GONE
        binding.ivNotificationPlantr.visibility = View.GONE
        binding.ivNotification.visibility = View.GONE
        println("JE SUIS LA VISITOR")

        when (Locale.getDefault().language.toString()) {
            "fr" -> {
                binding.ivMessageAcceuil.setImageDrawable(resources.getDrawable(R.drawable.message_accueil))
            }
            "en" -> {
                binding.ivMessageAcceuil.setImageDrawable(resources.getDrawable(R.drawable.message_accueil_en))
            }
            else -> {
                binding.ivMessageAcceuil.setImageDrawable(resources.getDrawable(R.drawable.message_accueil_en))
            }
        }


        //DISABLE SET ONCLICK LISTENER
        binding.clMenu1.setOnClickListener {
            AlerterService.showGoodHome(getString(R.string.alerter_first_nav), requireActivity())
        }
        binding.clMenu2.setOnClickListener {
            AlerterService.showGoodHome(getString(R.string.alerter_first_nav), requireActivity())
        }
        binding.clMenu3.setOnClickListener {
            AlerterService.showGoodHome(getString(R.string.alerter_first_nav), requireActivity())
        }
        binding.clMenu4.setOnClickListener {
            AlerterService.showGoodHome(getString(R.string.alerter_first_nav), requireActivity())
        }


        //   binding.clMenu6.isEnabled = false
        binding.invisibleTop.visibility = View.INVISIBLE
        binding.clSpinnerAndState.visibility = View.INVISIBLE
        binding.clVisitor.visibility = View.VISIBLE
        binding.ivHomeProfileUser.setOnClickListener {
            if (findNavController().currentDestination?.id == R.id.homeFragment) {
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToProfileFragment(
                        true
                    )
                )
            }
        }
        binding.ivArrowMyGardener.alpha = 0.1F
        binding.ivArrowMyPlants.alpha = 0.1F
        binding.ivArrowFriends.alpha = 0.1F
        binding.ivArrowMissions.alpha = 0.1F

        //  binding.ivArrowBle.alpha = 0.5F
        binding.homeMyTasks.setTextColor(resources.getColor(R.color.extra_light_black_plantr, null))
        binding.homeMyGardener.setTextColor(
            resources.getColor(R.color.extra_light_black_plantr, null)
        )
        binding.homeMesPlants.setTextColor(
            resources.getColor(R.color.extra_light_black_plantr, null)
        )

        //   binding.homeMyBle.setTextColor(resources.getColor(R.color.extra_light_black_plantr, null))
        binding.homeMesAmis.setTextColor(resources.getColor(R.color.extra_light_black_plantr, null))

        binding.homeMySubscribe.setTextColor(resources.getColor(R.color.black_plantr, null))
        binding.homeMySubscribe.alpha = 0.2F
        binding.homeMesAmis.alpha = 0.2F
        binding.homeMesPlants.alpha = 0.2F
        binding.homeMyGardener.alpha = 0.2F
        binding.homeMyTasks.alpha = 0.2F

        //CHANGE COLOR
        println("FIN DU SETUP VISITOR")
        hideLoading()
        Singleton.instance.hideLoadingScreenByLang()
    }


    private fun setupViewOwners() {
        if (profilViewModel.userService.typeGarden != null) {
            println("ma garden :" + profilViewModel.userService.typeGarden)
        }

        hideLoading()

        binding.ivArrowMyPlants.alpha = 0.8F
        println("ALPHA : " + binding.ivArrowMyPlants.alpha)
        binding.ivArrowMyGardener.alpha = 0.8F
        binding.ivArrowSubscribe.alpha = 0.8F
        binding.ivArrowFriends.alpha = 0.8F
        binding.ivArrowMissions.alpha = 0.8F

        binding.homeMySubscribe.alpha = 0.8F
        binding.homeMesAmis.alpha = 0.8F
        binding.homeMesPlants.alpha = 0.8F
        binding.homeMyGardener.alpha = 0.8F
        binding.homeMyTasks.alpha = 0.8F

        binding.clMenu1.isEnabled = true
        println("activated : " + binding.clMenu1.isEnabled)
        binding.clMenu2.isEnabled = true
        binding.clMenu3.isEnabled = true
        binding.clMenu4.isEnabled = true
        binding.clMenu5.isEnabled = true
        binding.invisibleTop.visibility = View.VISIBLE
        binding.clVisitor.visibility = View.GONE
        try {
            binding.homeMyGardener.setTextColor(resources.getColor(R.color.black_plantr, null))
        } catch (e: IllegalStateException) {
            println("Erreur getContext ligne 677")
        }

        //   binding.ivArrowBle.alpha = 0.8F

        try {
            binding.ivArrowMyGardener.setImageDrawable(
                resources.getDrawable(
                    R.drawable.arrow_grey,
                    null
                )
            )
            binding.clSpinnerAndState.visibility = View.VISIBLE
            binding.homeMesPlants.setTextColor(resources.getColor(R.color.black_plantr, null))

            binding.ivArrowMyPlants.setImageDrawable(
                resources.getDrawable(
                    R.drawable.arrow_grey,
                    null
                )
            )

            binding.homeMyTasks.setTextColor(resources.getColor(R.color.black_plantr, null))

            binding.homeMesAmis.setTextColor(resources.getColor(R.color.black_plantr, null))

            //       binding.homeMyBle.setTextColor(resources.getColor(R.color.black_plantr, null))

            binding.ivArrowFriends.setImageDrawable(
                resources.getDrawable(
                    R.drawable.arrow_grey,
                    null
                )
            )

            binding.clMenu5.visibility =
                View.GONE //CACHER LE CODE DES ABO CAR PAS ASSEZ TRAVAILLER POUR LE MOMENT
            binding.homeMySubscribe.setTextColor(resources.getColor(R.color.black_plantr, null))

            binding.ivArrowSubscribe.setImageDrawable(
                resources.getDrawable(
                    R.drawable.arrow_grey,
                    null
                )
            )
        } catch (e: IllegalStateException) {
            println("Affiche des images setviewOwners")
        }

        binding.ivHomeProfileUser.setOnClickListener {
            if (findNavController().currentDestination?.id == R.id.homeFragment) {
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToProfileFragment(
                        false
                    )
                )
            }
        }


        println("FIN DU SETUP OWNERS")
        initView()
        Singleton.instance.hideLoadingScreenByLang()

        //FIN
    }

    private fun observerGardenersStats(gardenerId: String) {
        println("MON TYPE DE POPO EST " + profilViewModel.userService.typeGarden)
        when (profilViewModel.userService.typeGarden) {
            "parcelle" -> {
                profilViewModel.userService.gardenerParent?.let {
                    checkFavsTrueOrFalse(it)
                }
            }
            else -> {
                profilViewModel.userService.gardenerId?.let {
                    checkFavsTrueOrFalse(it)
                }
            }
        }
        println(gardenerId)
        gardenersStats(gardenerId)
    }

    private fun gardenersStats(gardenerId: String) {
        gardenersStatsRef = FirebaseService().getGardenerStatsById(gardenerId)
        handleGardenerStatsData =
            gardenersStatsRef.addValueEventListener(object :
                ValueEventListener {
                @RequiresApi(Build.VERSION_CODES.N)
                @SuppressLint("ResourceAsColor", "ObjectAnimatorBinding")
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val gardenerStats = dataSnapshot.getValue(GardenerStats::class.java)
                    println("Stats : " + gardenerStats)
                    if (gardenerStats != null) {
                        binding.clParentStateGardener.visibility = View.VISIBLE
                        binding.tvStateGeneralGardener.visibility = View.VISIBLE
                        binding.tvTitleGardenerState.visibility = View.VISIBLE
                        setupProgressBar(gardenerStats)
                    } else {
                        binding.clParentStateGardener.visibility = View.GONE
                        binding.tvStateGeneralGardener.visibility = View.GONE
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    try {
                        AlerterService.showError(
                            "${context?.getString(R.string.ERROR)}",
                            requireActivity()
                        )
                    } catch (e: Exception) {
                        println(e.localizedMessage)
                    }
                }
            })
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setupProgressBar(gardenerStats: GardenerStats?) {
        val v = profilViewModel.userService.typeGarden
        val calc =
            if (v == "cle_en_main") gardenerStats?.battery?.plus(gardenerStats.capacities.c1!!)
                ?.plus(gardenerStats.waterLevel!!)!!.div(3).toFloat() else if (v == "parcelle") {
                gardenerStats?.battery?.plus(gardenerStats.capacities.c1!!)?.div(2)?.toFloat()
            } else gardenerStats?.battery?.plus(gardenerStats.capacities.c1!!)?.div(2)?.toFloat()
        if (calc != null) checkProgressNumber(calc, binding.pbBarStateGardenerGeneral)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("ResourceAsColor")
    private fun checkProgressNumber(
        value: Float,
        bar: ProgressBar
    ) {
        when (value) {
            in 1..25 -> {
                value.let {
                    bar.setProgress(it.toInt(), true)
                    bar.progressDrawable =
                        requireActivity().getDrawable(R.drawable.custom_general_seekbar_progress_red)
                    binding.tvStateGeneralGardener.text =
                        "${context?.getString(R.string.STATE_LEVEL_3)}"

                }
            }
            in 25..50 -> {
                value.let {
                    bar.setProgress(it.toInt(), true)
                    bar.progressDrawable =
                        requireActivity().getDrawable(R.drawable.custom_general_seekbar_progress_orange)
                    binding.tvStateGeneralGardener.text =
                        "${context?.getString(R.string.STATE_LEVEL_2)}"
                }
            }
            else -> {
                value.let {
                    bar.setProgress(it.toInt(), true)
                    try {
                        bar.progressDrawable =
                            requireActivity().getDrawable(R.drawable.custom_general_seekbar_progress_green)
                        binding.tvStateGeneralGardener.text =
                            "${context?.getString(R.string.STATE_LEVEL_1)}"
                    } catch (exception: java.lang.IllegalStateException) {
                        println("Un probleme a eu lieu dans home fragment ligne 809")
                    }
                }
            }
        }
    }

    private fun hideLoading() {
        //VISIBLE TO INVISIBLE
        binding.pbLoadingSpinner.visibility = View.GONE

        //INVISIBLE TO VISIBLE
        binding.scrollView.visibility = View.VISIBLE
        binding.invisibleTop.visibility = View.VISIBLE
        binding.blocParent.visibility = View.VISIBLE
        binding.tvEntretenirMaJardiniere.visibility = View.VISIBLE
    }

    private fun qrCode() {
//        IntentIntegrator
//                .forSupportFragment(this)
//                .captureActivity =  AnyOrientationCaptureActivity::class.java
//                .setBeepEnabled(false) // beep false car elsa pas content
//                .setOrientationLocked(false)
//                .setPrompt(getString(R.string.tv_scan_popup))
//                .initiateScan()
        val intent = Intent(requireContext(), AnyOrientationCaptureActivity::class.java)
        intent.putExtra("SCAN_MODE", "ONE_D_MODE")
        intent.putExtra(
            "SCAN_FORMATS",
            "CODE_39,CODE_93,CODE_128,DATA_MATRIX,ITF,CODABAR,EAN_13,EAN_8,UPC_A,QR_CODE"
        )
        intent.action = Intents.Scan.ACTION
        startActivityForResult(intent, 1)
//
//        val integrator = IntentIntegrator(requireActivity())
//        integrator.captureActivity = AnyOrientationCaptureActivity::class.java
//        integrator.setOrientationLocked(false)
//            .setPrompt(getString(R.string.tv_scan_popup))
//            .setBeepEnabled(false) // beep false car elsa pas content
//            .initiateScan()
    }

    private fun setSubcribeGardener(guid: String, uid: String) {
        gardenerGuestRef = FirebaseService().getUserByid(uid)
        gardenerGuestRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val guestGardener = snapshot.getValue(UserGardenerGuest::class.java)
                if (guestGardener != null) {
                    val guestAlreadyExist =
                        guestGardener.gardenersGuest.filter { data -> data.key == guid }
                    if (guestAlreadyExist.count() >= 1) {
                        activity?.let {
                            AlerterService.showError(
                                "${context?.getString(R.string.ALREADY_SUBSCRIBE)}",
                                it
                            )
                            println("${context?.getString(R.string.ALREADY_SUBSCRIBE)}")
                        }
                    } else {
                        findNavController().navigate(
                            HomeFragmentDirections.actionHomeFragmentToFragmentGardenerSubscribe(
                                guid
                            )
                        )
                    }
                }
            }
        })
    }

    private fun checkMyGardeners(
        guid: String,
        etage: String,
        type: String,
        dimension: Int,
        gardenerParent: String,
        rangs: Int
    ) {
        FirebaseService().firebase.currentUser?.uid?.let { uid ->
            gardenerOwnerRef = FirebaseService().getGardenerOwnersById(guid)
            handleGardenerOwnerRef =
                gardenerOwnerRef.addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        println("OnCancelled" + error.toException())
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        val owners = snapshot.getValue<HashMap<String, Boolean>>()
                        if (owners != null) {
                            if (owners.keys.contains(uid)) {
                                activity?.let { act ->

                                }
                            } else {
                                val popupScan =
                                    PopupScan(requireContext()) { popup, bool, str ->
                                        if (bool && str == "join") {
                                            println("Etat du isPublic dans le popup : " + isPublic)
                                            if (isPublic) {
                                                FirebaseService().getGardenerById(guid)
                                                    .child("subscribemember")
                                                    .child(uid).setValue(true)
                                                AlerterService.showGood(
                                                    "${getString(R.string.alerter_send_to_garden)}",
                                                    requireActivity()
                                                )
                                                popup.dismiss()
                                            } else {
                                                AlerterService.showError(
                                                    "${getString(R.string.alerter_private_garden)}",
                                                    requireActivity()
                                                )
                                            }
                                        } else if (bool && str == "sub") {
                                            //Cette fonctionnalité est caché dans le popup :)
                                            println("Vous allez vous abonner à cette jardinière")
                                            activity?.let { act2 ->
                                                AlerterService.showGood(
                                                    "${getString(R.string.alerter_sub_to_garden)}",
                                                    act2
                                                )
                                                setSubcribeGardener(guid, uid)
                                            }
                                            popup.dismiss()
                                        } else {
                                            popup.dismiss()
                                        }
                                    }
                                popupScan.show()
                            }
                        } else {
                            if (gardenerParent.isNotEmpty()) {
                                //GESTION CAS DE LA PARCELLE POUR SON ID
                                if (findNavController().currentDestination?.id == R.id.homeFragment) {
                                    findNavController().navigate(
                                        HomeFragmentDirections.actionHomeFragmentToFragmentAddGardenerJumelage(
                                            guid,
                                            etage,
                                            type,
                                            dimension,
                                            gardenerParent,
                                            rangs
                                        )
                                    )
                                }
                            } else {
                                if (findNavController().currentDestination?.id == R.id.homeFragment) {
                                    findNavController().navigate(
                                        HomeFragmentDirections.actionHomeFragmentToFragmentAddGardenerJumelage(
                                            guid,
                                            etage,
                                            type,
                                            dimension ?: -1,
                                            gardenerParent ?: "",
                                            rangs
                                        )
                                    )
                                }
                            }

                        }
                    }
                })
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, dataAct: Intent?) {
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            var contents = dataAct?.getStringExtra(Intents.Scan.RESULT);
            var formatName = dataAct?.getStringExtra(Intents.Scan.RESULT_FORMAT);
            println()

            if (contents != null) {
                if (contents == null) {
                    println("Aucune donnée recu pour le qrCode")
                    activity?.let {
                        AlerterService.showError(getString(R.string.QRCODE_ISEMPTY), it)
                    }
                } else {
                    try {
                        val answer = JSONObject(contents)
                        var stage = ""
                        var etage = ""
                        var newStage = ""
                        val guid = answer.get("_id")
                        val type = answer.get("type")
                        var gardenerParent: String? = null
                        var rangs: Int? = null

                        try {
                            answer.get("gardenerParent").let {
                                println("\ngardenerParent: " + it.toString())
                                gardenerParent = it.toString()
                            }
                        } catch (err: JSONException) {
                            println("PROBLEME DANS GARDENERPARENT")
                        }

                        try {
                            answer.get("rangs").let {
                                println("\nrangs : " + it.toString().toInt())
                                rangs = it.toString().toInt()
                            }

                        } catch (err: JSONException) {
                            println("PROBLEME DANS RANGS")
                        }

                        try {

                            answer.get("stage").let {
                                stage = it.toString()
                            }

                        } catch (err: JSONException) {
                            println("PROBLEME DANS STAGE DU QR CODE")
                        }

                        try {
                            answer.get("etage").let {
                                etage = it.toString()
                            }
                        } catch (err: JSONException) {
                            println("PROBLEME DANS ETAGE DU QR CODE")
                        }

                        println(
                            "\nqrCode:\n"
                                    + "\n_id : "
                                    + answer.get("_id")
                                    + "\ntype : "
                                    + answer.get("type")
                        )


                        newStage = if (etage.isNotEmpty()) {
                            println("\netage : " + etage)
                            etage
                        } else {
                            println("\nstage : " + stage)
                            stage
                        }

                        if (guid != null && newStage.isNotEmpty()) {
                            println("newStage : " + newStage)
                            activity?.let {
                                //AlerterService.showGood("scan ok", it)
                                when (type.toString()) {
                                    "capteur" -> {
                                        if (data.typeGardener == "capteur_carre") {
                                            println("J'entre dans le CAPTEUR CARRE DE SCAN")
                                            println("DIMENSION : " + data.dimension)
                                            println(
                                                "ETAGE PLUS DIMENSION : " + data.dimension.plus(
                                                    2
                                                ).toString()
                                            )
                                            checkMyGardeners(
                                                guid.toString(),
                                                data.dimension.plus(2).toString(),
                                                data.typeGardener,
                                                data.dimension, "",
                                                -1
                                            )
                                        } else {
                                            checkMyGardeners(
                                                guid.toString(),
                                                "1",
                                                data.typeGardener,
                                                data.dimension, "",
                                                -1
                                            )
                                        }
                                    }
                                    "mural" -> {
                                        checkMyGardeners(
                                            guid.toString(),
                                            "1",
                                            data.typeGardener,
                                            data.dimension, "",
                                            -1
                                        )
                                    }
                                    "cle_en_main" -> {
                                        checkMyGardeners(
                                            guid.toString(),
                                            newStage,
                                            data.typeGardener,
                                            -1, "",
                                            -1
                                        )
                                    }
                                    "parcelle" -> {
                                        checkMyGardeners(
                                            guid.toString(),
                                            newStage,
                                            type.toString(),
                                            -1,
                                            gardenerParent.toString() ?: "",
                                            rangs ?: 2
                                        )
                                    }
                                    else -> {
                                        checkMyGardeners(
                                            guid.toString(),
                                            newStage,
                                            type.toString(),
                                            2, "",
                                            -1
                                        )
                                    }
                                }
                            }
                        } else {
                            print("Il y a une erreur dans l'obtention du qrCode")
                        }
                    } catch (err: java.lang.Exception) {
                        activity?.let {
                            println("Une erreur est survenue " + err)
                            AlerterService.showError(
                                "${getString(R.string.alerter_qr_code_error)} " + err.localizedMessage,
                                it
                            )
                        }
                    }
                }
            } else {
                super.onActivityResult(requestCode, resultCode, dataAct);
                println("Aucune donnée recu pour le qrCode")
                activity?.let {
                    AlerterService.showError(getString(R.string.QRCODE_ISEMPTY), it)
                }
            }
        }
    }

    private fun setBtnNav() {
        println("type dans btn :" + profilViewModel.userService.typeGarden)
        println("stage dans btn : " + profilViewModel.userService.stage)
        binding.ivHomeBtnBug.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToFragmentReportBug())
        }

        binding.ivHomeBtnAdd.setOnClickListener {
            if (findNavController().currentDestination?.id == R.id.homeFragment) {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToFragmentChoicePrincipalGardenType())
            }
        }

        binding.clMenu1.setOnClickListener {
            if (findNavController().currentDestination?.id == R.id.homeFragment) {
                profilViewModel.userService.gardenerId?.let {
                    findNavController().navigate(
                        HomeFragmentDirections.actionHomeFragmentToPlantrFragment(
                            it
                        )
                    )
                }
            }
        }

        binding.clMenu2.setOnClickListener {
            if (findNavController().currentDestination?.id == R.id.homeFragment) {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToMesPlantesFragment())
            }
        }

        binding.clMenu3.setOnClickListener {
            if (findNavController().currentDestination?.id == R.id.homeFragment) {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToTasksFragment())
            }
        }

        binding.clMenu4.setOnClickListener {
            if (findNavController().currentDestination?.id == R.id.homeFragment) {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToFragmentTeam())
            }
        }

        //FEATURE EN ATTENTE DE PROD
        binding.clMenu5.visibility = View.GONE
        binding.clMenu5.setOnClickListener {
            if (findNavController().currentDestination?.id == R.id.homeFragment) {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToFragmentSubscribe())
            }
        }

//        binding.clMenu6.setOnClickListener { //BLUETOOTH
//            if (findNavController().currentDestination?.id == R.id.homeFragment) {
//                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToMainActivityBle())
//            }
//        }
    }

}