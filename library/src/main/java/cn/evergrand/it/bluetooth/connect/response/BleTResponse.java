package cn.evergrand.it.bluetooth.connect.response;


public interface BleTResponse<T> {
    void onResponse(int code, T data);
}
