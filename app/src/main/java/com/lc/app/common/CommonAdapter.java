package com.lc.app.common;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.lc.app.BaseAdapter;
import com.lc.app.model.CommonEntry;

/**
 * Created by Orange on 18-4-15.
 * Email:addskya@163.com
 */
class CommonAdapter extends BaseAdapter<CommonEntry, CommonContract.View> {

    CommonAdapter(
            @NonNull LayoutInflater inflater,
            @Nullable CommonContract.View view) {
        super(inflater, view);
    }

    @Nullable
    @Override
    protected RecyclerView.ViewHolder newHolder(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup parent,
            int viewType) {
        return CommonViewHolder.newHolder(inflater, parent);
    }

    @Override
    protected void bindViewHolder(
            @NonNull RecyclerView.ViewHolder holder,
            @Nullable CommonEntry data,
            @Nullable CommonContract.View view) {
        if (holder instanceof CommonViewHolder) {
            ((CommonViewHolder) holder).bind(data, view);
        }
    }
}
