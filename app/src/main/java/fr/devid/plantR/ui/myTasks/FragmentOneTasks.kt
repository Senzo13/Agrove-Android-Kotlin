package fr.devid.plantR.ui.myTasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import fr.devid.plantR.R
import fr.devid.plantR.base.BaseFragment
import fr.devid.plantR.databinding.FragmentOneTasksBinding
import fr.devid.plantR.models.*
import fr.devid.plantR.services.FirebaseService
import fr.devid.plantR.ui.home.ProfileViewModel
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.roundToLong

/**
 * lG
 * **/

class FragmentOneTasks(private val keys : String) : BaseFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val profilViewModel: ProfileViewModel by activityViewModels { viewModelFactory }
    private val PAGE = "***** FragmentOneTask *****"
    private val fragment = "0"
    private lateinit var binding : FragmentOneTasksBinding
    private lateinit var gardenerRef : DatabaseReference
    private lateinit var handleGardener : ValueEventListener
    private lateinit var gardenerRefFilterTask : DatabaseReference
    private lateinit var handleGardenerFilterTask : ValueEventListener
    private lateinit var adapter : MyTasksAdapter
    private lateinit var filterTaskered : FilterTask
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOneTasksBinding.inflate(inflater,container,false)
        getFirebaseFilterTask()
        profilViewModel.userService.gardenerId?.let { setGardenerObserver(it) }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStart() {
        super.onStart()
    }
    override fun onStop() {
        super.onStop()
        removeListener()
    }


    private fun setGardenerObserver(guid : String) {
        println("**** gUid :" + guid)
        gardenerRef = FirebaseService().getGardenerPlantsById(guid)
        handleGardener = gardenerRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                println("${PAGE} onCancelled ${error.toException()}")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val gardenerPlants = snapshot.getValue<HashMap<String, PositionDataPlant>>()
                gardenerPlants?.let {plants ->
                    println("liste des plants : " + plants)
                    adapter = MyTasksAdapter { id, title, description, position, guid, listPosition, taskId ->
                        println("GO PAGE")
                        if (findNavController().currentDestination?.id == R.id.tasksFragment) {
                            findNavController().navigate(
                                    TasksFragmentDirections.actionTasksFragmentToFragmentTipsTasks(id, title, description, position, guid, listPosition, taskId))
                        }
                    }

                    adapter.guid = guid
                    binding.rvOne.adapter = adapter
                    binding.rvOne.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)

                    val arrayTasks = arrayListOf<TaskCalendar>()
                    plants.forEach { plant ->
                        plant.value.tasks.forEachIndexed { index, task ->
                            arrayTasks.add(TaskCalendar(index, plant.value.plantID, plant.value.plantName, plant.key, task, plant.value.status))
                        }
                    }
                    val arrayFilter = arrayTasks.filter { data ->
                        println("date de la tache : " + data.tasks.date.roundToLong() * 1000)
                        println("date du jour XX: " + Date().time)
                        if(profilViewModel.userService.isDateSelected != null) {
                            println("Votre isDate selected : " + profilViewModel.userService.isDateSelected)
                            !data.tasks.done && data.tasks.date.roundToLong() * 1000 <= profilViewModel.userService.isDateSelected!! * 1000
                        } else {
                            !data.tasks.done && data.tasks.date.roundToLong() * 1000 <= Date().time
                        }
                    }

                    if(plants.count() > 0) {
                        binding.clTips.visibility = View.GONE
                        binding.viewUnderline.visibility = View.GONE
                        if(arrayFilter.count() > 0) {
                            binding.clTips.visibility = View.GONE
                            binding.viewUnderline.visibility = View.GONE
                        } else {
                            binding.clTips.visibility = View.VISIBLE
                            binding.viewUnderline.visibility = View.VISIBLE
                            binding.tvTips.text = context?.getString(R.string.NONE_TASKS)
                        }
                    } else {
                        binding.clTips.visibility = View.VISIBLE
                        binding.viewUnderline.visibility = View.VISIBLE
                        binding.tvTips.text = context?.getString(R.string.NONE_PLANT_NONE_TASK)
                    }



                   if(::filterTaskered.isInitialized) {
                    adapter.submitList(arrayFilter.filter {
                        when(it.status) {
                            "planter" -> {
                                !filterTaskered.planter.keys.contains(it.tasks.title.toLowerCase())
                            }
                            "semer" -> {
                                !filterTaskered.semer.keys.contains(it.tasks.title.toLowerCase())
                            }
                            else -> true
                        }
                    }.sortedByDescending {  if(it.tasks.priority == 1) it.tasks.priority == 1 else it.tasks.title.toLowerCase() == "récolter" })
                }

                }
                if(gardenerPlants == null) {
                    binding.clTips.visibility = View.VISIBLE
                    binding.viewUnderline.visibility = View.VISIBLE
                    binding.tvTips.text = context?.getString(R.string.NONE_PLANT_NONE_TASK)
                }

            }
        })
    }

    private fun getFirebaseFilterTask() {
        gardenerRefFilterTask = FirebaseService().getFilterTask()
        handleGardenerFilterTask = gardenerRefFilterTask.addValueEventListener(object :ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val array = snapshot.getValue(FilterTask::class.java)
                if(array != null) {
                    filterTaskered = array
                    println("FILTER TASKERED : " + filterTaskered)
                }
            }
        })
    }

    private fun removeListener() {
        if (::gardenerRef.isInitialized && ::handleGardener.isInitialized)
            gardenerRef.removeEventListener(handleGardener)
    }
}
