package com.lc.app.main;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lc.app.databinding.ItemAccountHeaderBinding;

/**
 * Created by Orange on 18-3-18.
 * Email:addskya@163.com
 */
class AccountHeaderViewHolder extends RecyclerView.ViewHolder {

    private AccountHeaderViewHolder(@NonNull View view) {
        super(view);
    }

    /**
     * Create the Account HeaderViewHolder
     *
     * @param inflater the LayoutInflater
     * @param parent   the ViewParent or Null
     * @return the AccountHeaderViewHolder
     */
    static AccountHeaderViewHolder newHolder(@NonNull LayoutInflater inflater,
                                             @Nullable ViewGroup parent) {
        ItemAccountHeaderBinding binding = ItemAccountHeaderBinding.inflate(
                inflater, parent, false);
        return new AccountHeaderViewHolder(binding.getRoot());
    }

    /**
     * bind View to ViewHolder
     *
     * @param view the HomeContract.View
     */
    void bind(@Nullable HomeContract.View view) {
        ItemAccountHeaderBinding binding = DataBindingUtil.getBinding(itemView);
        binding.setView(view);
        binding.executePendingBindings();
    }

}
