package cn.evergrand.it.bluetooth.connect;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import cn.evergrand.it.bluetooth.BlueToothConstants;
import cn.evergrand.it.bluetooth.Code;
import cn.evergrand.it.bluetooth.RuntimeChecker;
import cn.evergrand.it.bluetooth.connect.options.ConnectOptions;
import cn.evergrand.it.bluetooth.connect.request.BleConnectRequest;
import cn.evergrand.it.bluetooth.connect.request.BleIndicateRequest;
import cn.evergrand.it.bluetooth.connect.request.BleMtuRequest;
import cn.evergrand.it.bluetooth.connect.request.BleNotifyRequest;
import cn.evergrand.it.bluetooth.connect.request.BleReadDescriptorRequest;
import cn.evergrand.it.bluetooth.connect.request.BleReadRequest;
import cn.evergrand.it.bluetooth.connect.request.BleReadRssiRequest;
import cn.evergrand.it.bluetooth.connect.request.BleRefreshCacheRequest;
import cn.evergrand.it.bluetooth.connect.request.BleRequest;
import cn.evergrand.it.bluetooth.connect.request.BleUnnotifyRequest;
import cn.evergrand.it.bluetooth.connect.request.BleWriteDescriptorRequest;
import cn.evergrand.it.bluetooth.connect.request.BleWriteNoRspRequest;
import cn.evergrand.it.bluetooth.connect.request.BleWriteRequest;
import cn.evergrand.it.bluetooth.connect.response.BleGeneralResponse;
import cn.evergrand.it.bluetooth.utils.BluetoothLog;
import cn.evergrand.it.bluetooth.utils.ListUtils;

public class BleConnectDispatcher implements IBleConnectDispatcher, RuntimeChecker, Handler.Callback {

    private static final int MAX_REQUEST_COUNT = 100;
    private static final int MSG_SCHEDULE_NEXT = 0x12;

    private List<BleRequest> mBleWorkList;
    private BleRequest mCurrentRequest;

    private IBleConnectWorker mWorker;

    private String mAddress;

    private Handler mHandler;

    public static BleConnectDispatcher newInstance(String mac) {
        return new BleConnectDispatcher(mac);
    }

    private BleConnectDispatcher(String mac) {
        mAddress = mac;
        mBleWorkList = new LinkedList<BleRequest>();
        mWorker = new BleConnectWorker(mac, this);
        mHandler = new Handler(Looper.myLooper(), this);
    }

    public void connect(ConnectOptions options, BleGeneralResponse response) {
        addNewRequest(new BleConnectRequest(options, response));
    }

    public void disconnect() {
        checkRuntime();

        BluetoothLog.w(String.format("Process disconnect"));

        if (mCurrentRequest != null) {
            mCurrentRequest.cancel();
            mCurrentRequest = null;
        }

        for (BleRequest request : mBleWorkList) {
            request.cancel();
        }

        mBleWorkList.clear();

        mWorker.closeGatt();
    }

    public void refreshCache() {
        addNewRequest(new BleRefreshCacheRequest(null));
    }

    public void clearRequest(int clearType) {
        checkRuntime();

        BluetoothLog.w(String.format("clearRequest %d", clearType));

        List<BleRequest> requestClear = new LinkedList<BleRequest>();

        if (clearType == 0) {
            requestClear.addAll(mBleWorkList);
        } else {
            for (BleRequest request : mBleWorkList) {
                if (isRequestMatch(request, clearType)) {
                    requestClear.add(request);
                }
            }
        }

        for (BleRequest request : requestClear) {
            request.cancel();
        }

        mBleWorkList.removeAll(requestClear);
    }

    private boolean isRequestMatch(BleRequest request, int requestType) {
        if ((requestType & BlueToothConstants.REQUEST_READ) != 0) {
            return request instanceof BleReadRequest;
        } else if ((requestType & BlueToothConstants.REQUEST_WRITE) != 0) {
            return request instanceof BleWriteRequest || request instanceof BleWriteNoRspRequest;
        } else if ((requestType & BlueToothConstants.REQUEST_NOTIFY) != 0) {
            return request instanceof BleNotifyRequest || request instanceof BleUnnotifyRequest
                    || request instanceof BleIndicateRequest;
        } else if ((requestType & BlueToothConstants.REQUEST_RSSI) != 0) {
            return request instanceof BleReadRssiRequest;
        } else {
            return false;
        }
    }

    public void read(UUID service, UUID character, BleGeneralResponse response) {
        addNewRequest(new BleReadRequest(service, character, response));
    }

    public void write(UUID service, UUID character, byte[] bytes, BleGeneralResponse response) {
        addNewRequest(new BleWriteRequest(service, character, bytes, response));
    }

    public void writeNoRsp(UUID service, UUID character, byte[] bytes, BleGeneralResponse response) {
        addNewRequest(new BleWriteNoRspRequest(service, character, bytes, response));
    }

    public void readDescriptor(UUID service, UUID character, UUID descriptor, BleGeneralResponse response) {
        addNewRequest(new BleReadDescriptorRequest(service, character, descriptor, response));
    }

    public void writeDescriptor(UUID service, UUID character, UUID descriptor, byte[] bytes, BleGeneralResponse response) {
        addNewRequest(new BleWriteDescriptorRequest(service, character, descriptor, bytes, response));
    }

    public void notify(UUID service, UUID character, BleGeneralResponse response) {
        addNewRequest(new BleNotifyRequest(service, character, response));
    }

    public void unnotify(UUID service, UUID character, BleGeneralResponse response) {
        addNewRequest(new BleUnnotifyRequest(service, character, response));
    }

    public void indicate(UUID service, UUID character, BleGeneralResponse response) {
        addNewRequest(new BleIndicateRequest(service, character, response));
    }

    public void unindicate(UUID service, UUID character, BleGeneralResponse response) {
        addNewRequest(new BleUnnotifyRequest(service, character, response));
    }

    public void readRemoteRssi(BleGeneralResponse response) {
        addNewRequest(new BleReadRssiRequest(response));
    }

    public void requestMtu(int mtu, BleGeneralResponse response) {
        addNewRequest(new BleMtuRequest(mtu, response));
    }

    private void addNewRequest(BleRequest request) {
        checkRuntime();

        if (mBleWorkList.size() < MAX_REQUEST_COUNT) {
            request.setRuntimeChecker(this);
            request.setAddress(mAddress);
            request.setWorker(mWorker);
            mBleWorkList.add(request);
        } else {
            request.onResponse(Code.REQUEST_OVERFLOW);
        }

        scheduleNextRequest(10);
    }

    @Override
    public void onRequestCompleted(BleRequest request) {
        checkRuntime();

        if (request != mCurrentRequest) {
            throw new IllegalStateException("request not match");
        }

        mCurrentRequest = null;

        scheduleNextRequest(10);
    }

    private void scheduleNextRequest(long delayInMillis) {
        mHandler.sendEmptyMessageDelayed(MSG_SCHEDULE_NEXT, delayInMillis);
    }

    private void scheduleNextRequest() {
        if (mCurrentRequest != null) {
            return;
        }

        if (!ListUtils.isEmpty(mBleWorkList)) {
            mCurrentRequest = mBleWorkList.remove(0);
            mCurrentRequest.process(this);
        }
    }

    @Override
    public void checkRuntime() {
        if (Thread.currentThread() != mHandler.getLooper().getThread()) {
            throw new IllegalStateException("Thread Context Illegal");
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_SCHEDULE_NEXT:
                scheduleNextRequest();
                break;
        }
        return true;
    }
}
