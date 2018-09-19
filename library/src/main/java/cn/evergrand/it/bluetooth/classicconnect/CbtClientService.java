package cn.evergrand.it.bluetooth.classicconnect;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.UUID;

import cn.evergrand.it.bluetooth.BlueToothConstants;
import cn.evergrand.it.bluetooth.connect.response.ConnectResponse;

/**
 * 描述：蓝牙设备客户端服务
 *
 * @date 2018/6/1
 */
public class CbtClientService {

    private BluetoothSocket mBluetoothSocket;
    private BluetoothDevice mBluetoothDevice;
    private BluetoothAdapter mBluetoothAdapter;
    private ConnectResponse mResponse;
    public boolean isConnection = false;
    private Task mTask;

    public static CbtClientService getInstance() {
        return CbtClientServiceHolder.CBT_CLIENT_SERVICE;
    }

    private static class CbtClientServiceHolder {
        private static final CbtClientService CBT_CLIENT_SERVICE = new CbtClientService();
    }

    /**
     * 初始化
     *
     * @return
     */
    public void init(BluetoothAdapter bluetoothAdapter, BluetoothDevice device, ConnectResponse callBack) {
        mResponse = callBack;
        if (mBluetoothDevice != null) {
            if (mBluetoothAdapter.getAddress().equals(device.getAddress())) {
                mResponse.onResponse(BlueToothConstants.REQUEST_SUCCESS, null, 0);
                return;
            } else {
                cancel();
            }
        }

        mBluetoothAdapter = bluetoothAdapter;
        mBluetoothDevice = device;
        BluetoothSocket tmp = null;
        try {
            //尝试建立安全的连接
            tmp = mBluetoothDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
        } catch (IOException e) {
            mResponse.onResponse(BlueToothConstants.REQUEST_FAILED, null, 0);
        }
        mBluetoothSocket = tmp;
        connect();
    }

    private void connect() {
        mTask = new Task();
        mTask.execute();
    }

    private class Task extends AsyncTask<Void, Integer, Boolean> {

        boolean isFaild;

        @Override
        protected void onPreExecute() {
            //
        }

        @Override
        protected void onProgressUpdate(Integer... progresses) {
            //
        }

        //onPostExecute方法用于在执行完后台任务后更新UI,显示结果
        @Override
        protected void onPostExecute(Boolean isFaild) {
           //
            if (isFaild) {
                mResponse.onResponse(BlueToothConstants.REQUEST_FAILED, null, 0);
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try{
                if (mBluetoothAdapter.isDiscovering()) {
                    mBluetoothAdapter.cancelDiscovery();
                }
                mBluetoothSocket.connect();
            }catch (Exception connectException) {
                try {
                    Method m = mBluetoothDevice.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
                    mBluetoothSocket = (BluetoothSocket) m.invoke(mBluetoothDevice, 1);
                    mBluetoothSocket.connect();
                } catch (Exception e) {
                    try{
                        mBluetoothSocket.close();
                        return true;
                    }catch (IOException ie){

                    }
                }
            }

            return false;
        }
    };

    public BluetoothSocket getBluetoothSocket() {
        return mBluetoothSocket;
    }

    public void cancel() {
        try {
            mBluetoothAdapter = null;
            mBluetoothDevice = null;
            isConnection = false;
            if (mBluetoothSocket != null) {
                mBluetoothSocket.close();
            }
        } catch (IOException e) {

        }
    }
}
