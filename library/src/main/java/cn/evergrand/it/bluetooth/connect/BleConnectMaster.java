package cn.evergrand.it.bluetooth.connect;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.reflect.Method;
import java.util.UUID;

import cn.evergrand.it.bluetooth.connect.options.ConnectOptions;
import cn.evergrand.it.bluetooth.connect.response.BleGeneralResponse;
import cn.evergrand.it.bluetooth.utils.proxy.ProxyBulk;
import cn.evergrand.it.bluetooth.utils.proxy.ProxyInterceptor;
import cn.evergrand.it.bluetooth.utils.proxy.ProxyUtils;

public class BleConnectMaster implements IBleConnectMaster, ProxyInterceptor, Handler.Callback {

    private Handler mHandler;

    private String mAddress;
    private BleConnectDispatcher mBleConnectDispatcher;

    private BleConnectMaster(String mac, Looper looper) {
        mAddress = mac;
        mHandler = new Handler(looper, this);
    }

    // Runs in worker thread
    private BleConnectDispatcher getBleConnectDispatcher() {
        if (mBleConnectDispatcher == null) {
            mBleConnectDispatcher = BleConnectDispatcher.newInstance(mAddress);
        }
        return mBleConnectDispatcher;
    }

    static IBleConnectMaster newInstance(String mac, Looper looper) {
        BleConnectMaster master = new BleConnectMaster(mac, looper);
        return ProxyUtils.getProxy(master, IBleConnectMaster.class, master);
    }

    @Override
    public void connect(ConnectOptions options, BleGeneralResponse response) {
        getBleConnectDispatcher().connect(options, response);
    }

    @Override
    public void disconnect() {
        getBleConnectDispatcher().disconnect();
    }

    @Override
    public void read(UUID service, UUID character, BleGeneralResponse response) {
        getBleConnectDispatcher().read(service, character, response);
    }

    @Override
    public void write(UUID service, UUID character, byte[] bytes, BleGeneralResponse response) {
        getBleConnectDispatcher().write(service, character, bytes, response);
    }

    @Override
    public void writeNoRsp(UUID service, UUID character, byte[] bytes, BleGeneralResponse response) {
        getBleConnectDispatcher().writeNoRsp(service, character, bytes, response);
    }

    @Override
    public void readDescriptor(UUID service, UUID character, UUID descriptor, BleGeneralResponse response) {
        getBleConnectDispatcher().readDescriptor(service, character, descriptor, response);
    }

    @Override
    public void writeDescriptor(UUID service, UUID character, UUID descriptor, byte[] value, BleGeneralResponse response) {
        getBleConnectDispatcher().writeDescriptor(service, character, descriptor, value, response);
    }

    @Override
    public void notify(UUID service, UUID character, BleGeneralResponse response) {
        getBleConnectDispatcher().notify(service, character, response);
    }

    @Override
    public void unnotify(UUID service, UUID character, BleGeneralResponse response) {
        getBleConnectDispatcher().unnotify(service, character, response);
    }

    @Override
    public void readRssi(BleGeneralResponse response) {
        getBleConnectDispatcher().readRemoteRssi(response);
    }

    @Override
    public void indicate(UUID service, UUID character, BleGeneralResponse response) {
        getBleConnectDispatcher().indicate(service, character, response);
    }

    @Override
    public void requestMtu(int mtu, BleGeneralResponse response) {
        getBleConnectDispatcher().requestMtu(mtu, response);
    }

    @Override
    public void clearRequest(int clearType) {
        getBleConnectDispatcher().clearRequest(clearType);
    }

    @Override
    public void refreshCache() {
        getBleConnectDispatcher().refreshCache();
    }

    @Override
    public boolean onIntercept(Object object, Method method, Object[] args) {
        mHandler.obtainMessage(0, new ProxyBulk(object, method, args)).sendToTarget();
        return true;
    }

    @Override
    public boolean handleMessage(Message msg) {
        ProxyBulk.safeInvoke(msg.obj);
        return true;
    }
}
