package cn.evergrand.it.bluetooth.search.response;

import cn.evergrand.it.bluetooth.search.SearchResult;

public interface SearchResponse {

    void onSearchStarted();

    void onDeviceFounded(SearchResult device);

    void onSearchStopped();

    void onSearchCanceled();

    void onSearchError(int errorCode);
}
