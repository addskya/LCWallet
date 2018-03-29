package com.lc.app.transaction;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lc.app.databinding.ItemTransactionHistoryBinding;
import com.lc.app.ui.RecyclerView;

/**
 * Created by Orange on 18-3-29.
 * Email:addskya@163.com
 */

class HistoryViewHolder extends RecyclerView.ViewHolder {

    private HistoryViewHolder(View view) {
        super(view);
    }

    static HistoryViewHolder newHolder(@NonNull LayoutInflater inflater,
                                       @Nullable ViewGroup parent) {
        ItemTransactionHistoryBinding binding = ItemTransactionHistoryBinding.inflate(
                inflater, parent, false);
        return new HistoryViewHolder(binding.getRoot());
    }

    void bind(@Nullable History data) {
        ItemTransactionHistoryBinding binding = DataBindingUtil.getBinding(itemView);
        binding.setHistory(data);
        binding.executePendingBindings();
    }
}
