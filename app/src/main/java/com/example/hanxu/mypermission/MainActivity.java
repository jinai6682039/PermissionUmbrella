package com.example.hanxu.mypermission;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.fangdd.annotation.PermissionAllocation;
import com.fangdd.annotation.PermissionDeny;
import com.fangdd.mobile.PermissionUmbrella;
import com.fangdd.mobile.Umbrella;

import java.util.List;

public class MainActivity extends Activity {

    PermissionUmbrella allocation;

    public static final String[] permissions = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
    };

    TextView textView;
    TextView textView1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        allocation = Umbrella.allocation(this);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.test);
        textView1 = (TextView) findViewById(R.id.test1);
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
    }

    @PermissionAllocation(permissions = {android.Manifest.permission.CAMERA}, methodFlag = 1)
    public void allocationPermission() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra("camerasensortype", 1); // 调用前置摄像头
        intent.putExtra("autofocus", true); // 自动对焦
        intent.putExtra("fullScreen", false); // 全屏
        intent.putExtra("showActionIcons", false);
        startActivityForResult(intent, 0x11);
    }

    @PermissionDeny(methodFlag = 1)
    public void allocationPermissionDeny(List<String> permissionsAllocated) {
        Toast.makeText(this, "权限1被拒绝", Toast.LENGTH_SHORT).show();
    }

    @PermissionDeny(methodFlag = 2)
    public void allocationPermissionDeny_2(List<String> permissionsAllocated) {
        Toast.makeText(this, "权限2被拒绝", Toast.LENGTH_SHORT).show();
    }

    @PermissionAllocation(permissions = {
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
    }, methodFlag = 2)
    public void allocationPermission_2() {

    }

    @PermissionAllocation(permissions = {
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
    }, methodFlag = 3)
    public void allocationPermission_3() {

    }
}
