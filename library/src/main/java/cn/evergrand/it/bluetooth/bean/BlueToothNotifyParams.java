package cn.evergrand.it.bluetooth.bean;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.UUID;

import cn.evergrand.it.bluetooth.connect.response.BleNotifyResponse;

/**
 * author: wenshenghui
 * created on: 2018/7/19 17:36
 * description:
 */
public class BlueToothNotifyParams {
    private String mMac;
    private UUID mServiceUUID;
    private UUID mCharacterUUID;
    private BleNotifyResponse mResponse;
    private boolean mNeedParseAndPacking;

    public BlueToothNotifyParams(@NonNull String mac, UUID serviceUUID, UUID characterUUID,
                                 boolean needParseAndPacking, BleNotifyResponse response){
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

    public BleNotifyResponse getResponse() {
        return mResponse;
    }
}
