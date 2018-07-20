package cn.evergrand.it.bluetooth.search;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import cn.evergrand.it.bluetooth.BlueToothConstants;
import cn.evergrand.it.bluetooth.utils.BluetoothUtils;

public class SearchRequest implements Parcelable {

    private List<SearchTask> tasks;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(tasks);
    }

    public SearchRequest() {
    }

    protected SearchRequest(Parcel in) {
        this.tasks = new ArrayList<SearchTask>();
        in.readTypedList(this.tasks, SearchTask.CREATOR);
    }

    public static final Creator<SearchRequest> CREATOR = new Creator<SearchRequest>() {
        public SearchRequest createFromParcel(Parcel source) {
            return new SearchRequest(source);
        }

        public SearchRequest[] newArray(int size) {
            return new SearchRequest[size];
        }
    };

    public List<SearchTask> getTasks() {
        return tasks;
    }

    public void setTasks(List<SearchTask> tasks) {
        this.tasks = tasks;
    }

    public static class Builder {
        private List<SearchTask> tasks;

        public Builder() {
            tasks = new ArrayList<SearchTask>();
        }

        public Builder searchBluetoothLeDevice(int duration) {
            if (BluetoothUtils.isBleSupported()) {
                SearchTask search = new SearchTask();
                search.setSearchType(BlueToothConstants.SEARCH_TYPE_BLE);
                search.setSearchDuration(duration);
                tasks.add(search);
            }
            return this;
        }

        public Builder searchBluetoothLeDevice(int duration, int times) {
            for (int i = 0; i < times; i++) {
                searchBluetoothLeDevice(duration);
            }
            return this;
        }

        public Builder searchBluetoothClassicDevice(int duration) {
            SearchTask search = new SearchTask();
            search.setSearchType(BlueToothConstants.SEARCH_TYPE_CLASSIC);
            search.setSearchDuration(duration);
            tasks.add(search);
            return this;
        }

        public Builder searchBluetoothClassicDevice(int duration, int times) {
            for (int i = 0; i < times; i++) {
                searchBluetoothClassicDevice(duration);
            }
            return this;
        }

        public SearchRequest build() {
            SearchRequest group = new SearchRequest();
            group.setTasks(tasks);
            return group;
        }
    }
}
