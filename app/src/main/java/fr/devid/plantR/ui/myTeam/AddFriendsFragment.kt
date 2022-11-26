package fr.devid.plantR.ui.myTeam

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import fr.devid.plantR.databinding.FragmentAddFriendsBinding

/**
 * A simple [Fragment] subclass.
 */
class AddFriendsFragment : Fragment() {
private lateinit var binding : FragmentAddFriendsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddFriendsBinding.inflate(inflater, container, false)
        initView()
        return binding.root
    }

    private fun initView() {
        binding.ivBack.setOnClickListener {
                findNavController().popBackStack()
        }
    }

}
