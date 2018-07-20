package cn.evergrand.it.bluetooth.connect.listener;

import android.bluetooth.BluetoothGattDescriptor;

public interface WriteDescriptorListener extends GattResponseListener {

    void onDescriptorWrite(BluetoothGattDescriptor descriptor, int status);
}
