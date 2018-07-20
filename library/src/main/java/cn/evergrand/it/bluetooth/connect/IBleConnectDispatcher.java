package cn.evergrand.it.bluetooth.connect;


import cn.evergrand.it.bluetooth.connect.request.BleRequest;

public interface IBleConnectDispatcher {

    void onRequestCompleted(BleRequest request);
}
