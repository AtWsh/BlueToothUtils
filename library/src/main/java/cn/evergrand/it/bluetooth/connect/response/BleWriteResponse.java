package cn.evergrand.it.bluetooth.connect.response;


import cn.evergrand.it.bluetooth.encry.IBlueToothDecrypt;
import cn.evergrand.it.bluetooth.utils.DataUtils;

public abstract class BleWriteResponse implements BleTResponse<byte[]> {

    private IBlueToothDecrypt mIBlueToothDecrypt;
    private boolean mNeedParseAndPacking = true;

    public void setBlueToothDecrypt(IBlueToothDecrypt blueToothDecrypt) {
        this.mIBlueToothDecrypt = blueToothDecrypt;
    }

    public void setNeedParseAndPacking(boolean mNeedParseAndPacking) {
        this.mNeedParseAndPacking = mNeedParseAndPacking;
    }

    public void onRealResponse(int code, byte[] value) {
        byte[] data = DataUtils.parseData(mNeedParseAndPacking, value, mIBlueToothDecrypt);
        onResponse(code, data);
    }
}
