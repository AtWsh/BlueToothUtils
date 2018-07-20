package cn.evergrand.it.bluetooth.receiver;


import java.util.List;

import cn.evergrand.it.bluetooth.receiver.listener.BluetoothReceiverListener;

public interface IReceiverDispatcher {

    List<BluetoothReceiverListener> getListeners(Class<?> clazz);
}
