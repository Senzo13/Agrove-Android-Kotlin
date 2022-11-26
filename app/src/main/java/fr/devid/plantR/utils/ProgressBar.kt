package fr.devid.plantR.utils
import android.content.Context
import android.widget.TextView
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import fr.devid.plantR.R
import fr.devid.plantR.models.GardenerStats

class ProgressBarUtils() {

    fun calculate(garden : String, stats : GardenerStats, position : Int?) : Float? {
         return when(garden){
             "cle_en_main"-> stats.battery?.plus(stats.capacities.c1!!)?.plus(stats.waterLevel!!)!!.div(3).toFloat()
             "parcelle" -> position?.let { stats.battery?.plus(if(it <= 1) stats.capacities.c1!! else stats.capacities.c2!! )!!.div(2).toFloat()}
             else -> stats.battery?.plus(stats.capacities.c1!!)!!.div(2).toFloat()
        }
    }

    fun setColorPv(context : Context, stats: Float, bar: CircularProgressBar, text : TextView) {
        bar.setProgressWithAnimation(stats, 1000)
        text.text = if(stats > 50) context.getString(R.string.tv_state_good) else if(stats > 25) context.getString(R.string.tv_state_medium) else context.getString(R.string.tv_state_bad)
        bar.apply {
            this.progressBarColor = resources.getColor(if(stats > 50) R.color.green_plantr else if(stats > 25) R.color.orange_progress_plantr else R.color.red_progress_plantr, null)
        }
    }

}