package com.lc.app.transaction;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lc.app.R;

import java.util.List;

/**
 * Created by orange on 18-4-11.
 */

public class TransactionHistoryFragment extends Fragment {

    private TextView mStatusView;


    private RecyclerView mListView;
    private HistoryAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transaction_history, container,false);
        return view;
    }

    @Override
    public void onViewCreated(View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mStatusView = view.findViewById(R.id.statusView);
        mListView = view.findViewById(R.id.list);
        mAdapter = new HistoryAdapter(getLayoutInflater(), this);
        mListView.setAdapter(mAdapter);
    }

    public void showHistoryResult(String error, Object result) {
        if (!TextUtils.isEmpty(error)) {
            mStatusView.setText(error);
            mStatusView.setVisibility(View.VISIBLE);
            return;
        }

        if (result == null) {
            //No result
            mStatusView.setText(R.string.text_no_transaction_history);
            mStatusView.setVisibility(View.VISIBLE);
        } else {
            mStatusView.setVisibility(View.GONE);
            Gson gson = new Gson();
            List<History> list = gson.fromJson(String.valueOf(result),
                    new TypeToken<List<History>>() {
                    }.getType());
            mAdapter.addOrSetData(list, true);
        }
    }
}
