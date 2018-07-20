package cn.evergrand.it.bluetooth.receiver;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;

import java.util.Arrays;
import java.util.List;

import cn.evergrand.it.bluetooth.receiver.listener.BluetoothBondStateChangeListener;
import cn.evergrand.it.bluetooth.receiver.listener.BluetoothReceiverListener;

public class BluetoothBondReceiver extends AbsBluetoothReceiver {

    private static final String[] ACTIONS = {
            BluetoothDevice.ACTION_BOND_STATE_CHANGED,
    };

    protected BluetoothBondReceiver(IReceiverDispatcher dispatcher) {
        super(dispatcher);
    }

    public static BluetoothBondReceiver newInstance(IReceiverDispatcher dispatcher) {
        return new BluetoothBondReceiver(dispatcher);
    }

    @Override
    List<String> getActions() {
        return Arrays.asList(ACTIONS);
    }

    @Override
    boolean onReceive(Context context, Intent intent) {
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1);
        if (device != null) {
            onBondStateChanged(device.getAddress(), state);
        }
        return true;
    }

    private void onBondStateChanged(String mac, int bondState) {
        List<BluetoothReceiverListener> listeners = getListeners(BluetoothBondStateChangeListener.class);
        for (BluetoothReceiverListener listener : listeners) {
            listener.invoke(mac, bondState);
        }
    }
}
