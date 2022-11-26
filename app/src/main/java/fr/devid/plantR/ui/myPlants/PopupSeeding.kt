package fr.devid.plantR.ui.myPlants

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.format.DateFormat
import fr.devid.plantR.R
import kotlinx.android.synthetic.main.dialog_wait_to_planting.*
import kotlinx.android.synthetic.main.dialog_wait_to_seed.*
import kotlinx.android.synthetic.main.dialog_wait_to_seed.tv_popup_modal_middle
import java.util.*

class PopupSeeding(var texte : String, context: Context,var name : String, private val callback: ((Dialog, Boolean, Int) -> Unit)): Dialog(context) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_wait_to_seed)
        val width = (context.resources.displayMetrics.widthPixels * 0.85).toInt()
        val height = (context.resources.displayMetrics.heightPixels * 0.85).toInt()
        var defaultHeight = this.window?.attributes
        defaultHeight?.height?.toInt()?.let { this.window?.setLayout(width, it) }
        setupView()
    }

    fun getMonth(timestamp: Long): Int {
        val calendar = Calendar.getInstance(Locale.FRANCE)
        calendar.timeInMillis = timestamp * 1000L
        return DateFormat.format("MM", calendar).toString().toInt()
    }

    @SuppressLint("SetTextI18n")
    private fun setupView() {


        this.tv_popup_modal_middle.text = texte

        this.button_favs.setOnClickListener {
            callback(this,false, 0)
        }

        this.button_seeding.setOnClickListener {
            callback(this, false, 1)
        }

        this.setOnDismissListener {
            callback(this, false, -1)
        }

        this.button_cancel_seed.setOnClickListener {
            callback(this, false, -1)
        }
    }

}