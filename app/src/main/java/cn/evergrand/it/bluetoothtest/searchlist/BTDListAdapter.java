package cn.evergrand.it.bluetoothtest.searchlist;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wenshenghui.bluetoothtest.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.evergrand.it.bluetooth.search.SearchResult;

/**
 * author: wenshenghui
 * created on: 2018/6/14 17:26
 * description:
 */
public class BTDListAdapter extends RecyclerView.Adapter<BTDListViewHolder> implements Comparator<SearchResult> {

    private List<SearchResult> mDataList = new ArrayList<>();
    private Context mContext;
    private BTDListActivity.InConnectionListener mInConnectionListener;

    public BTDListAdapter(Context context) {
        mContext = context;
    }

    @Override
    public BTDListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 填充布局
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_btd_item, null);
        BTDListViewHolder holder = new BTDListViewHolder(view, this);
        return holder;
    }

    @Override
    public void onBindViewHolder(BTDListViewHolder holder, int position) {
        SearchResult result = mDataList.get(position);
        if (result != null) {
            holder.fillData(result);
            if (mInConnectionListener != null) {
                holder.setInConnectionListener(mInConnectionListener);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public void setDataList(List<SearchResult> datas) {
        mDataList.clear();
        mDataList.addAll(datas);
        Collections.sort(mDataList, this);
        notifyDataSetChanged();
    }

    @Override
    public int compare(SearchResult lhs, SearchResult rhs) {
        return rhs.rssi - lhs.rssi;
    }

    public void setInConnectionListener(BTDListActivity.InConnectionListener inConnectionListener) {
        mInConnectionListener = inConnectionListener;
    }
}
