package fr.devid.plantR.ui.myPlants

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import fr.devid.plantR.R
import kotlinx.android.synthetic.main.dialog_delete_plant.button_cancel
import kotlinx.android.synthetic.main.dialog_delete_plant_favs.tv_popup_modal
import kotlinx.android.synthetic.main.dialog_semer_or_planting.*

class PopupChoiceFavs(val plantName : String, var bodyDate : String, context: Context, private val callback: ((Dialog, Boolean, Int) -> Unit)): Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_semer_or_planting)
        val width = (context.resources.displayMetrics.widthPixels * 0.82).toInt()
        val height = (context.resources.displayMetrics.heightPixels * 0.82).toInt()
        var defaultHeight = this.window?.attributes
            defaultHeight?.height?.toInt()?.let { this.window?.setLayout(width, it) }
        setupView()
    }

    private fun setupView() {

        this.tv_popup_modal.text = "${context.getString(R.string.QUESTION_SEMIS_OR_PLANT)} $plantName?"

        this.button_semer.setOnClickListener {
            callback(this, false, 0)
        }

        this.button_planting.setOnClickListener {
            callback(this, false, 1)
        }

        this.button_cancel_two.setOnClickListener {
            callback(this, false, -1)
        }
        this.setOnDismissListener {
            callback(this, false, -1)
        }

    }
}