package cn.evergrand.it.bluetooth.classicconnect;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;

import cn.evergrand.it.bluetooth.BlueToothConstants;
import cn.evergrand.it.bluetooth.connect.response.ConnectResponse;

/**
 * 描述：Android经典蓝牙工具类
 * @date
 */
public class ClassicBtManager implements BaseConfigCallback {

    private Context mContext;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothReceiver mBluetoothReceiver;

    private ConnectResponse mResponse;

    public static ClassicBtManager getInstance() {
        return CbtManagerHolder.CBT_MANAGER;
    }

    @Override
    public void onConnect(BluetoothDevice device) {
        if (mResponse == null) {
            return;
        }
        CbtClientService.getInstance().isConnection = true;
        mResponse.onResponse(BlueToothConstants.REQUEST_SUCCESS, null);
        onDestroy();
    }

    private static class CbtManagerHolder {
        private static final ClassicBtManager CBT_MANAGER = new ClassicBtManager();
    }

    /**
     * 初始化
     *
     * @param appContext
     * @return
     */
    public ClassicBtManager init(Context appContext) {
        if (mContext == null && appContext != null) {
            mContext = appContext;
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            mBluetoothReceiver = new BluetoothReceiver(mContext, this);
        }
        return this;
    }

    public Context getContext() {
        return mContext;
    }


    /**
     * 设备连接
     *
     * @param response
     */
    public void connectDevice(String mac, ConnectResponse response) {
        mResponse = response;
        if (mBluetoothAdapter != null) {
            //配对蓝牙
            BluetoothDevice remoteDevice = mBluetoothAdapter.getRemoteDevice(mac);
            if (remoteDevice == null) {
                mResponse.onResponse(BlueToothConstants.REQUEST_FAILED, null);
                return;
            }
            CbtClientService.getInstance().init(mBluetoothAdapter, remoteDevice, response);
        }
    }



    /**
     * 关闭服务
     */
    public void onDestroy() {
        try {
            CbtClientService.getInstance().cancel();
        } catch (Exception e) {

        }
        if (mContext != null && mBluetoothReceiver != null) {
            mContext.unregisterReceiver(mBluetoothReceiver);
        }

    }
}
