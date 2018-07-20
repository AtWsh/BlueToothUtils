package cn.evergrand.it.bluetooth.receiver.listener;

import java.util.UUID;


public abstract class BleCharacterChangeListener extends BluetoothReceiverListener {

    protected abstract void onCharacterChanged(String mac, UUID service, UUID character, byte[] value);

    @Override
    public void onInvoke(Object... args) {
        String mac = (String) args[0];
        UUID service = (UUID) args[1];
        UUID character = (UUID) args[2];
        byte[] value = (byte[]) args[3];
        onCharacterChanged(mac, service, character, value);
    }

    @Override
    public String getName() {
        return BleCharacterChangeListener.class.getSimpleName();
    }
}
