package cn.evergrand.it.bluetooth.receiver.listener;


public abstract class BluetoothBondListener extends BluetoothClientListener {

    public abstract void onBondStateChanged(String mac, int bondState);

    @Override
    public void onSyncInvoke(Object... args) {
        String mac = (String) args[0];
        int bondState = (int) args[1];
        onBondStateChanged(mac, bondState);
    }
}
