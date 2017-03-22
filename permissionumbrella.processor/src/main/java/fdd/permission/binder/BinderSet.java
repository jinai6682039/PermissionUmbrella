package fdd.permission.binder;


import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import static com.google.auto.common.MoreElements.getPackage;
import static fdd.permission.utils.CheckUtil.checkSubOfType;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;

/**
 * Created by hanxu on 2017/3/7.
 */

public final class BinderSet {
    public static final String VIEW_TYPE = "android.view.View";
    public static final String ACTIVITY_TYPE = "android.app.Activity";
    public static final String DIALOG_TYPE = "android.app.Dialog";
    public static final String FRAGMENT_TYPE = "android.support.v4.app.Fragment";
    public static final ClassName APPCOMPAT_ACTIVITY = ClassName.get("android.support.v4.app", "AppCompatActivity");

    public static final ClassName ACP_OPTIONS = ClassName.get("com.fangdd.mobile.permission", "AcpOptions");
    public static final ClassName ACP = ClassName.get("com.fangdd.mobile.permission", "Acp");
    public static final ClassName ACP_LISTENER = ClassName.get("com.fangdd.mobile.permission", "AcpListener");

    public static final ClassName LIST = ClassName.get(List.class);

    public static final ClassName VIEW = ClassName.get("android.view", "View");
    public static final ClassName CONTEXT = ClassName.get("android.content", "Context");
    public static final ClassName RESOURCES = ClassName.get("android.content.res", "Resources");
    public static final ClassName UI_THREAD =
            ClassName.get("android.support.annotation", "UiThread");
    public static final ClassName CALL_SUPER =
            ClassName.get("android.support.annotation", "CallSuper");
    public static final ClassName SUPPRESS_LINT =
            ClassName.get("android.annotation", "SuppressLint");

    public static final String SIMPLE_PERMISSION_PREFIX = "android.permission";
    public static final String QUAKIFIED_PERMISSION_PREFIX = " android.Manifest.permission";
    public static final String METHOD_PARAMETERS_NAME = "permissionFlag";

    public static final ClassName PERMISSION_ALLOCATER = ClassName.get("com.fangdd.mobile", "PermissionUmbrella");
    public static final String INTERFACE_ALLOCATER_METHOD_NAME = "invokePermission";
    public static final String ALLOCATER_METHOD_NAME_PREFIX = "allocater_";
    public static final String ALLOCATER_DENY_METHOD_NAME_PREFIX = "allocaterDeny_";

    public static final ClassName BITMAP_FACTORY = ClassName.get("android.graphics", "BitmapFactory");
    public static final ClassName CONTEXT_COMPAT =
            ClassName.get("android.support.v4.content", "ContextCompat");

    public static final String GENERATED_CLASS_SUFFIX = "_PermissionAllocater";

    private final TypeName targetTypeName;
    private final ClassName binderClassName;
    private final String className;
    private final boolean isFinal;
    private final boolean isFragment;
    private final boolean isActivity;
    private final boolean isDialog;

    private BinderSet parentBinding;
    private Set<MethodBinder> permissionAlloctionMethods;
    private Set<MethodBinder> permissionAlloctionDenyMethods;

    private BinderSet(TypeName typeName, String className, ClassName binderClassName, boolean isFinal,
                      boolean isActivity, boolean isDialog, boolean isFragment, BinderSet parentBinding,
                      Set<MethodBinder> permissionAlloctionMethods, Set<MethodBinder> permissionAlloctionDenyMethods) {
        this.targetTypeName = typeName;
        this.binderClassName = binderClassName;
        this.className = className;
        this.isFinal = isFinal;
        this.isActivity = isActivity;
        this.isFragment = isFragment;
        this.isDialog = isDialog;
        this.parentBinding = parentBinding;
        this.permissionAlloctionMethods = permissionAlloctionMethods;
        this.permissionAlloctionDenyMethods = permissionAlloctionDenyMethods;
    }

    public JavaFile generateJava(int sdk) {
        return JavaFile.builder(binderClassName.packageName(), generateType(sdk))
                .addFileComment("Generated code from PermissionAllocation. Do not modify!")
                .build();
    }

    public TypeSpec generateType(int sdk) {

        TypeSpec.Builder result = TypeSpec.classBuilder(binderClassName.simpleName())
                .addModifiers(PUBLIC);

        if (isFinal) {
            result.addModifiers(FINAL);
        }

        if (parentBinding != null) {
            result.superclass(parentBinding.binderClassName);
        } else {
            result.addSuperinterface(PERMISSION_ALLOCATER);
        }

        if (isActivity || isFragment) {
            result.addField(targetTypeName, "target", PRIVATE);
        }

        if (isActivity || isFragment) {
            result.addMethod(createBinderConstructorForActivity());
        }

        result.addMethod(initAllocaterMethod());

        for (MethodBinder binder : permissionAlloctionMethods) {
            result.addMethod(createPermissionAllocaterMethod(binder));
        }

        for (MethodBinder binder : permissionAlloctionDenyMethods) {
            result.addMethod(createPermissionAllocterDenyMethod(binder));
        }
        return result.build();
    }

    private MethodSpec createBinderConstructorForActivity() {
        MethodSpec.Builder constructor = MethodSpec.constructorBuilder()
                .addAnnotation(UI_THREAD)
                .addModifiers(PUBLIC)
                .addParameter(targetTypeName, "target");


        if (parentBinding != null) {
            constructor.addStatement("super(target)");
        }

        constructor.addStatement("this.target = target");

        return constructor.build();
    }

    private MethodSpec initAllocaterMethod() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(INTERFACE_ALLOCATER_METHOD_NAME)
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .addParameter(TypeName.INT, METHOD_PARAMETERS_NAME);

        builder.addCode("switch(" + METHOD_PARAMETERS_NAME + ") " +
                "{\n" + buildMethodCase() + "\n}\n" +
                buildSuperInvokePermission());

        return builder.build();
    }

    private MethodSpec createPermissionAllocaterMethod(MethodBinder methodBinder) {
        MethodSpec.Builder method = MethodSpec.methodBuilder(ALLOCATER_METHOD_NAME_PREFIX + methodBinder.getMethodFlag())
                .addModifiers(PUBLIC);

        method.addStatement("$T.Builder builder = new $T.Builder()", ACP_OPTIONS, ACP_OPTIONS);
        method.addStatement(buildPermission(methodBinder.getPermissions()));

        method.addStatement("builder.setPermissions(permissions)");
        method.addStatement(buildACPCallBack(methodBinder), ACP, ACP_LISTENER, LIST);

        return method.build();
    }

    private MethodSpec createPermissionAllocterDenyMethod(MethodBinder methodBinder) {
        MethodSpec.Builder method = MethodSpec.methodBuilder(ALLOCATER_DENY_METHOD_NAME_PREFIX + methodBinder.getMethodFlag())
                .addModifiers(PUBLIC);

        for (Parameter parameter : methodBinder.getParameters()) {
            method.addParameter(parameter.getType(), parameter.getName());
        }

        method.addStatement(buildParameterMethod(methodBinder));

        return method.build();
    }

    private String buildPermission(List<String> permissions) {
        StringBuilder sb = new StringBuilder("String[] permissions = new String[] {\n");
        for (String permission : permissions) {
            if (permission.startsWith(SIMPLE_PERMISSION_PREFIX)) {
                sb.append(permission.replace(SIMPLE_PERMISSION_PREFIX, QUAKIFIED_PERMISSION_PREFIX) + ",\n");
            } else {
                sb.append(permission + ",\n");
            }
        }
        sb.append("}");

        return sb.toString();
    }

    private String buildParameter(MethodBinder binder) {
        StringBuilder sb = new StringBuilder();
        for (Parameter parameter : binder.getParameters()) {
            sb.append(parameter.getName() + ", ");
        }
        sb = sb.deleteCharAt(sb.length() - 1);
        sb = sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    private String buildParameterMethod(MethodBinder binder) {
        StringBuilder sb = new StringBuilder("target." + binder.getMethodName() + "(");
        sb.append(buildParameter(binder));
        sb.append(")");
        return sb.toString();
    }

    private String buildACPCallBack(MethodBinder methodBinder) {
        StringBuilder sb = new StringBuilder(
                "$T.getInstance(target).request(builder.build(),\n" +
                        "                new $T() {\n" +
                        "                    @Override\n" +
                        "                    public void onGranted() {\n" +
                        "                        target." + methodBinder.getMethodName() + "();\n" +
                        "                    }\n" +
                        "\n" +
                        "                    @Override\n" +
                        "                    public void onDenied($T<String> permissionsAllocated) {\n");
        sb.append(buildDenyMethod(getPermissionDenyMethod(methodBinder)));
        sb.append("                    }\n" +
                "                })");

        return isFragment ? (sb.toString().replace("(target)", "(target.getActivity())")) : sb.toString();
    }

    private MethodBinder getPermissionDenyMethod(MethodBinder methodBinder) {
       return getPermissionDenyMethod(methodBinder.getMethodFlag());
    }

    private MethodBinder getPermissionDenyMethod(int methodFlag) {
        for (MethodBinder binder : permissionAlloctionDenyMethods) {
            if (binder.getMethodFlag() == methodFlag)
                return binder;
        }
        return null;
    }

    private String buildDenyMethod(MethodBinder denyMethodBinder) {
        if (denyMethodBinder != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("                         " + ALLOCATER_DENY_METHOD_NAME_PREFIX + denyMethodBinder.getMethodFlag()
                    + "(" + buildParameter(denyMethodBinder) + ");\n");
            return sb.toString();
        }
        return "\n";
    }

    private String buildMethodCase() {
        StringBuilder sb = new StringBuilder("");

        for (MethodBinder binder : permissionAlloctionMethods) {
            sb.append("    case " + binder.getMethodFlag() + ":\n");
            sb.append("        " + ALLOCATER_METHOD_NAME_PREFIX + binder.getMethodFlag() + "();\n");
            sb.append("        return;\n");
        }

        return sb.toString();
    }

    private String buildSuperInvokePermission() {
        StringBuilder sb = new StringBuilder("");

        if (parentBinding != null) {
            sb.append("super." + INTERFACE_ALLOCATER_METHOD_NAME + "(" + METHOD_PARAMETERS_NAME + ");\n");
        }

        return sb.toString();
    }

    public static Builder newBuilder(TypeElement enclosingElement) {
        TypeMirror typeMirror = enclosingElement.asType();

        boolean isFragment = checkSubOfType(typeMirror, FRAGMENT_TYPE);
        boolean isActivity = checkSubOfType(typeMirror, ACTIVITY_TYPE);
        boolean isDialog = checkSubOfType(typeMirror, DIALOG_TYPE);

        TypeName targetType = TypeName.get(typeMirror);
        if (targetType instanceof ParameterizedTypeName) {
            targetType = ((ParameterizedTypeName) targetType).rawType;
        }

        String packageName = getPackage(enclosingElement).getQualifiedName().toString();
        String className = enclosingElement.getQualifiedName().toString().substring(
                packageName.length() + 1).replace('.', '$');

        ClassName binderClassName = ClassName.get(packageName, className + GENERATED_CLASS_SUFFIX);
        boolean isFinal = enclosingElement.getModifiers().contains(Modifier.FINAL);
        return new Builder(targetType, className, binderClassName, isFinal, isFragment, isActivity, isDialog);
    }

    public static final class Builder {
        private final TypeName targetTypeName;
        private final ClassName binderClassName;
        private final String className;
        private final boolean isFinal;
        private final boolean isFragment;
        private final boolean isActivity;
        private final boolean isDialog;

        private Set<MethodBinder> permissionAlloctionMethods;
        private Set<MethodBinder> permissionAlloctionDenyMethods;
        private BinderSet parentBinding;

        private Builder(TypeName targetTypeName, String className, ClassName binderClassName, boolean isFinal,
                        boolean isFragment, boolean isActivity, boolean isDialog) {
            this.targetTypeName = targetTypeName;
            this.binderClassName = binderClassName;
            this.className = className;
            this.isFinal = isFinal;
            this.isActivity = isActivity;
            this.isFragment = isFragment;
            this.isDialog = isDialog;
            this.permissionAlloctionMethods = new LinkedHashSet<>();
            this.permissionAlloctionDenyMethods = new LinkedHashSet<>();
        }

        public Builder setParent(BinderSet parent) {
            this.parentBinding = parent;
            return this;
        }

        public boolean addPermissionMethod(MethodBinder methodBinder) {
            return permissionAlloctionMethods.add(methodBinder);
        }

        public boolean addPermissionDenyMethod(MethodBinder methodBinder) {
            return permissionAlloctionDenyMethods.add(methodBinder);
        }

        public BinderSet build() {
            return new BinderSet(targetTypeName, className, binderClassName, isFinal, isActivity, isDialog, isFragment,
                    parentBinding, permissionAlloctionMethods, permissionAlloctionDenyMethods);
        }
    }

    public static String getViewType() {
        return VIEW_TYPE;
    }

    public TypeName getTargetTypeName() {
        return targetTypeName;
    }

    public ClassName getBinderClassName() {
        return binderClassName;
    }

    public boolean isFragment() {
        return isFragment;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public boolean isActivity() {
        return isActivity;
    }

    public boolean isDialog() {
        return isDialog;
    }

    public BinderSet getParentBinding() {
        return parentBinding;
    }

    public Set<MethodBinder> getPermissionAlloctionMethods() {
        return permissionAlloctionMethods;
    }

    public Set<MethodBinder> getPermissionAlloctionDenyMethods() {
        return permissionAlloctionDenyMethods;
    }

    public String getClassName() {
        return className;
    }
}
