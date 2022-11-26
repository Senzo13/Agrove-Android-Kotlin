package fr.devid.plantR.utils
import android.content.ComponentName
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import fr.devid.plantR.services.FirebaseService
import fr.devid.plantR.ui.home.HomeFragmentDirections
import fr.devid.plantR.ui.home.ProfileViewModel

class NotificationClickUtils {

    fun hasNotificationClick(activity : FragmentActivity, findNavController : NavController) {
        //val recupDonnee = requireActivity().intent.getStringExtra("taskId")

        val hasNotificationClick = activity.intent.getBooleanExtra("hasNotificationClicked", false)
        if (hasNotificationClick) {
            activity.intent.removeExtra("hasNotificationClicked")
            println("* notification reçu *")
            println("* ${activity.intent.extras} *")
            val plantId = activity.intent.getStringExtra("plantId")
            val stage = activity.intent.getStringExtra("stage")
            val gardenerId = activity.intent.getStringExtra("gardenerId")
            val taskName = activity.intent.getStringExtra("taskName")
            val taskId = activity.intent.getStringExtra("taskId")
            val userId = activity.intent.getStringExtra("userId")
            println("taskName : " + taskName)
            findNavController.navigate(HomeFragmentDirections.actionHomeFragmentToFragmentNotifTaskClicked(plantId!!, stage!!, gardenerId!!, taskName!!, taskId!!, userId!!))
        }
    }

    fun hasNotificationClickTips(activity : FragmentActivity, findNavController : NavController, profilViewModel : ProfileViewModel) {
        Intent("com.jintin.clerk.LOG_ACTION").also {
            it.putExtra("data", "Dans la function en question")
            it.putExtra("channel", "specific channel for search (optional)")
            it.putExtra("app", " fr.devid.plantR")
            it.component = ComponentName("com.jintin.clerk", "com.jintin.clerk.app.LogReceiver")
            activity.sendBroadcast(it)
        }
        val hasNotificationClick = activity.intent.getBooleanExtra("hasNotificationClickedTipsState", false)
        if (hasNotificationClick) {
            Intent("com.jintin.clerk.LOG_ACTION").also {
                it.putExtra("data", "Dans le code en question des tips")
                it.putExtra("channel", "specific channel for search (optional)")
                it.putExtra("app", " fr.devid.plantR")
                it.component = ComponentName("com.jintin.clerk", "com.jintin.clerk.app.LogReceiver")
                activity.sendBroadcast(it)
            }
            activity.intent.removeExtra("hasNotificationClickedTipsState")
            println("* notification reçu du back *")
            val gardenerIdItent = activity.intent.getStringExtra("gardenerId")
            FirebaseService().firebase.currentUser?.uid?.let { uid ->
                //Je change le currentGardener de l'utilisateur afin que ça corresponde avec la notification qui la concerne
                gardenerIdItent?.let { guid -> FirebaseService().getUserCurrentGardener(uid).setValue(guid).addOnCompleteListener {
                    if(it.isSuccessful) println("Vous avez bien mis le currentGardener") else  println("Problème lors du changement")
                }
                }
            }
            Intent("com.jintin.clerk.LOG_ACTION").also {
                it.putExtra("data", "gd : " + gardenerIdItent!! + "typeGarden : " + profilViewModel.userService.typeGarden)
                it.putExtra("channel", "specific channel for search (optional)")
                it.putExtra("app", "fr.devid.plantR")
                it.component = ComponentName("com.jintin.clerk", "com.jintin.clerk.app.LogReceiver")
                activity.sendBroadcast(it)
            }
            profilViewModel.userService.gardenerId = gardenerIdItent!!
            findNavController.navigate(HomeFragmentDirections.actionHomeFragmentToPlantrFragment(gardenerIdItent))
        }

    }
}