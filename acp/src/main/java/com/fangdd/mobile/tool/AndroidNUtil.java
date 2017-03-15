package com.fangdd.mobile.tool;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import java.io.File;

/**
 * Created by hanxu on 2017/3/15.
 */

public class AndroidNUtil {

    public static Uri getFileUriFixAndroidN(Context context, File file) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return Uri.fromFile(file);
        } else {
            return FileProvider.getUriForFile(context, "com.fangdd.mobile.tool.fileProvider", file);
        }
    }
}
