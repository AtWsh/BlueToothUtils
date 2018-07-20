package cn.evergrand.it.bluetooth.connect.response;


import cn.evergrand.it.bluetooth.encry.IBlueToothDecrypt;
import cn.evergrand.it.bluetooth.utils.DataUtils;

public abstract class BleReadResponse implements BleTResponse<byte[]> {

    private boolean mNeedParseAndPacking = true;
    private IBlueToothDecrypt mIBlueToothDecrypt;

    public BleReadResponse(boolean needParseAndPacking) {
        mNeedParseAndPacking = needParseAndPacking;
    }

    public void setBlueToothDecrypt(IBlueToothDecrypt blueToothDecrypt) {
        this.mIBlueToothDecrypt = blueToothDecrypt;
    }

    public void onRealResponse(int code, byte[] value) {
        byte[] data = DataUtils.parseData(mNeedParseAndPacking, value, mIBlueToothDecrypt);
        onResponse(code, data);
    }
}
