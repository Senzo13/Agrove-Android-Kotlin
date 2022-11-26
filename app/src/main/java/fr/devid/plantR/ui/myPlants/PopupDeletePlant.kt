package fr.devid.plantR.ui.myPlants

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.google.firebase.storage.StorageReference
import fr.devid.plantR.R
import kotlinx.android.synthetic.main.dialog_delete_plant.*
import kotlinx.android.synthetic.main.dialog_delete_plant.button_cancel
import kotlinx.android.synthetic.main.dialog_delete_plant.button_validate_plant

class PopupDeletePlant(var name : String, var gardenerId : String, context: Context, private val callback: ((Dialog, Boolean, String) -> Unit)): Dialog(context) {

    private lateinit var storageRef : StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_delete_plant)
        val width = (context.resources.displayMetrics.widthPixels * 0.82).toInt()
        val height = (context.resources.displayMetrics.heightPixels * 0.82).toInt()
        var defaultHeight = this.window?.attributes
            defaultHeight?.height?.toInt()?.let { this.window?.setLayout(width, it) }

        setupView()
    }


    private fun setupView() {

        this.tv_popup_middle_body.text = context.getString(R.string.delete_plant)
        this.button_validate_plant.setOnClickListener {
            callback(this, true, "")
        }

        this.setOnDismissListener {
            callback(this, false, "")
        }

        this.button_cancel.setOnClickListener {
            callback(this, false, "")
        }

    }
}