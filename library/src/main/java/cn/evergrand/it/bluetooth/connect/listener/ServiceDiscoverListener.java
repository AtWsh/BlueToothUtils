package cn.evergrand.it.bluetooth.connect.listener;


import cn.evergrand.it.bluetooth.model.BleGattProfile;

public interface ServiceDiscoverListener extends GattResponseListener {
    void onServicesDiscovered(int status, BleGattProfile profile);
}
