package fr.devid.plantR
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.navigation.fragment.findNavController
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import fr.devid.plantR.base.BaseActivity
import fr.devid.plantR.services.MyFirebaseMessagingService
import fr.devid.plantR.services.SharedPreferencesService
import fr.devid.plantR.services.Singleton
import fr.devid.plantR.utils.NotificationClickUtils
import io.branch.referral.Branch
import timber.log.Timber
import javax.inject.Inject

class MainActivity : BaseActivity() {

    @Inject
    lateinit var sharedPreferencesService: SharedPreferencesService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
           setContentView(R.layout.activity_main)
        } catch (e: Exception) {
            println("Un probleme avec la vue créer je crois")
        }
    }

        private val callback = Branch.BranchReferralInitListener { referringParams, error ->
            try {
                if (error == null) {
                    println("*******************************/////////// \n" + referringParams.toString() + "\n*******************************///////////")
                    referringParams?.let {
                        if (it.has("type") && it.has("gardenerId")) {
                            val type = it.get("type") as String
                            val gardenerId = it.get("gardenerId") as String
                            val gardenerName = it.get("name") as String
                            sharedPreferencesService.gardenerId = gardenerId
                            sharedPreferencesService.gardenerName = gardenerName
                            sharedPreferencesService.type = type
                            sharedPreferencesService.isBranch = true
                            println("success branch")
                            val popupAddTeam = PopupAddTeam(gardenerName, gardenerId, this) { popup, bool, str ->
                                    if (bool) {
                                        popup.dismiss()
//                                        val intent =
//                                            Intent(this, MainActivity::class.java)
//                                        this.startActivity(intent)
//                                        finishAffinity() Pour redémarrer l'app
                                    } else {
                                        popup.dismiss()
                                    }
                                }
                            popupAddTeam.show()

                        }
                    }
                } else {
                    println(error.message)
                    sharedPreferencesService.isBranch = false
                    sharedPreferencesService.gardenerId = null
                    sharedPreferencesService.gardenerName = null
                    sharedPreferencesService.type = null
                    Timber.e("BRANCH SDK error: %s", error.message)
                }
            } catch (e: Exception){
                println("un probleme dans main activity branch sdk")
            }


        }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Timber.e("onNewIntent")
        println("* mon print intent * " + intent?.extras)
        Branch.sessionBuilder(this).withCallback(callback).reInit()

    }

    override fun onDestroy() {
        println("Je passe dans la déconnexion !!!!")
        if (Singleton.instance.connectedState) {
            Singleton.instance.stopCyclicTaskData()
            println("Deconnexion du bleDevice")
                Handler().postDelayed({ //Lancement de la task qui clear le user task
                    println("Deconnexion de l'appli")
                    BleManager.getInstance().disconnectAllDevice()
                    BleManager.getInstance().destroy()
                    Singleton.instance.connectedState = false
                }, 600)
        }
        super.onDestroy()
    }
    override fun onResume() {
        super.onResume()
    }


    override fun onStart() {
        super.onStart()
        try {
            Branch.sessionBuilder(this).withCallback(callback).withData(this.intent?.data).init()

        } catch (e: Exception) {

        }
    }

}
