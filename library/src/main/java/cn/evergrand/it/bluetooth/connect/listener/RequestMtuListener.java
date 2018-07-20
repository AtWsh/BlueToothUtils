package cn.evergrand.it.bluetooth.connect.listener;


public interface RequestMtuListener extends GattResponseListener {
    void onMtuChanged(int mtu, int status);
}
