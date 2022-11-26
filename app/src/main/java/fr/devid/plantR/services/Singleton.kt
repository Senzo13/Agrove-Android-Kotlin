package fr.devid.plantR.services

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import fr.devid.plantR.BleManager
import fr.devid.plantR.callback.BleNotifyCallback
import fr.devid.plantR.callback.BleReadCallback
import fr.devid.plantR.callback.BleWriteCallback
import fr.devid.plantR.data.BleDevice
import fr.devid.plantR.exception.BleException
import fr.devid.plantR.manager.JsonParser.getStringParser
import fr.devid.plantR.operation.CharacteristicOperationFragment
import fr.devid.plantR.utils.HexUtil
import fr.devid.plantR.viewmodels.Event
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.util.*
import kotlin.experimental.and

/**
 * Singleton class.
 */
class Singleton private constructor() {
    var CMD_DATA_MODE_CHANGE = byteArrayOf(
        0xa0.toByte(),
        0x1f.toByte()
    )

    var bleDevice: BleDevice? = null
    var bluetoothGattService: BluetoothGattService? = null
    var characteristic: BluetoothGattCharacteristic? = null
    var charaProp = 0
    var launchStateData = false
    var statusSucess = false
    var countCheck = 0;
    var notifyCallback: BleNotifyCallback? = null
    var mode = 2; //PERMET D UTILISER LA FUNCTION LOADSENSORDATA AVEC LE PARAMETRE VOULU
    var testage = false
    var writeFlower = false
    var ratioDataBle = 2;
    var popupSoinsVisibility = false
    var rejTeamInvit = false
    lateinit var batteryLevelTemporaire : String
    lateinit var tempLevelTemporaire : String
    lateinit var capaTemporaire : String
    lateinit var humidityAirTemporaire : String

    // VARIABLE POUR GERER LE CAS DES TIPS DANS MON POTAGER
    var batteryTips = false
    var capaTips = false


    //TYPE CAPTEUR {
    // 0 is WSE (Agrove)
    // 1 is WSH (Agrove)
    // 2 is FLOWER CARE (Xiaomi)
    var typeCapteur: String? = null

    //TYPE CAPTEUR END }
    lateinit var rawData: ByteArray
    var jsonData: String? = null
    var sensorName: String? = null
    var sensorAddress: String? = null
    var TAG = "SENSOR DATA"
    var wsh_mode_capa_slave: Int? = null

    @JvmName("setBleDevice1")
    fun setBleDevice(bleDeviceParam: BleDevice) {
        bleDevice = bleDeviceParam
    }
    val capaProgress: LiveData<Event<Float>>
        get() = _capaProgress
    val _capaProgress = MutableLiveData<Event<Float>>()

    val plantsAddedCallBack : LiveData<Event<Boolean>>
        get() = _plantsAddedCallBack
    var _plantsAddedCallBack = MutableLiveData<Event<Boolean>>()

    val apiCheckingOk : LiveData<Event<Boolean>>
        get() = _apiCheckingOk
    var _apiCheckingOk = MutableLiveData<Event<Boolean>>()

    val namePays : LiveData<Event<String>>
        get() = _namePays
    var _namePays = MutableLiveData<Event<String>>()

    val zipCode : LiveData<Event<String>>
        get() = _zipCode
    var _zipCode = MutableLiveData<Event<String>>()

    val batteryProgress: LiveData<Event<Float>>
        get() = _batteryProgress
    val _batteryProgress = MutableLiveData<Event<Float>>()

    val progressBarBle: LiveData<Event<Float>>
        get() = _progressBarBle
    val _progressBarBle = MutableLiveData<Event<Float>>()



    val batteryLevel: LiveData<Event<Float>>
        get() = _batteryLevel
    val _batteryLevel = MutableLiveData<Event<Float>>()


    val temp: LiveData<Event<Float>>
        get() = _temp
    val _temp = MutableLiveData<Event<Float>>()

    val humidityWind: LiveData<Event<Float>>
        get() = _humidityWind
    val _humidityWind = MutableLiveData<Event<Float>>()

    val humiditySol: LiveData<Event<Float?>>
        get() = _humiditySol
    val _humiditySol = MutableLiveData<Event<Float?>>()

    //Récupère l'état du ble
    var connectedState = false
    var gardenerIdResaBle: String? = null
    var gardenerId: String? = null
    var gardenerIdFORBLE: String? = null
    fun setConnected(state: Boolean) { //Actualise l'état du bluetooth, permet de set si la personne est connecté en ble ou non.
        connectedState = state
    }

    fun hideLoadingScreenByLang() {
        when(Locale.getDefault().language.toString()) {
            "fr" -> {
                AlerterService.hideDialogSplash()
            }
            "eng" -> {
                AlerterService.hideDialogSplashEng()
            }
            else -> {
                AlerterService.hideDialogSplashEng()
            }
        }
    }

    fun startNotification(
        bleDevice: BleDevice?
    ) {
        BleManager.getInstance().notify(
            bleDevice,
            object : BleNotifyCallback() {
                override fun onNotifySuccess() {
                    Timber.d("****** NOTIFY SUCESSS *****")
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                addText(txt, "notify success");
//                            }
//                        });
                }

                override fun onNotifyFailure(exception: BleException) {
                    Timber.d("****** NOTIFY FAILURE *****")
                    print("Ma notification a fail " + exception)
                    //                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                addText(txt, exception.toString());
//                            }
//                        });
                }

                override fun onCharacteristicChanged(data: ByteArray) {
                    Timber.d("****** DATA CHANGED *****")
                    val jsonBrut = hexToString(HexUtil.formatHexString(data, false))
                    Timber.d("**********Affichage du json serialize *************\n$jsonBrut")
                    val jsonparser = getStringParser(jsonBrut)
                }
            })
    }
    fun startNotificationFlowerCare(
        bleDevice: BleDevice?
    ) {
        BleManager.getInstance().notifyFlowerCare(
            bleDevice,
            object : BleNotifyCallback() {
                override fun onNotifySuccess() {
                    Timber.d("****** NOTIFY SUCESSS *****")
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                addText(txt, "notify success");
//                            }
//                        });
                }

                override fun onNotifyFailure(exception: BleException) {
                    Timber.d("****** NOTIFY FAILURE *****")
                    print("Ma notification a fail " + exception)
                    //                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                addText(txt, exception.toString());
//                            }
//                        });
                }

                override fun onCharacteristicChanged(data: ByteArray) {
                    Timber.d("****** DATA CHANGED *****")
                    val jsonBrut = hexToString(HexUtil.formatHexString(data, false))
                    println(jsonBrut)
                }
            })
    }

    companion object {
        val instance = Singleton()

    }

    fun  hexToString(hex: String): String {
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
    fun clearUserTaskData() {
        //Commande 61;63;62;;//
        val hexClear = "36313b36333b36323b3b"
        countCheck += 1
        if (TextUtils.isEmpty(hexClear)) {
            return
        }
        BleManager.getInstance().write(
            bleDevice,
            HexUtil.hexStringToBytes(hexClear),
            object : BleWriteCallback() {
                override fun onWriteSuccess(current: Int, total: Int, justWrite: ByteArray) {
                }

                override fun onWriteFailure(exception: BleException) {
                }
            })
    }


    fun readFlowerData() {
        testage = true
        Handler().postDelayed({

//        BleManager.getInstance().readFlower(bleDevice, object : BleReadCallback() {
//
//            override fun onReadSuccess(data: ByteArray?) {
//                println("data : " + data)
//                if (data != null) {
//                    if (validate(data)) {
//                        rawData = data
//                        println("data payload : " + data)
//                        println("Ajout des data !")
//                        parse()
//
//                    } else {
//                        println("Data invalid")
//                        testage = false
//
//                    }
//
//                }
//            }
//
//            override fun onReadFailure(exception: BleException?) {
//                println("Data non reçu sniff")
//            }
//        })
//
                              startNotificationFlowerCare(bleDevice)

       },700)
        Handler().postDelayed({

        testage = false
        loadSensorData(2)
    },600)
    }

    fun stopCyclicTaskData() {
        countCheck += 1
        //Commande 61;76;62;;//
        val hexStopCyclic = "36313b37363b36323b3b"
        if (TextUtils.isEmpty(hexStopCyclic)) {
            return
        }
        BleManager.getInstance().write(bleDevice, HexUtil.hexStringToBytes(hexStopCyclic),
            object : BleWriteCallback() {
                override fun onWriteSuccess(current: Int, total: Int, justWrite: ByteArray) {
                    println("StopCyclic ok")
                }

                override fun onWriteFailure(exception: BleException) {
                }
            })
    }


    fun execGetDataTask() {
        //Commande 61;78,0,3,0;75;62;;//
        val hexExecGetData = "36313b37382c302c332c303b37353b36323b3b" //Se lance toute les 2 seconde
        countCheck += 1
        if (TextUtils.isEmpty(hexExecGetData)) {
            return
        }
        BleManager.getInstance().write(bleDevice, HexUtil.hexStringToBytes(hexExecGetData),
            object : BleWriteCallback() {
                override fun onWriteSuccess(current: Int, total: Int, justWrite: ByteArray) {

                }

                override fun onWriteFailure(exception: BleException) {

                }
            })
    }

    fun getDataTask(command: String) {
        //Commande 60;101;122;123;125;128,0,0,0;102;62;;//
        countCheck += 1
        if (TextUtils.isEmpty(command)) {
            return
        }
        BleManager.getInstance().write(
            bleDevice,
            HexUtil.hexStringToBytes(command),
            object : BleWriteCallback() {
                override fun onWriteSuccess(current: Int, total: Int, justWrite: ByteArray) {
                    println("Récupération des data réussis")

                }

                override fun onWriteFailure(exception: BleException) {
                    println("Récupération des data failure")

                }
            })
    }


    fun getDataWsh() {

        BleManager.getInstance().readAgrove(bleDevice, object : BleReadCallback() {
            override fun onReadSuccess(data: ByteArray?) {
                println("data : " + data)
                if (data != null) {
                    println("PRINT OUAIIIS " + hexToString(HexUtil.formatHexString(data, false)))
                    Timber.d("****** DATA CHANGED *****")
                    val jsonBrut = hexToString(HexUtil.formatHexString(data, false))
                    Timber.d("**********Affichage du json serialize *************\n$jsonBrut")
                    getStringParser(jsonBrut)
                    testage = false
                    loadSensorData(0)
                } else {
                    println("Data invalid")
                }
            }

            override fun onReadFailure(exception: BleException?) {
                println("Failure lors de la lecture")
            }
        })
    }

    fun loadSensorData(mode: Int) {
        //DEBUG//
        print("Singleton : loadSensorData function \n")
        println("BleDevice on : " + bleDevice?.key)
        println("My capteur type is : " + typeCapteur)
        //DEBUG//
        when (mode) {
            0 -> { // Lance des get ponctuelle cyclique côté app pour le WSH afin de récupérer les données !
                println("Singleton : STATE MODE 0")

                Handler().postDelayed({
                    while(connectedState && !testage) {
                        testage = true
                        //Commande 60;101;122;123;125;128,0,0,0;102;62;;//
                        val command = "36313b3130313b3132382c302c302c303b3132323b3130323b36323b3b"
                        countCheck += 1
                        if (TextUtils.isEmpty(command)) {
                            return@postDelayed
                        }
                        BleManager.getInstance().write(
                            bleDevice,
                            HexUtil.hexStringToBytes(command),
                            object : BleWriteCallback() {
                                override fun onWriteSuccess(current: Int, total: Int, justWrite: ByteArray) {
                                    println("Récupération des data réussis")

                                }

                                override fun onWriteFailure(exception: BleException) {
                                    println("Récupération des data failure")

                                }
                            })

                        println("Singleton : STATE MODE 0")

                        Handler().postDelayed({
                            println("Lancement du cyclique task côté app")
                            getDataWsh()
                        }, 700)
                    }
                }, 800)
            }
            1 -> { // Stock avec la commande 60 les data voulu dans l'appareil cyclique tasks pour le WSE//
                println("Singleton : STATE MODE 1")
                getDataTask("36303b39393b3130333b3132303b3132313b3132333b3132353b3130343b3130303b36323b3b")
                Handler().postDelayed({
                    println("Lancement de l'ecriture")
                    execGetDataTask()
                }, 800)
                Handler().postDelayed({
                    if (bleDevice != null) {
                        startNotification(bleDevice)
                    }
                }, 1000)
            }
            2 -> { // Lance l'écriture de la CMD DATA MODE pour le capteur Flower Care et la lecture des données //
                println("Singleton : STATE MODE 2")
                if(!writeFlower) {
                    writeFlowerData()
                } else {
                    println("Ecriture du flower care deja faites !")
                }
                testage = false

                println("ConnectedState est egal a : " + connectedState)
                Handler().postDelayed({
                while(connectedState && !testage) {

                    println("Lancement de la lecture")
                        readFlowerData()

                }
            }, 1800)

        }
            3 -> {// Si le premier set de task cyclique est déjà fait, cela start directement la task qui est stocké. N'inclus pas les capteurs qui ne sont pas Agrove//
                println("Singleton : STATE MODE 3")

                BleManager.getInstance().readAgrove(bleDevice, object : BleReadCallback() {
                    override fun onReadSuccess(data: ByteArray?) {
                        println("data : " + data)
                        if (data != null) {
                            println("Data lisible du onRead : " + CharacteristicOperationFragment.hexToString(HexUtil.formatHexString(data, false)))
                            println("Une notification est déjà en cours, je lance donc la lecture de celle ci.")
                            startNotification(bleDevice)
                        } else {
                            println("Data invalid")
                        }
                    }

                    override fun onReadFailure(exception: BleException?) {
                        println("Aucune notification était lancé !\nLa procédure d'écriture est donc lancé.")
                        Handler().postDelayed({
                            println("Lancement de l'ecriture")
                            execGetDataTask()
                        }, 0)
                        Handler().postDelayed({
                            if (bleDevice != null) {
                                startNotification(bleDevice)
                            }
                        }, 400)
                    }
                })

            }
            4 -> { //Reset stop et reset clear
                println("Singleton : STATE MODE 4")
                Handler().postDelayed({
                    if (bleDevice != null) {
                        stopCyclicTaskData()
                    }
                }, 0)
                Handler().postDelayed({
                    if (bleDevice != null) {
                        clearUserTaskData()
                    }
                }, 400)
            }
        }
    }

    fun createGetDataTask() {


    }

    fun writeFlowerData() {
        BleManager.getInstance().writeFlowerCare(
            bleDevice,
            CMD_DATA_MODE_CHANGE,
            object : BleWriteCallback() {
                override fun onWriteSuccess(current: Int, total: Int, justWrite: ByteArray) {
                    println("Ecrire réussis !")
                    writeFlower = true
                }

                override fun onWriteFailure(exception: BleException) {
                    println("Ecrire pas réussis !")
                writeFlower = false
                }
            })
    }


    //        def _parse_data(self):
    //        """Parses the byte array returned by the sensor.
    //
    //        The sensor returns 16 bytes in total. It's unclear what the meaning of these bytes
    //        is beyond what is decoded in this method.
    //
    //                semantics of the data (in little endian encoding):
    //        bytes 0-1: temperature in 0.1 °C
    //        byte 2: unknown
    //        bytes 3-4: brightness in Lux
    //        bytes 5-6: unknown
    //        byte 7: conductivity in µS/cm
    //        byte 8-9: brightness in Lux
    //        bytes 10-15: unknown
    //        """
    //        data = self._cache
    //        res = dict()
    //        temp, res[MI_LIGHT], res[MI_MOISTURE], res[MI_CONDUCTIVITY] = \
    //        unpack('<hxIBhxxxxxx', data)
    //        res[MI_TEMPERATURE] = temp/10.0
    //        return res
    //    MI_TEMPERATURE = "temperature"
    //    MI_LIGHT = "light"
    //    MI_MOISTURE = "moisture"
    //    MI_CONDUCTIVITY = "conductivity"
    //    MI_BATTERY = "battery"

    fun parse() {

        var jObjectData = JSONObject()
        var temperature: Float =
            (((this.rawData.get(0) and 0xFF.toByte()) + 0x100 * (this.rawData.get(1) and 0xFF.toByte())) / 10).toFloat()
        var bright: Int =
            (this.rawData.get(3) and 0xFF.toByte()) + 0x100 * (this.rawData.get(4) and 0xFF.toByte())
        var moisture: Byte = this.rawData.get(7) and 0xFF.toByte()
        var conductivity: Int =
            (this.rawData.get(8) and 0xFF.toByte()) + 0x100 * (this.rawData.get(9) and 0xFF.toByte())
        var battery: Int =
            (this.rawData.get(5) and 0xFF.toByte()) + 0x100 * (this.rawData.get(6) and 0xFF.toByte())
        try {
            jObjectData.put("temperature", temperature.toDouble())
            jObjectData.put("brightness", bright)
            jObjectData.put("conductivity", conductivity)
            jObjectData.put("moisture", moisture)
            jObjectData.put("battery", battery)


            println("Le testage est : " + testage)

            jObjectData.put(
                "SerialNumber",
                this.sensorName?.toLowerCase() + "-" + this.sensorAddress?.toUpperCase()
            )
            Log.i(TAG, "Sensor data $moisture")
            Timber.d("bright : $bright\nmoisture : $moisture")
            jsonData = jObjectData.toString()
            println("XIAMI FLOWER CARE DATA CHANGED : \n")
            println("Battery : ${jObjectData["battery"]}")
            println("Temperature : ${jObjectData["temperature"]}")
            println("Humidité du sol : ${jObjectData["moisture"]}")
            println("Luminosité : ${jObjectData["brightness"]}")

        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }


    fun validate(data: ByteArray): Boolean {
        if (data[7] > 100) {
            return false
        }
        var sum = 0
        //for FW >= '2.6.6'
        for (i in 10 until data.size) {
            sum += data[i]
        }
        //        //for all others
//        sum=0;
//        for (int i:this.rawData) {
//            sum+=rawData[i];
//        }
        return if (sum == 0) {
            false
        } else true
    }


}