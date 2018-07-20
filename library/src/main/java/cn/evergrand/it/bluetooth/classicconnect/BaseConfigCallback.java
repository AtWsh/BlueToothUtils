package cn.evergrand.it.bluetooth.classicconnect;

import android.bluetooth.BluetoothDevice;

/**
 * 描述：蓝牙广播回调
 */
public interface BaseConfigCallback {

    /**
     * 连接设备
     *
     * @param device
     */
    void onConnect(BluetoothDevice device);
}
