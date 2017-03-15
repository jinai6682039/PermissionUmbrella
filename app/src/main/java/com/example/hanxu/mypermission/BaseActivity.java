package com.example.hanxu.mypermission;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.fangdd.mobile.PermissionUmbrella;
import com.fangdd.mobile.Umbrella;

/**
 * Created by hanxu on 2017/3/15.
 */

public abstract class BaseActivity extends Activity{

    PermissionUmbrella allocation;
    Context mContext;

    public abstract void init();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        allocation = Umbrella.allocation(this);
        init();
    }
}
