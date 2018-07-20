package cn.evergrand.it.bluetooth.connect.listener;


public interface DisconnectListener extends GattResponseListener {
    void onDisconnected();
}
