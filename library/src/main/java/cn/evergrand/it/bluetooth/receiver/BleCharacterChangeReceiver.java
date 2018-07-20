package cn.evergrand.it.bluetooth.receiver;

import android.content.Context;
import android.content.Intent;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import cn.evergrand.it.bluetooth.BlueToothConstants;
import cn.evergrand.it.bluetooth.receiver.listener.BleCharacterChangeListener;
import cn.evergrand.it.bluetooth.receiver.listener.BluetoothReceiverListener;

public class BleCharacterChangeReceiver extends AbsBluetoothReceiver {

    private static final String[] ACTIONS = {
            BlueToothConstants.ACTION_CHARACTER_CHANGED
    };

    protected BleCharacterChangeReceiver(IReceiverDispatcher dispatcher) {
        super(dispatcher);
    }

    public static BleCharacterChangeReceiver newInstance(IReceiverDispatcher dispatcher) {
        return new BleCharacterChangeReceiver(dispatcher);
    }

    @Override
    List<String> getActions() {
        return Arrays.asList(ACTIONS);
    }

    @Override
    boolean onReceive(Context context, Intent intent) {
        String mac = intent.getStringExtra(BlueToothConstants.EXTRA_MAC);
        UUID service = (UUID) intent.getSerializableExtra(BlueToothConstants.EXTRA_SERVICE_UUID);
        UUID character = (UUID) intent.getSerializableExtra(BlueToothConstants.EXTRA_CHARACTER_UUID);
        byte[] value = intent.getByteArrayExtra(BlueToothConstants.EXTRA_BYTE_VALUE);
        onCharacterChanged(mac, service, character, value);
        return true;
    }

    private void onCharacterChanged(String mac, UUID service, UUID character, byte[] value) {
        List<BluetoothReceiverListener> listeners = getListeners(BleCharacterChangeListener.class);
        for (BluetoothReceiverListener listener : listeners) {
            listener.invoke(mac, service, character, value);
        }
    }
}
