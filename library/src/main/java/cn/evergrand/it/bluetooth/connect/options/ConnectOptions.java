package cn.evergrand.it.bluetooth.connect.options;

import android.os.Parcel;
import android.os.Parcelable;

public class ConnectOptions implements Parcelable {

    private int connectRetry;

    private int serviceDiscoverRetry;

    private int connectTimeout;

    private int serviceDiscoverTimeout;

    public static class Builder {

        private static final int DEFAULT_CONNECT_RETRY = 0;
        private static final int DEFAULT_SERVICE_DISCOVER_RETRY = 0;
        private static final int DEFAULT_CONNECT_TIMEOUT=  30000;
        private static final int DEFAULT_SERVICE_DISCOVER_TIMEOUT = 30000;

        private int connectRetry = DEFAULT_CONNECT_RETRY;

        private int serviceDiscoverRetry = DEFAULT_SERVICE_DISCOVER_RETRY;

        private int connectTimeout = DEFAULT_CONNECT_TIMEOUT;

        private int serviceDiscoverTimeout = DEFAULT_SERVICE_DISCOVER_TIMEOUT;

        public Builder setConnectRetry(int retry) {
            connectRetry = retry;
            return this;
        }

        public Builder setServiceDiscoverRetry(int retry) {
            serviceDiscoverRetry = retry;
            return this;
        }

        public Builder setConnectTimeout(int timeout) {
            connectTimeout = timeout;
            return this;
        }

        public Builder setServiceDiscoverTimeout(int timeout) {
            serviceDiscoverTimeout = timeout;
            return this;
        }

        public ConnectOptions build() {
            return new ConnectOptions(this);
        }
    }

    public ConnectOptions(Builder builder) {
        this.connectRetry = builder.connectRetry;
        this.serviceDiscoverRetry = builder.serviceDiscoverRetry;
        this.connectTimeout = builder.connectTimeout;
        this.serviceDiscoverTimeout = builder.serviceDiscoverTimeout;
    }

    protected ConnectOptions(Parcel in) {
        connectRetry = in.readInt();
        serviceDiscoverRetry = in.readInt();
        connectTimeout = in.readInt();
        serviceDiscoverTimeout = in.readInt();
    }

    public static final Creator<ConnectOptions> CREATOR = new Creator<ConnectOptions>() {
        @Override
        public ConnectOptions createFromParcel(Parcel in) {
            return new ConnectOptions(in);
        }

        @Override
        public ConnectOptions[] newArray(int size) {
            return new ConnectOptions[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(connectRetry);
        dest.writeInt(serviceDiscoverRetry);
        dest.writeInt(connectTimeout);
        dest.writeInt(serviceDiscoverTimeout);
    }

    public int getConnectRetry() {
        return connectRetry;
    }

    public void setConnectRetry(int connectRetry) {
        this.connectRetry = connectRetry;
    }

    public int getServiceDiscoverRetry() {
        return serviceDiscoverRetry;
    }

    public void setServiceDiscoverRetry(int serviceDiscoverRetry) {
        this.serviceDiscoverRetry = serviceDiscoverRetry;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getServiceDiscoverTimeout() {
        return serviceDiscoverTimeout;
    }

    public void setServiceDiscoverTimeout(int serviceDiscoverTimeout) {
        this.serviceDiscoverTimeout = serviceDiscoverTimeout;
    }

    @Override
    public String toString() {
        return "ConnectOptions{" +
                "connectRetry=" + connectRetry +
                ", serviceDiscoverRetry=" + serviceDiscoverRetry +
                ", connectTimeout=" + connectTimeout +
                ", serviceDiscoverTimeout=" + serviceDiscoverTimeout +
                '}';
    }
}
