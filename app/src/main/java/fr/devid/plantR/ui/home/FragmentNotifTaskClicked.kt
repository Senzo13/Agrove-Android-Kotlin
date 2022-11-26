package fr.devid.plantR.ui.home

import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference
import fr.devid.plantR.R
import fr.devid.plantR.base.BaseFragment
import fr.devid.plantR.databinding.FragmentTaskRealizeBinding
import fr.devid.plantR.models.Metadata
import fr.devid.plantR.models.Plant
import fr.devid.plantR.models.Tasks
import fr.devid.plantR.services.FirebaseService
import java.util.*

import javax.inject.Inject

/**
 * lG
 * **/

class FragmentNotifTaskClicked() :  BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val profilViewModel: ProfileViewModel by activityViewModels { viewModelFactory }
    private val PAGE = "***** FragmentTwo *****"
    private val data: FragmentNotifTaskClickedArgs by navArgs()
    private lateinit var binding : FragmentTaskRealizeBinding
    private lateinit var plantRef : DatabaseReference
    private lateinit var handlePlant : ValueEventListener
    private lateinit var plantTaskRef : DatabaseReference
    private lateinit var handleTaskPlant : ValueEventListener
    private lateinit var userRef : DatabaseReference
    private lateinit var handleUser : ValueEventListener
    private lateinit var storageRef : StorageReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTaskRealizeBinding.inflate(inflater,container,false)
        initView()
        setPlantObserver()
        setPlantTaskObserver()
        setPlantImage()
        return binding.root
    }

    private fun setPlantImage() {
        storageRef = FirebaseService().getStoragePlantsReference()
            .child(data.plantId).child("plant.jpg")
        storageRef.downloadUrl.addOnSuccessListener {
            Glide.with(binding.ivPlants.context)
                .load(it.toString())
                .into(binding.ivPlants)
        }
    }

    private fun initView() {
        when(Locale.getDefault().language.toString()) {
            "fr" -> {
                binding.tvTitleTask.text = data.taskName
            }
            "en" -> {
                when(data.taskName.toLowerCase()) {
                    "semer" -> {
                        binding.tvTitleTask.text = "Sow"

                    }
                    "planter" -> {
                        binding.tvTitleTask.text = "Plant"
                    }

                }
            }

            else -> {
                when(data.taskName.toLowerCase()) {
                    "semer" -> {
                        binding.tvTitleTask.text = "Sow"

                    }
                    "planter" -> {
                        binding.tvTitleTask.text = "Plant"
                    }
                }
            }
        }
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setUserObserver(date : Long) {
        userRef = FirebaseService().getUserByidMetadata(data.userId)
        handleUser = userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val metadata = snapshot.getValue(Metadata::class.java)
                if(metadata != null) {
                    binding.tvNameRealized.text = "${context?.getString(R.string.TASK_DATE_NAME)} ${metadata.firstName} ${metadata.lastName}"
                    binding.tvDate.text = "${context?.getString(R.string.TASK_DATE_DATE)} ${getDate(date)}"
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    fun getDate(timestamp: Long): String {
        val calendar = Calendar.getInstance(Locale.FRENCH)
        calendar.timeInMillis = timestamp * 1000L
        val date = DateFormat.format("dd/MM/yyyy", calendar).toString()
        val words = date.split(" ")
        var newStr = ""
        words.forEach {
            newStr += it.capitalize() + " "
        }
        return newStr.trimEnd()
    }

    private fun setPlantObserver() {
        plantRef = FirebaseService().getPlantsReferenceById(data.plantId)
        handlePlant = plantRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val plant = snapshot.getValue(Plant::class.java)
                if(plant != null) {
                    binding.tvTitleName.text = plant.name

                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun setPlantTaskObserver() {
        plantTaskRef = FirebaseService().getGardenerPlantsById(data.gardenerId).child(data.stage).child("tasks").child(data.taskId)
        handleTaskPlant = plantTaskRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val taskPlant = snapshot.getValue(Tasks::class.java)
                if(taskPlant != null) {
                    binding.tvTitleTask.text = taskPlant.title
                    binding.tvDescription.text = taskPlant.description
                    setUserObserver(taskPlant.doneBy?.date!!.toLong())
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
        removeListener()
    }

    private fun removeListener() {
        if (::plantRef.isInitialized && ::handlePlant.isInitialized)
            plantRef.removeEventListener(handlePlant)
        if (::userRef.isInitialized && ::handleUser.isInitialized)
            userRef.removeEventListener(handleUser)
        if (::plantTaskRef.isInitialized && ::handleTaskPlant.isInitialized)
            plantTaskRef.removeEventListener(handleTaskPlant)
    }

}
