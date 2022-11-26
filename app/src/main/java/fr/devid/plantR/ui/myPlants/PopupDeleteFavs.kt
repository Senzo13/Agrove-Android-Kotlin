package fr.devid.plantR.ui.myPlants

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import fr.devid.plantR.R
import kotlinx.android.synthetic.main.dialog_delete_plant.button_cancel
import kotlinx.android.synthetic.main.dialog_delete_plant.button_validate_plant
import kotlinx.android.synthetic.main.dialog_delete_plant_favs.*

class PopupDeleteFavs(val name : String, context: Context, private val callback: ((Dialog, Boolean, Int) -> Unit)): Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_delete_plant_favs)
        val width = (context.resources.displayMetrics.widthPixels * 0.82).toInt()
        val height = (context.resources.displayMetrics.heightPixels * 0.82).toInt()
        val defaultHeight = this.window?.attributes
            defaultHeight?.height?.toInt()?.let { this.window?.setLayout(width, it) }

        setupView()
    }


    private fun setupView() {

        this.tv_popup_middle.text = "${context.getString(R.string.delete_plant_2)} $name ${context.getString(R.string.delete_plant_3)}"

        this.button_cancel.setOnClickListener {
            callback(this, false, 0)
        }

        this.button_validate_plant.setOnClickListener {
            callback(this, true, 1)
        }

        this.iv_button_cancel_favs.setOnClickListener {
            callback(this, true, -1)
        }

        this.setOnDismissListener {
            callback(this, false, -1)
        }

    }
}