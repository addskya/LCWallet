package com.lc.app.transaction.record;

import com.lc.app.BasePresenter;
import com.lc.app.BaseView;
import com.lc.app.transaction.History;

/**
 * Created by Orange on 18-4-15.
 * Email:addskya@163.com
 */
public interface RecordContract {

    interface View extends BaseView<Presenter> {

        void back();

        void copy(History history);

        String getFormat(float value);

    }

    interface Presenter extends BasePresenter{}
}
