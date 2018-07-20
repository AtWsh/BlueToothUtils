package cn.evergrand.it.bluetooth.connect.request;


import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

import java.util.UUID;

import cn.evergrand.it.bluetooth.BlueToothConstants;
import cn.evergrand.it.bluetooth.Code;
import cn.evergrand.it.bluetooth.connect.listener.WriteCharacterListener;
import cn.evergrand.it.bluetooth.connect.response.BleGeneralResponse;

public class BleWriteRequest extends BleRequest implements WriteCharacterListener {

    private UUID mServiceUUID;
    private UUID mCharacterUUID;
    private byte[] mBytes;

    public BleWriteRequest(UUID service, UUID character, byte[] bytes, BleGeneralResponse response) {
        super(response);
        mServiceUUID = service;
        mCharacterUUID = character;
        mBytes = bytes;
    }

    @Override
    public void processRequest() {
        switch (getCurrentStatus()) {
            case BlueToothConstants.STATUS_DEVICE_DISCONNECTED:
                onRequestCompleted(Code.REQUEST_FAILED);
                break;

            case BlueToothConstants.STATUS_DEVICE_CONNECTED:
                startWrite();
                break;

            case BlueToothConstants.STATUS_DEVICE_SERVICE_READY:
                startWrite();
                break;

            default:
                onRequestCompleted(Code.REQUEST_FAILED);
                break;
        }
    }

    private void startWrite() {
        if (!writeCharacteristic(mServiceUUID, mCharacterUUID, mBytes)) {
            onRequestCompleted(Code.REQUEST_FAILED);
        } else {
            startRequestTiming();
        }
    }

    @Override
    public void onCharacteristicWrite(BluetoothGattCharacteristic characteristic, int status, byte[] value) {
        stopRequestTiming();

        if (status == BluetoothGatt.GATT_SUCCESS) {
            putByteArray(BlueToothConstants.EXTRA_WRITE_RES, value);
            onRequestCompleted(Code.REQUEST_SUCCESS);
        } else {
            putByteArray(BlueToothConstants.EXTRA_WRITE_RES, value);
            onRequestCompleted(Code.REQUEST_FAILED);
        }
    }
}
