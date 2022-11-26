package fr.devid.plantR.manager

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import fr.devid.plantR.services.Singleton
import fr.devid.plantR.viewmodels.Event
import org.json.JSONObject

object JsonParser {
    var ratioCalculBle = 12
    var ratioDataBle = 2;
    fun getStringParser(str: String): JsonParserObject {
        if (isJSONValid(str)) {
            val jsonObject = JSONObject(str)
            if (jsonObject.has("name")) {
                println(str)
                return when (jsonObject.getString("name")) {
                    "RH" -> {
                        val airHumidity = Gson().fromJson(str, AirHumidityLevelModel::class.java)
                        Singleton.instance._humidityWind.postValue(Event(airHumidity.value.toFloat()))
                        Singleton.instance.humidityAirTemporaire = airHumidity.value.toString()
                        println(airHumidity.DeviceID + " value : " + airHumidity.value)
                        airHumidity
                    }
                    "Temp" -> {
                        val temp = Gson().fromJson(str, TempLevelModel::class.java)
                        println(temp.DeviceID + " value : " + temp.value)
                        Singleton.instance.tempLevelTemporaire = temp.value.toString()
                        Singleton.instance._temp.postValue(Event(temp.value.toFloat()))
                        temp
                    }
                    "Battery level" -> {
                        val battery = Gson().fromJson(str, BatteryLevelModel::class.java)
                        println(battery.DeviceID + " value : " + battery.value)
                        Singleton.instance._batteryLevel.postValue(Event(battery.value.toFloat()))
                        var calcB = battery.value.toInt()
                        Singleton.instance.batteryLevelTemporaire = calcB.toString()
                        Singleton.instance._batteryProgress.postValue(Event(calcB.toFloat()))
                        battery
                    }
                    "fdc" -> {
                        val fdc = Gson().fromJson(str, SoilMistureLevelModel::class.java)
                        println(" value : " + fdc.value?.get(1)?:0)
                        val fdcValue = fdc.value?.get(1)?:1.0
                        println("FDC : " + fdcValue)
                        println(fdcValue *100 / ratioCalculBle)
                        var calcF = fdcValue *100 / ratioCalculBle
                        Singleton.instance._humiditySol.postValue(Event(fdcValue.toFloat()))
                        Singleton.instance.capaTemporaire = fdcValue.toString()
                        Singleton.instance._capaProgress.postValue(Event(calcF.toFloat()))
                        fdc
                    }
                    else -> ErrorModel
                }
            } else {
                return ErrorModel
            }
        } else {
            return ErrorModel
        }
    }
}

enum class AgroveCapteurType {
    BATTERY_LEVEL, TEMP, SOIL_MISTURE, AIR_HUMIDITY
}

sealed class JsonParserObject
data class BatteryLevelModel(
    var DeviceID: String,
    var ssnID: String,
    var name: String,
    var value: String
) : JsonParserObject()

data class TempLevelModel(
    var DeviceID: String,
    var ssnID: String,
    var name: String,
    var value: String
) : JsonParserObject()

data class SoilMistureLevelModel(
    var DeviceID: String,
    var ssnID: String,
    var name: String,
    var value: ArrayList<Double>?
) : JsonParserObject()

data class AirHumidityLevelModel(
    var DeviceID: String,
    var ssnID: String,
    var name: String,
    var value: String
) : JsonParserObject()

object ErrorModel : JsonParserObject()

fun isJSONValid(jsonInString: String?): Boolean {
    return try {
        Gson().fromJson(jsonInString, Any::class.java)
        true
    } catch (ex: JsonSyntaxException) {
        false
    }
}