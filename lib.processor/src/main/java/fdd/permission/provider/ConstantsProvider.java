package fdd.permission.provider;

import com.squareup.javapoet.ClassName;

/**
 * Created by hanxu on 2017/3/6.
 */

public interface ConstantsProvider {
    ClassName APPCOMPAT_ACTIVITY = ClassName.get("android.support.v4.app", "AppCompatActivity");
    String ACTIVITY = "android.app.Activity";
    String FRAGMENT = "android.support.v4.app.Fragment";

    String GENERATED_CLASS_SUFFIX = "_PermissionAllocater";
}
