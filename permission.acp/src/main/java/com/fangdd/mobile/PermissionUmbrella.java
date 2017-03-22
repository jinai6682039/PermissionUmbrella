package com.fangdd.mobile;

import android.support.annotation.UiThread;

/**
 * Created by hanxu on 2017/3/7.
 */

public interface PermissionUmbrella {

    PermissionUmbrella EMPTY = new PermissionUmbrella() {
        @Override
        public void invokePermission(int permissionFlag) {
        }
    };

    @UiThread
    void invokePermission(int permissionFlag);
}
