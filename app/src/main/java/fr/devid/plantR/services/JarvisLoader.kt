package fr.devid.plantR.services

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.LinearLayout
import fr.devid.plantR.R

class JarvisLoader(context : Context) : Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_loader_jarvis)
        window?.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT)
        window?.setBackgroundDrawableResource(R.color.fui_transparent)
    }
}