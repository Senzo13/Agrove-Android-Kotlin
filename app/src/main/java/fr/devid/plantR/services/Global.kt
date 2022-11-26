package fr.devid.plantR.services

import android.app.Application

internal class BleState : Application() {
    var stateBle : Int = 0

    fun getState() : Int {
        return stateBle
    }

    fun setState(i: Int) {
        stateBle = i
    }
}