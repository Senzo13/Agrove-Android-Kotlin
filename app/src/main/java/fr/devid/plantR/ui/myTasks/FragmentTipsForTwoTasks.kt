package fr.devid.plantR.ui.myTasks

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragment
import com.google.firebase.storage.StorageReference
import fr.devid.plantR.Constants
import fr.devid.plantR.R
import fr.devid.plantR.base.BaseFragment
import fr.devid.plantR.databinding.FragmentConseilsMyTaskTwoTasksRealizedBinding
import fr.devid.plantR.models.*
import fr.devid.plantR.services.FirebaseService
import fr.devid.plantR.ui.home.ProfileViewModel
import fr.devid.plantR.ui.myPlants.FicheMissionAdapter
import fr.devid.plantR.ui.myPlants.StepsList
import fr.devid.plantR.ui.myPlants.ToolsList
import java.lang.IllegalStateException
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * LG
 */

class FragmentTipsForTwoTasks : BaseFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val profilViewModel: ProfileViewModel by activityViewModels { viewModelFactory }
    private val PAGE = "**** FragmentTipsTasks ****"
    private lateinit var binding: FragmentConseilsMyTaskTwoTasksRealizedBinding
    private val data: FragmentTipsForTwoTasksArgs by navArgs()
    private lateinit var storageRef: StorageReference
    private var path: String = "plant.jpg"
    private lateinit var gardenerRef: DatabaseReference
    private lateinit var handleGardener: ValueEventListener
    private lateinit var tasksToDone: Tasks
    lateinit var plantRef: DatabaseReference
    lateinit var handlePlant: ValueEventListener
    private lateinit var adapter: FicheMissionAdapter
    private lateinit var dataGardenerType : DatabaseReference
    private lateinit var dataGardenerTypeHandle : ValueEventListener
    private lateinit var youtubeFragment: YouTubePlayerSupportFragment
    var mySteps: ArrayList<StepsList> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentConseilsMyTaskTwoTasksRealizedBinding.inflate(inflater, container, false)
        adapter = FicheMissionAdapter()
        binding.rvFicheMission.adapter = adapter
        binding.rvFicheMission.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        initView()
        return binding.root
    }

    private fun getTypeGarden() {
        profilViewModel.userService.gardenerId.let { gardenerId ->
            dataGardenerType = FirebaseService().getGardenerById(gardenerId!!)
            dataGardenerTypeHandle =
                dataGardenerType.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var data1 = snapshot.getValue(GardenerStage::class.java)
                            println("STAGE CACA")
                        val positionSplited = data.position.split("")

                        if (data1 != null) {
                                when (data1.stage) {
                                    "4" -> {
                                        println("Jardinière de 4 étages")
                                        when (positionSplited[0]) {
                                            "0" -> {
                                                println("Vous êtes au quatrieme emplacement d'etage")
                                                binding.ivEtage!!.setImageDrawable(requireActivity().getDrawable(R.drawable.icon_etage4_4))
                                                binding.tvLegendEtage!!.text = "Étage 4"
                                            }
                                            "1" -> {
                                                println("Vous êtes au troisième emplacement d'etage")
                                                binding.ivEtage!!.setImageDrawable(requireActivity().getDrawable(R.drawable.icon_etage4_3))
                                                binding.tvLegendEtage!!.text = "Étage 3"
                                            }
                                            "2" -> {
                                                println("Vous êtes au deuxième emplacement d'etage")
                                                binding.ivEtage!!.setImageDrawable(requireActivity().getDrawable(R.drawable.icon_etage4_2))
                                                binding.tvLegendEtage!!.text = "Étage 2"
                                            }
                                            "3" -> {
                                                print("Vous êtes au premier emplacement d'etage")
                                                binding.ivEtage!!.setImageDrawable(requireActivity().getDrawable(R.drawable.icon_etage4_1))
                                                binding.tvLegendEtage!!.text = "Étage 1"
                                            }
                                            "4" -> {
                                                print("Vous êtes au premier RANGS OUAIS")
                                                binding.ivEtage!!.setImageDrawable(requireActivity().getDrawable(R.drawable.icon_etage4_1))
                                                binding.tvLegendEtage!!.text = "Étage 1"
                                            }
                                        }
                                    }
                                    "3" -> {
                                        println("Jardinière de 3 étages")
                                        when (positionSplited[0]) {
                                            "0" -> {
                                                println("Vous êtes au troisieme emplacement d'etage")
                                                binding.ivEtage!!.setImageDrawable(requireActivity().getDrawable(R.drawable.icon_etage3_3))
                                                binding.tvLegendEtage!!.text = "Étage 3"
                                            }
                                            "1" -> {
                                                println("Vous êtes au deuxieme emplacement d'etage")
                                                binding.ivEtage!!.setImageDrawable(requireActivity().getDrawable(R.drawable.icon_etage3_2))
                                                binding.tvLegendEtage!!.text = "Étage 2"
                                            }
                                            "2" -> {
                                                print("Vous êtes au premier emplacement d'etage")
                                                binding.ivEtage!!.setImageDrawable(requireActivity().getDrawable(R.drawable.icon_etage3_1))
                                                binding.tvLegendEtage!!.text = "Étage 1"
                                            }
                                        }
                                    }
                                    "2" -> {
                                        println("Jardinière de 2 étages")
                                        when (positionSplited[0]) {
                                            "0" -> {
                                                println("Vous êtes au 2eme emplacement d'etage")
                                                binding.ivEtage.setImageDrawable(requireActivity().getDrawable(R.drawable.icon_etage2_2))
                                                binding.tvLegendEtage.text = "Étage 2"
                                            }
                                            "1" -> {
                                                println("Vous êtes au deuxieme emplacement d'etage")
                                                binding.ivEtage!!.setImageDrawable(requireActivity().getDrawable(R.drawable.icon_etage2_1))
                                                binding.tvLegendEtage!!.text = "Étage 1"
                                            }
                                        }
                                    }
                                }
                            }


                        when (positionSplited[1]) {
                            "0" -> {
                                print("et êtes a l'emplacement 1 ")
                                binding.ivEmplacement.setImageDrawable(requireActivity().getDrawable(R.drawable.emplacement_1))
                                binding.tvLegendPosition!!.text = "Emplacement 1"
                            }
                            "1" -> {
                                print("et êtes a l'emplacement 2 ")
                                binding.ivEmplacement.setImageDrawable(requireActivity().getDrawable(R.drawable.emplacement_2))
                                binding.tvLegendPosition!!.text = "Emplacement 2"

                            }
                            "2" -> {
                                print("et êtes a l'emplacement 3 ")
                                binding.ivEmplacement.setImageDrawable(requireActivity().getDrawable(R.drawable.emplacement_3))
                                binding.tvLegendPosition!!.text = "Emplacement 3"

                            }
                            "3" -> {
                                print("et êtes a l'emplacement 4 ")
                                binding.ivEmplacement.setImageDrawable(requireActivity().getDrawable(R.drawable.emplacement_4))
                                binding.tvLegendPosition!!.text = "Emplacement 4"
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })
        }
    }

    private fun setAvatarUser(uid: String) {

        if (data.date.isNotEmpty() && data.userName.isNotEmpty()) {
            binding.tvDate.text =
                "${context?.getString(R.string.TASK_DATE_DATE)} " + data.date.capitalize() + " par"
            binding.tvNameRealized.text = data.userName.capitalize()
        }

        storageRef = FirebaseService().getStoragePictureByUserId(uid)
        storageRef.downloadUrl.addOnSuccessListener { uri ->
            Glide.with(requireContext())
                .load(uri.toString())
                .circleCrop()
                .into(binding.ivUserProfil)
        }
        storageRef.downloadUrl.addOnFailureListener {
            Glide.with(requireContext())
                .load(R.drawable.user)
                .circleCrop()
                .into(binding.ivUserProfil)
        }
    }

    private fun initView() {
        youtubeFragment =
            childFragmentManager.findFragmentById(R.id.youtube_fragment) as YouTubePlayerSupportFragment
        setAvatarUser(data.userId)
        youtubeFragment = childFragmentManager.findFragmentById(R.id.youtube_fragment) as YouTubePlayerSupportFragment

        binding.tvPageName.text = resources.getString(R.string.my_tasks)

//        if(data.description.isNotEmpty()) {
//            binding.tvDescription.visibility = View.VISIBLE
//            binding.tvDescription.text = data.description.capitalize()
//        } else {
//            binding.tvDescription.visibility = View.GONE
//        }

        when(Locale.getDefault().language.toString()) {
            "fr" -> {
                binding.tvNameMyMission.text = data.title.toUpperCase()
            }
            "en" -> {
                when(data.title.toLowerCase()) {
                    "semer" -> {
                        binding.tvNameMyMission.text = "Sow"
                    }
                    "planter" -> {
                        binding.tvNameMyMission.text = "Plant"
                    }
                    else -> {
                        binding.tvNameMyMission.text = data.title.toUpperCase()
                    }
                }
            }
            else -> {
                when(data.title.toLowerCase()) {
                    "semer" -> {
                        binding.tvNameMyMission.text = "Sow"

                    }
                    "planter" -> {
                        binding.tvNameMyMission.text = "Plant"
                    }
                    else -> {
                        binding.tvNameMyMission.text = data.title.toUpperCase()
                    }
                }
            }
        }

        println("Je suis a la position : " + data.position)
        val positionSplited = data.position.split("-")

        when (profilViewModel.userService.typeGarden) {
            "carre" -> {
                println("JE SUIS LA MORREY")
                when (profilViewModel.userService.dimension) {
                    0 -> {
                        when (positionSplited[1]) {
                            "0" -> {
                                print("et êtes a l'emplacement 1 ")
                                binding.ivEmplacement.setImageDrawable(
                                    requireActivity().getDrawable(
                                        R.drawable.emplacement_1_2
                                    )
                                )
                                binding.ivEmplacement.rotation = -90F

                                binding.tvLegendPosition!!.text = "Emplacement 1"
                            }
                            "1" -> {
                                print("et êtes a l'emplacement 2 ")
                                binding.ivEmplacement.setImageDrawable(
                                    requireActivity().getDrawable(
                                        R.drawable.emplacement_2_2
                                    )
                                )
                                binding.ivEmplacement.rotation = -90F
                                binding.tvLegendPosition!!.text = "Emplacement 2"

                            }
                        }

                        when (positionSplited[0]) {
                            "0" -> {
                                println("Vous êtes à la 1ème rangée du petit carre de potager")
                                binding.ivEtage?.setImageResource(R.drawable.rang1_2)
                                binding.tvLegendEtage!!.text = "Rangée 2"
                                binding.ivEtage.setColorFilter(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.green_plantr
                                    ), android.graphics.PorterDuff.Mode.MULTIPLY
                                );

                            }
                            "1" -> {
                                println("Vous êtes à la 2ème rangée du petit carre de potager")
                                binding.ivEtage.setImageResource(R.drawable.rang2_2)
                                binding.tvLegendEtage.text = "Rangée 1"
                                binding.ivEtage.setColorFilter(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.green_plantr
                                    ), android.graphics.PorterDuff.Mode.MULTIPLY
                                );

                            }
                        }
                    }
                    1 -> {
                        when (positionSplited[1]) {
                            "0" -> {
                                print("et êtes a l'emplacement 1 ")
                                binding.ivEmplacement.setImageDrawable(
                                    requireActivity().getDrawable(
                                        R.drawable.emplacement_1_3
                                    )
                                )
                                binding.ivEmplacement.rotation = -90F

                                binding.tvLegendPosition!!.text = "Emplacement 1"

                            }

                            "1" -> {
                                print("et êtes a l'emplacement 2 ")
                                binding.ivEmplacement.setImageDrawable(
                                    requireActivity().getDrawable(
                                        R.drawable.emplacement_2_3
                                    )
                                )
                                binding.ivEmplacement.rotation = -90F

                                binding.tvLegendPosition!!.text = "Emplacement 2"

                            }
                            "2" -> {
                                print("et êtes a l'emplacement 3 ")
                                binding.ivEmplacement.setImageDrawable(
                                    requireActivity().getDrawable(
                                        R.drawable.emplacement_3_3
                                    )
                                )
                                binding.ivEmplacement.rotation = -90F

                                binding.tvLegendPosition!!.text = "Emplacement 3"

                            }
                        }

                        when (positionSplited[0]) {
                            "0" -> {
                                println("Vous êtes à la 3ème rangée du moyen carre de potager")
                                binding.ivEtage?.setImageResource(R.drawable.rang1_3)
                                binding.tvLegendEtage!!.text = "Rangée 3"
                                binding.ivEtage!!.setColorFilter(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.green_plantr
                                    ), android.graphics.PorterDuff.Mode.MULTIPLY
                                );
                            }
                            "1" -> {
                                println("Vous êtes à la 2ème rangée du moyen carre de potager")
                                binding.ivEtage?.setImageResource(R.drawable.rang2_3)
                                binding.tvLegendEtage!!.text = "Rangée 2"
                                binding.ivEtage!!.setColorFilter(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.green_plantr
                                    ), android.graphics.PorterDuff.Mode.MULTIPLY
                                );
                            }
                            "2" -> {
                                println("Vous êtes à la 2ème rangée du moyen carre de potager")
                                binding.ivEtage?.setImageResource(R.drawable.rang3_3)
                                binding.tvLegendEtage!!.text = "Rangée 1"
                                binding.ivEtage!!.setColorFilter(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.green_plantr
                                    ), android.graphics.PorterDuff.Mode.MULTIPLY
                                );

                            }
                        }
                    }
                    2 -> {
                        when (positionSplited[1]) {
                            "0" -> {
                                print("et êtes a l'emplacement 1 ")
                                binding.ivEmplacement.setImageDrawable(
                                    requireActivity().getDrawable(
                                        R.drawable.emplacement_1_4
                                    )
                                )
                                binding.ivEmplacement.rotation = -90F

                                binding.tvLegendPosition!!.text = "Emplacement 1"
                            }
                            "1" -> {
                                print("et êtes a l'emplacement 2 ")
                                binding.ivEmplacement.setImageDrawable(
                                    requireActivity().getDrawable(
                                        R.drawable.emplacement_2_4
                                    )
                                )
                                binding.ivEmplacement.rotation = -90F

                                binding.tvLegendPosition!!.text = "Emplacement 2"
                            }
                            "2" -> {
                                print("et êtes a l'emplacement 3 ")
                                binding.ivEmplacement.setImageDrawable(
                                    requireActivity().getDrawable(
                                        R.drawable.emplacement_3_4
                                    )
                                )
                                binding.ivEmplacement.rotation = -90F

                                binding.tvLegendPosition!!.text = "Emplacement 3"
                            }
                            "3" -> {
                                print("et êtes a l'emplacement 4 ")
                                binding.ivEmplacement.setImageDrawable(
                                    requireActivity().getDrawable(
                                        R.drawable.emplacement_4_4
                                    )
                                )
                                binding.ivEmplacement.rotation = -90F

                                binding.tvLegendPosition!!.text = "Emplacement 4"
                            }
                        }
                        when (positionSplited[0]) {
                            "0" -> {
                                println("Vous êtes à la 4ème rangée du grand carre de potager")
                                binding.ivEtage?.setImageResource(R.drawable.rang1_4)
                                println("JE SUIS LA MAGL")
                                binding.tvLegendEtage!!.text = "Rangée 4"
                                binding.ivEtage!!.setColorFilter(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.green_plantr
                                    ), android.graphics.PorterDuff.Mode.MULTIPLY
                                );
                            }
                            "1" -> {
                                println("Vous êtes à la 3ème rangée du grand carre de potager")
                                binding.ivEtage?.setImageResource(R.drawable.rang2_4)
                                binding.tvLegendEtage!!.text = "Rangée 3"
                                binding.ivEtage!!.setColorFilter(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.green_plantr
                                    ), android.graphics.PorterDuff.Mode.MULTIPLY
                                );
                            }
                            "2" -> {
                                println("Vous êtes à la 2ème rangée du grand carre de potager")
                                binding.ivEtage?.setImageResource(R.drawable.rang3_4)
                                binding.tvLegendEtage!!.text = "Rangée 2"
                                binding.ivEtage!!.setColorFilter(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.green_plantr
                                    ), android.graphics.PorterDuff.Mode.MULTIPLY
                                );
                            }
                            "3" -> {
                                println("Vous êtes à la 1ème rangée du grand carre de potager")
                                binding.ivEtage?.setImageResource(R.drawable.rang4_4)
                                binding.tvLegendEtage!!.text = "Rangée 1"
                                binding.ivEtage!!.setColorFilter(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.green_plantr
                                    ), android.graphics.PorterDuff.Mode.MULTIPLY
                                );
                            }
                        }
                    }
                }
            }
            "jardiniere" -> {
                when (profilViewModel.userService.dimension) {
                    0 -> {
                        when (positionSplited[1]) {
                            "0" -> {
                                print("et êtes a l'emplacement 1 ")
                                binding.ivEmplacement.setImageDrawable(
                                    requireActivity().getDrawable(
                                        R.drawable.emplacement_1_2
                                    )
                                )
                                binding.ivEmplacement.rotation = -90F

                                binding.tvLegendPosition!!.text = "Emplacement 1"
                            }
                            "1" -> {
                                print("et êtes a l'emplacement 2 ")
                                binding.ivEmplacement.setImageDrawable(
                                    requireActivity().getDrawable(
                                        R.drawable.emplacement_2_2
                                    )
                                )
                                binding.ivEmplacement.rotation = -90F

                                binding.tvLegendPosition!!.text = "Emplacement 2"

                            }
                        }

                        when (positionSplited[0]) {
                            "0" -> {
                                println("Vous êtes à la 1ème rangée du petit jardiniere")
                                binding.ivEtage?.setImageResource(R.drawable.mon_potager_jardiniere_petit)
                                binding.tvLegendEtage!!.text = "Jardinière"
                                binding.ivEtage!!.setColorFilter(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.green_plantr
                                    ), android.graphics.PorterDuff.Mode.MULTIPLY
                                );

                            }
                            "1" -> {
                                println("Vous êtes à la 2ème rangée du petite jardiniere")
                                binding.ivEtage?.setImageResource(R.drawable.mon_potager_jardiniere_petit)
                                binding.tvLegendEtage!!.text = "Jardinière"
                                binding.ivEtage!!.setColorFilter(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.green_plantr
                                    ), android.graphics.PorterDuff.Mode.MULTIPLY
                                );

                            }
                        }
                    }
                    1 -> {
                        when (positionSplited[1]) {
                            "0" -> {
                                print("et êtes a l'emplacement 1 ")
                                binding.ivEmplacement.setImageDrawable(
                                    requireActivity().getDrawable(
                                        R.drawable.emplacement_1_2
                                    )
                                )
                                binding.ivEmplacement.rotation = -90F

                                binding.tvLegendPosition!!.text = "Emplacement 1"
                            }
                            "1" -> {
                                print("et êtes a l'emplacement 2 ")
                                binding.ivEmplacement.setImageDrawable(
                                    requireActivity().getDrawable(
                                        R.drawable.emplacement_2_2
                                    )
                                )
                                binding.ivEmplacement.rotation = -90F

                                binding.tvLegendPosition!!.text = "Emplacement 2"

                            }
                        }

                        when (positionSplited[0]) {
                            "0" -> {
                                println("Vous êtes à la 1ème rangée d'une moyenne jardiniere")
                                binding.ivEtage?.setImageResource(R.drawable.type_jardiniere)
                                binding.tvLegendEtage!!.text = "Jardinière"
                                binding.ivEtage!!.setColorFilter(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.green_plantr
                                    ), android.graphics.PorterDuff.Mode.MULTIPLY
                                );

                            }
                            "1" -> {
                                println("Vous êtes à la 2ème rangée d'une moyenne'jardiniere")
                                binding.ivEtage?.setImageResource(R.drawable.type_jardiniere)
                                binding.tvLegendEtage!!.text = "Jardinière"
                                binding.ivEtage!!.setColorFilter(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.green_plantr
                                    ), android.graphics.PorterDuff.Mode.MULTIPLY
                                );

                            }
                        }
                    }
                    2 -> {
                        when (positionSplited[1]) {
                            "0" -> {
                                print("et êtes a l'emplacement 1 ")
                                binding.ivEmplacement.setImageDrawable(
                                    requireActivity().getDrawable(
                                        R.drawable.emplacement_1_4
                                    )
                                )
                                binding.ivEmplacement.rotation = -90F

                                binding.tvLegendPosition!!.text = "Emplacement 1"
                            }
                            "1" -> {
                                print("et êtes a l'emplacement 2 ")
                                binding.ivEmplacement.setImageDrawable(
                                    requireActivity().getDrawable(
                                        R.drawable.emplacement_2_4
                                    )
                                )
                                binding.ivEmplacement.rotation = -90F

                                binding.tvLegendPosition!!.text = "Emplacement 2"

                            }
                            "2" -> {
                                print("et êtes a l'emplacement 3 ")
                                binding.ivEmplacement.setImageDrawable(
                                    requireActivity().getDrawable(
                                        R.drawable.emplacement_3_4
                                    )
                                )
                                binding.ivEmplacement.rotation = -90F

                                binding.tvLegendPosition!!.text = "Emplacement 3"

                            }
                            "3" -> {
                                print("et êtes a l'emplacement 4 ")
                                binding.ivEmplacement.setImageDrawable(
                                    requireActivity().getDrawable(
                                        R.drawable.emplacement_4_4
                                    )
                                )
                                binding.ivEmplacement.rotation = -90F

                                binding.tvLegendPosition!!.text = "Emplacement 4"

                            }

                        }
                        when (positionSplited[0]) {
                            "0" -> {
                                println("Vous êtes au premier emplacement d'une grande jardiniere")
                                binding.ivEtage?.setImageResource(R.drawable.type_jardiniere)
                                binding.tvLegendEtage!!.text = "Jardinière"
                                binding.ivEtage!!.setColorFilter(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.green_plantr
                                    ), android.graphics.PorterDuff.Mode.MULTIPLY
                                );

                            }
                            "1" -> {
                                println("Vous êtes au deuxieme emplacement d'une grande jardiniere")
                                binding.ivEtage?.setImageResource(R.drawable.type_jardiniere)
                                binding.tvLegendEtage!!.text = "Jardinière"
                                binding.ivEtage!!.setColorFilter(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.green_plantr
                                    ), android.graphics.PorterDuff.Mode.MULTIPLY
                                );

                            }
                            "2" -> {
                                println("Vous êtes au troisieme emplacement d'une grande jardiniere")
                                binding.ivEtage?.setImageResource(R.drawable.type_jardiniere)
                                binding.tvLegendEtage!!.text = "Jardinière"
                                binding.ivEtage!!.setColorFilter(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.green_plantr
                                    ), android.graphics.PorterDuff.Mode.MULTIPLY
                                );

                            }
                            "3" -> {
                                println("Vous êtes au quatrieme emplacement d'une grande jardiniere")
                                binding.ivEtage?.setImageResource(R.drawable.type_jardiniere)
                                binding.tvLegendEtage!!.text = "Jardinière"
                                binding.ivEtage!!.setColorFilter(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.green_plantr
                                    ), android.graphics.PorterDuff.Mode.MULTIPLY
                                );

                            }
                        }
                    }
                }
            }
            "pot" -> {
                when (profilViewModel.userService.dimension) {
                    0 -> {
                        when (positionSplited[1]) {
                            "0" -> {
                                print("et êtes a l'emplacement 1 ")
                                //Pas image
                                binding.ivEmplacement.visibility = View.GONE
                                binding.tvLegendPosition!!.text = "Emplacement 1"
                            }
                        }
                        when (positionSplited[0]) {
                            "0" -> {
                                println("Vous êtes a l'emplacement 1 du pot")
                                binding.ivEmplacement.visibility = View.GONE
                                binding.ivEtage?.setImageResource(R.drawable.type_pot)
                                binding.tvLegendEtage!!.text = "Pot"
                                binding.ivEtage!!.setColorFilter(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.green_plantr
                                    ), android.graphics.PorterDuff.Mode.MULTIPLY
                                );
                            }
                        }
                    }
                    1 -> {
                        when (positionSplited[1]) {
                            "0" -> {
                                print("et êtes a l'emplacement 1 ")
                                //Pas image
                                binding.ivEmplacement.visibility = View.GONE
                                binding.tvLegendPosition!!.text = "Emplacement 1"
                            }
                        }
                        when (positionSplited[0]) {
                            "0" -> {
                                binding.ivEmplacement.visibility = View.GONE
                                println("Vous êtes a l'emplacement 1 du pot")
                                binding.ivEtage?.setImageResource(R.drawable.type_pot)
                                binding.tvLegendEtage!!.text = "Pot"
                                binding.ivEtage!!.setColorFilter(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.green_plantr
                                    ), android.graphics.PorterDuff.Mode.MULTIPLY
                                );
                            }
                        }
                    }
                    2 -> {
                        when (positionSplited[1]) {
                            "0" -> {
                                print("et êtes a l'emplacement 1 ")
                                //Pas image
                                binding.ivEmplacement.visibility = View.GONE
                                binding.tvLegendPosition!!.text = "Emplacement 1"
                            }
                        }
                        when (positionSplited[0]) {
                            "0" -> {
                                println("Vous êtes a l'emplacement 1 du pot")
                                binding.ivEmplacement.visibility = View.GONE
                                binding.ivEtage?.setImageResource(R.drawable.type_pot)
                                binding.tvLegendEtage!!.text = "Pot"
                                binding.ivEtage!!.setColorFilter(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.green_plantr
                                    ), android.graphics.PorterDuff.Mode.MULTIPLY
                                );
                            }
                        }
                    }
                }
            }
            "cle_en_main" -> {
               getTypeGarden()
            }
            "parcelle" -> {
                when (positionSplited[1]) {
                    "0" -> {
                        print("et êtes a l'emplacement 1 ")
                        binding.ivEmplacement.setImageDrawable(requireActivity().getDrawable(R.drawable.emplacement_1))
                        binding.tvLegendPosition!!.text = "Emplacement 1"
                    }
                    "1" -> {
                        print("et êtes a l'emplacement 2 ")
                        binding.ivEmplacement.setImageDrawable(requireActivity().getDrawable(R.drawable.emplacement_2))
                        binding.tvLegendPosition!!.text = "Emplacement 2"

                    }
                    "2" -> {
                        print("et êtes a l'emplacement 3 ")
                        binding.ivEmplacement.setImageDrawable(requireActivity().getDrawable(R.drawable.emplacement_3))
                        binding.tvLegendPosition!!.text = "Emplacement 3"

                    }
                    "3" -> {
                        print("et êtes a l'emplacement 4 ")
                        binding.ivEmplacement.setImageDrawable(requireActivity().getDrawable(R.drawable.emplacement_4))
                        binding.tvLegendPosition!!.text = "Emplacement 4"
                    }
                }
            }
            "" -> {
                when (positionSplited[1]) {
                    "0" -> {
                        print("et êtes a l'emplacement 1 ")
                        binding.ivEmplacement.setImageDrawable(requireActivity().getDrawable(R.drawable.emplacement_1))
                        binding.tvLegendPosition!!.text = "Emplacement 1"
                    }
                    "1" -> {
                        print("et êtes a l'emplacement 2 ")
                        binding.ivEmplacement.setImageDrawable(requireActivity().getDrawable(R.drawable.emplacement_2))
                        binding.tvLegendPosition!!.text = "Emplacement 2"

                    }
                    "2" -> {
                        print("et êtes a l'emplacement 3 ")
                        binding.ivEmplacement.setImageDrawable(requireActivity().getDrawable(R.drawable.emplacement_3))
                        binding.tvLegendPosition!!.text = "Emplacement 3"

                    }
                    "3" -> {
                        print("et êtes a l'emplacement 4 ")
                        binding.ivEmplacement.setImageDrawable(requireActivity().getDrawable(R.drawable.emplacement_4))
                        binding.tvLegendPosition!!.text = "Emplacement 4"
                    }
                }
            }
            else -> {
                when (positionSplited[1]) {
                    "0" -> {
                        print("et êtes a l'emplacement 1 ")
                        binding.ivEmplacement.setImageDrawable(requireActivity().getDrawable(R.drawable.emplacement_1))
                        binding.tvLegendPosition!!.text = "Emplacement 1"
                    }
                    "1" -> {
                        print("et êtes a l'emplacement 2 ")
                        binding.ivEmplacement.setImageDrawable(requireActivity().getDrawable(R.drawable.emplacement_2))
                        binding.tvLegendPosition!!.text = "Emplacement 2"

                    }
                    "2" -> {
                        print("et êtes a l'emplacement 3 ")
                        binding.ivEmplacement.setImageDrawable(requireActivity().getDrawable(R.drawable.emplacement_3))
                        binding.tvLegendPosition!!.text = "Emplacement 3"

                    }
                    "3" -> {
                        print("et êtes a l'emplacement 4 ")
                        binding.ivEmplacement.setImageDrawable(requireActivity().getDrawable(R.drawable.emplacement_4))
                        binding.tvLegendPosition!!.text = "Emplacement 4"
                    }
                }
            }
        }

        profilViewModel.userService.stage?.let {  stage ->
            checkStageOrRangs(stage, profilViewModel.userService.rangs?.let { it }?:null, positionSplited)
        }




        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        setTaskViewPicture()
    }



    private fun checkStageOrRangs(stage : String, rangs : Int?, positionSplited : List<String>) {
        println("****************Check Stage Or Rangs******************\n")
        println("Stage : " + profilViewModel.userService.stage+"\n")
        println("Rangs : " + profilViewModel.userService.rangs)
        if(rangs != null && rangs != -1 && stage == "4")  {
            //Rangs est différent de nul et différent de moins 1 et il y a 4 étages d'existant
            when(positionSplited[0]) {
                //Le plante regardé est potentiellement dans un rang de la parcelle, alors nous vérifions ça ensemble
                "0" -> {
                    println("Vous êtes au quatrieme emplacement d'etage")
                    binding.ivEtage!!.setImageDrawable(requireActivity().getDrawable(R.drawable.icon_etage4_4))
                    binding.tvLegendEtage!!.text = "Étage 4"
                }
                "1" -> {
                    println("Vous êtes au troisième emplacement d'etage")
                    binding.ivEtage!!.setImageDrawable(requireActivity().getDrawable(R.drawable.icon_etage4_3))
                    binding.tvLegendEtage!!.text = "Étage 3"
                }
                "2" -> {
                    println("Vous êtes au deuxième emplacement d'etage")
                    binding.ivEtage!!.setImageDrawable(requireActivity().getDrawable(R.drawable.icon_etage4_2))
                    binding.tvLegendEtage!!.text = "Étage 2"
                }
                "3" -> {
                    print("Vous êtes au premier emplacement d'etage")
                    binding.ivEtage!!.setImageDrawable(requireActivity().getDrawable(R.drawable.icon_etage4_1))
                    binding.tvLegendEtage!!.text = "Étage 1"
                }
                "4" -> {
                    print("Vous êtes au premier emplacement de rangs")
                    binding.ivEtage!!.setImageDrawable(requireActivity().getDrawable(R.drawable.rang1_2))
                    binding.ivEtage!!.setColorFilter(ContextCompat.getColor(requireContext(), R.color.green_plantr), android.graphics.PorterDuff.Mode.MULTIPLY);
                    binding.tvLegendEtage!!.text = "Rangée 2"
                }
                "5" -> {
                    print("Vous êtes au premier emplacement de rangs")
                    binding.ivEtage!!.setImageDrawable(requireActivity().getDrawable(R.drawable.rang2_2))
                    binding.ivEtage!!.setColorFilter(ContextCompat.getColor(requireContext(), R.color.green_plantr), android.graphics.PorterDuff.Mode.MULTIPLY);
                    binding.tvLegendEtage!!.text = "Rangée 1"
                }
            }
        }

        if(rangs == null) {
            when (stage) {
                "4" -> {
                    println("Jardinière de 4 étages")
                    when (positionSplited[0]) {
                        "0" -> {
                            println("Vous êtes au quatrieme emplacement d'etage")
                            binding.ivEtage!!.setImageDrawable(requireActivity().getDrawable(R.drawable.icon_etage4_4))
                            binding.tvLegendEtage!!.text = "Étage 4"
                        }
                        "1" -> {
                            println("Vous êtes au troisième emplacement d'etage")
                            binding.ivEtage!!.setImageDrawable(requireActivity().getDrawable(R.drawable.icon_etage4_3))
                            binding.tvLegendEtage!!.text = "Étage 3"
                        }
                        "2" -> {
                            println("Vous êtes au deuxième emplacement d'etage")
                            binding.ivEtage!!.setImageDrawable(requireActivity().getDrawable(R.drawable.icon_etage4_2))
                            binding.tvLegendEtage!!.text = "Étage 2"
                        }
                        "3" -> {
                            print("Vous êtes au premier emplacement d'etage")
                            binding.ivEtage!!.setImageDrawable(requireActivity().getDrawable(R.drawable.icon_etage4_1))
                            binding.tvLegendEtage!!.text = "Étage 1"
                        }
                        "4" -> {
                            print("Vous êtes au premier RANGS OUAIS")
                            binding.ivEtage!!.setImageDrawable(requireActivity().getDrawable(R.drawable.icon_etage4_1))
                            binding.tvLegendEtage!!.text = "Étage 1"
                        }
                    }
                }
                "3" -> {
                    println("Jardinière de 3 étages")
                    when (positionSplited[0]) {
                        "0" -> {
                            println("Vous êtes au troisieme emplacement d'etage")
                            binding.ivEtage!!.setImageDrawable(requireActivity().getDrawable(R.drawable.icon_etage3_3))
                            binding.tvLegendEtage!!.text = "Étage 3"
                        }
                        "1" -> {
                            println("Vous êtes au deuxieme emplacement d'etage")
                            binding.ivEtage!!.setImageDrawable(requireActivity().getDrawable(R.drawable.icon_etage3_2))
                            binding.tvLegendEtage!!.text = "Étage 2"
                        }
                        "2" -> {
                            print("Vous êtes au premier emplacement d'etage")
                            binding.ivEtage!!.setImageDrawable(requireActivity().getDrawable(R.drawable.icon_etage3_1))
                            binding.tvLegendEtage!!.text = "Étage 1"
                        }
                    }
                }
                "2" -> {
                    println("Jardinière de 2 étages")
                    when (positionSplited[0]) {
                        "0" -> {
                            println("Vous êtes au 2eme emplacement d'etage")
                            binding.ivEtage.setImageDrawable(requireActivity().getDrawable(R.drawable.icon_etage2_2))
                            binding.tvLegendEtage.text = "Étage 2"
                        }
                        "1" -> {
                            println("Vous êtes au deuxieme emplacement d'etage")
                            binding.ivEtage!!.setImageDrawable(requireActivity().getDrawable(R.drawable.icon_etage2_1))
                            binding.tvLegendEtage!!.text = "Étage 1"
                        }
                    }
                }
            }

//            when (positionSplited[1]) {
//                "0" -> {
//                    print("et êtes a l'emplacement 1 ")
//                    binding.ivEmplacement.setImageDrawable(requireActivity().getDrawable(R.drawable.emplacement_1))
//                    binding.tvLegendPosition!!.text = "Emplacement 1"
//                }
//                "1" -> {
//                    print("et êtes a l'emplacement 2 ")
//                    binding.ivEmplacement.setImageDrawable(requireActivity().getDrawable(R.drawable.emplacement_2))
//                    binding.tvLegendPosition!!.text = "Emplacement 2"
//
//                }
//                "2" -> {
//                    print("et êtes a l'emplacement 3 ")
//                    binding.ivEmplacement.setImageDrawable(requireActivity().getDrawable(R.drawable.emplacement_3))
//                    binding.tvLegendPosition!!.text = "Emplacement 3"
//
//                }
//                "3" -> {
//                    print("et êtes a l'emplacement 4 ")
//                    binding.ivEmplacement.setImageDrawable(requireActivity().getDrawable(R.drawable.emplacement_4))
//                    binding.tvLegendPosition!!.text = "Emplacement 4"
//                }
//            }
        }
    }

    private fun setTaskViewPicture() {
        //   binding.tvTitleTask.text = data.title.capitalize()
        ///  binding.tvDescription.text = data.description
        getPlantNameObserver(data.id)
        storageRef = FirebaseService().getStoragePlantsReference()
            .child(data.id).child(path)
        storageRef.downloadUrl.addOnSuccessListener {
            //  Glide.with(binding.ivPlants.context)
            //      .load(it.toString())
            //      .into(binding.ivPlants)
        }

    }

    private fun getPlantNameObserver(plantId: String) {
        plantRef = FirebaseService().getPlantsReferenceById(plantId)
        handlePlant = plantRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var plant = snapshot.getValue(Plant::class.java)
                if (plant != null) {
                    binding.tvNameMyPlant.text = plant.name?.toUpperCase()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }


    override fun onResume() {
        super.onResume()
        println("Nom de la plante" + data.id)
        setGardenerObserver(data.guid, data.position, data.listPosition)
        Log.e("FRAGMENT TIPS TASK", "LIST POSITION : " + data.listPosition)
    }

    private fun setGardenerObserver(guid: String, position: String, listPosition: Int) {
        gardenerRef = FirebaseService().getGardenerPlantsById(guid).child(position).child("tasks")
            .child(listPosition.toString())
        handleGardener = gardenerRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                println("${PAGE} onCancelled ${error.toException()}")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val ficheMission = snapshot.getValue(Tasks::class.java)
                if (ficheMission != null) {

                    tasksToDone = ficheMission
                    if (ficheMission.priority == 1) {
                        binding.ivPrio.visibility = View.VISIBLE
                        binding.tvPrioText.text = getString(R.string.tv_fortement_recommand)
                    } else if (ficheMission.priority == 2) {
                        binding.ivPrio.visibility = View.GONE
                        binding.tvPrioText.text =  getString(R.string.tv_conseil)
                    } else {
                        binding.tvPrioText.text =  getString(R.string.tv_recommand)
                        binding.ivPrio.visibility = View.GONE
                        println("Il y a une erreur dans la priority de la plante " + data.id.capitalize())
                    }

                    binding.tvTimeText.text = ficheMission.doInTime

                    if (ficheMission.url!!.isNotEmpty()) {
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
                                            player.loadVideo(ficheMission.url)
                                            player.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT)
                                            if (wasRestored) {
                                                player.setShowFullscreenButton(false);
                                                player.setFullscreen(false)
                                                player.loadVideo(ficheMission.url)
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
                    SetStepsLoad(ficheMission.steps)
                    SetToolsLoad(ficheMission.tools)
                }
            }
        })
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
//                                Constants.youtubeApiKey",
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

    private fun SetStepsLoad(Steps: ArrayList<Steps>) {

        mySteps.clear()
        Steps.forEach {
            it.title?.let { it1 ->
                StepsList(data.id,
                    it1, it.description, it.image ?: null)
            }?.let { it2 -> mySteps.add(it2) }
            println("JE SUIS DANS STEPS, j'affiche le titre : " + it.title)
        }

        println("Je print mon mySteps " + mySteps)
        adapter.submitList(mySteps.toList())
    }

    private fun SetToolsLoad(Tools: HashMap<String, Boolean>) {

        Tools.forEach {
            when (it.key) {
                "pulverisateur" -> {
                    binding.ivToolsSpray?.visibility = View.VISIBLE

                }
                "pelle " -> {
                    binding.ivToolsPelle?.visibility = View.VISIBLE

                }
                "gants" -> {
                    binding.ivToolsGants?.visibility = View.VISIBLE

                }
                "rateau" -> {
                    binding.ivToolsRateau?.visibility = View.VISIBLE

                }
                "secateur" -> {
                    binding.ivToolsSecateur?.visibility = View.VISIBLE

                }
                "binette" -> {
                    binding.ivToolsBinette?.visibility = View.VISIBLE

                }
                "ficelle" -> {
                    binding.ivToolsFicelle?.visibility = View.VISIBLE

                }
                "arrosoir" -> {
                    binding.ivToolsArrosoir?.visibility = View.VISIBLE

                }
                "couteau" -> {
                    binding.ivToolsCouteau?.visibility = View.VISIBLE

                }
                "verre" -> {
                    binding.ivToolsVerre?.visibility = View.VISIBLE

                }
                "tuteur" -> {
                    binding.ivToolsTuteur?.visibility = View.VISIBLE

                }
                "semoir" -> {
                    binding.ivToolsSemoir?.visibility = View.VISIBLE

                }
                "pinceau" -> {
                    binding.ivToolsPinceau?.visibility = View.VISIBLE

                }
                "fertilisant" -> {
                    binding.ivToolsFertilisant?.visibility = View.VISIBLE

                }
                "godets" -> {
                    binding.ivToolsGodets?.visibility = View.VISIBLE

                }
                "ciseaux" -> {
                    binding.ivToolsCiseaux?.visibility = View.VISIBLE

                }
                "savon" -> {
                    binding.ivToolsSavon?.visibility = View.VISIBLE

                }

            }
        }

    }

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

    }
}
