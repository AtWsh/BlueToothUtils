package cn.evergrand.it.bluetooth.utils.hook;


import android.os.IBinder;
import android.os.IInterface;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import cn.evergrand.it.bluetooth.utils.BluetoothLog;
import cn.evergrand.it.bluetooth.utils.hook.utils.HookUtils;

public class BluetoothManagerProxyHandler implements InvocationHandler {

    private Object iBluetoothManager;

    private Class<?> bluetoothGattClaz;
    private Object bluetoothGatt;

    BluetoothManagerProxyHandler(Object iBluetoothManager) {
        this.iBluetoothManager = iBluetoothManager;

        this.bluetoothGattClaz = HookUtils.getClass("android.bluetooth.IBluetoothGatt");
        Class<?> stub = HookUtils.getClass("android.bluetooth.IBluetoothManager");
        Method method = HookUtils.getMethod(stub, "getBluetoothGatt");
        this.bluetoothGatt = HookUtils.invoke(method, iBluetoothManager);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        BluetoothLog.v(String.format("IBluetoothManager method: %s", method.getName()));

        if ("getBluetoothGatt".equals(method.getName())) {
            return Proxy.newProxyInstance(proxy.getClass().getClassLoader(),
                    new Class<?>[] {IBinder.class, IInterface.class, bluetoothGattClaz},
                    new BluetoothGattProxyHandler(bluetoothGatt));
        }
        return method.invoke(iBluetoothManager, args);
    }
}
