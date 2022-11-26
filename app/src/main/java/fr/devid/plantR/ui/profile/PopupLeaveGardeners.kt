package fr.devid.plantR.ui.profile

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import fr.devid.plantR.R
import kotlinx.android.synthetic.main.dialog_leave_planter.*

class PopupLeaveGardeners(private val gardenersName :  String, context: Context, private val callback: ((Dialog, Boolean, String) -> Unit)): Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_leave_planter)
        val width = (context.resources.displayMetrics.widthPixels * 0.85).toInt()
        val height = (context.resources.displayMetrics.heightPixels * 0.85).toInt()
        var defaultHeight = this.window?.attributes
            defaultHeight?.height?.toInt()?.let { this.window?.setLayout(width, it) }
        setupView()
    }


    private fun setupView() {


        this.tv_popup_modal_middle_leave_gardeners.text = gardenersName
        this.tv_confirm_leave_it.setOnClickListener {
            callback(this, true, "confirm")
        }

        this.setOnDismissListener {
            callback(this, false, "")
        }

        this.button_cancel_leave_gardeners.setOnClickListener {
            callback(this, false, "")
        }
    }
}