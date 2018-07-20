package cn.evergrand.it.bluetooth.connect.response;

import java.util.UUID;

import cn.evergrand.it.bluetooth.encry.IBlueToothDecrypt;
import cn.evergrand.it.bluetooth.utils.DataUtils;

public abstract class BleNotifyResponse implements BleResponse {

    private boolean mNeedParseAndPacking = true;

    private IBlueToothDecrypt mIBlueToothDecrypt;

    public BleNotifyResponse(boolean needParseAndPacking) {
        mNeedParseAndPacking = needParseAndPacking;
    }

    public void setBlueToothDecrypt(IBlueToothDecrypt blueToothDecrypt) {
        this.mIBlueToothDecrypt = blueToothDecrypt;
    }

    public void onRealResponse(UUID service, UUID character, byte[] value) {
        byte[] data = DataUtils.parseData(mNeedParseAndPacking, value, mIBlueToothDecrypt);
        onNotify(service, character, data);
    }

    public abstract void onNotify(UUID service, UUID character, byte[] value);


}
