package cn.evergrand.it.bluetooth.receiver;


import android.content.Context;
import android.content.Intent;

import java.util.Arrays;
import java.util.List;

import cn.evergrand.it.bluetooth.BlueToothConstants;
import cn.evergrand.it.bluetooth.receiver.listener.BleConnectStatusChangeListener;
import cn.evergrand.it.bluetooth.receiver.listener.BluetoothReceiverListener;
import cn.evergrand.it.bluetooth.utils.BluetoothLog;

public class BleConnectStatusChangeReceiver extends AbsBluetoothReceiver {

    private static final String[] ACTIONS = {
            BlueToothConstants.ACTION_CONNECT_STATUS_CHANGED
    };

    protected BleConnectStatusChangeReceiver(IReceiverDispatcher dispatcher) {
        super(dispatcher);
    }

    public static BleConnectStatusChangeReceiver newInstance(IReceiverDispatcher dispatcher) {
        return new BleConnectStatusChangeReceiver(dispatcher);
    }

    @Override
    List<String> getActions() {
        return Arrays.asList(ACTIONS);
    }

    @Override
    boolean onReceive(Context context, Intent intent) {
        String mac = intent.getStringExtra(BlueToothConstants.EXTRA_MAC);
        int status = intent.getIntExtra(BlueToothConstants.EXTRA_STATUS, 0);

        BluetoothLog.v(String.format("onConnectStatusChanged for %s, status = %d", mac, status));
        onConnectStatusChanged(mac, status);
        return true;
    }

    private void onConnectStatusChanged(String mac, int status) {
        List<BluetoothReceiverListener> listeners = getListeners(BleConnectStatusChangeListener.class);
        for (BluetoothReceiverListener listener : listeners) {
            listener.invoke(mac, status);
        }
    }
}
