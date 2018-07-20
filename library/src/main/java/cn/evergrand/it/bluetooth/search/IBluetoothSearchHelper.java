package cn.evergrand.it.bluetooth.search;


import cn.evergrand.it.bluetooth.search.response.BluetoothSearchResponse;

public interface IBluetoothSearchHelper {

    void startSearch(BluetoothSearchRequest request, BluetoothSearchResponse response);

    void stopSearch();
}
