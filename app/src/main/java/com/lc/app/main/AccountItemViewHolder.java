package com.lc.app.main;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lc.app.databinding.ItemAccountBinding;
import com.lc.app.model.Account;

/**
 * Created by Orange on 18-3-17.
 * Email:addskya@163.com
 */
class AccountItemViewHolder extends RecyclerView.ViewHolder {

    private AccountItemViewHolder(View itemView) {
        super(itemView);
    }

    /**
     * Create the AccountItemViewHolder object
     *
     * @param inflater the LayoutInflater
     * @param parent   the view parent or Null
     * @return AccountItemViewHolder
     */
    static RecyclerView.ViewHolder newHolder(@NonNull LayoutInflater inflater,
                                             @Nullable ViewGroup parent) {
        ItemAccountBinding binding = ItemAccountBinding.inflate(
                inflater, parent, false);
        return new AccountItemViewHolder(binding.getRoot());
    }

    /**
     * Bind the Data And View into ViewHolder
     *
     * @param data the Account Data
     * @param view the Ui
     */
    void bind(@Nullable Account data,
              @Nullable HomeContract.View view) {
        ItemAccountBinding binding = DataBindingUtil.getBinding(itemView);
        binding.setAccount(data);
        binding.setView(view);
        binding.executePendingBindings();
    }
}
