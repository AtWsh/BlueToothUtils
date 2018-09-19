package cn.evergrand.it.bluetooth;


import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;

import com.inuker.bluetooth.library.IBluetoothService;
import com.inuker.bluetooth.library.IResponse;

import java.util.UUID;

import cn.evergrand.it.bluetooth.connect.BleConnectManager;
import cn.evergrand.it.bluetooth.connect.options.ConnectOptions;
import cn.evergrand.it.bluetooth.connect.response.BleGeneralResponse;
import cn.evergrand.it.bluetooth.search.BluetoothSearchManager;
import cn.evergrand.it.bluetooth.search.SearchRequest;
import cn.evergrand.it.bluetooth.utils.BluetoothLog;

public class BluetoothServiceImpl extends IBluetoothService.Stub implements Handler.Callback {

    private static BluetoothServiceImpl sInstance;

    private Handler mHandler;

    private BluetoothServiceImpl() {
        mHandler = new Handler(Looper.getMainLooper(), this);
    }

    public static BluetoothServiceImpl getInstance() {
        if (sInstance == null) {
            synchronized (BluetoothServiceImpl.class) {
                if (sInstance == null) {
                    sInstance = new BluetoothServiceImpl();
                }
            }
        }
        return sInstance;
    }

    @Override
    public void callBluetoothApi(int code, Bundle args, final IResponse response) throws RemoteException {
        Message msg = mHandler.obtainMessage(code, new BleGeneralResponse() {

            @Override
            public void onResponse(int code, Bundle data, int requestId) {
                if (response != null) {
                    if (data == null) {
                        data = new Bundle();
                    }
                    try {
                        response.onResponse(code, data);
                    } catch (Throwable e) {
                        BluetoothLog.e(e);
                    }
                }
            }
        });

        args.setClassLoader(getClass().getClassLoader());
        msg.setData(args);
        msg.sendToTarget();
    }

    @Override
    public boolean handleMessage(Message msg) {
        Bundle args = msg.getData();
        String mac = args.getString(BlueToothConstants.EXTRA_MAC);
        UUID service = (UUID) args.getSerializable(BlueToothConstants.EXTRA_SERVICE_UUID);
        UUID character = (UUID) args.getSerializable(BlueToothConstants.EXTRA_CHARACTER_UUID);
        UUID descriptor = (UUID) args.getSerializable(BlueToothConstants.EXTRA_DESCRIPTOR_UUID);
        byte[] value = args.getByteArray(BlueToothConstants.EXTRA_BYTE_VALUE);
        BleGeneralResponse response = (BleGeneralResponse) msg.obj;

        switch (msg.what) {
            case BlueToothConstants.CODE_CONNECT_BLE:
                ConnectOptions options = args.getParcelable(BlueToothConstants.EXTRA_OPTIONS);
                BleConnectManager.connect(mac, options, response);
                break;

            case BlueToothConstants.CODE_DISCONNECT:
                BleConnectManager.disconnect(mac);
                break;

            case BlueToothConstants.CODE_READ:
                BleConnectManager.read(mac, service, character, response);
                break;

            case BlueToothConstants.CODE_WRITE:
                BleConnectManager.write(mac, service, character, value, response);
                break;

            case BlueToothConstants.CODE_WRITE_NORSP:
                BleConnectManager.writeNoRsp(mac, service, character, value, response);
                break;

            case BlueToothConstants.CODE_READ_DESCRIPTOR:
                BleConnectManager.readDescriptor(mac, service, character, descriptor, response);
                break;

            case BlueToothConstants.CODE_WRITE_DESCRIPTOR:
                BleConnectManager.writeDescriptor(mac, service, character, descriptor, value, response);
                break;

            case BlueToothConstants.CODE_NOTIFY:
                BleConnectManager.notify(mac, service, character, response);
                break;

            case BlueToothConstants.CODE_UNNOTIFY:
                BleConnectManager.unnotify(mac, service, character, response);
                break;

            case BlueToothConstants.CODE_READ_RSSI:
                BleConnectManager.readRssi(mac, response);
                break;

            case BlueToothConstants.CODE_SEARCH:
                SearchRequest request = args.getParcelable(BlueToothConstants.EXTRA_REQUEST);
                BluetoothSearchManager.search(request, response);
                break;

            case BlueToothConstants.CODE_STOP_SESARCH:
                BluetoothSearchManager.stopSearch();
                break;

            case BlueToothConstants.CODE_INDICATE:
                BleConnectManager.indicate(mac, service, character, response);
                break;

            case BlueToothConstants.CODE_REQUEST_MTU:
                int mtu = args.getInt(BlueToothConstants.EXTRA_MTU);
                BleConnectManager.requestMtu(mac, mtu, response);
                break;

            case BlueToothConstants.CODE_CLEAR_REQUEST:
                int clearType = args.getInt(BlueToothConstants.EXTRA_TYPE, 0);
                BleConnectManager.clearRequest(mac, clearType);
                break;

            case BlueToothConstants.CODE_REFRESH_CACHE:
                BleConnectManager.refreshCache(mac);
                break;
        }
        return true;
    }
}
