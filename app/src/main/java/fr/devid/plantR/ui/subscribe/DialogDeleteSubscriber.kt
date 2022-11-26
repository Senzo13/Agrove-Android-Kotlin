package fr.devid.plantR.ui.subscribe

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import fr.devid.plantR.R
import kotlinx.android.synthetic.main.dialog_change_password.*
import kotlinx.android.synthetic.main.dialog_delete_team_subscriber.*


class DialogDeleteSubscriber(context: Context, private val callback: ((Dialog, Boolean, String) -> Unit)): Dialog(context) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_delete_team_subscriber)
        setupView()
        val width = (context.resources.displayMetrics.widthPixels * 0.90).toInt() //It allows to have a bigger popup
        var defaultHeight = this.window?.attributes
        defaultHeight?.height?.toInt()?.let { this.window?.setLayout(width, it) }
    }

    private fun setupView() {


        this.setOnDismissListener {
            callback(this, false, "")
        }

        this.button_delete.setOnClickListener {
          callback(this,true,"")
        }
        this.button_close.setOnClickListener {
            callback(this, false, "")
        }


    }
}