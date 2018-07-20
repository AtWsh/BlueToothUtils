package cn.evergrand.it.bluetooth.connect.request;

import android.bluetooth.BluetoothGatt;

import cn.evergrand.it.bluetooth.BlueToothConstants;
import cn.evergrand.it.bluetooth.Code;
import cn.evergrand.it.bluetooth.connect.listener.ReadRssiListener;
import cn.evergrand.it.bluetooth.connect.response.BleGeneralResponse;

public class BleReadRssiRequest extends BleRequest implements ReadRssiListener {

    public BleReadRssiRequest(BleGeneralResponse response) {
        super(response);
    }

    @Override
    public void processRequest() {
        switch (getCurrentStatus()) {
            case BlueToothConstants.STATUS_DEVICE_DISCONNECTED:
                onRequestCompleted(Code.REQUEST_FAILED);
                break;

            case BlueToothConstants.STATUS_DEVICE_CONNECTED:
                startReadRssi();
                break;

            case BlueToothConstants.STATUS_DEVICE_SERVICE_READY:
                startReadRssi();
                break;

            default:
                onRequestCompleted(Code.REQUEST_FAILED);
                break;
        }
    }

    private void startReadRssi() {
        if (!readRemoteRssi()) {
            onRequestCompleted(Code.REQUEST_FAILED);
        } else {
            startRequestTiming();
        }
    }

    @Override
    public void onReadRemoteRssi(int rssi, int status) {
        stopRequestTiming();
        
        if (status == BluetoothGatt.GATT_SUCCESS) {
            putIntExtra(BlueToothConstants.EXTRA_RSSI, rssi);
            onRequestCompleted(Code.REQUEST_SUCCESS);
        } else {
            onRequestCompleted(Code.REQUEST_FAILED);
        }
    }
}

