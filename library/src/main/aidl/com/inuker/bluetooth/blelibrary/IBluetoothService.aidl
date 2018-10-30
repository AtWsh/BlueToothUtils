// IBluetoothManager.aidl
package com.inuker.bluetooth.blelibrary;

// Declare any non-default types here with import statements

import com.inuker.bluetooth.blelibrary.IResponse;

interface IBluetoothService {
    void callBluetoothApi(int code, inout Bundle args, IResponse response);
}
