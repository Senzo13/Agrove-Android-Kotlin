package fr.devid.plantR.services

import android.app.Activity
import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.tapadoo.alerter.Alerter
import fr.devid.plantR.R

object AlerterService {
    private var jarvisLoader : JarvisLoader ? = null
    private var loaderAgrove :  LoaderAgrove ? = null
    private var jarvisLoaderSplash : JarvisLoaderSplashScreen ? = null
    private var jarvisLoaderSplashEng : JarvisLoaderSplashScreenEng ? = null

    fun showGood(error: String, activity: FragmentActivity) {
        Alerter.create(activity)
            .setText(error)
            .setDuration(1000)
            .setBackgroundColorRes(R.color.green_plantr)
            .setTextAppearance(android.R.style.TextAppearance_Medium)
            .show()
    }

    fun showGoodCardGeneral(error: String, activity: FragmentActivity) {
        Alerter.create(activity)
            .setText(error)
            .setDuration(1500)
            .setBackgroundColorRes(R.color.green_plantr)
            .setTextAppearance(android.R.style.TextAppearance_Medium)
            .show()
    }
    fun showGoodPlant(error: String, activity: FragmentActivity) {
        Alerter.create(activity)
                .setText(error)
                .setDuration(1500)
                .setBackgroundColorRes(R.color.green_plantr)
                .setTextAppearance(android.R.style.TextAppearance_Medium)
                .show()
    }
    fun showError(error: String, activity: FragmentActivity) {
        Alerter.create(activity)
            .setText(error)
            .setDuration(1000)
            .setBackgroundColorRes(R.color.red_progress_plantr)
            .show()
    }
    fun showGoodHome(error: String, activity: FragmentActivity) {
        Alerter.create(activity)
            .setText(error)
            .setDuration(2500)
            .setBackgroundColorRes(R.color.green_plantr)
            .show()
    }
    fun showGoodTemenik(error: String, activity: Activity) {
        Alerter.create(activity)
            .setText(error)
            .setDuration(1000)
            .setBackgroundColorRes(R.color.green_plantr)
            .setTextAppearance(android.R.style.TextAppearance_Medium)
            .show()
    }
    fun showDialogLoader(context: Context?, isCancelable: Boolean) {
        println("Je suis dans le showDialog")
        hideDialog()
        if (context != null) {
            try {
                loaderAgrove = LoaderAgrove(context)
                loaderAgrove?. let { jarvisLoader ->
                    jarvisLoader.show()
                }

            } catch (e : Exception) {
                e.printStackTrace()
            }
        }
    }

        fun showDialogLoaderPlant(context: Context?, isCancelable: Boolean) {
            println("Je suis dans le showDialog")
            if (context != null) {
                try {
                    jarvisLoader = JarvisLoader(context)
                    jarvisLoader?. let { jarvisLoader ->
                        jarvisLoader.setCanceledOnTouchOutside(true)
                        jarvisLoader.setCancelable(isCancelable)
                        jarvisLoader.show()
                    }

                } catch (e : Exception) {
                    e.printStackTrace()
                }
            }
        }

    fun showDialogLoaderSplash(context: Context?, isCancelable: Boolean) {
        if (context != null) {
            try {
                jarvisLoaderSplash = JarvisLoaderSplashScreen(context)
                jarvisLoaderSplash?. let { jarvisLoader ->
                    jarvisLoader.setCanceledOnTouchOutside(true)
                    jarvisLoader.setCancelable(isCancelable)
                    jarvisLoader.show()
                }

            } catch (e : Exception) {
                e.printStackTrace()
            }
        }
    }

    fun showDialogLoaderSplashEng(context: Context?, isCancelable: Boolean) {
        if (context != null) {
            try {
                jarvisLoaderSplashEng = JarvisLoaderSplashScreenEng(context)
                jarvisLoaderSplashEng?. let { jarvisLoader ->
                    jarvisLoader.setCanceledOnTouchOutside(true)
                    jarvisLoader.setCancelable(isCancelable)
                    jarvisLoader.show()
                }

            } catch (e : Exception) {
                e.printStackTrace()
            }
        }
    }

    fun hideDialogSplash() {
        if (jarvisLoaderSplash!=null && jarvisLoaderSplash?. isShowing !!) {
            jarvisLoaderSplash = try {
                jarvisLoaderSplash?.dismiss()
                null
            } catch (e: Exception) {
                null
            }
        }
    }


    fun hideDialogSplashEng() {
        if (jarvisLoaderSplashEng!=null && jarvisLoaderSplashEng?. isShowing !!) {
            jarvisLoaderSplashEng = try {
                jarvisLoaderSplashEng?.dismiss()
                null
            } catch (e: Exception) {
                null
            }
        }
    }

    fun hideDialog() {
        if (jarvisLoader!=null && jarvisLoader?. isShowing !!) {
            jarvisLoader = try {
                jarvisLoader?.dismiss()
                null
            } catch (e: Exception) {
                null
            }
        }
    }

    fun hideDialogLoader() {
        if (loaderAgrove!=null && loaderAgrove?. isShowing !!) {
            loaderAgrove = try {
                loaderAgrove?.dismiss()
                null
            } catch (e: Exception) {
                null
            }
        }
    }

}