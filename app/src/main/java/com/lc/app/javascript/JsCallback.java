package com.lc.app.javascript;

/**
 * Created by Orange on 18-3-27.
 * Email:addskya@163.com
 */

public interface JsCallback {

    int MESSAGE_INIT_WALLET = 0;

    int MESSAGE_BALANCE = 10;

    int MESSAGE_RATE = 20;

    int MESSAGE_TRANSFER = 30;

    int MESSAGE_TRANSFER_HISTORY = 40;

    /**
     * The callback when call JavaScript
     *
     * @param message the message type
     *                {@link #MESSAGE_BALANCE,
     *                #MESSAGE_RATE,
     *                #MESSAGE_TRANSFER,
     *                #MESSAGE_TRANSFER_HISTORY}
     * @param error   the error info or Null
     * @param result  the call result
     */
    void onCallback(int message, String error, Object result);
}
