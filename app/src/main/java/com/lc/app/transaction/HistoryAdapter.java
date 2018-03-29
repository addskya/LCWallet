package com.lc.app.transaction;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.lc.app.BaseAdapter;

/**
 * Created by Orange on 18-3-29.
 * Email:addskya@163.com
 */

class HistoryAdapter extends BaseAdapter<History,Object> {

    public HistoryAdapter(@NonNull LayoutInflater inflater,
                          @Nullable Object view) {
        super(inflater, view);
    }

    @Nullable
    @Override
    protected RecyclerView.ViewHolder newHolder(@NonNull LayoutInflater inflater,
                                                @Nullable ViewGroup parent,
                                                int viewType) {
        return HistoryViewHolder.newHolder(inflater,parent);
    }

    @Override
    protected void bindViewHolder(@NonNull RecyclerView.ViewHolder holder,
                                  @Nullable History data,
                                  @Nullable Object view) {
        if (holder instanceof HistoryViewHolder) {
            ((HistoryViewHolder)holder).bind(data);
        }
    }
}
