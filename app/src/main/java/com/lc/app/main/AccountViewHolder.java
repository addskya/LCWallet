package com.lc.app.main;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lc.app.databinding.ItemAccountOverViewBinding;
import com.lc.app.ui.RecyclerView;

/**
 * Created by Orange on 18-3-18.
 * Email:addskya@163.com
 */
class AccountViewHolder extends RecyclerView.ViewHolder {

    private AccountViewHolder(@NonNull View view) {
        super(view);
    }

    static AccountViewHolder newHolder(@NonNull LayoutInflater inflater,
                                       @Nullable ViewGroup parent) {
        ItemAccountOverViewBinding binding = ItemAccountOverViewBinding.inflate(
                inflater, parent, false);
        return new AccountViewHolder(binding.getRoot());
    }

    void bind(@Nullable HomeContract.View view) {
        //
    }
}
