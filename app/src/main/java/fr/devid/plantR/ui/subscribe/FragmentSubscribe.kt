package fr.devid.plantR.ui.subscribe

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.storage.StorageReference
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import fr.devid.plantR.R
import fr.devid.plantR.base.BaseFragment
import fr.devid.plantR.databinding.FragmentMyGardenersSubscribeBinding
import fr.devid.plantR.models.GardenerMetadata
import fr.devid.plantR.models.UserGardenerGuest
import fr.devid.plantR.services.AlerterService
import fr.devid.plantR.services.FirebaseService
import fr.devid.plantR.ui.home.PopupScanGardener
import fr.devid.plantR.ui.home.ProfileViewModel
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

class FragmentSubscribe : BaseFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val profilViewModel: ProfileViewModel by activityViewModels { viewModelFactory }
    private val PAGE: String = "****** FragmentManageTeam ******"
    private lateinit var userGardenersGuestRef: DatabaseReference
    private lateinit var gardenerRef : DatabaseReference
    private lateinit var handleUserGardenerRef : ValueEventListener
    private lateinit var binding: FragmentMyGardenersSubscribeBinding
    private lateinit var adapter : SubscribeAdapter
    private lateinit var storageRef : StorageReference
    private lateinit var listGardenersGuest : List<String>
    private lateinit var gardenerOwnerRef : DatabaseReference
    private lateinit var handleGardenerOwnerRef : ValueEventListener
    private lateinit var gardenerGuestRef : DatabaseReference
    private var imagesHandler: Handler? = null

    val runnableImages = object : Runnable {
        override fun run() {
            setGardenerObserver(listGardenersGuest)
            imagesHandler?.postDelayed(this, 3000)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyGardenersSubscribeBinding.inflate(inflater, container, false)
        initView()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        setObserver()
    }

    private fun initView() {
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.addGardenersGuest.setOnClickListener {
            val description = getString(R.string.tv_title_public_add_garden)
            val popupAddGardener = PopupScanGardener(description, requireContext()) { popup, bool, str ->
                if (bool) {
                    popup.dismiss()
                    qrCode()
                } else {
                    popup.dismiss()
                }
            }
            popupAddGardener.show()
        }
    }

    private  fun setGardenerObserver(myList: List<String>) {
        if(!myList.isNullOrEmpty()) {
            val choiceId = myList.random()
            println("MON CHOIX DE ID ALEATOIRE : " + choiceId)
            binding.clGardenersName.visibility = View.VISIBLE
            gardenerRef = FirebaseService().getGardenerMetadataById(choiceId)
            gardenerRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val gardenersData = snapshot.getValue(GardenerMetadata::class.java)
                    if (gardenersData != null) {
                        binding.tvGardenerName.setText(gardenersData.name)
                        if (!gardenersData.images.isNullOrEmpty()) {
                            val profileAndBackGround = gardenersData.images.keys.random()
                            println("MA KEY DE GARDENERS " + gardenersData.images.keys.random())
                            setStoragePictureBackgroundGardenersObserver(binding.ivGardenersGuestPicture, choiceId, profileAndBackGround)
                            if (gardenersData.images.keys.contains("logo")) {
                                setStoragePictureGardenersObserver(
                                    binding.ivProfilePicture,
                                    choiceId,
                                    "logo"
                                )
                                setStoragePictureBackgroundGardenersObserver(
                                    binding.ivGardenersGuestPicture,
                                    choiceId,
                                    "logo"
                                )
                            } else {
                                setStoragePictureGardenersObserver(
                                    binding.ivProfilePicture,
                                    choiceId,
                                    gardenersData.images.keys.first().toString()
                                )
                            }
                        } else {
                            setStoragePictureBackgroundGardenersObserver(
                                binding.ivGardenersGuestPicture,
                                choiceId,
                                "null"
                            )
                            setStoragePictureGardenersObserver(
                                binding.ivProfilePicture,
                                choiceId,
                                "null"
                            )
                        }
                    } else {
//                        binding.tvGardenerName.setText("Jardinière sans nom")
//                        setStoragePictureGardenersObserver(
//                            binding.ivProfilePicture,
//                            choiceId,
//                            "null"
//                        )
                        adapter.submitList(emptyList())
                    }
                    hideLoading()
                }
            })
        } else {
            adapter.submitList(emptyList())
            binding.clGardenersName.visibility = View.INVISIBLE
            Glide.with(binding.ivGardenersGuestPicture.context)
                .load(R.drawable.mes_abonnements)
                .into(binding.ivGardenersGuestPicture)
        }
    }

    fun setStoragePictureBackgroundGardenersObserver(
        gardenersPicture: ImageView,
        guid: String?,
        images: String
    ) {
        if(guid != null) {
        if(images != "null" && images != "logo") {
            storageRef = FirebaseService().getStorageGardenerPicture(guid, images + ".jpg")
            storageRef.downloadUrl.addOnSuccessListener { data ->
                Glide.with(gardenersPicture.context)
                    .load(data.toString())
                    .into(gardenersPicture)
            }
            storageRef.downloadUrl.addOnFailureListener {
                Glide.with(gardenersPicture.context)
                    .load(R.drawable.mes_abonnements)
                    .into(gardenersPicture)
            }
        } else {
            Glide.with(gardenersPicture.context)
                .load(R.drawable.mes_abonnements)
                .into(gardenersPicture)
        }
        } else {
            Glide.with(gardenersPicture.context)
                .load(R.drawable.mes_abonnements)
                .into(gardenersPicture)
        }

    }
    fun setStoragePictureGardenersObserver(
        gardenersPicture: ImageView,
        guid: String,
        images: String
    ) {

        if(images != "null") {
            storageRef = FirebaseService().getStorageGardenerPicture(guid, images + ".jpg")
            storageRef.downloadUrl.addOnSuccessListener { data ->
                Glide.with(gardenersPicture.context)
                    .load(data.toString())
                    .circleCrop()
                    .into(gardenersPicture)
            }
            storageRef.downloadUrl.addOnFailureListener {
                Glide.with(gardenersPicture.context)
                    .load(R.mipmap.ic_launcher)
                    .circleCrop()
                    .into(gardenersPicture)
            }
        } else {
            Glide.with(gardenersPicture.context)
                .load(R.mipmap.ic_launcher)
                .circleCrop()
                .into(gardenersPicture)
        }
    }
    private fun setObserver() {
        FirebaseService().firebase.currentUser?.uid?.let { uid ->
            setAdapter(uid)
            userGardenersGuestRef = FirebaseService().getGardenersGuestUserByid(uid)
            handleUserGardenerRef =
                userGardenersGuestRef.addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        println("***" + error.toException() + "*****")
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        println("************************")
                        val gardenersGuestList = snapshot.getValue<HashMap<String, Boolean>>()
                        listGardenersGuest = emptyList()
                        if (gardenersGuestList != null) {
                            listGardenersGuest = gardenersGuestList.keys.toList()
                            imagesHandler = Handler(Looper.getMainLooper())
                            imagesHandler?.post(runnableImages)
                            println("UPDATE " + listGardenersGuest)
                            adapter.submitList(listGardenersGuest)
                        } else {
                            adapter.submitList(emptyList())
                        }
                        if (listGardenersGuest.isEmpty()) {
                            binding.clGardenersName.visibility = View.GONE
                        }
                    }
                })
        }
    }

    private fun setAdapter(uid: String) {
        adapter = SubscribeAdapter(
            { guid, gardenerName, gardenerType, gardenerRangs, gardenerStage ->
                findNavController().navigate(FragmentSubscribeDirections.actionFragmentSubscribeToMesPlantesSubscribeFragment(guid, gardenerName, gardenerType, gardenerRangs?:-1, gardenerStage))
            }, { id, gardenerId, position ->
            val dialogDeleteSubscriber = DialogDeleteSubscriber(requireContext()) { popup, bool, str ->
                if (bool) {
                    popup.dismiss()
                    FirebaseService().getUserByid(uid).child("gardenersGuest").child(gardenerId)
                            .removeValue()
                            .addOnSuccessListener {
                                    binding.clGardenersName.visibility = View.VISIBLE
                                    activity?.let {
                                        adapter.notifyItemRemoved(position)
                                        adapter.notifyDataSetChanged()
                                        AlerterService.showGood("${context?.getString(R.string.SUBSCRIBE_ERROR5)}", it)

                                }
                            }.addOnFailureListener {
                                activity?.let {
                                    AlerterService.showError(
                                            "${context?.getString(R.string.SUBSCRIBE_ERROR6)}",
                                            it
                                    )
                                }
                            }
                } else {
                    popup.dismiss()
                }
            }
            dialogDeleteSubscriber.show()

            })
            binding.recyclerView.adapter = adapter
            activity?.let {
                binding.recyclerView.layoutManager =
                    LinearLayoutManager(it, LinearLayoutManager.HORIZONTAL, false)
            }
    }

    private fun hideLoading() {
        binding.tvGardenerName.visibility = View.VISIBLE
        binding.pbLoadingSpinner.visibility = View.GONE
    }

    override fun onStop() {
        super.onStop()
        removeListener()
        removeTimer()
    }
    private fun removeTimer() {
        imagesHandler?.removeCallbacks(runnableImages)
        imagesHandler = null
    }

    private fun removeListener() {
        if(::userGardenersGuestRef.isInitialized && ::handleUserGardenerRef.isInitialized)
            userGardenersGuestRef.removeEventListener(handleUserGardenerRef)
        if(::gardenerOwnerRef.isInitialized && :: handleGardenerOwnerRef.isInitialized)
            gardenerOwnerRef.removeEventListener(handleGardenerOwnerRef)
    }

    private fun qrCode() {
        IntentIntegrator
            .forSupportFragment(this)
            .setPrompt("${context?.getString(R.string.SCAN_QRCODE)}")
            .setBeepEnabled(false)
            .setBarcodeImageEnabled(false)
            .setOrientationLocked(false)
            .initiateScan()
    }


    private fun setSubscribeGardener(guid: String, uid: String) {
        gardenerGuestRef = FirebaseService().getUserByid(uid)
        gardenerGuestRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val guestGardener = snapshot.getValue(UserGardenerGuest::class.java)
                if (guestGardener != null) {
                    val guestAlreadyExist =
                        guestGardener.gardenersGuest.filter { data -> data.key == guid }
                    if (guestAlreadyExist.count() >= 1) {
                        activity?.let {
                            AlerterService.showError(
                                "${context?.getString(R.string.ALREADY_SUBSCRIBE)}",
                                it
                            )
                        }
                    } else {
                        activity?.let { act ->

                            println("Vous allez vous abonner à cette jardinière")
                            FirebaseService().firebase.currentUser?.uid?.let { uid ->
                                FirebaseService().getUserByid(uid).child("gardenersGuest")
                                    .child(guid).setValue(true).addOnSuccessListener {
                                        AlerterService.showGood(
                                            "${context?.getString(R.string.SUBSCRIBE_ERROR3)}",
                                            act
                                        )
                                    }.addOnFailureListener {
                                        AlerterService.showError(
                                            "${context?.getString(R.string.SUBSCRIBE_ERROR4)}",
                                            act
                                        )
                                    }
                            }
                        }
                    }
                }
            }

        })
    }

    private fun checkMyGardeners(guid: String, etage: String) {

        FirebaseService().firebase.currentUser?.uid?.let { uid ->
            gardenerOwnerRef = FirebaseService().getGardenerOwnersById(guid)
            handleGardenerOwnerRef =
                gardenerOwnerRef.addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        println("OnCancelled" + error.toException())
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {

                        val owners = snapshot.getValue<HashMap<String, Boolean>>()
                        if (owners != null) {
                            if (owners.keys.contains(uid)) {
                                activity?.let { act ->
                                    AlerterService.showError(
                                        "${context?.getString(R.string.SUBSCRIBE_ERROR1)}",
                                        act
                                    )
                                }
                            } else {
                                setSubscribeGardener(guid, uid)

                            }
                        } else {
                            activity?.let {
                                AlerterService.showError(
                                    "${context?.getString(R.string.SUBSCRIBE_ERROR2)}",
                                    it
                                )
                            }
                        }

                    }
                })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val result: IntentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (result.contents == null) {
                activity?.let {
                    AlerterService.showError("${context?.getString(R.string.QRCODE_ISEMPTY)}", it)
                }
            } else {
                try {
                    val answer = JSONObject(result.contents)
                    println(
                        "\nqrCode:\netage : "
                                + "\n_id : "
                                + answer.get("_id")
                    )
                    val guid = answer.get("_id")
                    var stage = ""
                    var etage = ""
                    try {

                        answer.get("stage").let {
                            stage = it.toString()
                        }

                    } catch (err : JSONException) {
                        println("PROBLEME DANS STAGE DU QR CODE")
                    }

                    try {
                        answer.get("etage").let {
                            etage = it.toString()
                        }
                    } catch (err : JSONException) {
                        println("PROBLEME DANS ETAGE DU QR CODE")
                    }

                    if (etage.isNotEmpty() || stage.isNotEmpty()) {
                        if(etage.isNotEmpty()) {
                            checkMyGardeners(guid.toString(), etage.toString())
                        }
                        if(stage.isNotEmpty()) {
                            checkMyGardeners(guid.toString(), stage.toString())
                        }
                    } else {
                        print("Il y a une erreur dans la lecture du QR Code")
                    }
                } catch (e: java.lang.Exception) {
                    activity?.let { act ->
                        println("une erreur est survenu " + e)
                        AlerterService.showError("${context?.getString(R.string.QRCODE)}" + e, act)
                    }
                }
            }
        }
    }
}