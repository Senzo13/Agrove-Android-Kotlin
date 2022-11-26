package fr.devid.plantR.ui.home

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.storage.StorageReference
import fr.devid.plantR.R
import fr.devid.plantR.models.SortOfPlant
import fr.devid.plantR.services.FirebaseService
import fr.devid.plantR.services.StringRef
import kotlinx.android.synthetic.main.dialog_add_gardener.*
import kotlinx.android.synthetic.main.popup_add_plant.*

class PopupScanGardener(var nameOfVegatableGardener : String, context: Context, private val callback: ((Dialog, Boolean, String) -> Unit)): Dialog(context) {


    private lateinit var storageRef : StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_add_gardener)
        setupView()
        val width = (context.resources.displayMetrics.widthPixels * 0.80).toInt()
        var defaultHeight = this.window?.attributes
        defaultHeight?.height?.toInt()?.let { this.window?.setLayout(width, it) }
    }


    private fun setupView() {

        this.tv_description_scan.text = nameOfVegatableGardener

        this.button_scan.setOnClickListener {
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