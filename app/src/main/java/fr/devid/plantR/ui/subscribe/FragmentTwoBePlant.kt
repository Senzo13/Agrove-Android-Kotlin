package fr.devid.plantR.ui.subscribe

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import fr.devid.plantR.R
import fr.devid.plantR.databinding.FragmentBePlantTwoBinding
import fr.devid.plantR.models.GardenerPlant
import fr.devid.plantR.models.PositionDataPlant
import fr.devid.plantR.services.FirebaseService

/**
 * lG
 * **/

class FragmentTwoBePlant(private val keys : String, private val name : String, private val dateHarvested : Int, private val dateSowing : Int, private val etagePlantPosition : String, private val gardenerId : String) : Fragment() {

    private val PAGE = "***** FragmentTwoBePlant *****"
    private lateinit var binding: FragmentBePlantTwoBinding
    private lateinit var gardenerRef : DatabaseReference
    private lateinit var handleGardener : ValueEventListener
    private lateinit var adapter : TasksSubscribeAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBePlantTwoBinding.inflate(inflater, container, false)
        initView()
        return binding.root
    }

    override fun onStop() {
        super.onStop()
        removeListener()
    }

    override fun onResume() {
        super.onResume()
        println("*******" + etagePlantPosition)
     //   setGardenerObserver(gardenerId, etagePlantPosition)
    }

    private fun initView() {
    }

//
//    private fun setGardenerObserver(guid : String, position: String) {
//        println("**** gUid :" + guid +" position : "+ position +" ****")
//        gardenerRef = FirebaseService().getGardenerPlantsById(guid).child(position)
//        handleGardener = gardenerRef.addValueEventListener(object :ValueEventListener {
//            override fun onCancelled(error: DatabaseError) {
//                println("${PAGE} onCancelled ${error.toException()}")
//            }
//
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val gardenerPlants = snapshot.getValue(PositionDataPlant::class.java)
//                gardenerPlants.let {
//                    println("liste des tâches : "+ it?.tasks)
//                    adapter = TasksSubscribeAdapter { _, _, _, _, _, _ ->
//                        println("GO PAGE MAN")
//                    }
//
//                    adapter.setPosition = position
//                    adapter.guid = guid
//                    adapter.plantId = keys
//                    binding.rvPlants.adapter = adapter
//                    binding.rvPlants.layoutManager = LinearLayoutManager(
//                        requireActivity().let { it },
//                        LinearLayoutManager.VERTICAL,
//                        false)
//                    val lists = it?.tasks?.filter { value -> !value.done && value.doneInTime != false}
//                    println("nb element dans la liste " + lists?.count())
//
//                    if(lists.isNullOrEmpty()) {
//                        binding.clTips.visibility = View.VISIBLE
//                        binding.tvTips.text ="${context?.getString(R.string.NOT_TASK_TODAY)}"
//                    } else {
//                        binding.clTips.visibility = View.GONE
//                        binding.tvTips.text = ""
//                    }
//                    adapter.submitList(lists)
//                }
//            }
//        })
//    }

    private fun removeListener() {
        if (::gardenerRef.isInitialized && ::handleGardener.isInitialized)
           gardenerRef.removeEventListener(handleGardener)
    }


}
