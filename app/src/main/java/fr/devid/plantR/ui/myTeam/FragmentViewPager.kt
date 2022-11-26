package fr.devid.plantR.ui.myTeam

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import fr.devid.plantR.R
import fr.devid.plantR.base.BaseFragment
import fr.devid.plantR.databinding.FragmentManageTeamBinding
import fr.devid.plantR.manager.PagerManager
import javax.inject.Inject

class FragmentViewPager : BaseFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var binding: FragmentManageTeamBinding
    lateinit var plantRef : DatabaseReference
    lateinit var handlePlant : ValueEventListener

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentManageTeamBinding.inflate(inflater, container, false)
        initView()
        return binding.root
    }

    private fun initView() {

    }

    override fun onResume() {
        super.onResume()
        println("COUSCOUS TAJINE")

    }

}