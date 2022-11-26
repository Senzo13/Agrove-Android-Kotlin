package fr.devid.plantR.ui.subscribe

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.bumptech.glide.Glide
import com.google.firebase.storage.StorageReference
import fr.devid.plantR.R
import fr.devid.plantR.models.SortOfPlant
import fr.devid.plantR.services.FirebaseService
import fr.devid.plantR.services.StringRef
import kotlinx.android.synthetic.main.popup_add_plant.*

class PopupAddPlantSubscribe(val listPlant : List<SortOfPlant>, val etage : Int, context: Context, private val callback: ((Dialog, Boolean, String, Int, Int) -> Unit)): Dialog(context) {

    private var etageOne : String? = null
    private var etageTwo : String? = null
    private var etageThree : String? = null
    private var etageFourth : String? = null

    private lateinit var storageRef : StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.popup_add_plant)
        setupView()
        val width = (context.resources.displayMetrics.widthPixels * 0.78).toInt()
        val height = (context.resources.displayMetrics.heightPixels * 0.90).toInt()
        var defaultHeight = this.window?.attributes
        defaultHeight?.height?.toInt()?.let { this.window?.setLayout(width, it) }
    }


    private fun setupView() {

        Glide.with(this.iv_position_one.context)
            .load(R.drawable.add_green)
            .circleCrop()
            .into(this.iv_position_one)

        Glide.with(this.iv_position_two.context)
            .load(R.drawable.add_green)
            .circleCrop()
            .into(this.iv_position_two)


        Glide.with(this.iv_position_three.context)
            .load(R.drawable.add_green)
            .circleCrop()
            .into(this.iv_position_three)

        Glide.with(this.iv_position_fourth.context)
            .load(R.drawable.add_green)
            .circleCrop()
            .into(this.iv_position_fourth)


        listPlant.map { data->

            when(data.id[2].toString()) {
            "0" -> {
                    storageRef = FirebaseService().getStoragePlantsReference()
                        .child(data.positionDataPlant?.plantID!!).child(StringRef.plantJpg)
                    storageRef.downloadUrl.addOnSuccessListener {
                        Glide.with(this.iv_position_one.context)
                            .load(it.toString())
                            .circleCrop()
                            .into(this.iv_position_one)
                    }
                this.etageOne =  "busy"
                this.iv_position_one.isClickable = false

            }

            "1" -> {
                    storageRef = FirebaseService().getStoragePlantsReference()
                        .child(data.positionDataPlant?.plantID!!).child(StringRef.plantJpg)
                    storageRef.downloadUrl.addOnSuccessListener {
                        Glide.with(this.iv_position_two.context)
                            .load(it.toString())
                            .circleCrop()
                            .into(this.iv_position_two)
                    }
                this.etageTwo = "busy"
                this.iv_position_two.isClickable = false

            }

            "2" -> {
                    storageRef = FirebaseService().getStoragePlantsReference()
                        .child(data.positionDataPlant?.plantID!!)
                        .child(StringRef.plantJpg)
                    storageRef.downloadUrl.addOnSuccessListener {
                        Glide.with(this.iv_position_three.context)
                            .load(it.toString())
                            .circleCrop()
                            .into(this.iv_position_three)
                    }
                this.etageThree =  "busy"
                this.iv_position_three.isClickable = false

            }
                "3" -> {
                        storageRef = FirebaseService().getStoragePlantsReference()
                            .child(data.positionDataPlant?.plantID!!)
                            .child(StringRef.plantJpg)
                        storageRef.downloadUrl.addOnSuccessListener {
                            Glide.with(this.iv_position_fourth.context)
                                .load(it.toString())
                                .circleCrop()
                                .into(this.iv_position_fourth)
                        }
                    this.etageFourth =  "busy"
                    this.iv_position_fourth.isClickable = false
                }
            }
        }

        this.iv_position_one.setOnClickListener {
            println(etage)
            if (etageOne == null) {
                callback(this, true, "" , etage, 0)
            } else {
                val plant = listPlant.firstOrNull { it.id[2].toString() == "0" }
                print("pour etre sur "  + plant)
                    val name = plant?.positionDataPlant?.plantID ?: ""
                callback(this, false, name , etage, 0)
            }
        }
        this.iv_position_two.setOnClickListener {
            println(etage)
            if (etageTwo == null) {
                callback(this, true, "" , etage, 1)
            } else {
                val name = listPlant.firstOrNull { it.id[2].toString() == "1" }?.positionDataPlant?.plantID ?: ""
                callback(this, false, name , etage, 1)
            }
        }
        this.iv_position_three.setOnClickListener {
            println(etage)
            if (etageThree == null) {
                callback(this, true, "" , etage, 2)
            } else {
                val name = listPlant.firstOrNull { it.id[2].toString() == "2" }?.positionDataPlant?.plantID ?: ""
                callback(this, false, name , etage, 2)
            }
        }

        this.iv_position_fourth.setOnClickListener {
            println(etage)
            if (etageFourth == null) {
                callback(this, true, "" , etage, 3)
            } else {
                val name = listPlant.firstOrNull { it.id[2].toString() == "3" }?.positionDataPlant?.plantID ?: ""
                callback(this, false, name , etage, 3)
            }
        }


        this.setOnDismissListener {
            callback(this, true, "", -1, -1)
        }

        this.ll_canceled.setOnClickListener {
            callback(this, true, "",-1, -1)
        }

    }
}