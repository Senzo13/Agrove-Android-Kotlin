package fr.devid.plantR.ui.myTeam

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import fr.devid.plantR.R
import kotlinx.android.synthetic.main.dialog_delete_team_user.*

class PopupDeleteTeamUser(var name: String, context: Context, private val callback: ((Dialog, Boolean, String) -> Unit)): Dialog(context) {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_delete_team_user)
        setupView()
    }


    @SuppressLint("SetTextI18n")
    private fun setupView() {
        this.tv_dialog.text = "${context.getString(R.string.CONFIRM_ONE)} \n$name ${context.getString(R.string.CONFIRM_TWO)}"

        this.button_delete.setOnClickListener {
            callback(this, true, "")
        }

        this.setOnDismissListener {
            callback(this, false, "")
        }

        this.button_close.setOnClickListener {
            callback(this, false, "")
        }

    }
}