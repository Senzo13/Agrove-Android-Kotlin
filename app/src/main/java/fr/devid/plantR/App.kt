package fr.devid.plantR

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.iid.FirebaseInstanceId
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.android.DaggerApplication
import fr.devid.plantR.dagger.DaggerAppComponent
import fr.devid.plantR.services.AlerterService
import io.branch.referral.Branch
import timber.log.Timber


class App : DaggerApplication() {
    private var mFirebaseAnalytics: FirebaseAnalytics? = null


    override fun onCreate() {
        super.onCreate()
        val bundle = Bundle()
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image")
        mFirebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        Branch.enableLogging()
        Branch.getAutoInstance(this)
        AndroidThreeTen.init(this)

        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener {
            if (it.isSuccessful) {
                val token = it.result?.token
                Timber.d(token);
            }
        }

    }



    override fun applicationInjector() = DaggerAppComponent.factory().create(this)
}