package cn.evergrand.it.bluetoothtest.searchlist;


import android.bluetooth.BluetoothDevice;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wenshenghui.bluetoothtest.R;

import java.util.List;
import java.util.UUID;

import cn.evergrand.it.bluetooth.BlueToothConstants;
import cn.evergrand.it.bluetooth.BluetoothManager;
import cn.evergrand.it.bluetooth.bean.BlueToothNotifyParams;
import cn.evergrand.it.bluetooth.connect.options.ConnectOptions;
import cn.evergrand.it.bluetooth.connect.response.BleNotifyResponse;
import cn.evergrand.it.bluetooth.connect.response.ConnectResponse;
import cn.evergrand.it.bluetooth.encry.IBlueToothDecrypt;
import cn.evergrand.it.bluetooth.model.BleGattCharacter;
import cn.evergrand.it.bluetooth.model.BleGattProfile;
import cn.evergrand.it.bluetooth.model.BleGattService;
import cn.evergrand.it.bluetooth.search.SearchResult;
import cn.evergrand.it.bluetooth.utils.BluetoothLog;

/**
 * author: wenshenghui
 * created on: 2018/6/14 17:27
 * description:
 */
public class BTDListViewHolder extends RecyclerView.ViewHolder {

    private View mRootView;
    private TextView mName;
    private TextView mMac;
    private TextView mRssi;
    private BTDListAdapter mAdapter;
    private BTDListActivity.InConnectionListener mInConnectionListener;

    public BTDListViewHolder(View itemView, BTDListAdapter adapter) {
        super(itemView);
        mRootView = itemView;
        mAdapter = adapter;
        initView(itemView);
    }

    private void initView(View itemView) {
        mName = (TextView) itemView.findViewById(R.id.btd_name);
        mMac = (TextView) itemView.findViewById(R.id.btd_mac);
        mRssi = (TextView) itemView.findViewById(R.id.btd_rssi);
    }

    public void fillData(SearchResult result) {
        if (result == null) {
            return;
        }
        mName.setText(result.getName());
        BluetoothLog.d("name = " + result.getName());
        mMac.setText(result.getAddress());
        mRssi.setText(String.format("Rssi: %d", result.rssi));

        initAction(result);
    }

    private void initAction(final SearchResult result) {
        mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectDevice(result);
            }
        });
    }

    private void connectDevice(final SearchResult result) {
        if (result == null) {
            return;
        }

        if (mInConnectionListener != null) {
            mInConnectionListener.onStartTryConnection();
        }

        final BluetoothDevice device = result.device;

        ConnectOptions options = new ConnectOptions.Builder()
                .setConnectRetry(3)
                .setConnectTimeout(20000)
                .setServiceDiscoverRetry(3)
                .setServiceDiscoverTimeout(10000)
                .build();

        BluetoothManager.getInstance().connect(result.device.getAddress(), new ConnectResponse() {
            @Override
            public void onGetPin(int pin) {

            }

            @Override
            public void onResponse(int code, BleGattProfile profile, int requestId) {
                BluetoothLog.v(String.format("profile:\n%s", profile));
                if (mInConnectionListener != null) {
                    mInConnectionListener.onConnectionSuccess();
                }

                if (code == BlueToothConstants.REQUEST_SUCCESS) {
                    Toast.makeText(mRootView.getContext(), "蓝牙连接成功；" + device.getAddress(), Toast.LENGTH_SHORT).show();
                    //Intent in = new Intent(mRootView.getContext(), DataOprationActivity.class);
                    //in.putExtra("SearchResult", result);
                    //mRootView.getContext().startActivity(in);
                    Log.d("wsh_log", "蓝牙连接成功");
                    if (profile == null) {
                        Log.d("wsh", "onResponse:  profile = profile");
                        return;
                    }
                    List<BleGattService> services = profile.getServices();

                    UUID serviceUUID = null;
                    UUID characterUUID = null;
                    for (BleGattService service : services) {
                        serviceUUID = service.getUUID();
                        List<BleGattCharacter> characters = service.getCharacters();
                        for (BleGattCharacter character : characters) {
                            characterUUID = character.getUuid();
                            break;
                        }
                        if (characterUUID != null) {
                            break;
                        }
                    }
                    Log.d("wsh", "serviceUUID = " + serviceUUID.toString() + "   characterUUID = " + characterUUID.toString());
                    notifyBle(device.getAddress(), serviceUUID, characterUUID, new BleNotifyResponse() {
                        @Override
                        public void onNotify(UUID service, UUID character, byte[] value, int requestId) {
                            Log.d("wsh", "onNotify");
                        }

                        @Override
                        public void onResponse(int code) {
                            Log.d("wsh", "onNotify  onResponse code =" + code);
                        }
                    });
                }
            }
        }, options);
        //BluetoothManager.getInstance().connectClassic(device);
        /*ClassicBtManager
                .getInstance()
                .connectDevice(device, new ConnectDeviceCallback() {
                    @Override
                    public void connectSuccess(BluetoothSocket socket, BluetoothDevice device) {
                        // 连接成功
                        Toast.makeText(mRootView.getContext(), "连接成功！", Toast.LENGTH_SHORT).show();
                        if (mInConnectionListener != null) {
                            mInConnectionListener.onConnectionSuccess();
                        }
                    }

                    @Override
                    public void connectError(Throwable throwable) {
                        // 连接失败
                        Toast.makeText(mRootView.getContext(), "连接失败：" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        if (mInConnectionListener != null) {
                            mInConnectionListener.onConnectionSuccess();
                        }
                    }
                });*/
        /*BluetoothManager.getInstance().connect(device.getAddress(), new ConnectResponse() {
            @Override
            public void onGetPin(int pin) {

            }

            @Override
            public void onResponse(int code, BleGattProfile profile) {
                BluetoothLog.v(String.format("profile:\n%s", profile));
                if (mInConnectionListener != null) {
                    mInConnectionListener.onConnectionSuccess();
                }

                if (code == BlueToothConstants.REQUEST_SUCCESS) {
                    Toast.makeText(mRootView.getContext(), "蓝牙连接成功；" + device.getAddress(), Toast.LENGTH_SHORT).show();
                    Intent in = new Intent(mRootView.getContext(), DataOprationActivity.class);
                    in.putExtra("SearchResult", result);
                    mRootView.getContext().startActivity(in);
                }
            }
        }, options);*/
    }

    public void notifyBle(String mac, UUID serviceUUID, UUID characterUUID, BleNotifyResponse response) {

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

    public void setInConnectionListener(BTDListActivity.InConnectionListener inConnectionListener) {
        mInConnectionListener = inConnectionListener;
    }
}
