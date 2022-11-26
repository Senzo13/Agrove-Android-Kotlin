package fr.devid.plantR.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.toBitmap
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import fr.devid.plantR.MainActivity
import fr.devid.plantR.R
import fr.devid.plantR.utils.NotificationString
import timber.log.Timber


class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val TAG = "FireBaseMessagingService"
    var NOTIFICATION_CHANNEL_ID = "net.larntech.notification"
    val NOTIFICATION_ID = 100

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        println("onMessageReceived => $p0")
        Timber.e("onMessageReceived OKKK")
        if (p0.data.isNotEmpty()) {
            val title = p0.data["title"] ?: ""
            val body = p0.data["body"] ?: ""
            val typeKey = p0.data["type"] ?: null
            val gardenerId = p0.data["gardenerId"] ?: null
            val data = p0.data
            val type = data.getOrDefault("type", "")
            when(type) {
                "taskDone" -> {
                    showNotification(title, body, data)
                }
                else -> {
                    showNotification(applicationContext, title, body, typeKey, gardenerId)
                }
            }
        }
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        println("onNewToken => $p0")
    }

     fun showNotification(title: String?, message: String?, data: Map<String, String>) {
         val plantID = data.getOrDefault("plantUID", "")
         val stage = data.getOrDefault("stage", "")
         val gardenerId = data.getOrDefault("gardenerId", "")
         val taskName = data.getOrDefault("taskName", "")
         val taskId = data.getOrDefault("taskId", "")
         val userId = data.getOrDefault("userId", "")
         val intent = Intent(this, MainActivity::class.java)
         intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
         intent.putExtra("hasNotificationClicked", true)
         intent.putExtra("plantId", plantID)
         intent.putExtra("stage", stage)
         intent.putExtra("gardenerId", gardenerId)
         intent.putExtra("taskName", taskName)

         intent.putExtra("taskId", taskId)
         intent.putExtra("userId", userId)
         val pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
         val channelId = 100
         val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
         val n = NotificationCompat.Builder(this, channelId.toString())
             .setLargeIcon(this.resources.getDrawable(R.mipmap.ic_launcher).toBitmap())
             .setSmallIcon(R.mipmap.ic_launcher)
             .setColor(resources.getColor(R.color.fui_transparent))
             .setContentTitle(title)
             .setAutoCancel(true)
             .setSound(defaultSoundUri)
             .setContentText(message)
             .setContentIntent(pIntent)
             .setStyle(
                 NotificationCompat.BigTextStyle()
                     .bigText(message)
             )
             .build()
         val notificationManager =
             getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
             val channel = NotificationChannel(
                 channelId.toString(),
                 "Channel human readable title",
                 NotificationManager.IMPORTANCE_HIGH
             )
             notificationManager.createNotificationChannel(channel)
         }
         notificationManager.notify(0, n)
     }

    @SuppressLint("ServiceCast")
    fun showNotification(context: Context, title: String?, message: String?, typeKey: String?, gardenerId: String?) {
        val stringSplit = typeKey?.let { NotificationString().getString(context, it) }?.split("/")
        val title0 = stringSplit?.get(0) ?: title
        val message0 = stringSplit?.get(1) ?: message
        val intent = Intent(this, MainActivity::class.java)
        if(typeKey != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.putExtra("hasNotificationClickedTipsState", true)
            intent.putExtra("gardenerId", gardenerId)
            Intent("com.jintin.clerk.LOG_ACTION").also {
                it.putExtra("data", "JAI CLIQUER ICI MON AMI")
                it.putExtra("channel", "specific channel for search (optional)")
                it.putExtra("app", " fr.devid.plantR")
                it.component = ComponentName("com.jintin.clerk", "com.jintin.clerk.app.LogReceiver")
                context.sendBroadcast(it)
            }
        }

        val pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val channelId = 100
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val n = NotificationCompat.Builder(this, channelId.toString())
            //.setLargeIcon(this.resources.getDrawable(R.mipmap.ic_launcher).toBitmap())
            .setSmallIcon(R.mipmap.ic_launcher_transparent)
            .setColor(resources.getColor(R.color.green_plantr))
            .setContentTitle(title0)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentText(message0)
            .setContentIntent(pIntent)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(message0)
            )
            .build()
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId.toString(),
                "Channel human readable title",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0, n)
    }

    private fun getNotificationIcon(): Int {
        val useWhiteIcon = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
        return if (useWhiteIcon) R.drawable.ic_launcher else R.drawable.ic_launcher
    }
}
