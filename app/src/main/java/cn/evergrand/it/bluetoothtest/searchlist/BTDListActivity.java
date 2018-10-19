package cn.evergrand.it.bluetoothtest.searchlist;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.wenshenghui.bluetoothtest.R;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import cn.evergrand.it.bluetooth.BlueToothConstants;
import cn.evergrand.it.bluetooth.BluetoothManager;
import cn.evergrand.it.bluetooth.beacon.Beacon;
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
import cn.evergrand.it.bluetooth.model.BleGattProfile;
import cn.evergrand.it.bluetooth.search.SearchRequest;
import cn.evergrand.it.bluetooth.search.SearchResult;
import cn.evergrand.it.bluetooth.search.response.SearchResponse;
import cn.evergrand.it.bluetooth.utils.BluetoothLog;
import cn.evergrand.it.bluetooth.utils.type.DataType;

/**
 * author: wenshenghui
 * created on: 2018/6/14 15:44
 * description:蓝牙设备列表页
 */
public class BTDListActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView = null;
    private ProgressBar mPbar;
    private TextView mRestartTv = null;
    private BTDListAdapter mBTDListAdapter = null;
    private List<SearchResult> mDevices = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_btd_list_activity);
        initView();
        initAdapter();
        initRecyclerView();
        initAction();

        BluetoothManager.getInstance().init(this);

        //开始扫描
        checkAndSearchDevice();

        BluetoothManager.getInstance().registerBluetoothStateListener(new BluetoothStateListener() {
            @Override
            public void onBluetoothStateChanged(boolean open) {
                //
            }
        });
    }

    private void initAdapter() {
        if (mRecyclerView == null) {
            return;
        }
        mBTDListAdapter = new BTDListAdapter(BTDListActivity.this);
        mRecyclerView.setAdapter(mBTDListAdapter);
        mBTDListAdapter.setInConnectionListener(mInConnectionListener);
    }

    private void initRecyclerView() {
        if (mRecyclerView == null) {
            return;
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(BTDListActivity.this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

    }

    private void initAction() {
        mRestartTv.setOnClickListener(mRestartClickListener);
    }

    private void initView() {
        mRestartTv = (TextView) findViewById(R.id.tv_restart);
        mRecyclerView = (RecyclerView) findViewById(R.id.list_btd);
        mPbar = (ProgressBar) findViewById(R.id.pbar);
    }

    private View.OnClickListener mRestartClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            checkAndSearchDevice();
        }
    };

    /**
     * 此时需要停止扫描
     */
    @Override
    protected void onPause() {
        super.onPause();

        BluetoothManager.getInstance().stopSearch();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BluetoothManager.getInstance().clear();
    }

    /**
     * 开始扫描
     */
    private void checkAndSearchDevice() {
        searchDevice();
    }

    /**
     * 开启搜索
     */
    private void searchDevice() {
        SearchRequest request = new SearchRequest.Builder().searchBluetoothClassicDevice(5000).
        searchBluetoothLeDevice(5000, 2).build();

        BluetoothManager.getInstance().searchBle(mSearchResponse, request);
    }

    /**
     * 搜索监听
     */
    private final SearchResponse mSearchResponse = new SearchResponse() {
        @Override
        public void onSearchError(int errorCode) {
            //errorCode == SearchErrorState.BLE_NOT_SUPPORT 当前设备不支持
            //errorCode == SearchErrorState.USER_CANCEL_OPEN_BT 用户取消打开蓝牙
        }

        @Override
        public void onSearchStarted() {
            BluetoothLog.w("MainActivity.onSearchStarted");
            mDevices.clear();
        }

        @Override
        public void onDeviceFounded(SearchResult device) {
            BluetoothLog.w("MainActivity.onDeviceFounded " + device.device.getAddress());
            if (!mDevices.contains(device)) {
                mDevices.add(device);
                mBTDListAdapter.setDataList(mDevices);

                Beacon beacon = new Beacon(device.scanRecord);
                BluetoothLog.v(String.format("beacon for %s\n%s", device.getAddress(), beacon.toString()));

//                BeaconItem beaconItem = null;
//                BeaconParser beaconParser = new BeaconParser(beaconItem);
//                int firstByte = beaconParser.readByte(); // 读取第1个字节
//                int secondByte = beaconParser.readByte(); // 读取第2个字节
//                int productId = beaconParser.readShort(); // 读取第3,4个字节
//                boolean bit1 = beaconParser.getBit(firstByte, 0); // 获取第1字节的第1bit
//                boolean bit2 = beaconParser.getBit(firstByte, 1); // 获取第1字节的第2bit
//                beaconParser.setPosition(0); // 将读取起点设置到第1字节处
            }
        }

        @Override
        public void onSearchStopped() {
            BluetoothLog.w("MainActivity.onSearchStopped");
        }

        @Override
        public void onSearchCanceled() {
            BluetoothLog.w("MainActivity.onSearchCanceled");
        }
    };

    private InConnectionListener mInConnectionListener = new InConnectionListener() {
        @Override
        public void onStartTryConnection() {
            if (mPbar != null) {
                mPbar.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onConnectionSuccess() {
            if (mPbar != null) {
                mPbar.setVisibility(View.GONE);
            }
        }
    };

    public interface InConnectionListener{
        void onStartTryConnection();
        void onConnectionSuccess();
    }

    public void searchBLE() {

        BluetoothManager.getInstance().searchBle(new SearchResponse() {
            @Override
            public void onSearchError(int errorCode) {
                //errorCode == SearchErrorState.BLE_NOT_SUPPORT 当前设备不支持
                //errorCode == SearchErrorState.USER_CANCEL_OPEN_BT 用户取消打开蓝牙
            }

            @Override
            public void onSearchStarted() {
                //扫描开始，此处显示进度条或者其他界面操作
            }

            @Override
            public void onDeviceFounded(SearchResult device) {
                //TODO 扫描到一个设备，持续扫描中，此处会重复扫描到相同设备，目前尚未做过滤

                int rssi = device.rssi; //蓝牙的信号值

                /*Rssi和接收功率有关，单位是dBm，一般为负值，反应的是信号的衰减程度，理想状态下（无衰减），Rssi = 0dBm，
                实际情况是，即使蓝牙设备挨得非常近，Rssi也只有-50dBm的强度，在传输过程中，不可避免要损耗。
                一般情况下，经典蓝牙强度
                -50 ~ 0dBm   信号强
                -70 ~-50dBm  信号中
                <-70dBm      信号弱

                低功耗蓝牙分四级
                -60 ~ 0   4  信号强
                -70 ~ -60 3  信号良好
                -80 ~ -70 2  信号较弱
                <-80 1       信号弱*/

            }

            @Override
            public void onSearchStopped() {
                //扫描停止
            }

            @Override
            public void onSearchCanceled() {
                //扫描取消
            }
        });


        SearchRequest request = new SearchRequest.Builder()
                .searchBluetoothLeDevice(6000, 2)   // 先扫BLE设备2次，每次6s
                .searchBluetoothClassicDevice(6000) //经典蓝牙扫描6s
                .build();

        BluetoothManager.getInstance().searchBle(mSearchResponse, request);

        BluetoothManager.getInstance().stopSearch();

    }

    public void connect(SearchResult result) {

        BluetoothManager.getInstance().connect(result, new ConnectResponse() {
            @Override
            public void onGetPin(int pin) {
                // 连接过程中，收到的pin会回传至此处，如果为0或者不回调，则没有
            }

            @Override
            public void onResponse(int code, BleGattProfile data, int requestId) {
                /*// 注意这是操作样例，实际操作根据需求来写
                if (code == BlueToothConstants.REQUEST_SUCCESS){ //表示连接成功
                    //TODO  如果是经典蓝牙，此处data为null
                    //data 存储设备信息，services，遍历可拿到每个service信息
                    List<BleGattService> services = data.getServices();
                    for (BleGattService service : services) {
                        UUID serviceUuid = service.getUUID();
                        List<BleGattCharacter> characters = service.getCharacters();
                        for (BleGattCharacter character : characters) {
                            UUID characterUuid = character.getUuid();
                        }
                    }
                }*/
            }
        });

        ConnectOptions options = new ConnectOptions.Builder()
                .setConnectRetry(2)   // 连接如果失败重试2次
                .setConnectTimeout(10000)   // 连接超时10s
                .setServiceDiscoverRetry(2)  // 发现服务如果失败重试2次
                .setServiceDiscoverTimeout(10000)  // 发现服务超时10s
                .build();
        BluetoothManager.getInstance().connect(result, mConnectResponse, options);

    }

    private ConnectResponse mConnectResponse = new  ConnectResponse() {
        @Override
        public void onGetPin(int pin) {
            // 连接过程中，收到的pin会回传至此处，如果为0或者不回调，则没有
        }

        @Override
        public void onResponse(int code, BleGattProfile data, int requestId) {
                /*// 注意这是操作样例，实际操作根据需求来写
                if (code == BlueToothConstants.REQUEST_SUCCESS){ //表示连接成功
                    //TODO
                    //data 存储设备信息，services，遍历可拿到每个service信息
                    List<BleGattService> services = data.getServices();
                    for (BleGattService service : services) {
                        UUID serviceUuid = service.getUUID();
                        List<BleGattCharacter> characters = service.getCharacters();
                        for (BleGattCharacter character : characters) {
                            UUID characterUuid = character.getUuid();
                        }
                    }
                }*/
        }
    };

    public void disConnect(String mac) {

        BluetoothManager.getInstance().disConnect(mac);

    }

    public void notify(String mac, UUID serviceUUID, UUID characterUUID, BleNotifyResponse response) {

        BlueToothNotifyParams blueToothNotifyParams = new BlueToothNotifyParams(mac, serviceUUID,
                characterUUID, true,response);
        BluetoothManager.getInstance().notify(blueToothNotifyParams);

        BluetoothManager.getInstance().notify(blueToothNotifyParams, new IBlueToothDecrypt() {
            @Override
            public byte[] decrypt(byte[] data) {

                //TODO 解密算法获取解密后的data
                return data;
            }
        });

    }

    private BleNotifyResponse mBleNotifyResponse = new BleNotifyResponse() {
        @Override
        public void onNotify(UUID service, UUID character, byte[] value, int requestId) {

        }

        @Override
        public void onResponse(int code) {

        }
    };

    public void unNotify(String mac, UUID serviceUUID, UUID characterUUID, BleUnnotifyResponse response) {

        BlueToothUnnotifyParams blueToothUnnotifyParams = new BlueToothUnnotifyParams(mac, serviceUUID,
                characterUUID, response);
        BluetoothManager.getInstance().unNotify(blueToothUnnotifyParams);

    }

    public void registerBluetoothStateListener () {

        BluetoothManager.getInstance().registerBluetoothStateListener(new BluetoothStateListener() {
            @Override
            public void onBluetoothStateChanged(boolean open) {
                //todo
            }
        });

    }

    public void read(String mac, UUID serviceUUID, UUID characterUUID, BleReadResponse response) {

        BlueToothReadParams blueToothReadParams = new BlueToothReadParams(mac, serviceUUID,
                characterUUID, true, mReadRsp);
        BluetoothManager.getInstance().read(blueToothReadParams);


        BluetoothManager.getInstance().read(blueToothReadParams, new IBlueToothDecrypt() {
            @Override
            public byte[] decrypt(byte[] data) {
                //todo 处理解密
                return data;
            }
        });

    }

    private BleReadResponse mReadRsp = new BleReadResponse() {
        @Override
        public void onResponse(int code, byte[] data, int requestId) {
            if (code == BlueToothConstants.REQUEST_SUCCESS) {
                //读取成功
            } else {
                //读取失败
            }
        }
    };

    public void write(String mac, UUID serviceUUID, UUID characterUUID, byte[] value, BleWriteResponse mWriteRsp) {

        BlueToothWriteParams blueToothWriteParams = new BlueToothWriteParams(value, mac, serviceUUID,
                characterUUID, true, DataType.DOOR_HOME, 1, mWriteRsp);
        BluetoothManager.getInstance().write(blueToothWriteParams);


        BluetoothManager.getInstance().write(blueToothWriteParams, new IBlueToothEncryAndDecry() {
            @Override
            public byte[] decrypt(byte[] data) {
                //todo 解密
                return data;
            }

            @Override
            public byte[] encrypt(byte[] data) {
                //TODO 加密
                return data;
            }
        });

    }

    private final BleWriteResponse mWriteRsp = new BleWriteResponse() {
        @Override
        public void onResponse(int code, byte[] value, int requestId) {
            if (code == BlueToothConstants.REQUEST_SUCCESS) { //写入成功
                //value为设备返回数据
                //这里注意，如果是分包写入的，实际可能会调用多次，
                // 目前的机制如果要得到最后一段数据写入成功，需要在此处自行计算（以21字节为一段）
                //具体数据交互需与BLE设备模块沟通

            } else {//写入失败

            }
        }
    };

    /**
     * 获取pin码
     * @return
     */
    public void clear() {

        BluetoothManager.getInstance().clear();

        BluetoothManager.getInstance().openLog(true);

    }

    /**
     * 是否需要拦截Pin码提示弹窗
     * @return
     */
    public void setNeedInterceptPinDialog() {

        //设置需要拦截Pin码弹窗
        BluetoothManager.getInstance().setNeedInterceptPinDialog(true);

    }

    public void openBlueTooth() {

        //开启蓝牙
        BluetoothManager.getInstance().openBlueTooth();

    }

    /**
     * 判断蓝牙是否已经开启
     */
    public boolean isBluetoothOpened() {

        //判断蓝牙是否已经开启
        boolean opened = BluetoothManager.getInstance().isBluetoothOpened();

        return opened;
    }

}
