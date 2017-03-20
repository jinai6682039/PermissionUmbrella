package com.example.hanxu.mypermission;

import android.Manifest;
import android.content.Intent;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.fangdd.annotation.PermissionAllocation;
import com.fangdd.annotation.PermissionDeny;

import java.util.List;

/**
 * Created by hanxu on 2017/3/20.
 */

public class MainActivity2 extends MainActivity {

    @Override
    public void init() {
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.test);
        textView1 = (TextView) findViewById(R.id.test1);
        textView2 = (TextView) findViewById(R.id.test2);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allocation.invokePermission(2);
            }
        });
        textView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allocation.invokePermission(1);
            }
        });
        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyFragmentActivity.toHere(mContext);
            }
        });
    }

    @PermissionAllocation(permissions = {android.Manifest.permission.CAMERA}, methodFlag = 4)
    public void allocationPermission() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra("camerasensortype", 1); // 调用前置摄像头
        intent.putExtra("autofocus", true); // 自动对焦
        intent.putExtra("fullScreen", false); // 全屏
        intent.putExtra("showActionIcons", false);
        startActivityForResult(intent, 0x11);
    }

    @PermissionDeny(methodFlag = 4)
    public void allocationPermissionDeny(List<String> permissionsAllocated) {
        Toast.makeText(this, "权限1被拒绝", Toast.LENGTH_SHORT).show();
    }

    @PermissionDeny(methodFlag = 7)
    public void allocationPermissionDeny_2(List<String> permissionsAllocated) {
        Toast.makeText(this, "权限2被拒绝", Toast.LENGTH_SHORT).show();
    }

    @PermissionAllocation(permissions = {
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
    }, methodFlag =5)
    public void allocationPermission_2() {

    }

    @PermissionAllocation(permissions = {
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
    }, methodFlag = 6)
    public void allocationPermission_3() {

    }


}
