package com.example.hanxu.mypermission;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

/**
 * Created by hanxu on 2017/3/15.
 */

public class MyFragmentActivity extends FragmentActivity {

    public static void toHere(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, MyFragmentActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
    }
}
