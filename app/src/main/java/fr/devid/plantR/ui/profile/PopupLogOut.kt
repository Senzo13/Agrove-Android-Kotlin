package fr.devid.plantR.ui.profile

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import fr.devid.plantR.R
import kotlinx.android.synthetic.main.dialog_delete_plant.*
import kotlinx.android.synthetic.main.dialog_log_out.*

class PopupLogOut(context: Context, private val callback: ((Dialog, Boolean, String) -> Unit)): Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_log_out)
        val width = (context.resources.displayMetrics.widthPixels * 0.82).toInt()
        val height = (context.resources.displayMetrics.heightPixels * 0.82).toInt()
        var defaultHeight = this.window?.attributes
            defaultHeight?.height?.toInt()?.let { this.window?.setLayout(width, it) }

        setupView()
    }


    private fun setupView() {

        this.button_deco.setOnClickListener {
            callback(this, true, "")
        }

        this.setOnDismissListener {
            callback(this, false, "")
        }

        this.button_close_deco.setOnClickListener {
            callback(this, false, "")
        }

    }
}