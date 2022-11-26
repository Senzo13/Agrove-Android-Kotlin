package fr.devid.plantR.callback;


import java.util.List;
import fr.devid.plantR.data.BleDevice;

public abstract class BleScanCallback implements BleScanPresenterImp {

    public abstract void onScanFinished(List<BleDevice> scanResultList);

    public void onLeScan(BleDevice bleDevice) {
    }
}
