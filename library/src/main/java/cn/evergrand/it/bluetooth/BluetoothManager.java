package cn.evergrand.it.bluetooth;


import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import java.util.UUID;

import cn.evergrand.it.bluetooth.bean.BlueToothNotifyParams;
import cn.evergrand.it.bluetooth.bean.BlueToothReadParams;
import cn.evergrand.it.bluetooth.bean.BlueToothUnnotifyParams;
import cn.evergrand.it.bluetooth.bean.BlueToothWriteParams;
import cn.evergrand.it.bluetooth.connect.listener.BluetoothStateListener;
import cn.evergrand.it.bluetooth.connect.options.ConnectOptions;
import cn.evergrand.it.bluetooth.connect.response.BleNotifyResponse;
import cn.evergrand.it.bluetooth.connect.response.BleReadResponse;
import cn.evergrand.it.bluetooth.connect.response.BleUnnotifyResponse;
import cn.evergrand.it.bluetooth.connect.response.BleWriteResponse;
import cn.evergrand.it.bluetooth.connect.response.ConnectResponse;
import cn.evergrand.it.bluetooth.encry.IBlueToothDecrypt;
import cn.evergrand.it.bluetooth.encry.IBlueToothEncryAndDecry;
import cn.evergrand.it.bluetooth.search.SearchRequest;
import cn.evergrand.it.bluetooth.search.SearchResult;
import cn.evergrand.it.bluetooth.search.response.SearchErrorState;
import cn.evergrand.it.bluetooth.search.response.SearchResponse;
import cn.evergrand.it.bluetooth.utils.BluetoothLog;
import cn.evergrand.it.bluetooth.utils.ByteUtils;
import cn.evergrand.it.bluetooth.utils.ClsUtils;
import cn.evergrand.it.bluetooth.utils.DataUtils;

/**
 * author: wenshenghui
 * created on: 2018/6/21 15:44
 * description:蓝牙设备操作管理 （对外开放）
 */
public class BluetoothManager {

    private static final String TAG = "BluetoothManager";
    private static volatile BluetoothManager sInstance = null;
    private BluetoothClient mClient;

    private static final int TRY_OPEN_BT_DELAY = 10000;
    private ConnectResponse mConnectResponse;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private boolean mNeedInterceptPinDialog = false; //是否拦截Pin码提示弹窗


    private BluetoothManager() {}

    /**
     * 框架使用的初始工作
     * @param context
     */
    public void init(Context context) {
        if (mClient == null) {
            mClient = new BluetoothClient(context);
        }

        registerPairingRequest(context);
    }

    public static BluetoothManager getInstance() {
        if (sInstance == null) {
            synchronized (BluetoothManager.class) {
                if (sInstance == null) {
                    sInstance = new BluetoothManager();
                }
            }
        }
        return sInstance;
    }

    /**
     * 尝试开启蓝牙，在连接蓝牙，没开启蓝牙时，优先开启
     */
    private void tryOpenBle(final SearchResponse searchResponse) {
        if (!mClient.isBleSupported()) {
            searchResponse.onSearchError(SearchErrorState.BLE_NOT_SUPPORT);
            return;
        }
        if (!mClient.isBluetoothOpened()) {
            mClient.openBluetooth();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mClient.isBluetoothOpened()) {
                        searchBle(searchResponse);
                    }else {
                        searchResponse.onSearchError(SearchErrorState.USER_CANCEL_OPEN_BT);
                    }
                }
            }, TRY_OPEN_BT_DELAY);
        }
    }

    /**
     * 开始扫描蓝牙，默认请求配置
     *
     * @param searchReponse 扫描回调
     */
    public void searchBle(SearchResponse searchReponse) {
        //startTimer(16,BLE_SEARCH_FAIL);
        //mBleEventLisenter = lisenter;
        boolean opened = mClient.isBluetoothOpened();
        if (opened) {
            if (mClient != null) {
                SearchRequest request = new SearchRequest.Builder()
                        .searchBluetoothLeDevice(BlueToothConstants.BLE_SEARCH_DURATION, BlueToothConstants.BLE_SEARCH_TIMES)   // 先扫BLE设备2次，每次8s
                        .searchBluetoothClassicDevice(BlueToothConstants.CLASS_SEARCH_DURATION) //经典蓝牙扫描8s
                        .build();
                mClient.search(request, searchReponse);
            }
        }else {
            tryOpenBle(searchReponse);
        }
    }

    /**
     * 开始搜索蓝牙,自定义搜索请求配置
     *
     * @param searchReponse 扫描回调
     * @param request       扫描配置
     */
    public void searchBle(SearchResponse searchReponse, SearchRequest request) {
        //startTimer(16,BLE_SEARCH_FAIL);
        //mBleEventLisenter = lisenter;
        tryOpenBle(searchReponse);
        if (mClient != null) {
            mClient.search(request, searchReponse);
        }
    }

    /**
     * 停止搜索
     */
    public void stopSearch() {
        if (mClient != null) {
            mClient.stopSearch();
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    /**
     * 连接经典蓝牙，默认连接配置
     *
     * @param device
     * @param connectResponse
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    private void connectClassic(final BluetoothDevice device, ConnectResponse connectResponse) {
        if (device == null) {
            return;
        }

        mConnectResponse = connectResponse;
        mClient.connectClassic(device.getAddress(), connectResponse);
    }

    /**
     * 连接蓝牙，默认连接配置
     *
     * @param result 搜素结果
     */
    public void connect(SearchResult result, ConnectResponse connectResponse) {
        if (result == null || result.device == null) {
            return;
        }

        if (result.isBleDevice) {
            connectBle(result.device, connectResponse);
        }else {
            connectClassic(result.device, connectResponse);
        }

    }

    private void connectBle(BluetoothDevice device, ConnectResponse connectResponse) {
        if (device == null) {
            return;
        }
        //startTimer(20,BLE_CONNECT_FAIL);
        mConnectResponse = connectResponse;
        ConnectOptions options = new ConnectOptions.Builder()
                .setConnectRetry(BlueToothConstants.BLE_CONNECT_RETRY)   // 连接如果失败重试2次
                .setConnectTimeout(BlueToothConstants.BLE_CONNECT_TIMEOUT)   // 连接超时10s
                .setServiceDiscoverRetry(BlueToothConstants.BLE_SERVICE_DISRETRY)  // 发现服务如果失败重试2次
                .setServiceDiscoverTimeout(BlueToothConstants.BLE_SERVICE_DISTIMEOUT)  // 发现服务超时10s
                .build();
        //mClient.registerConnectStatusListener(mac, mBleConnectStatusListener);
        mClient.connect(device.getAddress(), options, connectResponse);
        //mClient.notify(mac, UUID.fromString(BT_UUID), UUID.fromString(BT_READ_UUID), mNotifyRsp);
    }

    /**
     * 连接蓝牙，自定义连接配置
     *
     * @param result
     * @param options
     */
    public void connect(SearchResult result, ConnectResponse connectResponse, ConnectOptions options) {
        //startTimer(20,BLE_CONNECT_FAIL);
        if (result == null || result.device == null) {
            return;
        }
        if (result.isBleDevice) {
            connectBle(result.device, connectResponse, options);
        }else {
            connectClassic(result.device, connectResponse);
        }

    }

    /**
     *
     * @param device
     * @param connectResponse
     * @param options
     */
    private void connectBle(BluetoothDevice device, ConnectResponse connectResponse, ConnectOptions options) {
        if (device == null || TextUtils.isEmpty(device.getAddress())) {
            return;
        }
        mConnectResponse = connectResponse;
        //mClient.registerConnectStatusListener(mac, mBleConnectStatusListener);
        mClient.connect(device.getAddress(), options, connectResponse);
        //mClient.notify(mac, UUID.fromString(BT_UUID), UUID.fromString(BT_READ_UUID), mNotifyRsp);
    }

    /**
     * 连接状态监听，断开重连，如果需要，可以开放
     */
    /*private final BleConnectStatusListener mBleConnectStatusListener = new BleConnectStatusListener() {

        @Override
        public void onConnectStatusChanged(String mac, int status) {

            boolean mConnected = (status == BlueToothConstants.STATUS_CONNECTED);
            if (!mConnected && mConnectResponse != null) {
                connect(mac, mConnectResponse);
            }
        }
    };*/

    /**
     * 断开蓝牙连接
     */
    public void disConnect(String mac) {
        mConnectResponse = null;
        if (mClient != null) {
            mClient.disconnect(mac);
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    /**
     * 订阅通知  回调数据不做解密
     *
     * @param blueToothNotifyParams
     */
    public void notify(BlueToothNotifyParams blueToothNotifyParams) {
        notify(blueToothNotifyParams, null);
    }

    /**
     * 订阅通知  回调数据做解密
     *
     * @param blueToothNotifyParams
     */
    public void notify(BlueToothNotifyParams blueToothNotifyParams, IBlueToothDecrypt decrypt) {
        if (blueToothNotifyParams == null || blueToothNotifyParams.isEmpty()) {
            return;
        }

        String mac = blueToothNotifyParams.getMac();
        UUID serviceUUID = blueToothNotifyParams.getServiceUUID();
        UUID characterUUID = blueToothNotifyParams.getCharacterUUID();
        BleNotifyResponse response = blueToothNotifyParams.getResponse();
        if (decrypt != null) {
            response.setBlueToothDecrypt(decrypt);
        }
        if (mClient != null) {
            mClient.notify(mac, serviceUUID, characterUUID, response);
        }
    }

    /**
     * 取消订阅通知
     *
     * @param params
     */
    public void unNotify(BlueToothUnnotifyParams params) {
        if (params == null || params.isEmpty()) {
            return;
        }
        String mac = params.getMac();
        UUID serviceUUID = params.getServiceUUID();
        UUID characterUUID = params.getCharacterUUID();
        BleUnnotifyResponse response = params.getResponse();
        if (mClient != null) {
            mClient.unnotify(mac, serviceUUID, characterUUID, response);
        }
    }

    /**
     * 注册蓝牙开关状态监听
     */
    public void registerBluetoothStateListener(BluetoothStateListener bluetoothStateListener) {
        if (mClient != null && bluetoothStateListener != null) {
            mClient.registerBluetoothStateListener(bluetoothStateListener);
        }
    }

    /**
     * 读取数据  不做解密
     *
     * @param blueToothReadParams
     */
    public void read(BlueToothReadParams blueToothReadParams) {
        read(blueToothReadParams, null);
    }

    /**
     * 读取数据  需要解密
     *
     * @param blueToothReadParams
     */
    public void read(BlueToothReadParams blueToothReadParams, IBlueToothDecrypt decrypt) {
        if (blueToothReadParams == null || blueToothReadParams.isEmpty()) {
            return;
        }
        String mac = blueToothReadParams.getMac();
        UUID serviceUUID = blueToothReadParams.getServiceUUID();
        UUID characterUUID = blueToothReadParams.getCharacterUUID();
        BleReadResponse response = blueToothReadParams.getResponse();
        if (decrypt != null) {
            response.setBlueToothDecrypt(decrypt);
        }
        if (mClient != null) {
            mClient.read(mac, serviceUUID, characterUUID, response);
        }
    }

    /**
     * 写数据（分包处理） 不做数据加解密
     *
     * @param writeParams
     */
    public void write(BlueToothWriteParams writeParams) {
        write(writeParams, null);
    }

    /**
     * 写数据（分包处理） 不做数据加解密
     *
     * @param writeParams
     */
    public void write(BlueToothWriteParams writeParams, IBlueToothEncryAndDecry encryAndDecry) {
        if (mClient == null) {
            return;
        }

        if (writeParams == null || writeParams.isEmpty()) {
            return;
        }
        byte[] value = writeParams.getValue();
        if (encryAndDecry != null) {
            value = encryAndDecry.encrypt(value);
        }
        String mac = writeParams.getMac();
        UUID serviceUUID = writeParams.getServiceUUID();
        UUID characterUUID = writeParams.getCharacterUUID();
        boolean needParseAndPacking = writeParams.isNeedParseAndPacking();
        int type = writeParams.getDataType();
        BleWriteResponse response = writeParams.getResponse();
        response.setNeedParseAndPacking(needParseAndPacking);
        if (encryAndDecry != null) {
            response.setBlueToothDecrypt(encryAndDecry);
        }

        byte[] realValue = DataUtils.packingData(value, needParseAndPacking, type , encryAndDecry);
        int length = realValue.length;
        int count = length / 21;
        int sendCount = length % 21 == 0 ? count : (count + 1);
        for (int i = 0; i < sendCount; i++) {
            byte[] data;
            if (i == 0) {
                data = ByteUtils.subBytes(realValue, i * 20, length > 21 ? 20 : length);
            } else {
                //每次只能写21 byte ，总长度分批发送
                data = ByteUtils.subBytes(realValue, i * 20,
                        (length - (i + 1) * 20) > 0 ? 20 : 20 - ((i + 1) * 20 - length));
            }
            mClient.write(mac, serviceUUID, characterUUID,
                    data, response);
        }
    }

    /**
     * 注册配对请求监听
     */
    private void registerPairingRequest(Context context) {
        if (context == null) {
            return;
        }
        Context appContext = context.getApplicationContext();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
        filter.setPriority(1000);
        appContext.registerReceiver(mPairingReceiver, filter);
    }

    /**
     * 注销配对请求监听 （clear时请求）
     */
    private void unRegisterPairingRequest() {
        if (mPairingReceiver != null) {
            BluetoothContext.get().unregisterReceiver(mPairingReceiver);
        }
    }

    /**
     * 设置是否需要拦截Pin码弹窗
     * @param needInterceptPinDialog
     */
    public void setNeedInterceptPinDialog(boolean needInterceptPinDialog) {
        this.mNeedInterceptPinDialog = needInterceptPinDialog;
    }

    /**
     * 配对请求Receiver
     */
    private BroadcastReceiver mPairingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_PAIRING_REQUEST.equals(action)) {
                try {
                    int pin = intent.getIntExtra(BluetoothDevice.EXTRA_PAIRING_KEY,
                            BluetoothDevice.ERROR);

                    if (mConnectResponse != null) {
                        mConnectResponse.onGetPin(pin);
                    }
                    BluetoothLog.d("pairingKey = " + pin);
                    Log.d("wsh_log", "pin = " + pin);
                    if(mNeedInterceptPinDialog) {
                        BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        ClsUtils.setPairingConfirmation(bluetoothDevice.getClass(), bluetoothDevice, true);
                        abortBroadcast();//如果没有将广播终止，则会出现一个配对框。
                        Log.d("wsh_log", "abortBroadcast");
                        ClsUtils.setPin(bluetoothDevice.getClass(), bluetoothDevice, pin + "");
                    }
                } catch (Exception e) {
                    //e.printStackTrace();
                }
            }
        }
    };

    /**
     * 不再进行蓝牙操作
     */
    public void clear() {
        BluetoothManager.getInstance().stopSearch();
        unRegisterPairingRequest();
    }
}
