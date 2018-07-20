package cn.evergrand.it.bluetooth;


import java.util.UUID;

import cn.evergrand.it.bluetooth.connect.listener.BleConnectStatusListener;
import cn.evergrand.it.bluetooth.connect.listener.BluetoothStateListener;
import cn.evergrand.it.bluetooth.connect.options.ConnectOptions;
import cn.evergrand.it.bluetooth.connect.response.BleMtuResponse;
import cn.evergrand.it.bluetooth.connect.response.BleNotifyResponse;
import cn.evergrand.it.bluetooth.connect.response.BleReadResponse;
import cn.evergrand.it.bluetooth.connect.response.BleReadRssiResponse;
import cn.evergrand.it.bluetooth.connect.response.BleUnnotifyResponse;
import cn.evergrand.it.bluetooth.connect.response.BleWriteResponse;
import cn.evergrand.it.bluetooth.connect.response.ConnectResponse;
import cn.evergrand.it.bluetooth.receiver.listener.BluetoothBondListener;
import cn.evergrand.it.bluetooth.search.SearchRequest;
import cn.evergrand.it.bluetooth.search.response.SearchResponse;

public interface IBluetoothClient {

    void connect(String mac, ConnectOptions options, ConnectResponse response);

    void connectClassic(String mac, ConnectResponse response);

    void disconnect(String mac);

    void registerConnectStatusListener(String mac, BleConnectStatusListener listener);

    void unregisterConnectStatusListener(String mac, BleConnectStatusListener listener);

    void read(String mac, UUID service, UUID character, BleReadResponse response);

    void write(String mac, UUID service, UUID character, byte[] value, BleWriteResponse response);

    void readDescriptor(String mac, UUID service, UUID character, UUID descriptor, BleReadResponse response);

    void writeDescriptor(String mac, UUID service, UUID character, UUID descriptor, byte[] value, BleWriteResponse response);

    void writeNoRsp(String mac, UUID service, UUID character, byte[] value, BleWriteResponse response);

    void notify(String mac, UUID service, UUID character, BleNotifyResponse response);

    void unnotify(String mac, UUID service, UUID character, BleUnnotifyResponse response);

    void indicate(String mac, UUID service, UUID character, BleNotifyResponse response);

    void unindicate(String mac, UUID service, UUID character, BleUnnotifyResponse response);

    void readRssi(String mac, BleReadRssiResponse response);

    void requestMtu(String mac, int mtu, BleMtuResponse response);

    void search(SearchRequest request, SearchResponse response);

    void stopSearch();

    void registerBluetoothStateListener(BluetoothStateListener listener);

    void unregisterBluetoothStateListener(BluetoothStateListener listener);

    void registerBluetoothBondListener(BluetoothBondListener listener);

    void unregisterBluetoothBondListener(BluetoothBondListener listener);

    void clearRequest(String mac, int type);

    void refreshCache(String mac);
}
