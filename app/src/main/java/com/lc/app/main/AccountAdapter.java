package com.lc.app.main;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.lc.app.BaseAdapter;
import com.lc.app.model.Account;

/**
 * Created by Orange on 18-3-17.
 * Email:addskya@163.com
 */
class AccountAdapter extends BaseAdapter<Account, HomeContract.View> {

    private static final String TAG = "AccountAdapter";
    private static final int ITEM_TYPE_HEADER_1 = 0;
    private static final int ITEM_TYPE_HEADER_2 = 1;
    private static final int ITEM_TYPE_ACCOUNT = 2;

    private static final int POSITION_HEADER_1 = 0;
    private static final int POSITION_HEADER_2 = 1;

    AccountAdapter(@NonNull LayoutInflater inflater,
                   @Nullable HomeContract.View view) {
        super(inflater, view);
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case POSITION_HEADER_1: {
                return ITEM_TYPE_HEADER_1;
            }
            case POSITION_HEADER_2: {
                return ITEM_TYPE_HEADER_2;
            }
            default:
                return ITEM_TYPE_ACCOUNT;
        }
    }

    @Override
    public int getHeaderCount() {
        return 2;
    }

    @Nullable
    @Override
    protected RecyclerView.ViewHolder newHolder(@NonNull LayoutInflater inflater,
                                                @Nullable ViewGroup parent,
                                                int viewType) {
        switch (viewType) {
            case ITEM_TYPE_HEADER_1: {
                return AccountHeaderViewHolder.newHolder(inflater, parent);
            }
            case ITEM_TYPE_HEADER_2: {
                return AccountViewHolder.newHolder(inflater, parent);
            }
            case ITEM_TYPE_ACCOUNT: {
                return AccountItemViewHolder.newHolder(inflater, parent);
            }
            default: {
                throw new IllegalArgumentException("Unknown viewType:" + viewType);
            }
        }
    }

    @Override
    protected void bindViewHolder(@NonNull RecyclerView.ViewHolder holder,
                                  @Nullable Account data,
                                  @Nullable HomeContract.View view) {
        if (holder instanceof AccountItemViewHolder) {
            ((AccountItemViewHolder) holder).bind(data, view);
        } else if (holder instanceof AccountHeaderViewHolder) {
            ((AccountHeaderViewHolder) holder).bind(view);
        }
    }
}
