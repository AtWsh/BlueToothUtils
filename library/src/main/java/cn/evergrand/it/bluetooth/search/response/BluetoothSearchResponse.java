package cn.evergrand.it.bluetooth.search.response;

import cn.evergrand.it.bluetooth.search.SearchResult;

public interface BluetoothSearchResponse {
    void onSearchStarted();

    void onDeviceFounded(SearchResult device);

    void onSearchStopped();

    void onSearchCanceled();
}
