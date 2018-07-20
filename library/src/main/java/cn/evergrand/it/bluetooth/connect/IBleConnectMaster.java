package cn.evergrand.it.bluetooth.connect;


import java.util.UUID;

import cn.evergrand.it.bluetooth.connect.options.ConnectOptions;
import cn.evergrand.it.bluetooth.connect.response.BleGeneralResponse;

public interface IBleConnectMaster {

    void connect(ConnectOptions options, BleGeneralResponse response);

    void disconnect();

    void read(UUID service, UUID character, BleGeneralResponse response);

    void write(UUID service, UUID character, byte[] bytes, BleGeneralResponse response);

    void writeNoRsp(UUID service, UUID character, byte[] bytes, BleGeneralResponse response);

    void readDescriptor(UUID service, UUID character, UUID descriptor, BleGeneralResponse response);

    void writeDescriptor(UUID service, UUID character, UUID descriptor, byte[] value, BleGeneralResponse response);

    void notify(UUID service, UUID character, BleGeneralResponse response);

    void unnotify(UUID service, UUID character, BleGeneralResponse response);

    void readRssi(BleGeneralResponse response);

    void indicate(UUID service, UUID character, BleGeneralResponse response);

    void requestMtu(int mtu, BleGeneralResponse response);

    void clearRequest(int clearType);

    void refreshCache();
}
