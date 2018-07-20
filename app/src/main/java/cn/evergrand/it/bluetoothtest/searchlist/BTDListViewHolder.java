package cn.evergrand.it.bluetoothtest.searchlist;


import android.bluetooth.BluetoothDevice;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wenshenghui.bluetoothtest.R;

import cn.evergrand.it.bluetooth.BlueToothConstants;
import cn.evergrand.it.bluetooth.BluetoothManager;
import cn.evergrand.it.bluetooth.connect.options.ConnectOptions;
import cn.evergrand.it.bluetooth.connect.response.ConnectResponse;
import cn.evergrand.it.bluetooth.model.BleGattProfile;
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

        BluetoothManager.getInstance().connect(result, new ConnectResponse() {
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
                    //Intent in = new Intent(mRootView.getContext(), DataOprationActivity.class);
                    //in.putExtra("SearchResult", result);
                    //mRootView.getContext().startActivity(in);
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

    public void setInConnectionListener(BTDListActivity.InConnectionListener inConnectionListener) {
        mInConnectionListener = inConnectionListener;
    }
}
