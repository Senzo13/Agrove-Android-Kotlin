package fr.devid.plantR.ui.myPlants
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import fr.devid.plantR.Constants
import fr.devid.plantR.R
import fr.devid.plantR.models.PositionDataPlant
import kotlinx.android.synthetic.main.dialog_wishlist_add_plant.*
import kotlinx.android.synthetic.main.fragment_home.*

class PopupWishListAddPlant(var currentPlantId : String, var toDo : String, var currentPlantName : String , var stage : String, var typeGarden : String, var listPlantForAdd : List<Map<String, PositionDataPlant>>, context: Context, private val callback: ((Dialog, Boolean, Int, String, ArrayList<String>) -> Unit)): Dialog(context) {
    private lateinit var adapter : PopupWishListAdapter
    var arrayListAdd = arrayListOf<String>()
    private lateinit var mSpinnerEtagesOrRangs: Spinner
    private var selectSpinnerEtagesOrRangs : Int = 0
    var stageListOfPlants = HashMap<String, PositionDataPlant>()
    val arrayEtagePlant = arrayListOf<Map<String, PositionDataPlant>>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_wishlist_add_plant)
        val width = (context.resources.displayMetrics.widthPixels * 0.99).toInt()
        val height = (context.resources.displayMetrics.heightPixels * 0.99).toInt()
        var defaultHeight = this.window?.attributes
        defaultHeight?.height?.toInt()?.let { this.window?.setLayout(width, it) }
        setupView()
    }

    @SuppressLint("ResourceAsColor", "SetTextI18n")
    private fun setupView() {
        if(typeGarden == "parcelle") {
            other()
            this.cl_spinner_rang.visibility = View.VISIBLE
            mSpinnerEtagesOrRangs = this.spinner_rangs_etages
            // We have here a layout which is stored in a variable in an arrayAdapter, this one is then used to store the name of the planters there.
            val adapterEtagesOrRangs = ArrayAdapter(context, R.layout.view_drop_down_menu_spinner, mutableListOf<String>())
            mSpinnerEtagesOrRangs.adapter = adapterEtagesOrRangs
            mSpinnerEtagesOrRangs.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                        parent: AdapterView<*>, view: View,
                        position: Int, id: Long
                ) {
                    if(position == 0 ) {
                        println("Selection : " + Constants.arrayRangsOrStages.get(0))
                        selectSpinnerEtagesOrRangs = 0
                        stage = "4"
                        arrayEtagePlant.clear()
                        val maxIndex = stage.toInt() - 1
                        for (i in 0..maxIndex) {
                            val list = stageListOfPlants.filter {
                                it.key[0].toString() == i.toString()
                            }
                            arrayEtagePlant.add(list)
                        }
                        other()
                        setView(stage, false)
                    }
                    if(position == 1) {
                        println("Selection : " + Constants.arrayRangsOrStages.get(1))
                        selectSpinnerEtagesOrRangs = 1
                        arrayEtagePlant.clear()
                        stage = "2"
                        val maxIndex = stage.toInt() - 1
                        for (i in 0..maxIndex) {
                            val list = stageListOfPlants.filter {
                                it.key[0].toString() == i.toString()
                            }
                            println("POKOS NEGRO" + list)
                            arrayEtagePlant.add(list)
                        }
                        other()
                        setView(stage, true)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }

            val adapterRangs = mSpinnerEtagesOrRangs.adapter as ArrayAdapter<String>
            adapterRangs.clear()
            val arrayStringRangs = Constants.arrayRangsOrStages
            adapterRangs.addAll(arrayStringRangs)
            println("Selection : " + Constants.arrayRangsOrStages.get(0))
        } else {
            this.cl_spinner_rang.visibility = View.GONE
            setView(stage, false)
            println("Le spinner est normalement invisible")
            otherDefault()
        }




        if(selectSpinnerEtagesOrRangs == 1) {
            adapter.nbAdd = 4
        } else {
            when(typeGarden) {
                "jardiniere" -> {
                    stage = "1"
                    val maxIndex = stage.toInt() - 1
                    for (i in 0..maxIndex) {
                        val list = stageListOfPlants.filter {
                            it.key[0].toString() == i.toString()
                        }
                        println("POKOS NEGRO" + list)
                        arrayEtagePlant.add(list)
                    }
                    adapter.nbAdd = 2
                }
                "cle_en_main" -> {
                    adapter.nbAdd = 4
                }
                "parcelle" -> {
                    adapter.nbAdd = 4
                }
                "carre" -> {
                    adapter.nbAdd = 3
                }
                "capteur_pot" -> {
                    adapter.nbAdd = 1
                }
                "pot" -> {
                    adapter.nbAdd = 1
                }
                "mural" -> {
                    adapter.nbAdd = 4
                }
            }
        }


        this.tv_popup_modal.text = "${context.getString(R.string.add_trad_10)} $toDo \n${context.getString(R.string.add_trad_11)} $currentPlantName ?"



        this.iv_add_plants.setOnClickListener {
            if(this.arrayListAdd.count() >= 1) {
                println("Je peux envoyer des données")
                callback(this, false, 1, "", this.arrayListAdd?: arrayListOf())
            } else {
                println("je peux pas envoyer donneer")
            }
        }

        this.iv_cancel_add_plant.setOnClickListener {
            callback(this, false, 0, "", arrayListOf())
            //ANNULER NE RIEN FAIRE
        }

        this.setOnDismissListener {
            callback(this, false, -1, "", arrayListOf())
        }

    }

    private fun other() {
        //modifier probleme wishlist
        this.adapter = PopupWishListAdapter(stage, { plantId ->
            if(plantId != null) {
                callback(this, false, 2, plantId,arrayListOf())
            } else {
                callback(this, false, 2, "une plante", arrayListOf())
            }
        }, { addElement ->
            this.arrayListAdd.add(addElement)
            if(this.arrayListAdd.count() >= 1) {
                println("Je click car c'est bon")
                this.iv_add_plants.setTextColor(context.resources.getColor(R.color.greenFonce_plantr, null))
            } else {
                this.iv_add_plants.setTextColor(context.resources.getColor(R.color.grey_plantr, null))
            }
        }, { removeElement ->
            this.arrayListAdd.remove(removeElement)
            if(this.arrayListAdd.count() >= 1) {
                println("Je click car c'est bon")
                this.iv_add_plants.setTextColor(context.resources.getColor(R.color.greenFonce_plantr, null))
            } else {
                this.iv_add_plants.setTextColor(context.resources.getColor(R.color.grey_plantr, null))
            }
        })

        this.recyclerView_wishlist.adapter = adapter

        this.recyclerView_wishlist.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        this.adapter.submitList(arrayEtagePlant)
    }


    private fun otherDefault() {
        //modifier probleme wishlist
        this.adapter = PopupWishListAdapter(stage, { plantId ->
            if(plantId != null) {
                callback(this, false, 2, plantId,arrayListOf())
            } else {
                callback(this, false, 2, "une plante", arrayListOf())
            }
        }, { addElement ->
            this.arrayListAdd.add(addElement)
            if(this.arrayListAdd.count() >= 1) {
                println("Je click car c'est bon")
                this.iv_add_plants.setTextColor(context.resources.getColor(R.color.greenFonce_plantr, null))
            } else {
                this.iv_add_plants.setTextColor(context.resources.getColor(R.color.grey_plantr, null))
            }
        }, { removeElement ->
            this.arrayListAdd.remove(removeElement)
            if(this.arrayListAdd.count() >= 1) {
                println("Je click car c'est bon")
                this.iv_add_plants.setTextColor(context.resources.getColor(R.color.greenFonce_plantr, null))
            } else {
                this.iv_add_plants.setTextColor(context.resources.getColor(R.color.grey_plantr, null))
            }
        })

        this.recyclerView_wishlist.adapter = adapter

        this.recyclerView_wishlist.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        this.adapter.submitList(listPlantForAdd)
    }
    //SET DES IMAGE DES ETAGES

    private fun setView(stage: String, rang : Boolean) {

        if(rang) {
            println("Stage dans rang : " + stage)
            this.iv_etage_4.visibility = View.GONE
            this.iv_etage_3.visibility = View.GONE
            this.iv_etage_2.setImageResource(R.drawable.rang2_2)
            this.iv_etage_2.setColorFilter(
                    ContextCompat.getColor(context, R.color.green_plantr), android.graphics.PorterDuff.Mode.MULTIPLY);
            this.iv_etage_1.setImageResource(R.drawable.rang1_2)
            this.iv_etage_1.setColorFilter(
                    ContextCompat.getColor(context, R.color.green_plantr), android.graphics.PorterDuff.Mode.MULTIPLY);
        } else {
            println("Stage dans le sinon : " + stage)
            when(typeGarden) {
                "cle_en_main" -> {
                    if (stage == "4") {
                        this.iv_etage_4.setImageResource(R.drawable.etage4_4)
                        this.iv_etage_3.setImageResource(R.drawable.etage4_3)
                        this.iv_etage_2.setImageResource(R.drawable.etage4_2)
                        this.iv_etage_1.setImageResource(R.drawable.etage4_1)
                    }
                    if (stage == "3") {
                        this.iv_etage_4.visibility = View.GONE
                        this.iv_etage_3.setImageResource(R.drawable.etage3_3)
                        this.iv_etage_2.setImageResource(R.drawable.etage3_2)
                        this.iv_etage_1.setImageResource(R.drawable.etage3_1)
                    }
                    if (stage == "2") {
                        this.iv_etage_4.visibility = View.GONE
                        this.iv_etage_3.visibility = View.GONE
                        this.iv_etage_2.setImageResource(R.drawable.etage2_2)
                        this.iv_etage_1.setImageResource(R.drawable.etage2_1)
                    }
                    if (stage == "1") {
                        this.iv_etage_4.visibility = View.GONE
                        this.iv_etage_3.visibility = View.GONE
                        this.iv_etage_2.visibility = View.GONE
                        this.iv_etage_1.setImageResource(R.drawable.etage1_black)
                        this.iv_etage_1.setColorFilter(
                                ContextCompat.getColor(context, R.color.green_plantr), android.graphics.PorterDuff.Mode.MULTIPLY);
                    }
                }
                "parcelle" -> {
                    this.iv_etage_4.visibility = View.VISIBLE
                    this.iv_etage_3.visibility = View.VISIBLE
                    if (stage == "4") {
                        this.iv_etage_4.setImageResource(R.drawable.etage4_4)
                        this.iv_etage_3.setImageResource(R.drawable.etage4_3)
                        this.iv_etage_2.setImageResource(R.drawable.etage4_2)
                        this.iv_etage_1.setImageResource(R.drawable.etage4_1)
                    }
                    if (stage == "3") {
                        this.iv_etage_4.visibility = View.GONE
                        this.iv_etage_3.setImageResource(R.drawable.etage3_3)
                        this.iv_etage_2.setImageResource(R.drawable.etage3_2)
                        this.iv_etage_1.setImageResource(R.drawable.etage3_1)
                    }
                    if (stage == "2") {
                        this.iv_etage_4.visibility = View.GONE
                        this.iv_etage_3.visibility = View.GONE
                        this.iv_etage_2.setImageResource(R.drawable.etage2_2)
                        this.iv_etage_1.setImageResource(R.drawable.etage2_1)
                    }
                    if (stage == "1") {
                        this.iv_etage_4.visibility = View.GONE
                        this.iv_etage_3.visibility = View.GONE
                        this.iv_etage_2.visibility = View.GONE
                        this.iv_etage_1.setImageResource(R.drawable.etage1_black)
                        this.iv_etage_1.setColorFilter(
                                ContextCompat.getColor(context, R.color.green_plantr), android.graphics.PorterDuff.Mode.MULTIPLY);
                    }
                }
                "mural" -> {
                    if (stage == "4") {
                        this.iv_etage_4.setImageResource(R.drawable.etage4_4)
                        this.iv_etage_3.setImageResource(R.drawable.etage4_3)
                        this.iv_etage_2.setImageResource(R.drawable.etage4_2)
                        this.iv_etage_1.setImageResource(R.drawable.etage4_1)
                    }
                    if (stage == "3") {
                        this.iv_etage_4.visibility = View.GONE
                        this.iv_etage_3.setImageResource(R.drawable.etage3_3)
                        this.iv_etage_2.setImageResource(R.drawable.etage3_2)
                        this.iv_etage_1.setImageResource(R.drawable.etage3_1)
                    }
                    if (stage == "2") {
                        this.iv_etage_4.visibility = View.GONE
                        this.iv_etage_3.visibility = View.GONE
                        this.iv_etage_2.setImageResource(R.drawable.etage2_2)
                        this.iv_etage_1.setImageResource(R.drawable.etage2_1)
                    }
                    if (stage == "1") {
                        this.iv_etage_4.visibility = View.GONE
                        this.iv_etage_3.visibility = View.GONE
                        this.iv_etage_2.visibility = View.GONE
                        this.iv_etage_1.setImageResource(R.drawable.etage1_black)
                        this.iv_etage_1.setColorFilter(
                                ContextCompat.getColor(context, R.color.green_plantr), android.graphics.PorterDuff.Mode.MULTIPLY);
                    }
                }
                "capteur_pot" -> {
                    if (stage == "4") {
                        this.iv_etage_4.setImageResource(R.drawable.etage4_4)
                        this.iv_etage_3.setImageResource(R.drawable.etage4_3)
                        this.iv_etage_2.setImageResource(R.drawable.etage4_2)
                        this.iv_etage_1.setImageResource(R.drawable.etage4_1)
                    }
                    if (stage == "3") {
                        this.iv_etage_4.visibility = View.GONE
                        this.iv_etage_3.setImageResource(R.drawable.etage3_3)
                        this.iv_etage_2.setImageResource(R.drawable.etage3_2)
                        this.iv_etage_1.setImageResource(R.drawable.etage3_1)
                    }
                    if (stage == "2") {
                        this.iv_etage_4.visibility = View.GONE
                        this.iv_etage_3.visibility = View.GONE
                        this.iv_etage_2.setImageResource(R.drawable.etage2_2)
                        this.iv_etage_1.setImageResource(R.drawable.etage2_1)
                    }
                    if (stage == "1") {
                        this.iv_etage_4.visibility = View.GONE
                        this.iv_etage_3.visibility = View.GONE
                        this.iv_etage_2.visibility = View.GONE
                        this.iv_etage_1.setImageResource(R.drawable.etage1_black)
                        this.iv_etage_1.setColorFilter(
                                ContextCompat.getColor(context, R.color.green_plantr), android.graphics.PorterDuff.Mode.MULTIPLY);
                    }
                }
                "capteur_jardiniere" -> {
                    if (stage == "4") {
                        this.iv_etage_4.setImageResource(R.drawable.etage4_4)
                        this.iv_etage_3.setImageResource(R.drawable.etage4_3)
                        this.iv_etage_2.setImageResource(R.drawable.etage4_2)
                        this.iv_etage_1.setImageResource(R.drawable.etage4_1)
                    }
                    if (stage == "3") {
                        this.iv_etage_4.visibility = View.GONE
                        this.iv_etage_3.setImageResource(R.drawable.etage3_3)
                        this.iv_etage_2.setImageResource(R.drawable.etage3_2)
                        this.iv_etage_1.setImageResource(R.drawable.etage3_1)
                    }
                    if (stage == "2") {
                        this.iv_etage_4.visibility = View.GONE
                        this.iv_etage_3.visibility = View.GONE
                        this.iv_etage_2.setImageResource(R.drawable.etage2_2)
                        this.iv_etage_1.setImageResource(R.drawable.etage2_1)
                    }
                    if (stage == "1") {
                        this.iv_etage_4.visibility = View.GONE
                        this.iv_etage_3.visibility = View.GONE
                        this.iv_etage_2.visibility = View.GONE
                        this.iv_etage_1.setImageResource(R.drawable.etage1_black)
                        this.iv_etage_1.setColorFilter(
                                ContextCompat.getColor(context, R.color.green_plantr), android.graphics.PorterDuff.Mode.MULTIPLY);
                    }
                }
                "capteur_carre" -> {
                    if (stage == "4") {
                        this.iv_etage_4.setImageResource(R.drawable.etage4_4)
                        this.iv_etage_3.setImageResource(R.drawable.etage4_3)
                        this.iv_etage_2.setImageResource(R.drawable.etage4_2)
                        this.iv_etage_1.setImageResource(R.drawable.etage4_1)
                    }
                    if (stage == "3") {
                        this.iv_etage_4.visibility = View.GONE
                        this.iv_etage_3.setImageResource(R.drawable.etage3_3)
                        this.iv_etage_2.setImageResource(R.drawable.etage3_2)
                        this.iv_etage_1.setImageResource(R.drawable.etage3_1)
                    }
                    if (stage == "2") {
                        this.iv_etage_4.visibility = View.GONE
                        this.iv_etage_3.visibility = View.GONE
                        this.iv_etage_2.setImageResource(R.drawable.etage2_2)
                        this.iv_etage_1.setImageResource(R.drawable.etage2_1)
                    }
                    if (stage == "1") {
                        this.iv_etage_4.visibility = View.GONE
                        this.iv_etage_3.visibility = View.GONE
                        this.iv_etage_2.visibility = View.GONE
                        this.iv_etage_1.setImageResource(R.drawable.etage1_black)
                        this.iv_etage_1.setColorFilter(
                                ContextCompat.getColor(context, R.color.green_plantr), android.graphics.PorterDuff.Mode.MULTIPLY);
                    }
                }
                "carre" -> {
                    this.iv_etage_4.setColorFilter(
                            ContextCompat.getColor(context, R.color.green_plantr), android.graphics.PorterDuff.Mode.MULTIPLY);
                    this.iv_etage_3.setColorFilter(
                            ContextCompat.getColor(context, R.color.green_plantr), android.graphics.PorterDuff.Mode.MULTIPLY);
                    this.iv_etage_2.setColorFilter(
                            ContextCompat.getColor(context, R.color.green_plantr), android.graphics.PorterDuff.Mode.MULTIPLY);
                    this.iv_etage_1.setColorFilter(
                            ContextCompat.getColor(context, R.color.green_plantr), android.graphics.PorterDuff.Mode.MULTIPLY);
                    if (stage == "4") {
                        this.iv_etage_4.setImageResource(R.drawable.rang1_4)
                        this.iv_etage_3.setImageResource(R.drawable.rang2_4)
                        this.iv_etage_2.setImageResource(R.drawable.rang3_4)
                        this.iv_etage_1.setImageResource(R.drawable.rang4_4)
                    }
                    if (stage == "3") {
                        this.iv_etage_4.visibility = View.GONE
                        this.iv_etage_3.setImageResource(R.drawable.rang1_3)
                        this.iv_etage_2.setImageResource(R.drawable.rang2_3)
                        this.iv_etage_1.setImageResource(R.drawable.rang3_3)
                    }
                    if (stage == "2") {
                        this.iv_etage_4.visibility = View.GONE
                        this.iv_etage_3.visibility = View.GONE
                        this.iv_etage_2.setImageResource(R.drawable.rang2_2)
                        this.iv_etage_1.setImageResource(R.drawable.rang1_2)
                    }
                    if (stage == "1") {
                        this.iv_etage_4.visibility = View.GONE
                        this.iv_etage_3.visibility = View.GONE
                        this.iv_etage_2.visibility = View.GONE
                        this.iv_etage_1.setImageResource(R.drawable.etage1_black)
                        this.iv_etage_1.setColorFilter(
                                ContextCompat.getColor(context, R.color.green_plantr), android.graphics.PorterDuff.Mode.MULTIPLY);
                    }
                }
                "jardiniere" -> {
                    this.iv_etage_4.visibility = View.GONE
                    this.iv_etage_3.visibility = View.GONE
                    this.iv_etage_2.visibility = View.GONE
                    this.iv_etage_1.setImageResource(R.drawable.mon_potager_jardiniere_petit)
                    this.iv_etage_1.scaleX = 1F
                    this.iv_etage_1.scaleY = 1F
                    this.iv_etage_1.setColorFilter(ContextCompat.getColor(context, R.color.green_plantr), android.graphics.PorterDuff.Mode.MULTIPLY);
                }
                "pot" -> {
                    this.iv_etage_4.visibility = View.GONE
                    this.iv_etage_3.visibility = View.GONE
                    this.iv_etage_2.visibility = View.GONE
                    this.iv_etage_1.visibility = View.GONE

                }

            }
        }




    }
    fun Context.lifecycleOwner(): LifecycleOwner? {
        var curContext = this
        var maxDepth = 20
        while (maxDepth-- > 0 && curContext !is LifecycleOwner) {
            curContext = (curContext as ContextWrapper).baseContext
        }
        return if (curContext is LifecycleOwner) {
            curContext as LifecycleOwner
        } else {
            null
        }
    }
}