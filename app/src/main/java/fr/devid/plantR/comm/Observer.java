package fr.devid.plantR.comm;


import fr.devid.plantR.data.BleDevice;

public interface Observer {

    void disConnected(BleDevice bleDevice);
}
