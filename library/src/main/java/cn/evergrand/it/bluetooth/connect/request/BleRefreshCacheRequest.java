package cn.evergrand.it.bluetooth.connect.request;


import cn.evergrand.it.bluetooth.Code;
import cn.evergrand.it.bluetooth.connect.response.BleGeneralResponse;

public class BleRefreshCacheRequest extends BleRequest {

    public BleRefreshCacheRequest(BleGeneralResponse response) {
        super(response);
    }

    @Override
    public void processRequest() {
        refreshDeviceCache();

        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                onRequestCompleted(Code.REQUEST_SUCCESS);
            }
        }, 3000);
    }
}
