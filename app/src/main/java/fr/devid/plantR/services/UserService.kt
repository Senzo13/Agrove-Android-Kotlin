package fr.devid.plantR.services

import android.bluetooth.BluetoothGattCharacteristic
import fr.devid.plantR.BleManager
import fr.devid.plantR.callback.BleNotifyCallback
import fr.devid.plantR.data.BleDevice
import fr.devid.plantR.exception.BleException
import fr.devid.plantR.manager.AirHumidityLevelModel
import fr.devid.plantR.manager.BatteryLevelModel
import fr.devid.plantR.manager.JsonParser.getStringParser
import fr.devid.plantR.manager.TempLevelModel
import fr.devid.plantR.models.PaysProperties
import fr.devid.plantR.ui.myPlants.Auxiliairage
import fr.devid.plantR.utils.HexUtil
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserService @Inject constructor() {
    var gardenerId: String? = null
    var saveGardenerId : String? = null
    var cityUser : String? = null
    var isDateSelected: Long? = null
    var stage: String? = null
    var rangs : Int? = null
    var statesGardener: String? = null
    var plantsOk : Boolean = false
    var teston : String = "Bonjour"
    @JvmName("getGardenerName1")
    public fun getGardenerNameKulu() : String? {
        return gardenerName
    }

    var properties : List<PaysProperties> = emptyList()
    var cancelJumelage = false
    var arrayTestAuxiliaire = ArrayList<Auxiliairage>()
    var data_kit_already = false
    var tempCapteur : Float = 1F
    var batteryCapteur :  Float = 1F
    var humiditeAir : Float = 1F
    var SoilMisture : Float = 1F
    var currentUsername: String? = null
    var gardenerName: String? = null
    var gardenerNameBle: String? = null
    var categorieIsSelected: String? = null
    var currentDay: String? = null
    var currentMonth: String? = null
    var kitMural = 0;
    var kitCleEnMain = 1;
    var kitCapteur = 2;
    var kitParcelle = 3;
    var stateForAddGardener : Boolean = false
    var typeGarden : String? = null
    var gardenerParent : String? = null
    var dimension : Int? = null
    var kit_dimension : Int? = null

    fun hexToString(hex: String): String {
        val sb = StringBuilder()
        var count = 0
        while (count < hex.length - 1) {
            try {
                val output =
                    hex.substring(count, count + 2) //grab the hex in pairs
                val decimal = output.toInt(16) //convert hex to decimal
                sb.append(decimal.toChar()) //convert the decimal to character
            } catch (exception: NumberFormatException) {
                Timber.d("Bonjour probleme exception de nombre $exception")
            }
            count += 2
        }
        return sb.toString()
    }
}