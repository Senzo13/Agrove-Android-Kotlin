package fr.devid.plantR.ui.myPlants

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.storage.StorageReference
import fr.devid.plantR.R
import fr.devid.plantR.databinding.FragmentBePlantInformationsBinding
import fr.devid.plantR.models.Association
import fr.devid.plantR.models.Plant
import fr.devid.plantR.services.FirebaseService
import fr.devid.plantR.services.StringRef

class FragmentInformations() : Fragment() {

    private val PAGE = "***** FragmentInformations *****"
    private val data: FragmentInformationsArgs by navArgs()
    private lateinit var binding: FragmentBePlantInformationsBinding
    private lateinit var plantRef: DatabaseReference
    private lateinit var handlePlant: ValueEventListener
    private lateinit var associationRef: DatabaseReference
    private lateinit var handleAssociation: ValueEventListener
    private var nameBadAssociation : String = ""
    private var nameAssociation : String = ""
    private lateinit var storageRef: StorageReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBePlantInformationsBinding.inflate(inflater, container, false)
        initView()
        return binding.root
    }

    override fun onStop() {
        super.onStop()
    }

    private fun initView() {
        setPlantsObserver()
        setPlantsPictureObserver()
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setPlantsObserver() {
        plantRef = FirebaseService().getPlantsReferenceById(data.keys)
        handlePlant = plantRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                println(PAGE + "onCancelled " + error.toException())
            }

            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                val plantsById = snapshot.getValue(Plant::class.java)?.let { p->

                    binding.tvHeightBody.text = "${p.characteristic?.height} cm"
                    binding.tvDescriptionPlants.text = p.description
                    val associations = p.association

                    val associationSize = associations?.filter { it.value }?.size
                    var count = 0
                    var countBadAsso = 0
                    var namesPlant = ""

                    associations.forEach {
                        if(it.value) {
                            if (associationSize != null) {
                                if(count != associationSize-1) {
                                    getGoodAssociationWithFirebase(it.key, p.name, true)
                                    count++
                                } else {
                                    getGoodAssociationWithFirebase(it.key, p.name, false)
                                }
                            }
                        }
                    }

                    associations?.forEach {
                        if(!it.value) {
                            binding.clBadAssociation.visibility = View.VISIBLE
                            if (associationSize != null) {
                                if(countBadAsso != associationSize-1) {
                                    getBadAssociationWithFirebase(it.key, p.name, true)
                                    countBadAsso++
                                } else {
                                    getBadAssociationWithFirebase(it.key, p.name, false)
                                }
                            }
                        } else {
                            binding.clBadAssociation.visibility = View.GONE
                        }
                    }

                    //binding.tvAssociation.text ="${p.name} s'associe bien avec \nles espèces suivantes : $namesPlant"

                    val water = p.characteristic?.water
                    val exhibition = p.characteristic?.exhibition
                    val rusticite = p.characteristic?.rusticite
                    p.sowingPeriod?.let { sowing ->
                        initSowing(sowing.startMonth?:-1 - 1, sowing.endMonth?:-1 - 1)
                    }
                    p.harvestPeriod?.let { harvest ->
                        initHarvest(harvest.startMonth?:-1 - 1, harvest.endMonth?:-1 - 1)
                    }
                    checkImageView(water, "water")
                    checkImageView(exhibition, "exhibition")
                    checkImageView(rusticite, "rusticite")
                }
            }
        })
    }

    private fun getGoodAssociationWithFirebase(id : String, namePlant : String, virgule : Boolean) {
        associationRef = FirebaseService().getAssociationById(id)
        handleAssociation = associationRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                var association = snapshot.getValue<Association>()
                if(association != null) {
                    if(virgule) {
                        nameAssociation += association.name + ", "
                    } else {
                        nameAssociation += association.name
                    }
                    binding.tvAssociation.text ="${namePlant} "+ requireActivity().getString(R.string.tv_association) + " $nameAssociation"
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
    private fun getBadAssociationWithFirebase(id : String, namePlant : String, virgule : Boolean) {
        associationRef = FirebaseService().getAssociationById(id)
        handleAssociation = associationRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var association = snapshot.getValue<Association>()
                if(association != null) {
                    if(virgule) {
                        nameBadAssociation += association.name + ", "
                    } else {
                        nameBadAssociation += association.name
                    }
                    binding.tvBadAssociation.text ="${namePlant} s'associe pas avec \nles espèces suivantes : $nameBadAssociation"
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun setPlantsPictureObserver() {
        storageRef = FirebaseService().getStoragePlantsReference()
            .child(data.keys).child(StringRef.plantJpg)
        storageRef.downloadUrl.addOnSuccessListener {
            Glide.with(binding.ivPlants.context)
                .load(it.toString())
                .into(binding.ivPlants)
        }
    }

    private fun checkImageView(number: Int?, name: String) {
        when (number) {
            3 -> {
                if (name == "water") {
                    binding.ivWaterOne.setImageDrawable(requireActivity().getDrawable(R.drawable.water_green))
                    binding.ivWaterTwo.setImageDrawable(requireActivity().getDrawable(R.drawable.water_green))
                    binding.ivWaterThree.setImageDrawable(requireActivity().getDrawable(R.drawable.water_green))
                } else if (name == "rusticite") {
                    binding.ivResiOne.setImageDrawable(requireActivity().getDrawable(R.drawable.snowflake_green))
                    binding.ivResiTwo.setImageDrawable(requireActivity().getDrawable(R.drawable.snowflake_green))
                    binding.ivResiThree.setImageDrawable(requireActivity().getDrawable(R.drawable.snowflake_green))
                } else if (name == "exhibition") {
                    binding.ivExpoOne.setImageDrawable(requireActivity().getDrawable(R.drawable.exposure_green))
                    binding.ivExpoTwo.setImageDrawable(requireActivity().getDrawable(R.drawable.exposure_green))
                    binding.ivExpoThree.setImageDrawable(requireActivity().getDrawable(R.drawable.exposure_green))
                }
            }
            2 -> {
                if (name == "water") {
                    binding.ivWaterOne.setImageDrawable(requireActivity().getDrawable(R.drawable.water_green))
                    binding.ivWaterTwo.setImageDrawable(requireActivity().getDrawable(R.drawable.water_green))
                    binding.ivWaterThree.setImageDrawable(requireActivity().getDrawable(R.drawable.water_grey))
                } else if (name == "rusticite") {
                    binding.ivResiOne.setImageDrawable(requireActivity().getDrawable(R.drawable.snowflake_green))
                    binding.ivResiTwo.setImageDrawable(requireActivity().getDrawable(R.drawable.snowflake_green))
                    binding.ivResiThree.setImageDrawable(requireActivity().getDrawable(R.drawable.snowflake_grey))
                } else if (name == "exhibition") {
                    binding.ivExpoOne.setImageDrawable(requireActivity().getDrawable(R.drawable.exposure_green))
                    binding.ivExpoTwo.setImageDrawable(requireActivity().getDrawable(R.drawable.exposure_green))
                    binding.ivExpoThree.setImageDrawable(requireActivity().getDrawable(R.drawable.exposure_grey))
                }
            }
            1 -> {
                if (name == "water") {
                    binding.ivWaterOne.setImageDrawable(requireActivity().getDrawable(R.drawable.water_green))
                    binding.ivWaterTwo.setImageDrawable(requireActivity().getDrawable(R.drawable.water_grey))
                    binding.ivWaterThree.setImageDrawable(requireActivity().getDrawable(R.drawable.water_grey))
                } else if (name == "rusticite") {
                    binding.ivResiOne.setImageDrawable(requireActivity().getDrawable(R.drawable.snowflake_green))
                    binding.ivResiTwo.setImageDrawable(requireActivity().getDrawable(R.drawable.snowflake_grey))
                    binding.ivResiThree.setImageDrawable(requireActivity().getDrawable(R.drawable.snowflake_grey))
                } else if (name == "exhibition") {
                    binding.ivExpoOne.setImageDrawable(requireActivity().getDrawable(R.drawable.exposure_green))
                    binding.ivExpoTwo.setImageDrawable(requireActivity().getDrawable(R.drawable.exposure_grey))
                    binding.ivExpoThree.setImageDrawable(requireActivity().getDrawable(R.drawable.exposure_grey))
                }
            }
        }
    }


    private fun setBackgroundSowingView(incr: Int, left: Boolean, right: Boolean) {
        val resourceDrawable =
            if (left) {
                resources.getDrawable(R.drawable.rounded_month_info_left, null)
            } else if (right){
                resources.getDrawable(R.drawable.rounded_month_info_right, null)
            } else {
                resources.getDrawable(R.drawable.rounded_month_info, null)
            }
        when (incr) {
            0 -> {
                binding.tSowing0.background = resourceDrawable
                binding.tSowing0.setTextColor(resources.getColor(R.color.white_plantr, null))
            }
            1 -> {
                binding.tSowing1.background = resourceDrawable
                binding.tSowing1.setTextColor(resources.getColor(R.color.white_plantr, null))
            }
            2 -> {
                binding.tSowing2.background = resourceDrawable
                binding.tSowing2.setTextColor(resources.getColor(R.color.white_plantr, null))
            }
            3 -> {
                binding.tSowing3.background = resourceDrawable
                binding.tSowing3.setTextColor(resources.getColor(R.color.white_plantr, null))
            }
            4 -> {
                binding.tSowing4.background = resourceDrawable
                binding.tSowing4.setTextColor(resources.getColor(R.color.white_plantr, null))
            }
            5 -> {
                binding.tSowing5.background = resourceDrawable
                binding.tSowing5.setTextColor(resources.getColor(R.color.white_plantr, null))
            }
            6 -> {
                binding.tSowing6.background = resourceDrawable
                binding.tSowing6.setTextColor(resources.getColor(R.color.white_plantr, null))
            }
            7 -> {
                binding.tSowing7.background = resourceDrawable
                binding.tSowing7.setTextColor(resources.getColor(R.color.white_plantr, null))
            }
            8 -> {
                binding.tSowing8.background = resourceDrawable
                binding.tSowing8.setTextColor(resources.getColor(R.color.white_plantr, null))
            }
            9 -> {
                binding.tSowing9.background = resourceDrawable
                binding.tSowing9.setTextColor(resources.getColor(R.color.white_plantr, null))
            }
            10 -> {
                binding.tSowing10.background = resourceDrawable
                binding.tSowing10.setTextColor(resources.getColor(R.color.white_plantr, null))
            }
            11 -> {
                binding.tSowing11.background = resourceDrawable
                binding.tSowing11.setTextColor(resources.getColor(R.color.white_plantr, null))
            }
        }
    }

    private fun setBackgroundHarvestView(incr: Int, left: Boolean, right: Boolean) {
        val resourceDrawable =
            if (left) {
                resources.getDrawable(R.drawable.rounded_month_info_left, null)
            } else if (right){
                resources.getDrawable(R.drawable.rounded_month_info_right, null)
            } else {
                resources.getDrawable(R.drawable.rounded_month_info, null)
            }
        when (incr) {
            0 -> {
                binding.tHarvest0.background = resourceDrawable
                binding.tHarvest0.setTextColor(resources.getColor(R.color.white_plantr, null))
            }
            1 -> {
                binding.tHarvest1.background = resourceDrawable
                binding.tHarvest1.setTextColor(resources.getColor(R.color.white_plantr, null))
            }
            2 -> {
                binding.tHarvest2.background = resourceDrawable
                binding.tHarvest2.setTextColor(resources.getColor(R.color.white_plantr, null))
            }
            3 -> {
                binding.tHarvest3.background = resourceDrawable
                binding.tHarvest3.setTextColor(resources.getColor(R.color.white_plantr, null))
            }
            4 -> {
                binding.tHarvest4.background = resourceDrawable
                binding.tHarvest4.setTextColor(resources.getColor(R.color.white_plantr, null))
            }
            5 -> {
                binding.tHarvest5.background = resourceDrawable
                binding.tHarvest5.setTextColor(resources.getColor(R.color.white_plantr, null))
            }
            6 -> {
                binding.tHarvest6.background = resourceDrawable
                binding.tHarvest6.setTextColor(resources.getColor(R.color.white_plantr, null))
            }
            7 -> {
                binding.tHarvest7.background = resourceDrawable
                binding.tHarvest7.setTextColor(resources.getColor(R.color.white_plantr, null))
            }
            8 -> {
                binding.tHarvest8.background = resourceDrawable
                binding.tHarvest8.setTextColor(resources.getColor(R.color.white_plantr, null))
            }
            9 -> {
                binding.tHarvest9.background = resourceDrawable
                binding.tHarvest9.setTextColor(resources.getColor(R.color.white_plantr, null))
            }
            10 -> {
                binding.tHarvest10.background = resourceDrawable
                binding.tHarvest10.setTextColor(resources.getColor(R.color.white_plantr, null))
            }
            11 -> {
                binding.tHarvest11.background = resourceDrawable
                binding.tHarvest11.setTextColor(resources.getColor(R.color.white_plantr, null))
            }
        }
    }



    private fun initSowing(start: Int, end: Int) {
        var incr: Int = start
        var boolLoop = true
        while (boolLoop) {

            if (incr == start) {
                setBackgroundSowingView(incr, true, false)
            }
            if (incr != start && incr != end) {
                setBackgroundSowingView(incr, false, false)
            }
            if (incr == end) {
                boolLoop = false
                setBackgroundSowingView(incr, false, true)
            } else if (incr == 11) {
                setBackgroundSowingView(11, false, false)
                incr = 0
            } else {
                incr += 1
            }
        }
    }

    private fun initHarvest(start: Int, end: Int) {
        var incr: Int = start
        var boolLoop = true
        while (boolLoop) {

            if (incr == start) {
                setBackgroundHarvestView(incr, true, false)
            }
            if (incr != start && incr != end) {
                setBackgroundHarvestView(incr, false, false)
            }
            if (incr == end) {
                boolLoop = false
                setBackgroundHarvestView(incr, false, true)
            } else if (incr == 11) {
                setBackgroundHarvestView(11, false, false)
                incr = 0
            } else {
                incr += 1
            }
        }
    }

}
