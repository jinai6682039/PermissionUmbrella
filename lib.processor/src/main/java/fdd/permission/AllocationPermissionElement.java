package fdd.permission;

import com.fangdd.annotation.PermissionAllocation;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeVariableName;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;

import fdd.permission.provider.ConstantsProvider;
import fdd.permission.tool.AllocationClassType;
import fdd.permission.tool.TypeResolver;
import fdd.permission.utils.CheckUtil;
import fdd.permission.utils.ProcesserUtil;

/**
 * 注解处理器权限分配实体
 * Created by hanxu on 2017/3/6.
 */

public class AllocationPermissionElement {

    private String packageName;
    private String className;
    // 注解器生成的类名
    private String generatedClassName;

    // 处理的类是Activity or Fragment
    private AllocationClassType classType;
    private TypeResolver typeResolver;
    private TypeName typeName;

    // 处理类的所有类型名
    private List<TypeVariableName> typeVariables;

    private List<ExecutableElement> permissionAllocationMethods;

    public AllocationPermissionElement(TypeElement typeElement, TypeResolver typeResolver) {
        this.typeResolver = typeResolver;
        this.typeName = TypeName.get(typeElement.asType());

        this.typeVariables = new ArrayList<>();
        // 拿到对应类的所有类型参数
        List<? extends TypeParameterElement> typeParameterElements = typeElement.getTypeParameters();
        for (TypeParameterElement element : typeParameterElements) {
            typeVariables.add(TypeVariableName.get(element));
        }

        String fullClassName = typeElement.getQualifiedName().toString();

        className = ProcesserUtil.getClassName(fullClassName);
        packageName = ProcesserUtil.getPackageName(fullClassName);
        classType = CheckUtil.checkClassType(typeElement, typeResolver);

        generatedClassName = typeElement.getSimpleName().toString() + ConstantsProvider.GENERATED_CLASS_SUFFIX;
        permissionAllocationMethods = ProcesserUtil.getMethods(typeElement, PermissionAllocation.class);

        checkMethodlegality(PermissionAllocation.class);
    }

    private void checkMethodlegality(Class clazz) {
//        CheckUtil.checkPrivateMethod(permissionAllocationMethods, clazz);
//        CheckUtil.checkMethodReturnType(permissionAllocationMethods, clazz);
//        CheckUtil.checkMethodThrownType(permissionAllocationMethods, clazz);
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getGeneratedClassName() {
        return generatedClassName;
    }

    public void setGeneratedClassName(String generatedClassName) {
        this.generatedClassName = generatedClassName;
    }

    public AllocationClassType getClassType() {
        return classType;
    }

    public void setClassType(AllocationClassType classType) {
        this.classType = classType;
    }

    public TypeResolver getTypeResolver() {
        return typeResolver;
    }

    public void setTypeResolver(TypeResolver typeResolver) {
        this.typeResolver = typeResolver;
    }

    public TypeName getTypeName() {
        return typeName;
    }

    public void setTypeName(TypeName typeName) {
        this.typeName = typeName;
    }

    public List<TypeVariableName> getTypeVariables() {
        return typeVariables;
    }

    public void setTypeVariables(List<TypeVariableName> typeVariables) {
        this.typeVariables = typeVariables;
    }

    public List<ExecutableElement> getPermissionAllocationMethods() {
        return permissionAllocationMethods;
    }

    public void setPermissionAllocationMethods(List<ExecutableElement> permissionAllocationMethods) {
        this.permissionAllocationMethods = permissionAllocationMethods;
    }

}
