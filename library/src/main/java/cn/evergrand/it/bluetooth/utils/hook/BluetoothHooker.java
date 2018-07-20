package cn.evergrand.it.bluetooth.utils.hook;


import android.os.IBinder;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;

import cn.evergrand.it.bluetooth.utils.hook.compat.ServiceManagerCompat;
import cn.evergrand.it.bluetooth.utils.hook.utils.HookUtils;

public class BluetoothHooker {

    private static final String BLUETOOTH_MANAGER = "bluetooth_manager";

    public static void hook() {
        Method getService = ServiceManagerCompat.getService();
        IBinder iBinder = HookUtils.invoke(getService, null, BLUETOOTH_MANAGER);

        IBinder proxy = (IBinder) Proxy.newProxyInstance(iBinder.getClass().getClassLoader(),
                new Class<?>[]{IBinder.class},
                new BluetoothManagerBinderProxyHandler(iBinder));

        HashMap<String, IBinder> cache = ServiceManagerCompat.getCacheValue();
        cache.put(BLUETOOTH_MANAGER, proxy);
    }
}
