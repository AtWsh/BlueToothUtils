package cn.evergrand.it.bluetooth.bean;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.UUID;

import cn.evergrand.it.bluetooth.connect.response.BleReadResponse;

/**
 * author: wenshenghui
 * created on: 2018/7/19 17:36
 * description:
 */
public class BlueToothReadParams {
    private String mMac;
    private UUID mServiceUUID;
    private UUID mCharacterUUID;
    private BleReadResponse mResponse;
    private boolean mNeedParseAndPacking;

    public BlueToothReadParams(@NonNull String mac, UUID serviceUUID, UUID characterUUID,
                               boolean needParseAndPacking,
                               BleReadResponse response){
        mMac = mac;
        mServiceUUID = serviceUUID;
        mCharacterUUID = characterUUID;
        mNeedParseAndPacking = needParseAndPacking;
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

    public boolean isNeedParseAndPacking() {
        return mNeedParseAndPacking;
    }

    public BleReadResponse getResponse() {
        return mResponse;
    }
}
