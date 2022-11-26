package fr.devid.plantR.ui.myTeam

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import fr.devid.plantR.base.BaseFragment
import fr.devid.plantR.databinding.DialogSubscribeAdapterBinding
import fr.devid.plantR.models.Metadata
import fr.devid.plantR.models.SubscribeMember
import fr.devid.plantR.models.User
import fr.devid.plantR.services.FirebaseService
import fr.devid.plantR.ui.home.ProfileViewModel
import timber.log.Timber
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class FragmentTwoTeam : BaseFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val profilViewModel: ProfileViewModel by activityViewModels { viewModelFactory }
    private lateinit var binding: DialogSubscribeAdapterBinding
    private lateinit var adapter: AccessGardenerAdapter
    private lateinit var subscribeRef: DatabaseReference
    private lateinit var handleSubscribe: ValueEventListener
    private lateinit var metadataRef: DatabaseReference
    private lateinit var handleMetadataRef: ValueEventListener
    private lateinit var listesMembre: List<SubscribeMember>
    private lateinit var listesMembreSubscribe: List<String>

    var listsSubscribe: ArrayList<SubscribeMember> = arrayListOf()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogSubscribeAdapterBinding.inflate(inflater, container, false)


        listesMembre = emptyList()


        println("FragmentTwo oue")
        adapter = AccessGardenerAdapter()
        binding.rvSubscribe.adapter = adapter
        binding.rvSubscribe.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        setAdapter()
        return binding.root
    }

    fun getMetadataUser(uid : String, guid : String) {

        metadataRef = FirebaseService().getUserByid(uid)
        metadataRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val metadata = snapshot.getValue(User::class.java)
                if (metadata != null) {
                    println("metadata")
                    val arrayTest = ArrayList<SubscribeMember>()
                    arrayTest.add(SubscribeMember(uid, metadata.Metadata, guid))
                    adapter.submitList(arrayTest.toList())
                    binding.ivEmptyTeam.visibility = View.GONE
                }
            }
        })
    }

    private fun setAdapter() {
        profilViewModel.userService.gardenerId?.let { guid ->
            subscribeRef = FirebaseService().getSubscribeMember(guid)
            handleSubscribe = subscribeRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val members = snapshot.getValue<java.util.HashMap<String, Boolean>>()
                    println("MEMBERS : " + members)
                    if (members != null) {
                        if(members.keys.count() > 0) {
                            println("Members : " + members)
                            listesMembreSubscribe = members.keys.toList()
                            members.keys.forEach { user ->
                                getMetadataUser(user, guid)
                            }
                        }
                    } else {
                        Timber.e("Member est null en entier")
                        binding.ivEmptyTeam.visibility = View.VISIBLE
                        adapter.submitList(emptyList())
                    }



                }
                override fun onCancelled(error: DatabaseError) {

                }
            })
        }
    }

    override fun onStop() {
        super.onStop()
        removeListener()
    }

    private fun removeListener() {
        if(this::subscribeRef.isInitialized && this::handleSubscribe.isInitialized) {
            subscribeRef.removeEventListener(handleSubscribe)
        }
    }

}
