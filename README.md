# PermissionUmbrella(jcenter审核中)

一个基于gradle 插件annotationProcessor 的Android 权限分配插件。将方法与权限绑定起来，通过注解自动生成代码，然后使用相应的回调方法来即时申请权限，并执行相应的方法。如果权限被用户拒绝，那也可以提示用户相关权限所需以及执行权限被拒绝时的方法。

- 自动生成权限请求代码，摆脱重复工作.
- 使用**PermissionAllocation**注解来为你的方法申请权限.
- 使用**PermissionDeny**注解来为你的申请权限来提供特定的权限被拒绝后所执行的方法.
- 支持**权限**组（复数权限）的申请.
- 支持权限方法从**super class**继承，但所需的**method flag**（方法唯一标识）不能与**super class**重复.
- 权限被拒绝后，可以跳转至**设置页进行设置**.
- 为每个权限申请方法设置不同的权限请求提示语（**doing**）

## 开始使用

在对应的项目的gradle中的**dependencies**添加如下依赖
``` gradle
 compile 'com.fangdd.hanxu:permissionumbrella:0.6.0'
 annotationProcessor 'com.fangdd.hanxu:permissionumbrella-processor:0.6.0'
```

- 在需要权限申请的**Activity**或**Fragment(DialogFragment)**中添加一个**PermissionUmbrella**对象.
- 在对应的**onCreate**()和**onCreateView**()方法中添加相应**初始化**代码，并为创建的**PermissionUmbrella**对象赋值.
- 为你的**public方法**添加**PermissionAllocation**注解，使其成为一个权限权限申请方法.
- （可选）为你的权限申请方法添加对应的权限申请被拒绝时执行的方法，使用**PermissionDeny**注解来修饰.
- 在需要调用权限申请方法的地方，使用创建的**PermissionUmbrella**对象的**invokePermission**方法来调用相应方法。

##在Activity中的使用
### 初始化操作
``` java
PermissionUmbrella allocation;

Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        allocation = Umbrella.allocation(this);
    }
```
### 生成一个权限申请方法
``` java
@PermissionAllocation(permissions = {android.Manifest.permission.CAMERA}, methodFlag = 1)
    public void allocationPermission() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra("camerasensortype", 1); // 调用前置摄像头
        intent.putExtra("autofocus", true); // 自动对焦
        intent.putExtra("fullScreen", false); // 全屏
        intent.putExtra("showActionIcons", false);
        startActivityForResult(intent, 0x11);
    }
```

### 生成一个权限申请被拒绝时执行的方法
``` java
@PermissionDeny(methodFlag = 1)
    // 这里的permissionsAllocated是已经申请成功的权限列表
    public void allocationPermissionDeny(List<String> permissionsAllocated) {
        Toast.makeText(this, "权限1被拒绝", Toast.LENGTH_SHORT).show();
    }
```

### 调用一个权限申请方法
``` java
textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 参数为特定的methodFlag
                allocation.invokePermission(1);
            }
        });
```


##在Fragment中的使用
### 初始化操作
``` java
PermissionUmbrella allocation;

 @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ...
        allocation = Umbrella.allocation(this);
        ...
    }

```

###其他操作与在Activity中一致.
