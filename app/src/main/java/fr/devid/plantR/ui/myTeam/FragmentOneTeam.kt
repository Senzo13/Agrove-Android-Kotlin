package fr.devid.plantR.ui.myTeam

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import fr.devid.plantR.R
import fr.devid.plantR.base.BaseFragment
import fr.devid.plantR.databinding.FragmentAddFriendsBinding
import fr.devid.plantR.databinding.FragmentMemberTeamBinding
import fr.devid.plantR.models.User
import fr.devid.plantR.services.AlerterService
import fr.devid.plantR.services.FirebaseService
import fr.devid.plantR.ui.home.ProfileViewModel
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class FragmentOneTeam : BaseFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val profilViewModel: ProfileViewModel by activityViewModels { viewModelFactory }
    private lateinit var gardenerRef: DatabaseReference
    private lateinit var handleGardener: ValueEventListener
    private lateinit var adapter: ManageTeamAdapter
    private lateinit var binding : FragmentMemberTeamBinding
    private lateinit var userRef : DatabaseReference
    private lateinit var handleUserRef : ValueEventListener
    private lateinit var teamRef: DatabaseReference
    private val PAGE: String = "****** FragmentManageTeam ******"
    lateinit var userName : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMemberTeamBinding.inflate(inflater, container, false)
        initView()
        adapter = ManageTeamAdapter { uid, position ->
             userRef = FirebaseService().getUserByid(uid)
             userRef.addListenerForSingleValueEvent(object:ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)
                    if (user != null) {
                        userName = user.Metadata.lastName + " " + user.Metadata.firstName
                        if(::userName.isInitialized) {
                            val popupAddGardener = PopupDeleteTeamUser(userName, requireContext()) { popup, bool, str ->
                                if (bool) {
                                    popup.dismiss()
                                    activity?.let { act ->
                                        profilViewModel.userService.gardenerId?.let { guid ->
                                            FirebaseService().getGardenerOwnersById(guid).child(uid).removeValue().addOnSuccessListener {
                                                AlerterService.showGood("$userName ${context?.getString(R.string.SUCCESS_DELETE)}", act)
                                            }
                                        }
                                    }
                                } else {
                                    popup.dismiss()
                                }

                            }
                            popupAddGardener.show()
                        }
                    }
                }
            })


        }
        binding.rvManageTeam.adapter = adapter
        binding.rvManageTeam.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        return binding.root
    }

    private fun initView() {
        println("TEST OUAIIIS OUAIIIS")
        profilViewModel.userService.gardenerId?.let { guid ->
           checkGardenersOwnersObserver(guid)
        }
    }

    private fun checkGardenersOwnersObserver(guid: String) {
        FirebaseService().firebase.currentUser?.uid.let { myUID ->
            gardenerRef = FirebaseService().getGardenerOwnersById(guid)
            handleGardener = gardenerRef.addValueEventListener(object : ValueEventListener {

                override fun onCancelled(error: DatabaseError) {
                    println("${PAGE} onCancelled method -> ${error.toException()}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val owners = snapshot.getValue<HashMap<String, Boolean>>()
                    if (owners != null) {
                        binding.ivEmptyTeam.visibility = View.GONE
                        if(owners.count() == 1) {
                            binding.ivEmptyTeam.visibility = View.VISIBLE
                        }
                        val teamList = owners.keys.filter { key -> key != myUID }.toList()
                        activity.let {
                            adapter.submitList(teamList) { adapter.notifyDataSetChanged()
                                hideLoading()
                            }
                            val nbMember = owners.keys.count() - 1
                            binding.tvNbFriends.text = "(${nbMember})"
                        }
                    } else {
                        binding.ivEmptyTeam.visibility = View.VISIBLE
                        hideLoading()
                    }
                }
            })
        }
    }

    override fun onStop() {
        super.onStop()
        removeObserver()
    }

    private fun removeObserver() {
        if(::gardenerRef.isInitialized && ::handleGardener.isInitialized) {
            gardenerRef.removeEventListener(handleGardener)
        }
    }

    private fun hideLoading() {
        binding.clTeam.visibility = View.VISIBLE
        binding.pbLoading.visibility = View.GONE
    }

}
