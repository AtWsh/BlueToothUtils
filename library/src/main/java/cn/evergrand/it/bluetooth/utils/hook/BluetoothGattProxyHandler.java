package cn.evergrand.it.bluetooth.utils.hook;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import cn.evergrand.it.bluetooth.utils.BluetoothLog;

public class BluetoothGattProxyHandler implements InvocationHandler {

    private Object bluetoothGatt;

    BluetoothGattProxyHandler(Object bluetoothGatt) {
        this.bluetoothGatt = bluetoothGatt;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        BluetoothLog.v(String.format("IBluetoothGatt method: %s", method.getName()));
        return method.invoke(bluetoothGatt, args);
    }
}
