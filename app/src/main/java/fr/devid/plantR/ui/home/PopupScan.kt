package fr.devid.plantR.ui.home

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.google.firebase.storage.StorageReference
import fr.devid.plantR.R
import kotlinx.android.synthetic.main.dialog_delete_plant.*
import kotlinx.android.synthetic.main.dialog_scan_choice.*
import kotlinx.android.synthetic.main.dialog_state.*

class PopupScan(context: Context, private val callback: ((Dialog, Boolean, String) -> Unit)): Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_scan_choice)
        val width = (context.resources.displayMetrics.widthPixels * 0.85).toInt()
        val height = (context.resources.displayMetrics.heightPixels * 0.85).toInt()
        var defaultHeight = this.window?.attributes
            defaultHeight?.height?.toInt()?.let { this.window?.setLayout(width, it) }

        setupView()
    }


    private fun setupView() {


        this.button_join.setOnClickListener {
            callback(this, true, "join")
        }

        this.setOnDismissListener {
            callback(this, false, "")
        }

        this.button_sub.setOnClickListener {
            callback(this, true, "sub")
        }
        this.button_cancel_close_choice_gardener.setOnClickListener {
            callback(this, false, "")
        }
    }
}