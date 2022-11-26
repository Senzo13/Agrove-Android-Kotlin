package fr.devid.plantR.ui.myPlants

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.format.DateFormat
import fr.devid.plantR.R
import kotlinx.android.synthetic.main.dialog_soins.*
import java.util.*

class PopupSoins(private var namePlant : String, context: Context, private val callback: ((Dialog, Boolean) -> Unit)): Dialog(context) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_soins)
        val width = (context.resources.displayMetrics.widthPixels * 0.85).toInt()
        val height = (context.resources.displayMetrics.heightPixels * 0.85).toInt()
        var defaultHeight = this.window?.attributes
            defaultHeight?.height?.toInt()?.let { this.window?.setLayout(width, it) }
        setupView()
    }

    fun getMonth(timestamp: Long): Int {
        val calendar = Calendar.getInstance(Locale.FRANCE)
        calendar.timeInMillis = timestamp * 1000L
        val month = DateFormat.format("MM", calendar).toString().toInt()
        return month
    }

    private fun setupView() {

        this.tv_title_public.text = context.getString(R.string.tv_title_public_popup) + "\n ${namePlant} ?"

        this.button_find_solution.setOnClickListener {
            callback(this, false)
        }

        this.button_inform_soins.setOnClickListener {
            callback(this, true)
        }

//        this.button_soins_close.setOnClickListener {
//            callback(this, true)
//        }

        this.setOnDismissListener {
            callback(this, false)
        }
    }


}