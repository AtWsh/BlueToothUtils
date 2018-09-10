package cn.evergrand.it.bluetooth.connect.response;

import cn.evergrand.it.bluetooth.encry.IBlueToothDecrypt;

/**
 * author: wenshenghui
 * created on: 2018/9/10 19:30
 * description:
 */
public class BleNotifyResponseBody {

    public boolean mNeedParseAndPacking = true;

    public IBlueToothDecrypt mIBlueToothDecrypt;

    public BleNotifyResponse mNotifyResponse;

    public BleNotifyResponseBody(boolean needParseAndPacking, IBlueToothDecrypt blueToothDecrypt,
                                 BleNotifyResponse notifyResponse) {
        mNeedParseAndPacking = needParseAndPacking;
        mIBlueToothDecrypt = blueToothDecrypt;
        mNotifyResponse = notifyResponse;
    }
}
