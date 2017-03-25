package com.example.hanxu.mypermission;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fangdd.annotation.PermissionAllocation;
import com.fangdd.mobile.PermissionUmbrella;
import com.fangdd.mobile.Umbrella;

/**
 * Created by hanxu on 2017/3/24.
 */

public class MyDialogFragment extends DialogFragment {

    PermissionUmbrella umbrella;
    TextView textView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_layout, container, false);
        umbrella = Umbrella.allocation(this);
        textView = (TextView) view.findViewById(R.id.test);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                umbrella.invokePermission(1);
            }
        });

        return view;
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
}
