package fr.devid.plantR.ui.myPlants

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.google.firebase.storage.StorageReference
import fr.devid.plantR.R
import fr.devid.plantR.models.SortOfPlant
import fr.devid.plantR.services.FirebaseService
import fr.devid.plantR.services.StringRef
import kotlinx.android.synthetic.main.popup_add_plant.*

class PopupAddPlant(val listPlant : List<SortOfPlant>, val etage : Int, val dimension : Int, val typeGarden : String, context: Context, private val callback: ((Dialog, Boolean, String, Int, Int) -> Unit)): Dialog(context) {

    private var etageOne : String? = null
    private var etageTwo : String? = null
    private var etageThree : String? = null
    private var etageFourth : String? = null

    private lateinit var storageRef : StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.popup_add_plant)
        val width = (context.resources.displayMetrics.widthPixels * 0.90).toInt()
        val height = (context.resources.displayMetrics.heightPixels * 0.90).toInt()
        var defaultHeight = this.window?.attributes
            defaultHeight?.height?.toInt()?.let { this.window?.setLayout(width, it) }

        setupView()
    }


    private fun setupView() {

        when(typeGarden) {
            "carre" ->{
                when(dimension) {
                    0 -> {
                        iv_etage_position_one.setImageDrawable(context.resources.getDrawable(R.drawable.emplacement_1_2, null))
                        iv_etage_position_two.setImageDrawable(context.resources.getDrawable(R.drawable.emplacement_2_2, null))
                        iv_position_three.visibility = View.GONE
                        iv_position_fourth.visibility = View.GONE
                        iv_etage_position_three.visibility = View.GONE
                        iv_etage_4.visibility = View.GONE
                    }
                    1 -> {
                        iv_etage_position_one.setImageDrawable(context.resources.getDrawable(R.drawable.emplacement_1_3, null))
                        iv_etage_position_two.setImageDrawable(context.resources.getDrawable(R.drawable.emplacement_2_3, null))
                        iv_etage_position_three.setImageDrawable(context.resources.getDrawable(R.drawable.emplacement_3_3, null))
                        iv_position_fourth.visibility = View.GONE
                        iv_etage_4.visibility = View.GONE
                    }
                    2 -> {
                        iv_etage_position_one.setImageDrawable(context.resources.getDrawable(R.drawable.emplacement_1_4, null))
                        iv_etage_position_two.setImageDrawable(context.resources.getDrawable(R.drawable.emplacement_2_4, null))
                        iv_etage_position_three.setImageDrawable(context.resources.getDrawable(R.drawable.emplacement_3_4, null))
                        iv_etage_4.setImageDrawable(context.resources.getDrawable(R.drawable.emplacement_4_4, null))
                    }
                }
            }
            "capteur_carre" ->{
                when(dimension) {
                    0 -> {
                        iv_etage_position_one.setImageDrawable(context.resources.getDrawable(R.drawable.emplacement_1_2, null))
                        iv_etage_position_two.setImageDrawable(context.resources.getDrawable(R.drawable.emplacement_2_2, null))
                        iv_position_three.visibility = View.GONE
                        iv_position_fourth.visibility = View.GONE
                        iv_etage_position_three.visibility = View.GONE
                        iv_etage_4.visibility = View.GONE
                    }
                    1 -> {
                        iv_etage_position_one.setImageDrawable(context.resources.getDrawable(R.drawable.emplacement_1_3, null))
                        iv_etage_position_two.setImageDrawable(context.resources.getDrawable(R.drawable.emplacement_2_3, null))
                        iv_etage_position_three.setImageDrawable(context.resources.getDrawable(R.drawable.emplacement_3_3, null))
                        iv_position_fourth.visibility = View.GONE
                        iv_etage_4.visibility = View.GONE
                    }
                    2 -> {
                        iv_etage_position_one.setImageDrawable(context.resources.getDrawable(R.drawable.emplacement_1_4, null))
                        iv_etage_position_two.setImageDrawable(context.resources.getDrawable(R.drawable.emplacement_2_4, null))
                        iv_etage_position_three.setImageDrawable(context.resources.getDrawable(R.drawable.emplacement_3_4, null))
                        iv_etage_4.setImageDrawable(context.resources.getDrawable(R.drawable.emplacement_4_4, null))
                    }
                }
            }
            "pot" -> {
                when(dimension) {
                    0 -> {
                        iv_etage_position_one.visibility = View.GONE
                        iv_position_two.visibility = View.GONE
                        iv_position_three.visibility = View.GONE
                        iv_position_fourth.visibility = View.GONE
                        iv_etage_position_two.visibility = View.GONE
                        iv_etage_position_three.visibility = View.GONE
                        iv_etage_4.visibility = View.GONE
                    }
                    1 -> {
                        iv_etage_position_one.visibility = View.GONE
                        iv_position_two.visibility = View.GONE
                        iv_position_three.visibility = View.GONE
                        iv_position_fourth.visibility = View.GONE
                        iv_etage_position_two.visibility = View.GONE
                        iv_etage_position_three.visibility = View.GONE
                        iv_etage_4.visibility = View.GONE
                    }
                    2 -> {
                        iv_etage_position_one.visibility = View.GONE
                        iv_position_two.visibility = View.GONE
                        iv_position_three.visibility = View.GONE
                        iv_position_fourth.visibility = View.GONE
                        iv_etage_position_two.visibility = View.GONE
                        iv_etage_position_three.visibility = View.GONE
                        iv_etage_4.visibility = View.GONE
                    }
                }
            }
            "capteur_pot" -> {
                when(dimension) {
                    0 -> {
                        iv_etage_position_one.visibility = View.GONE
                        iv_position_two.visibility = View.GONE
                        iv_position_three.visibility = View.GONE
                        iv_position_fourth.visibility = View.GONE
                        iv_etage_position_two.visibility = View.GONE
                        iv_etage_position_three.visibility = View.GONE
                        iv_etage_4.visibility = View.GONE
                    }
                    1 -> {
                        iv_etage_position_one.visibility = View.GONE
                        iv_position_two.visibility = View.GONE
                        iv_position_three.visibility = View.GONE
                        iv_position_fourth.visibility = View.GONE
                        iv_etage_position_two.visibility = View.GONE
                        iv_etage_position_three.visibility = View.GONE
                        iv_etage_4.visibility = View.GONE
                    }
                    2 -> {
                        iv_etage_position_one.visibility = View.GONE
                        iv_position_two.visibility = View.GONE
                        iv_position_three.visibility = View.GONE
                        iv_position_fourth.visibility = View.GONE
                        iv_etage_position_two.visibility = View.GONE
                        iv_etage_position_three.visibility = View.GONE
                        iv_etage_4.visibility = View.GONE
                    }
                }
            }
            "jardiniere" -> {
                when(dimension) {
                    0 -> {
                        iv_position_three.visibility = View.GONE
                        iv_position_fourth.visibility = View.GONE
                        iv_etage_position_three.visibility = View.GONE
                        iv_etage_4.visibility = View.GONE
                    }
                    1 -> {
                        iv_position_three.visibility = View.GONE
                        iv_position_fourth.visibility = View.GONE
                        iv_etage_position_three.visibility = View.GONE
                        iv_etage_4.visibility = View.GONE
                    }
                    2 -> {

                    }
                }
            }
            "capteur_jardiniere" -> {
                when(dimension) {
                    0 -> {
                        iv_position_three.visibility = View.GONE
                        iv_position_fourth.visibility = View.GONE
                        iv_etage_position_three.visibility = View.GONE
                        iv_etage_4.visibility = View.GONE
                    }
                    1 -> {
                        iv_position_three.visibility = View.GONE
                        iv_position_fourth.visibility = View.GONE
                        iv_etage_position_three.visibility = View.GONE
                        iv_etage_4.visibility = View.GONE
                    }
                    2 -> {

                    }
                }
            }
            "cle_en_main" -> {

            }
            "mural" -> {

            }
        }

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
                this.etageOne = "busy"
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
                //this.tv_position_two.text ="Emplacement 2"
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
                this.etageThree = "busy"
                this.iv_position_three.isClickable = false
              //  this.tv_position_three.text ="Emplacement 3"
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
                    this.etageFourth = "busy"


                    this.iv_position_fourth.isClickable = false
//                   this.tv_position_fourth.text ="Emplacement 4"

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
                callback(this, false, name.capitalize() , etage, 0)
            }
        }
        this.iv_position_two.setOnClickListener {
            println(etage)
            if (etageTwo == null) {
                callback(this, true, "" , etage, 1)
            } else {
                val name = listPlant.firstOrNull { it.id[2].toString() == "1" }?.positionDataPlant?.plantID ?: ""
                callback(this, false, name.capitalize() , etage, 1)
            }
        }
        this.iv_position_three.setOnClickListener {
            println(etage)
            if (etageThree == null) {
                callback(this, true, "" , etage, 2)
            } else {
                val name = listPlant.firstOrNull { it.id[2].toString() == "2" }?.positionDataPlant?.plantID ?: ""
                callback(this, false, name.capitalize() , etage, 2)
            }
        }

        this.iv_position_fourth.setOnClickListener {
            println(etage)
            if (etageFourth == null) {
                callback(this, true, "" , etage, 3)
            } else {
                val name = listPlant.firstOrNull { it.id[2].toString() == "3" }?.positionDataPlant?.plantID ?: ""
                callback(this, false, name.capitalize() , etage, 3)
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