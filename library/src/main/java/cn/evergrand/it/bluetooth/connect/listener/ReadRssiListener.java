package cn.evergrand.it.bluetooth.connect.listener;

public interface ReadRssiListener extends GattResponseListener {
    void onReadRemoteRssi(int rssi, int status);
}
