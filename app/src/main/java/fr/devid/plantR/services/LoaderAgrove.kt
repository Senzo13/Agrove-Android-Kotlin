package fr.devid.plantR.services

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.LinearLayout
import fr.devid.plantR.R

class LoaderAgrove(context : Context) : Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_loader)
        window?.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT)
        window?.setBackgroundDrawableResource(R.color.agrove_transparent)


    }
}