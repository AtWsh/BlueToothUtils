package cn.evergrand.it.bluetooth.classicconnect;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * 描述：监听蓝牙广播
 *
 */
public class BluetoothReceiver extends BroadcastReceiver {

    private BaseConfigCallback mCallback;

    public BluetoothReceiver(Context context, BaseConfigCallback callback) {
        mCallback = callback;
        IntentFilter filter = new IntentFilter();
        //设备建立连接
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);

        context.registerReceiver(this, filter);

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        BluetoothDevice dev;
        if (action == null) {
            return;
        }
        if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
            dev = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            mCallback.onConnect(dev);
        }
    }
}