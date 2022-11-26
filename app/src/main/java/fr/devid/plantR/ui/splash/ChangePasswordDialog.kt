package fr.devid.plantR.ui.splash

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import fr.devid.plantR.R
import kotlinx.android.synthetic.main.dialog_change_password.*


class ChangePasswordDialog(context: Context, private val callback: ((Dialog, Boolean, String) -> Unit)): Dialog(context) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_change_password)
        setupView()
        val width = (context.resources.displayMetrics.widthPixels * 0.85).toInt()
        var defaultHeight = this.window?.attributes
        defaultHeight?.height?.toInt()?.let { this.window?.setLayout(width, it) }
    }

    private fun setupView() {


        this.setOnDismissListener {
            callback(this, false, "")
        }

        this.button_cancel.setOnClickListener {
            callback(this, false, "")
        }

        this.iv_button_cancel_change_pwd.setOnClickListener {
            callback(this, false, "")
        }

        this.button_validate.setOnClickListener {
            if (this.et_email.text.toString().isNotEmpty()) {
                if (this.et_email.text.toString().length < 8) {
                    callback(this, false, context.getString(R.string.EIGHT_LETTERS))
                } else {
                        callback(this, true, this.et_email.text.toString().trimEnd())
                }
            } else {
                callback(this, false, context.getString(R.string.FILL_EMAIL))
            }
        }



    }
}