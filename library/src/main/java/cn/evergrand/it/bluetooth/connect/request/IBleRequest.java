package cn.evergrand.it.bluetooth.connect.request;


import cn.evergrand.it.bluetooth.connect.IBleConnectDispatcher;

public interface IBleRequest {

    void process(IBleConnectDispatcher dispatcher);

    void cancel();
}
