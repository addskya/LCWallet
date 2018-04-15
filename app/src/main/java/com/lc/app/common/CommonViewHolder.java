package com.lc.app.common;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.lc.app.databinding.ItemCommonBinding;
import com.lc.app.model.CommonEntry;
import com.lc.app.ui.RecyclerView;

/**
 * Created by Orange on 18-4-15.
 * Email:addskya@163.com
 */
class CommonViewHolder extends RecyclerView.ViewHolder {

    private CommonViewHolder(View itemView) {
        super(itemView);
    }

    static RecyclerView.ViewHolder newHolder(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup parent) {
        ItemCommonBinding binding = ItemCommonBinding.inflate(inflater, parent, false);
        return new CommonViewHolder(binding.getRoot());
    }

    public void bind(@Nullable CommonEntry entry,
                     CommonContract.View view) {
        ItemCommonBinding binding = DataBindingUtil.getBinding(itemView);
        if (binding == null) {
            return;
        }
        binding.setEntry(entry);
        binding.setView(view);
        binding.executePendingBindings();
    }
}
