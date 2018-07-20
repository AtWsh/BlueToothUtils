package cn.evergrand.it.bluetooth.search;


import android.os.Bundle;

import cn.evergrand.it.bluetooth.BlueToothConstants;
import cn.evergrand.it.bluetooth.connect.response.BleGeneralResponse;
import cn.evergrand.it.bluetooth.search.response.BluetoothSearchResponse;

public class BluetoothSearchManager {

    public static void search(SearchRequest request, final BleGeneralResponse response) {
        BluetoothSearchRequest requestWrapper = new BluetoothSearchRequest(request);
        BluetoothSearchHelper.getInstance().startSearch(requestWrapper, new BluetoothSearchResponse() {
            @Override
            public void onSearchStarted() {
                response.onResponse(BlueToothConstants.SEARCH_START, null);
            }

            @Override
            public void onDeviceFounded(SearchResult device) {
                Bundle bundle = new Bundle();
                bundle.putParcelable(BlueToothConstants.EXTRA_SEARCH_RESULT, device);
                response.onResponse(BlueToothConstants.DEVICE_FOUND, bundle);
            }

            @Override
            public void onSearchStopped() {
                response.onResponse(BlueToothConstants.SEARCH_STOP, null);
            }

            @Override
            public void onSearchCanceled() {
                response.onResponse(BlueToothConstants.SEARCH_CANCEL, null);
            }
        });
    }

    public static void stopSearch() {
        BluetoothSearchHelper.getInstance().stopSearch();
    }
}
