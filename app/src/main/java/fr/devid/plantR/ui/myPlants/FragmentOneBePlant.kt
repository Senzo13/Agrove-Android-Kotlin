package fr.devid.plantR.ui.myPlants

import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.storage.StorageReference
import fr.devid.plantR.R
import fr.devid.plantR.databinding.FragmentBePlantOneBinding
import fr.devid.plantR.services.FirebaseService
import fr.devid.plantR.services.StringRef
import org.threeten.bp.*
import org.threeten.bp.temporal.ChronoUnit
import java.util.*


/**
 * lG
 * **/

class FragmentOneBePlant(private val keys: String, private val name: String, private val dateHarvested : Int, private val dateSowing : Int, private val etagePlantPosition : String, private val currentUser : String) : Fragment() {

    private val PAGE = "***** FragmentBePlantProgress *****"
    private val fragment = "0"
    private lateinit var binding: FragmentBePlantOneBinding
    private lateinit var storageRef: StorageReference
    private var tsLongSowing = dateSowing
    private var tsLongHarvested = dateHarvested
    private var ts = tsLongSowing.toString()
    private lateinit var sowingYears : String
    private lateinit var sowingMonth : String
    private lateinit var sowingDays : String
    private lateinit var harvestYears : String
    private lateinit var harvestMonth : String
    private lateinit var harvestDays : String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBePlantOneBinding.inflate(inflater, container, false)
        initView()
        return binding.root
    }

    fun getDate(timestamp: Long) : String {
        val calendar = Calendar.getInstance(Locale.FRANCE)
        calendar.timeInMillis = timestamp * 1000L
        val date = DateFormat.format("dd",calendar).toString()
        return date
    }

    fun getDateTest(timestamp: Long) : String {
        val calendar = Calendar.getInstance(Locale.FRANCE)
        calendar.timeInMillis = timestamp * 1000L
        val date = DateFormat.format("yyyy",calendar).toString()
        return date
    }

    fun getDateMonth(timestamp: Long) : Int {
        val calendar = Calendar.getInstance(Locale.FRANCE)
        calendar.timeInMillis = timestamp * 1000L
        val date = DateFormat.format("MM",calendar).toString().toInt()
        return date
    }

    fun getDateYears(timestamp: Long) : String {
        val calendar = Calendar.getInstance(Locale.FRANCE)
        calendar.timeInMillis = timestamp * 1000L
        val date = DateFormat.format("yyyy",calendar).toString()
        return date
    }


    override fun onStop() {
        super.onStop()

        removeListener()
    }

    private fun initView() {
        setPlantsPictureObserver()
        val convertSowing : Long = tsLongSowing.toLong()
        val convertHarvest : Long = tsLongHarvested.toLong()
        sowingDays = getDate(convertSowing)
        sowingYears = getDateYears(convertSowing)
        harvestDays = getDate(convertHarvest)
        harvestYears = getDateYears(convertHarvest)
        checkMonthSowing()
        checkMonthHarvest()
    }

    private fun checkMonthSowing() {
        val convert : Long = tsLongSowing.toLong()
        when(getDateMonth(convert)) {
            1 -> sowingMonth = context?.getString(R.string.M1).toString()
            2 -> sowingMonth = context?.getString(R.string.M2).toString()
            3 -> sowingMonth = context?.getString(R.string.M3).toString()
            4 -> sowingMonth =context?.getString(R.string.M4).toString()
            5 -> sowingMonth = context?.getString(R.string.M5).toString()
            6 -> sowingMonth = context?.getString(R.string.M6).toString()
            7 -> sowingMonth = context?.getString(R.string.M7).toString()
            8 -> sowingMonth = context?.getString(R.string.M8).toString()
            9 -> sowingMonth = context?.getString(R.string.M9).toString()
            10 -> sowingMonth = context?.getString(R.string.M10).toString()
            11 -> sowingMonth = context?.getString(R.string.M11).toString()
            12 -> sowingMonth = context?.getString(R.string.M12).toString()
        }
        binding.tvSeeded.text = "${getString(R.string.sow_date_1)} ${sowingDays} ${sowingMonth} ${sowingYears}"
    }

    private fun checkMonthHarvest() {
        val convert : Long = tsLongHarvested.toLong()
        when(getDateMonth(convert)) {
            1 -> harvestMonth = context?.getString(R.string.M1).toString()
            2 -> harvestMonth = context?.getString(R.string.M2).toString()
            3 -> harvestMonth = context?.getString(R.string.M3).toString()
            4 -> harvestMonth = context?.getString(R.string.M4).toString()
            5 -> harvestMonth = context?.getString(R.string.M5).toString()
            6 -> harvestMonth = context?.getString(R.string.M6).toString()
            7 -> harvestMonth = context?.getString(R.string.M7).toString()
            8 -> harvestMonth = context?.getString(R.string.M8).toString()
            9 -> harvestMonth =context?.getString(R.string.M9).toString()
            10 -> harvestMonth = context?.getString(R.string.M10).toString()
            11 -> harvestMonth = context?.getString(R.string.M11).toString()
            12 -> harvestMonth = context?.getString(R.string.M12).toString()
        }
            binding.tvHarvest.text = "${context?.getString(R.string.HARVEST_ESTIMATED)} ${harvestDays} ${harvestMonth} ${harvestYears}"
    }

    private fun setPlantsPictureObserver() {
        storageRef = FirebaseService().getStoragePlantsReference()
            .child(keys).child(StringRef.plantJpg)
        storageRef.downloadUrl.addOnSuccessListener {
            Glide.with(binding.ivPlants.context)
                .load(it.toString())
                .into(binding.ivPlants)
        }
    }


    override fun onResume() {
        super.onResume()
        var x = (System.currentTimeMillis() / 1000L) - dateSowing
        var timeStamp = (x.toFloat() / (dateHarvested-dateSowing))
        binding.pbPowerBar.progress = (timeStamp * 100).toInt()

        val harvested = Instant.ofEpochMilli((dateHarvested.toLong() * 1000))
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()

        val currentDate = LocalDateTime.now()
        val days = ChronoUnit.DAYS.between(currentDate, harvested)
        println("******"+ keys)
        if(days.toInt() <= 0) {
            binding.tvProgress.text = "${context?.getString(R.string.STATE_FINISH)}"
        } else {
            binding.tvProgress.text = "${days+2} ${context?.getString(R.string.SOME_DAYS)}"
        }
    }


    private fun removeListener() {
//        adapter.removeListener()
//        if (::plantRef.isInitialized && ::handlePlant.isInitialized)
//            plantRef.removeEventListener(handlePlant)
//        if (::userRef.isInitialized && ::handleUser.isInitialized)
//            userRef.removeEventListener(handleUser)
    }


}
