package cn.evergrand.it.bluetooth.bean;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.UUID;

import cn.evergrand.it.bluetooth.connect.response.BleUnnotifyResponse;

/**
 * author: wenshenghui
 * created on: 2018/7/20 9:41
 * description:
 */
public class BlueToothUnnotifyParams {

    private String mMac;
    private UUID mServiceUUID;
    private UUID mCharacterUUID;
    private BleUnnotifyResponse mResponse;

    public BlueToothUnnotifyParams(@NonNull String mac, UUID serviceUUID, UUID characterUUID, BleUnnotifyResponse response){
        mMac = mac;
        mServiceUUID = serviceUUID;
        mCharacterUUID = characterUUID;
        mResponse = response;
    }

    public boolean isEmpty() {
        if (TextUtils.isEmpty(mMac)) {
            return true;
        }

        if (mServiceUUID == null || mCharacterUUID == null) {
            return true;
        }

        if (mResponse == null) {
            return true;
        }

        return false;
    }

    public String getMac() {
        return mMac;
    }

    public UUID getServiceUUID() {
        return mServiceUUID;
    }

    public UUID getCharacterUUID() {
        return mCharacterUUID;
    }

    public BleUnnotifyResponse getResponse() {
        return mResponse;
    }
}
