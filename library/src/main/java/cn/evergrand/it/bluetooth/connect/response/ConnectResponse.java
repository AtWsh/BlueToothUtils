package cn.evergrand.it.bluetooth.connect.response;


import cn.evergrand.it.bluetooth.model.BleGattProfile;

public interface ConnectResponse extends BleTResponse<BleGattProfile> {

    void onGetPin(int pin);
}
