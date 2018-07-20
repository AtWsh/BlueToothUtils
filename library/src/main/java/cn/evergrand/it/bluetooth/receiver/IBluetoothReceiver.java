package cn.evergrand.it.bluetooth.receiver;

import cn.evergrand.it.bluetooth.receiver.listener.BluetoothReceiverListener;

public interface IBluetoothReceiver {

    void register(BluetoothReceiverListener listener);
}
