package fr.devid.plantR.utils

import android.content.Context
import fr.devid.plantR.R

class NotificationString() {

    fun getString(context : Context, string : String) : String {
        return when (string) {
            "Battery" -> "${context.getString(R.string.tv_battery_title)}/${context.getString(R.string.tv_battery_description)}"
            "WaterLevel" -> "${context.getString(R.string.tv_waterLevel_title)}/${context.getString(R.string.tv_waterLevel_description)}"
            "Capa" -> "${context.getString(R.string.tv_capa_title)}/${context.getString(R.string.tv_capa_description)}"
            "LoraTs" -> "${context.getString(R.string.tv_panne_title)}/${context.getString(R.string.tv_panne_description)}"
            else -> string
        }
    }
}