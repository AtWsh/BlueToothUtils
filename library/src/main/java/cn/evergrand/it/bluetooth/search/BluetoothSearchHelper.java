package cn.evergrand.it.bluetooth.search;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.reflect.Method;

import cn.evergrand.it.bluetooth.search.response.BluetoothSearchResponse;
import cn.evergrand.it.bluetooth.utils.BluetoothUtils;
import cn.evergrand.it.bluetooth.utils.proxy.ProxyBulk;
import cn.evergrand.it.bluetooth.utils.proxy.ProxyInterceptor;
import cn.evergrand.it.bluetooth.utils.proxy.ProxyUtils;

public class BluetoothSearchHelper implements IBluetoothSearchHelper, ProxyInterceptor, Handler.Callback {

    private BluetoothSearchRequest mCurrentRequest;

    private static IBluetoothSearchHelper sInstance;

    private Handler mHandler;

    private BluetoothSearchHelper() {
        mHandler = new Handler(Looper.getMainLooper(), this);
    }

    public static IBluetoothSearchHelper getInstance() {
        if (sInstance == null) {
            synchronized (BluetoothSearchHelper.class) {
                if (sInstance == null) {
                    BluetoothSearchHelper helper = new BluetoothSearchHelper();
                    sInstance = ProxyUtils.getProxy(helper, IBluetoothSearchHelper.class, helper);
                }
            }
        }
        return sInstance;
    }

    @Override
    public void startSearch(BluetoothSearchRequest request, BluetoothSearchResponse response) {
        request.setSearchResponse(new BluetoothSearchResponseImpl(response));

        if (!BluetoothUtils.isBluetoothEnabled()) {
            request.cancel();
        } else {
            stopSearch();

            if (mCurrentRequest == null) {
                mCurrentRequest = request;
                mCurrentRequest.start();
            }
        }
    }

    private class BluetoothSearchResponseImpl implements BluetoothSearchResponse {

        BluetoothSearchResponse response;

        BluetoothSearchResponseImpl(BluetoothSearchResponse response) {
            this.response = response;
        }

        @Override
        public void onSearchStarted() {
            response.onSearchStarted();
        }

        @Override
        public void onDeviceFounded(SearchResult device) {
            response.onDeviceFounded(device);
        }

        @Override
        public void onSearchStopped() {
            response.onSearchStopped();
            mCurrentRequest = null;
        }

        @Override
        public void onSearchCanceled() {
            response.onSearchCanceled();
            mCurrentRequest = null;
        }
    }

    @Override
    public void stopSearch() {
        if (mCurrentRequest != null) {
            mCurrentRequest.cancel();
            mCurrentRequest = null;
        }
    }

    @Override
    public boolean onIntercept(Object object, Method method, Object[] args) {
        mHandler.obtainMessage(0, new ProxyBulk(object, method,args)).sendToTarget();
        return true;
    }

    @Override
    public boolean handleMessage(Message msg) {
        ProxyBulk.safeInvoke(msg.obj);
        return true;
    }
}
