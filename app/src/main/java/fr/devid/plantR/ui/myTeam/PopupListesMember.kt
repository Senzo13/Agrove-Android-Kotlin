package fr.devid.plantR.ui.myTeam

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import fr.devid.plantR.R
import fr.devid.plantR.models.SubscribeMember
import fr.devid.plantR.models.User
import fr.devid.plantR.services.FirebaseService
import kotlinx.android.synthetic.main.dialog_subscribe_adapter.*

class PopupListesMember(var guid : String, context: Context, private val callback: ((Dialog, Boolean, String, Int) -> Unit)): Dialog(context) {

private lateinit var adapter : AccessGardenerAdapter
    private lateinit var subscribeRef : DatabaseReference
    private lateinit var handleSubscribe : ValueEventListener
    private lateinit var userDataRef : DatabaseReference
    private lateinit var handleUsefData : ValueEventListener
    lateinit var listesMembre : List<SubscribeMember>
    var listsSubscribe : ArrayList<SubscribeMember> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_subscribe_adapter)
        val width = (context.resources.displayMetrics.widthPixels * 0.95).toInt()
        val height = (context.resources.displayMetrics.heightPixels * 0.95).toInt()
        val defaultHeight = this.window?.attributes
        defaultHeight?.height?.toInt()?.let { this.window?.setLayout(width, it) }
        setupView()
    }





    private fun setupView() {


        }

    private fun removeObserver() {
        if (::userDataRef.isInitialized &&::handleUsefData.isInitialized) {
            userDataRef.removeEventListener(handleUsefData)
        }
        if (::subscribeRef.isInitialized && ::handleSubscribe.isInitialized) {
            subscribeRef.removeEventListener(handleSubscribe)
        }
    }

    override fun onStop() {
        super.onStop()
        removeObserver()
    }
}