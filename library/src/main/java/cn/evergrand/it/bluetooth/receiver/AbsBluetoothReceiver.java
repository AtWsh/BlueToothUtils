package cn.evergrand.it.bluetooth.receiver;


import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import java.util.Collections;
import java.util.List;

import cn.evergrand.it.bluetooth.BluetoothContext;
import cn.evergrand.it.bluetooth.receiver.listener.BluetoothReceiverListener;
import cn.evergrand.it.bluetooth.utils.ListUtils;

public abstract class AbsBluetoothReceiver {

    protected Context mContext;

    protected Handler mHandler;

    protected IReceiverDispatcher mDispatcher;

    protected AbsBluetoothReceiver(IReceiverDispatcher dispatcher) {
        mDispatcher = dispatcher;
        mContext = BluetoothContext.get();
        mHandler = new Handler(Looper.getMainLooper());
    }

    boolean containsAction(String action) {
        List<String> actions = getActions();
        if (!ListUtils.isEmpty(actions) && !TextUtils.isEmpty(action)) {
            return actions.contains(action);
        }
        return false;
    }

    protected List<BluetoothReceiverListener> getListeners(Class<?> clazz) {
        List<BluetoothReceiverListener> listeners = mDispatcher.getListeners(clazz);
        return listeners != null ? listeners : Collections.EMPTY_LIST;
    }

    abstract List<String> getActions();

    abstract boolean onReceive(Context context, Intent intent);
}
