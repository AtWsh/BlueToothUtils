package cn.evergrand.it.bluetoothtest;

import android.app.Application;

import cn.evergrand.it.bluetooth.BluetoothManager;

/**
 * author: wenshenghui
 * created on: 2018/7/18 11:50
 * description:
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //CbtManager.getInstance().init(this);

        BluetoothManager.getInstance().init(this);

    }
}
