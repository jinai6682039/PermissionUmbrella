package com.fangdd.mobile.permission;

import android.content.Context;
import android.util.Log;

/**
 * Created by hupei on 2016/4/26.
 */
public class Acp {
    private static final String TAG = Acp.class.getSimpleName();

    private static volatile Acp mInstance;
    private AcpManager mAcpManager;

    public static Acp getInstance(Context context) {
        if (mInstance == null)
            synchronized (Acp.class) {
                if (mInstance == null) {
                    mInstance = new Acp(context);
                }
            }
        return mInstance;
    }

    private Acp(Context context) {
        mAcpManager = new AcpManager(context.getApplicationContext());
    }

    /**
     * 开始请求
     *
     * @param options
     * @param acpListener
     */
    public void request(AcpOptions options, AcpListener acpListener) {
        if (options == null) {
            Log.d(TAG, "AcpOptions is null...");
            return;
        }
        if (acpListener == null) {
            Log.d(TAG, "AcpListener is null...");
            return;
        }
        mAcpManager.request(options, acpListener);
    }

    AcpManager getAcpManager() {
        return mAcpManager;
    }
}
