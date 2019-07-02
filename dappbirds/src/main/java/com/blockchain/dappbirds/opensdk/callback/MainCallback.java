package com.blockchain.dappbirds.opensdk.callback;

import android.os.Handler;

import java.lang.reflect.Method;

/**
 * 回调的基类
 *
 * @param <C>
 * @param <T>
 */
public class MainCallback<C, T> {
    public C callback;
    public Handler mHandler = new Handler();

    public MainCallback(C callback) {
        this.callback = callback;
    }


    /**
     * 失败的回调
     */
    public void onError(final int errCode, final String errInfo) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Method onError = callback.getClass().getMethod("onError", int.class, String.class);
                    onError.invoke(callback, errCode, errInfo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 成功的回调，带参数的可以是任何类型
     */
    public void onSuccess(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Method onSuccess = callback.getClass().getMethod("onSuccess", String.class);
                    onSuccess.invoke(callback, message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 成功的回调，不带参数的
     */
    public void onSuccess() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Method onSuccess = callback.getClass().getMethod("onSuccess");
                    onSuccess.invoke(callback);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public void runOnUiThread(final Runnable runnable) {
        if (mHandler != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    runnable.run();
                }
            });
        }
    }
}
