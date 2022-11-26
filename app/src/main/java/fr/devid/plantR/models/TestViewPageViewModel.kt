package fr.devid.plantR.models

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModel
import fr.devid.plantR.R

class TestViewPageViewModel: ViewModel() {
    internal val nextPageEvent = SingleLiveEvent<Int>()
}