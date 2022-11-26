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
import kotlin.collections.HashMap
import kotlin.math.roundToLong

/**
 * lG
 * **/

class FragmentTwoTasks(private val keys : String) : BaseFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val profilViewModel: ProfileViewModel by activityViewModels { viewModelFactory }
    private val PAGE = "***** FragmentTwoTask *****"
    private lateinit var binding : FragmentOneTasksBinding
    private lateinit var gardenerRef : DatabaseReference
    private lateinit var handleGardener : ValueEventListener
    private lateinit var adapter : MyTasksAdapterFinish

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOneTasksBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        profilViewModel.userService.gardenerId?.let { setGardenerObserver(it) }
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
                gardenerPlants?.let {
                    println("liste des plants : " + it)
                    adapter = MyTasksAdapterFinish { id, title, description, position, guid, listPosition, userName, date, userId ->
                            println("GO PAGE MAN")
                            if (findNavController().currentDestination?.id == R.id.tasksFragment) {
                             //   99
                            findNavController().navigate(TasksFragmentDirections.actionTasksFragmentToFragmentTipsForTwoTasks(id, title, description, position, guid, listPosition, userName, date, userId))
                            }
                        }
                    adapter.guid = guid
                    binding.rvOne.adapter = adapter
                    binding.rvOne.layoutManager =
                        LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)

                    val arrayTasks = arrayListOf<TaskCalendar>()
                    it.forEach { plant ->
                        plant.value.tasks.forEachIndexed { index, task ->
                            arrayTasks.add(
                                TaskCalendar(
                                    index,
                                    plant.value.plantID,
                                    plant.value.plantName,
                                    plant.key,
                                    task
                                )
                            )
                        }
                    }

                    val arrayFilter = arrayTasks.filter { data->
                        println("date de la tache : " + data.tasks.date.roundToLong() * 1000)
                        println("date du jour lol: " + Date().time)

                        var tasks = data.tasks.done
                        data.tasks.done!! && data.tasks.date.roundToLong() * 1000 <= Date().time
                    }

                    if(arrayFilter.count() > 0) {
                            binding.clTips.visibility = View.GONE
                            binding.viewUnderline.visibility = View.GONE
                        } else {
                        binding.clTips.visibility = View.VISIBLE
                        binding.viewUnderline.visibility = View.VISIBLE
                        binding.tvTips.text = getString(R.string.tv_histo_empty)
                    }
                    adapter.submitList(arrayFilter)
                }
            }
        })
    }

    private fun removeListener() {
        if (::gardenerRef.isInitialized && ::handleGardener.isInitialized)
            gardenerRef.removeEventListener(handleGardener)
    }
}
