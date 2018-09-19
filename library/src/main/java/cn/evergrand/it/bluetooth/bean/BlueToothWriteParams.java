package cn.evergrand.it.bluetooth.bean;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.UUID;

import cn.evergrand.it.bluetooth.connect.response.BleWriteResponse;
import cn.evergrand.it.bluetooth.utils.type.DataType;

/**
 * author: wenshenghui
 * created on: 2018/7/19 17:36
 * description:
 */
public class BlueToothWriteParams {


    private byte[] mValue;
    private String mMac;
    private UUID mServiceUUID;
    private UUID mCharacterUUID;
    private BleWriteResponse mResponse;
    private boolean mNeedParseAndPacking = true;
    private int mDataType;
    private int mRequestId = 0;

    public BlueToothWriteParams(byte[] value, @NonNull String mac, UUID serviceUUID,
                                UUID characterUUID, boolean needParseAndPacking,
                                @DataType.BtDeviceDataType int type, int requestId,
                                BleWriteResponse response){
        mValue = value;
        mMac = mac;
        mServiceUUID = serviceUUID;
        mCharacterUUID = characterUUID;
        mNeedParseAndPacking = needParseAndPacking;
        mDataType = type;
        mRequestId = requestId;
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

    public BleWriteResponse getResponse() {
        return mResponse;
    }

    public boolean isNeedParseAndPacking() {
        return mNeedParseAndPacking;
    }

    public byte[] getValue() {
        return mValue ;
    }
    public int getDataType() {
        return mDataType;
    }

    public int getRequestId() {
        return mRequestId;
    }
}
