package fr.devid.plantR.ui.myPlants

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import fr.devid.plantR.R
import kotlinx.android.synthetic.main.dialog_plant_to_add.*

class PopupAddPlantSuccess(var texte : String, context: Context, private val callback: ((Dialog, Boolean, Int) -> Unit)): Dialog(context) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_plant_to_add)
        val width = (context.resources.displayMetrics.widthPixels * 0.85).toInt()
        val height = (context.resources.displayMetrics.heightPixels * 0.85).toInt()
        var defaultHeight = this.window?.attributes
        defaultHeight?.height?.toInt()?.let { this.window?.setLayout(width, it) }
        setupView()
    }

    @SuppressLint("SetTextI18n")
    private fun setupView() {

        this.setOnDismissListener {
            callback(this, false, -1)
        }

        this.tv_popup_modal_middle.text = texte
        this.iv_button_cancel.setOnClickListener {
            callback(this, false, 0)
        }
    }

}