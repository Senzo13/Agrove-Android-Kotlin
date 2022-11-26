package fr.devid.plantR.ui.myTeam

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import fr.devid.plantR.R
import fr.devid.plantR.base.BaseFragment
import fr.devid.plantR.databinding.FragmentManageTeamBinding
import fr.devid.plantR.manager.PagerManager
import fr.devid.plantR.models.*
import fr.devid.plantR.services.AlerterService
import fr.devid.plantR.services.FirebaseService
import fr.devid.plantR.ui.home.ProfileViewModel
import fr.devid.plantR.ui.myPlants.PopupAddPlantSuccess
import io.branch.indexing.BranchUniversalObject
import io.branch.referral.Branch
import io.branch.referral.BranchError
import io.branch.referral.util.LinkProperties
import io.branch.referral.util.ShareSheetStyle
import okhttp3.internal.wait
import javax.inject.Inject

class FragmentManageTeam : BaseFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val profilViewModel: ProfileViewModel by activityViewModels { viewModelFactory }
    private val PAGE: String = "****** FragmentManageTeam ******"
    private lateinit var binding: FragmentManageTeamBinding
    private lateinit var gardenerRef: DatabaseReference
    private lateinit var handleGardener: ValueEventListener
    private lateinit var gardenerStageRef: DatabaseReference
    private lateinit var handleGardenerStageRef: ValueEventListener
    private lateinit var handleUserStageRef: ValueEventListener
    private lateinit var userStageRef: DatabaseReference
    private lateinit var userRef: DatabaseReference
    private lateinit var handleUserRef: ValueEventListener
    private lateinit var currentGardener: String
    private lateinit var gardenerName: String
    private lateinit var gardenerId: String
    private lateinit var gardenerStage: String
    private val adapter by lazy { PagerManager(childFragmentManager) }
    private lateinit var isPublicValue: CheckIsPublic
    private lateinit var stateRef: DatabaseReference
    private lateinit var stateHandleRef: ValueEventListener
    private lateinit var subscribeRef: DatabaseReference
    private lateinit var handleSubscribe: ValueEventListener
    private lateinit var userSubscribeRef : DatabaseReference
    private lateinit var handleUserSubscribe : ValueEventListener

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentManageTeamBinding.inflate(inflater, container, false)
        setObserver()
        initView()
        publicObserver() //Observe la valeur de isPublic
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        setupViewPager()
        checkIsPage()

    }


    private fun checkIsPage() {
        if(binding.tlPlants.selectedTabPosition == 0) {
            println("Page mon équipe")
        }

        println("Chiffre connu : " + binding.tlPlants.selectedTabPosition)


    }

    private fun checkSubscribeMember(guid : String) {
        userSubscribeRef = FirebaseService().getSubscribeMember(guid)
        handleUserSubscribe = userSubscribeRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val listSubscribeMember = snapshot.getValue<HashMap<String, Boolean>>()
                if(listSubscribeMember != null) {
                    val subscribeCount = listSubscribeMember.keys.count()
                    if(subscribeCount > 0 && profilViewModel.userService.typeGarden != "pot" && profilViewModel.userService.typeGarden !="jardiniere" && profilViewModel.userService.typeGarden !="carre") {
                        binding.ivNotifsInvit.visibility = View.VISIBLE
                    }
                } else {
                    binding.ivNotifsInvit.visibility = View.GONE
                }

            }
        })
    }



    private fun setupViewPager() {
        adapter.resetAll()
        adapter.addFragmentPage(FragmentOneTeam(), getString(R.string.title_team))
        profilViewModel.userService.gardenerId?.let { guid ->
            if(guid.contains("Classic")){
                binding.ivReglageTeam.visibility = View.GONE
            } else {
                binding.ivReglageTeam.visibility = View.VISIBLE
                adapter.addFragmentPage(FragmentTwoTeam(), getString(R.string.title_demande))
            }
        }

        binding.tlPlants.setupWithViewPager(binding.vpTeam)
        binding.vpTeam.offscreenPageLimit = 1; //before setAdapter
        binding.vpTeam.adapter = adapter
        println("Numero : "+binding.tlPlants.selectedTabPosition)
    }

    private fun initView() {

        binding.clEnSavoirPlus.setOnClickListener {
            when(binding.tlPlants.selectedTabPosition) {
                0 -> {
                    val popup = Dialog_join_team(requireContext()) { dialog, bool, choice ->
                        if(choice == "fermer") {
                            dialog.dismiss()
                        }
                    }
                    popup.show()

                }
                1 -> {
                    val popup = Dialog_join_my_team(requireContext()) { dialog, bool, choice ->
                        if(choice == "fermer") {
                            dialog.dismiss()
                        }
                    }
                    popup.show()
                }
            }

        }

        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.addTeamUser.setOnClickListener {
            if (::currentGardener.isInitialized) {
                if (::gardenerStage.isInitialized) {
                    generateDeepLink(gardenerName, gardenerId, gardenerStage)
                }
            }
        }

        binding.ivReglageTeam.setOnClickListener { //CA ON TOUCHE PAS CA RESTE TJR LA
            //Je déclare stateRef pour qu'il soit set avant l'appel de la function
            profilViewModel.userService.gardenerId?.let { guid ->
                stateRef = FirebaseService().getIsPublic(guid)
                if(::isPublicValue.isInitialized) {
                    findNavController().navigate(FragmentManageTeamDirections.actionFragmentTeamToFragmentReglageTeam(isPublicValue.ispublic))
                }
            }
        }

        profilViewModel.userService.gardenerId?.let { guid ->
            checkSubscribeMember(guid)
        }
    }

    private fun setPbLoading(isPublicounet: Boolean) {

        if (isPublicounet) {
            activity?.let {
                if(profilViewModel.userService.typeGarden != "pot" && profilViewModel.userService.typeGarden !="jardiniere" && profilViewModel.userService.typeGarden !="carre") {
                //    binding.ivGardenerActivate.visibility = View.VISIBLE
//                    binding.ivGardenerActivate.setImageDrawable(
//                        resources.getDrawable(
//                            R.drawable.cadenas_ouvert,
//                            null
//                        )
                    //)
                }
            }
        } else {
            activity?.let {
                if(profilViewModel.userService.typeGarden != "pot" && profilViewModel.userService.typeGarden !="jardiniere" && profilViewModel.userService.typeGarden !="carre") {
                //    binding.ivGardenerActivate.visibility = View.VISIBLE
            //        binding.ivGardenerActivate.setImageDrawable(resources.getDrawable(R.drawable.cadenas_fermer, null))
                }
            }
        }
    }

    private fun publicObserver() {
        profilViewModel.userService.gardenerId?.let { guid ->
            stateRef = FirebaseService().getIsPublic(guid)
            stateHandleRef = stateRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val isPublicData = snapshot.getValue(CheckIsPublic::class.java)
                    if (isPublicData != null) {
                        isPublicValue = isPublicData
                        setPbLoading(isPublicData.ispublic)
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }
    }

    private fun publicState() {
        if (::isPublicValue.isInitialized) {
            val popupState = PopupState(isPublicValue.ispublic, requireContext()) { popup, bool, str ->
                if (bool) {
                    if (isPublicValue.ispublic) {
                        println("Vous passez votre jardinière en OFF")
                        isPublicValue.ispublic = false
                        setPbLoading(isPublicValue.ispublic)
                        stateRef.child("ispublic").setValue(isPublicValue.ispublic)
                        popup.dismiss()
                        AlerterService.showGood(resources.getString(R.string.GARDENER_LOCK), requireActivity())
                    } else if (!isPublicValue.ispublic) {
                        println("Vous passez votre jardinière en ON")
                        isPublicValue.ispublic = true
                        setPbLoading(isPublicValue.ispublic)
                        stateRef.child("ispublic").setValue(isPublicValue.ispublic)
                        popup.dismiss()
                        AlerterService.showGood(resources.getString(R.string.GARDENER_UNLOCK), requireActivity())
                    }
                } else {
                    popup.dismiss()
                }
            }
            popupState.show()
        }
    }

//    private fun subscribeMember(guid: String) {
//        subscribeRef = FirebaseService().getSubscribeMember(guid)
//        handleSubscribe = subscribeRef.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                println("Ici je perçoit un changement")
//                var members = snapshot.getValue<java.util.HashMap<String, Boolean>>()
//                if (members != null) {
//                    println("liste des membres : ${members.keys}")
//                    println("nb de membre : " + members.count())
//                    if (members.count() > 0) {
//                        binding.ivNotif.visibility = View.VISIBLE
//                    }
//                } else {
//                    binding.ivNotif.visibility = View.GONE
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//            }
//        })
//    }

    private fun setObserver() {
        FirebaseService().firebase.currentUser?.uid?.let { uid ->
            userStageRef = FirebaseService().getUserByid(uid)
            handleUserStageRef = userStageRef.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    var user = snapshot.getValue(User::class.java)
                    if (user != null) {
                        currentGardener = user.currentGardener
                        setGardenerObserver(currentGardener)
                    }
                }
            })
        }
    }

    private fun setGardenerObserver(guid: String) {

        gardenerStageRef = FirebaseService().getGardenerById(guid)
        handleGardenerStageRef = gardenerStageRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                var gardenerDataStage = snapshot.getValue(GardenerStage::class.java)
                if (gardenerDataStage != null) {
                    gardenerStage = gardenerDataStage.stage
                    gardenerId = guid
                    gardenerName = gardenerDataStage.metadata.name
                }
            }
        })
    }

    private fun generateDeepLink(gardenerName: String, gardenerId: String, gardenerStage: String) {
        //See manifest if you need better understand it
        val branchObject = BranchUniversalObject()
        val style = ShareSheetStyle(
                this.requireActivity(),
                getString(fr.devid.plantR.R.string.app),
                getString(fr.devid.plantR.R.string.share_gardener, "$gardenerName"))
                .setAsFullWidthStyle(true)
        branchObject.canonicalIdentifier = "InviteOwner"
        branchObject.contentMetadata.customMetadata["gardenerId"] = "$gardenerStage|$gardenerId"
        branchObject.contentMetadata.customMetadata["type"] = "goToOwner"
        branchObject.contentMetadata.customMetadata["name"] = "$gardenerName"
        branchObject.showShareSheet(
                this.requireActivity(),
                LinkProperties(),
                style,
                object : Branch.BranchLinkShareListener {
                    override fun onChannelSelected(channelName: String?) {

                    }

                    override fun onLinkShareResponse(
                            sharedLink: String?,
                            sharedChannel: String?,
                            error: BranchError?
                    ) {
                        if (error == null) {
                            activity?.let {
                                AlerterService.showGood(context?.getString(R.string.SUCCESS_SHARE)!!, it)
                            }
                        }
                    }

                    override fun onShareLinkDialogDismissed() {

                    }

                    override fun onShareLinkDialogLaunched() {

                    }
                })
    }


    override fun onStop() {
        super.onStop()
        removeListener()
    }

    private fun removeListener() {
        if (::gardenerRef.isInitialized && ::handleGardener.isInitialized) {
            gardenerRef.removeEventListener(handleGardener)
        }

        if (::userRef.isInitialized && ::handleUserRef.isInitialized) {
            userRef.removeEventListener(handleUserRef)
        }

        if (::userSubscribeRef .isInitialized && ::handleUserSubscribe.isInitialized) {
            userSubscribeRef.removeEventListener(handleUserSubscribe)
        }

        if (::subscribeRef.isInitialized && ::handleSubscribe.isInitialized) {
            subscribeRef.removeEventListener(handleSubscribe)
        }
    }

}