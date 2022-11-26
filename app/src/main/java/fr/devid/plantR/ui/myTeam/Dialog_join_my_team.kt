package fr.devid.plantR.ui.myTeam

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.google.firebase.storage.StorageReference
import fr.devid.plantR.R
import kotlinx.android.synthetic.main.dialog_delete_plant.*
import kotlinx.android.synthetic.main.dialog_en_savoir_plus_join_my_team.*
import kotlinx.android.synthetic.main.dialog_notif_plantr.*
import kotlinx.android.synthetic.main.dialog_scan_choice.*
import kotlinx.android.synthetic.main.dialog_state.*
import kotlinx.android.synthetic.main.fragment_manage_team.*

class Dialog_join_my_team(context: Context, private val callback: ((Dialog, Boolean, String) -> Unit)): Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_en_savoir_plus_join_my_team)
        val width = (context.resources.displayMetrics.widthPixels * 0.85).toInt()
        val height = (context.resources.displayMetrics.heightPixels * 0.85).toInt()
        val defaultHeight = this.window?.attributes
            defaultHeight?.height?.toInt()?.let { this.window?.setLayout(width, it) }

        setupView()
    }


    private fun setupView() {

        this.setOnDismissListener {
            callback(this, false, "")
        }

        this.button_cancel_join_my_team.setOnClickListener {
            callback(this, false, "fermer")
        }
    }
}