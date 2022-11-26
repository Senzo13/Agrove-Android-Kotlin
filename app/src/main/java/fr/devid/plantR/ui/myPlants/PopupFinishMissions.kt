package fr.devid.plantR.ui.myPlants

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import fr.devid.plantR.R
import kotlinx.android.synthetic.main.dialog_missions_confirmation.*

class PopupFinishMissions(val taskName : String, context: Context, private val callback: ((Dialog, Boolean, String) -> Unit)): Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_missions_confirmation)
        val width = (context.resources.displayMetrics.widthPixels * 0.85).toInt()
        val height = (context.resources.displayMetrics.heightPixels * 0.85).toInt()
        var defaultHeight = this.window?.attributes
            defaultHeight?.height?.toInt()?.let { this.window?.setLayout(width, it) }

        setupView()
    }


    @SuppressLint("SetTextI18n")
    private fun setupView() {

        this.tv_confirm_missions.text = "${context.getString(R.string.ARE_U_FINISH_TASK)} \"$taskName\" ?"

        this.button_validate_task.setOnClickListener {
            callback(this, true, "")
        }

        this.setOnDismissListener {
            callback(this, false, "")
        }

        this.button_cancel_task.setOnClickListener {
            callback(this, false, "")
        }

    }
}