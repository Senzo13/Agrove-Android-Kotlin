package fr.devid.plantR.ui.myPlants

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import fr.devid.plantR.Constants
import fr.devid.plantR.R
import fr.devid.plantR.base.BaseFragment
import fr.devid.plantR.databinding.FragmentConseilsMyTaskNuisiblesBinding
import fr.devid.plantR.models.*
import fr.devid.plantR.services.FirebaseService
import fr.devid.plantR.ui.home.ProfileViewModel
import java.lang.IllegalStateException
import javax.inject.Inject
import kotlin.collections.ArrayList

/**
 * LG
 */

data class Auxiliairage(var key : String = "", val auxiliaire : Auxiliaire? = Auxiliaire())

data class JustSoins(var description : String = "", var title : String ="", var url : String = "")

data class StepsListTraitement(
    var soins : JustSoins = JustSoins(),
    var name: String,
    var description: String,
    var images: String?
)

class FragmentTipsTasksNuisibles : BaseFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val profilViewModel: ProfileViewModel by activityViewModels { viewModelFactory }
    private val PAGE = "**** FragmentTipsTasks ****"
    private lateinit var binding: FragmentConseilsMyTaskNuisiblesBinding
    private val data: FragmentTipsTasksNuisiblesArgs by navArgs()
    private lateinit var gardenerRef: DatabaseReference
    private lateinit var handleGardener: ValueEventListener
    lateinit var plantRef: DatabaseReference
    lateinit var handlePlant: ValueEventListener
    private lateinit var adapter: FicheMissionAdapter
    private lateinit var adapterAuxiliaire: AuxiliaireAdapter
    private lateinit var adapterSoins: SoinsAdapter
    private lateinit var youtubeFragment: YouTubePlayerSupportFragment
    private lateinit var nuisibleRef : DatabaseReference
    private lateinit var handleNuisible : ValueEventListener
    private lateinit var auxiliaireRef : DatabaseReference
    private lateinit var handlerAuxiliaire : ValueEventListener
    var myStepsTraitement: ArrayList<StepsListTraitement> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentConseilsMyTaskNuisiblesBinding.inflate(inflater, container, false)
        getNuisiblesInPlant()
        initView()
        return binding.root
    }

    private fun initView() {
        adapterSoins = SoinsAdapter()

        binding.rvFicheSoins.adapter = adapterSoins

        binding.rvFicheSoins.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)

        adapterAuxiliaire = AuxiliaireAdapter()

        binding.rvFicheAuxiliaires.adapter = adapterAuxiliaire

        binding.rvFicheAuxiliaires.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)

        youtubeFragment = childFragmentManager.findFragmentById(R.id.youtube_fragment) as YouTubePlayerSupportFragment

        binding.tvNameMyNuisible.text = data.nameNuisible

        val storageRef = FirebaseService().getStorageNuisiblesId(data.imageNuisible)

        storageRef.downloadUrl.addOnSuccessListener {
            Glide.with(binding.ivFirstImg.context)
                .load(it.toString())
                .into(binding.ivFirstImg)
        }

            binding.tvDescription.text = data.descriptionNuisible

            binding.tvPageName.text = data.nameNuisible

            binding.btSuccess.setOnClickListener {
                //   sendSuccess()
            }

            binding.ivBack.setOnClickListener {
                findNavController().popBackStack()
            }

            setTaskViewPicture()

    }


    private fun getNuisiblesInPlant() { // PREND UNE PLANTE ET RECUPERE LA LISTE DES NUISIBLES
        println("Nuisible cliqué : " + data.keyNuisible)
        nuisibleRef = FirebaseService().getNuisiblesReferenceId(data.keyNuisible)
        handleNuisible = nuisibleRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                println(PAGE + "onCancelled " + error.toException())
            }
            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                val nuisiblesCheck = snapshot.getValue(Nuisible::class.java)
                println("nuisibles check : " + nuisiblesCheck)
                if (nuisiblesCheck != null) {
                   var listAuxiliaires= nuisiblesCheck.auxiliaires.listeauxiliaire.map { it.key }.toList()
                    getAuxiliaire(listAuxiliaires, nuisiblesCheck.soinsnuisibles)
                    println("getNuisiblesInPlant\n")
                    println("nuisibles : " + listAuxiliaires + "\n")
                    println("END")

                    if(!nuisiblesCheck.pieges.description.isEmpty()) {
                        binding.tvDescriptionPiege.text = nuisiblesCheck.pieges.description
                    } else {
                        binding.tvPiege.visibility = View.GONE
                        binding.tvDescriptionPiege.visibility = View.GONE
                    }
                    binding.tvDescriptionAuxiliaire.text = nuisiblesCheck.auxiliaires.description
                    binding.tvFicheDescriptionSoins.text = nuisiblesCheck.soinsnuisibles.description
                }
            }
        })
    }

    private fun getAuxiliaire(list : List<String>, soins : Soins) {
       var arrayOfAuxiliaires : List<Auxiliairage> = listOf()

        list.forEach { auxiliaire ->

            auxiliaireRef = FirebaseService().getAuxiliaire(auxiliaire)
            handlerAuxiliaire = auxiliaireRef.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    println(PAGE + "onCancelled " + error.toException())
                }

                @SuppressLint("SetTextI18n")
                override fun onDataChange(snapshot: DataSnapshot) {
                    var auxiliaireData = snapshot.getValue(Auxiliaire::class.java)
                        println("AuxiliaireData : " + auxiliaireData)
                    if(auxiliaireData != null) {
                        arrayOfAuxiliaires = arrayOfAuxiliaires + Auxiliairage(auxiliaire, auxiliaireData)
                        adapterAuxiliaire.submitList(arrayOfAuxiliaires)
                    }
                }
            })
        }

        setYoutubeVideoFromNuisible(soins)
        Log.e("FragmentTipsTaskNuisibles", "Les soins et traitement : " + soins)
    }


    private fun setYoutubeVideoFromNuisible(soins : Soins) {

        if (soins.url.isNotEmpty()) {
            binding.clYoutubeVideo.visibility = View.VISIBLE
            try {
                youtubeFragment.initialize(Constants.youtubeApiKey,
                    object : YouTubePlayer.OnInitializedListener {
                        override fun onInitializationSuccess(

                            provider: YouTubePlayer.Provider?,
                            player: YouTubePlayer?,
                            wasRestored: Boolean
                        ) {
                            if (player != null) {
                                player.setShowFullscreenButton(false);
                                player.setFullscreen(false)
                                player.loadVideo(soins.url)
                                player.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT)
                                if (wasRestored) {
                                    player.setShowFullscreenButton(false);
                                    player.setFullscreen(false)
                                    player.loadVideo(soins.url)
                                    player.play()
                                }
                            }
                        }

                        override fun onInitializationFailure(
                            p0: YouTubePlayer.Provider?,
                            p1: YouTubeInitializationResult?
                        ) {
                        }
                    })
            } catch (e: IllegalStateException) {
                println("Un probleme avec la vidéo")
            }

        } else {
            binding.clYoutubeVideo.visibility = View.GONE
        }
        SetStepsTraitementLoad(soins, soins.traitements!!)

    }

private fun SetStepsTraitementLoad(soins : Soins, traitement: ArrayList<Traitement>) {

    myStepsTraitement.clear()
    Log.e("FragmentTipsTasksNuisibles" , "Traitement : " + traitement +"\n")
    traitement.forEach {
        myStepsTraitement.add(StepsListTraitement(JustSoins(soins.description, soins.title, soins.url), it.name, it.description, it.image))
        adapterSoins.submitList(myStepsTraitement)
    }

    // adapter.submitList(mySteps.toList())
}

        private fun setTaskViewPicture() {
        //   binding.tvTitleTask.text = data.title.capitalize()
        ///  binding.tvDescription.text = data.description
//        getPlantNameObserver(data.id)
//        storageRef = FirebaseService().getStoragePlantsReference()
//            .child(data.id).child(path)
//        storageRef.downloadUrl.addOnSuccessListener {
//            //  Glide.with(binding.ivPlants.context)
//            //      .load(it.toString())
//            //      .into(binding.ivPlants)
//        }

    }

//    private fun getPlantNameObserver(plantId: String) {
//        plantRef = FirebaseService().getPlantsReferenceById(plantId)
//        handlePlant = plantRef.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                var plant = snapshot.getValue(Plant::class.java)
//                if (plant != null) {
//                   // binding.tvNameMyPlant.text = plant.name?.toUpperCase()
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//            }
//        })
//    }

//    private fun sendSuccess() {
//        profilViewModel.userService.gardenerId?.let { guid ->
//            FirebaseMessaging.getInstance().unsubscribeFromTopic(guid)
//            println("SEND DE LA NOTIF")
//        }
//    }

//        val popupConfirmTask =
//            PopupFinishMissions(data.title, requireContext()) { popup, bool, str ->
//                if (bool) {
//                    if (::tasksToDone.isInitialized) {
//                        tasksToDone.done = true
//                        tasksToDone.doneInTime = true
//                        FirebaseService().getGardenerPlantsById(data.guid)
//                            .child(data.position)
//                            .child("tasks")
//                            .child(data.listPosition.toString())
//                            .setValue(tasksToDone)
//                            .addOnSuccessListener {
//                                println("GardenerID : " + data.guid)
//                                println("Plant position : " + data.position)
//                                println("List position : " + data.listPosition)
//                                println("Title : " + data.title)
//                                println("Plant Id : " + data.id)
//                                println("TaskId " + data.taskId)
//                                println("Description : " + data.description)
//                                AlerterService.showGood("Changement effectué", requireActivity())
//                                FirebaseService().firebase.currentUser?.uid?.let { uid ->
//                                    profilViewModel.userService.gardenerId?.let { guid ->
//                                        profilViewModel.userService.currentUsername?.let { nameUser ->
//                                            profilViewModel.userService.gardenerName?.let { gardenerName ->
//                                                var doneBy =
//                                                    DoneBy(Date().time.toDouble() / 1000, uid)
//                                                //prepare and send the data here..
//                                                FirebaseService().getGardenerPlantsById(data.guid)
//                                                    .child(data.position).child("tasks")
//                                                    .child(data.listPosition.toString())
//                                                    .child("doneBy").setValue(doneBy)
//                                                println("J'envoie la notification du $guid")
//                                                var body =
//                                                    "La mission \"${data.title.capitalize()}\" a été éffectuée par $nameUser sur la plante ${data.id.capitalize()} de la jardinière $gardenerName"
//                                                var notificationData = NotificationData(
//                                                    uid,
//                                                    data.title,
//                                                    body,
//                                                    "taskDone",
//                                                    data.id,
//                                                    data.position,
//                                                    guid,
//                                                    data.id.capitalize(),
//                                                    data.taskId
//                                                )
//
//                                                registerViewModel.sendNotification(
//                                                    "$guid",
//                                                    "Tâche Éffectuée",
//                                                    body,
//                                                    "$uid",
//                                                    notificationData
//                                                )
//                                            }
//                                        }
//                                    }
//                                }
//                                findNavController().popBackStack()
//                            }.addOnFailureListener {
//                                AlerterService.showError("Erreur de validation", requireActivity())
//                            }
//                        popup.dismiss()
//                    }
//                } else {
//                    popup.dismiss()
//                }
//            }
//        popupConfirmTask.show()


    override fun onResume() {
        super.onResume()
//        println("Nom de la plante" + data.id)
//        setGardenerObserver(data.guid, data.position, data.listPosition)
    }



//    private fun setGardenerObserver(guid: String, position: String, listPosition: Int) {
//        gardenerRef = FirebaseService().getGardenerPlantsById(guid).child(position).child("tasks")
//            .child(listPosition.toString())
//        handleGardener = gardenerRef.addValueEventListener(object : ValueEventListener {
//            override fun onCancelled(error: DatabaseError) {
//                println("${PAGE} onCancelled ${error.toException()}")
//            }
//
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val ficheMission = snapshot.getValue(Tasks::class.java)
//                if (ficheMission != null) {
//
//                    tasksToDone = ficheMission
//                    if (ficheMission.priority == 1) {
//                        binding.ivPrio.visibility = View.VISIBLE
//                        binding.tvPrioText.text = "Fortement recommandée"
//                    } else if (ficheMission.priority == 2) {
//                        binding.ivPrio.visibility = View.GONE
//                        binding.tvPrioText.text = "Conseillée"
//                    } else {
//                        binding.tvPrioText.text = "Recommandée"
//                        binding.ivPrio.visibility = View.GONE
//                        println("Il y a une erreur dans la priority de la plante " + data.id.capitalize())
//                    }
//
//                    binding.tvTimeText.text = ficheMission.doInTime
//
//                    if (ficheMission.url.isNotEmpty()) {
//                        binding.clYoutubeVideo.visibility = View.VISIBLE
//                        try {
//                            youtubeFragment.initialize(
//                                Constants.youtubeApiKey},
//                                object : YouTubePlayer.OnInitializedListener {
//                                    override fun onInitializationSuccess(
//                                        provider: YouTubePlayer.Provider?,
//                                        player: YouTubePlayer?,
//                                        wasRestored: Boolean
//                                    ) {
//                                        if (player != null) {
//                                            player.loadVideo(ficheMission.url)
//                                            player.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT)
//                                            if (wasRestored) {
//                                                player.loadVideo(ficheMission.url)
//                                                player.play()
//                                            }
//                                        }
//                                    }
//
//                                    override fun onInitializationFailure(
//                                        p0: YouTubePlayer.Provider?,
//                                        p1: YouTubeInitializationResult?
//                                    ) {
//                                    }
//                                })
//                        } catch (e: IllegalStateException) {
//                            println("Un probleme avec la vidéo")
//                        }
//
//                    } else {
//                        binding.clYoutubeVideo.visibility = View.GONE
//                    }
//                    SetStepsLoad(ficheMission.steps)
//                    SetToolsLoad(ficheMission.tools)
//                }
//            }
//        })
//    }


    override fun onStop() {
        super.onStop()
        removeListener()
    }

    private fun removeListener() {
        if (::gardenerRef.isInitialized && ::handleGardener.isInitialized) {
            gardenerRef.removeEventListener(handleGardener)
        }
        if (::plantRef.isInitialized && ::handlePlant.isInitialized) {
            plantRef.removeEventListener(handlePlant)
        }
        if (::nuisibleRef.isInitialized && ::handleNuisible.isInitialized) {
            nuisibleRef.removeEventListener(handleNuisible)
        }
        if (::auxiliaireRef.isInitialized && ::handlerAuxiliaire.isInitialized) {
            auxiliaireRef.removeEventListener(handlerAuxiliaire)
        }

    }
}
