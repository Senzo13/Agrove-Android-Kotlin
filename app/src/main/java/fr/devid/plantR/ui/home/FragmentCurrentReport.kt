package fr.devid.plantR.ui.home

import android.os.Bundle
import android.view.*
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import fr.devid.plantR.base.BaseFragment
import fr.devid.plantR.databinding.FragmentCurrentBugBinding
import fr.devid.plantR.models.Ticket
import fr.devid.plantR.services.FirebaseService
import fr.devid.plantR.ui.register.RegisterViewModel
import javax.inject.Inject

/**
 * lG
 * **/

class FragmentCurrentReport(var keyFragment : String) : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val profilViewModel: ProfileViewModel by activityViewModels { viewModelFactory }
    private val registerViewModel: RegisterViewModel by activityViewModels { viewModelFactory }
    private val PAGE = "***** FragmentOne *****"
    private val fragment = keyFragment
    private lateinit var binding: FragmentCurrentBugBinding
    private lateinit var adapter: ReportAdapter
    private lateinit var reportRef: DatabaseReference
    private lateinit var handleReport: ValueEventListener
    private lateinit var reportTicket: DatabaseReference
    private lateinit var handleTicket: ValueEventListener


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCurrentBugBinding.inflate(inflater, container, false)
        adapter = ReportAdapter()
        adapter.currentEtage = fragment //J'envoie mon numéro du fragment, et ma gardenerId
        binding.rvCurrentBug.adapter = adapter
        activity?.let { act ->
            binding.rvCurrentBug.layoutManager = LinearLayoutManager(act, LinearLayoutManager.VERTICAL, false)
        }
        initView()
        return binding.root
    }

    override fun onStop() {
        super.onStop()
        removeListener()
    }

    private fun initView() {
        setRapportObserver()
    }

    private fun removeListener() {

    }

    override fun onResume() {
        super.onResume()

    }

    private fun setRapportObserver() {
        FirebaseService().firebase.currentUser?.uid?.let { uid ->
            retainInstance = true
            reportRef = FirebaseService().getRapport()
            handleReport = reportRef.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    println("${PAGE} onCancelled ${error.toException()}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val rapports = snapshot.getValue<HashMap<String, Ticket>>()?.let { rp ->
                         rp.forEach {
                             if(it.key != "owners") {
                                 setTicketObserver(it.key)
                                 println("LE USER ID : " + it.key)
                         }
                        }
                    }
                }
            })
        }
    }

    private fun setTicketObserver(userId: String) {
            reportTicket = FirebaseService().getRapportByUserId(userId)
            handleTicket = reportTicket.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    println("${PAGE} onCancelled ${error.toException()}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val rapports = snapshot.getValue<HashMap<String, Ticket>>()?.let { ticket ->
                        val tickets: ArrayList<RapportModel> = arrayListOf()
                        //println("ici le ticket : " + ticket.keys.first())
                        ticket.values.forEach {
                            tickets.add(RapportModel(userId, it))
                        }
                      adapter.submitList(tickets.toList())
                    }
                }
            })

    }
}