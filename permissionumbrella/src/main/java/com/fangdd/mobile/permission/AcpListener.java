package com.fangdd.mobile.permission;

import java.util.List;

/**
 * Created by hanxu on 2017/3/14.
 */

public interface AcpListener {
    /**
     *同意
     */
    void onGranted();

    /**
     * 拒绝
     * @param permissionsAllocated 已申请成功的权限列表
     */
    void onDenied(List<String> permissionsAllocated);
}
