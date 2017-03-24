package fdd.permission.utils;

import com.squareup.javapoet.ParameterizedTypeName;

import java.lang.annotation.Annotation;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import fdd.permission.exception.EnclosingElementClassUnSupportException;
import fdd.permission.exception.EnclosingElementNotClassException;
import fdd.permission.exception.EnclosingElementPrivateException;
import fdd.permission.exception.PrivateMethodException;
import fdd.permission.exception.UnSupportClassException;
import fdd.permission.exception.UnSupportNotNullParameterException;
import fdd.permission.exception.UnSupportNullParameterException;
import fdd.permission.exception.UnSupportPermissionDenyParameterException;
import fdd.permission.exception.UnSupportReturnTypeException;
import fdd.permission.exception.UnSupportThrowTypeException;
import fdd.permission.tool.AllocationClassType;
import fdd.permission.tool.TypeResolver;

/**
 * Created by hanxu on 2017/3/6.
 */

public class CheckUtil {

    public static AllocationClassType checkClassType(TypeElement typeElement, TypeResolver typeResolver) {
        AllocationClassType classType = AllocationClassType.getAllocationClassType(typeElement.getQualifiedName().toString(),
                typeResolver);

        if (classType == null) {
            throw new UnSupportClassException(typeElement);
        }
        return classType;
    }

    /**
     * 被注解修饰的方法不能是私有的，因为处理类似于ButterKnife,重新生成的java类无法使用原Activity or Fragment 的private方法。
     */
    public static void checkPrivateMethod(ExecutableElement element, Class<? extends Annotation> clazz) {
        if (element.getModifiers().contains(Modifier.PRIVATE)) {
            throw new PrivateMethodException(element, clazz);
        }
    }

    public static void checkMethodReturnType(ExecutableElement element, Class<? extends Annotation> clazz) {
        if (element.getReturnType().getKind() != TypeKind.VOID) {
            throw new UnSupportReturnTypeException(element, clazz);
        }
    }

    public static void checkMethodThrownType(ExecutableElement element, Class<? extends Annotation> clazz) {
        if (!element.getThrownTypes().isEmpty()) {
            throw new UnSupportThrowTypeException(element, clazz);
        }
    }

    public static void checkEnclosingElement(ExecutableElement element, Class<? extends Annotation> clazz) {
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
        if (enclosingElement.getKind() != ElementKind.CLASS) {
            throw new EnclosingElementNotClassException(element.getSimpleName().toString(), enclosingElement.getQualifiedName().toString(), clazz);
        }

        if (enclosingElement.getModifiers().contains(Modifier.PRIVATE)) {
            throw new EnclosingElementPrivateException(element.getSimpleName().toString(), enclosingElement.getQualifiedName().toString(), clazz);
        }

        if (enclosingElement.getQualifiedName().toString().startsWith("android.")) {
            throw new EnclosingElementClassUnSupportException(element.getSimpleName().toString(), enclosingElement.getQualifiedName().toString(), clazz);
        }

        if (enclosingElement.getQualifiedName().toString().startsWith("java.")) {
            throw new EnclosingElementClassUnSupportException(element.getSimpleName().toString(), enclosingElement.getQualifiedName().toString(), clazz);
        }
    }

    public static void checkMethodParameters(ExecutableElement element, Class<? extends Annotation> clazz) {
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
        List<? extends VariableElement> parameters = element.getParameters();
        if (parameters != null && parameters.size() > 0) {
            throw new UnSupportNotNullParameterException(element.getSimpleName().toString(), enclosingElement.getQualifiedName().toString(), clazz);
        }
    }

    public static void checkPermissionNotNullMethodParameters(ExecutableElement element, Class<? extends Annotation> clazz) {
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
        List<? extends VariableElement> parameters = element.getParameters();
        if (parameters == null && parameters.size() == 0) {
            throw new UnSupportNullParameterException(element.getSimpleName().toString(), enclosingElement.getQualifiedName().toString(), clazz);
        }
    }

    public static void checkPermissionDenyMethodParameters(ExecutableElement element, Class<? extends Annotation> clazz) {
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
        List<? extends VariableElement> parameters = element.getParameters();
        if (parameters == null && parameters.size() == 0) {
            throw new UnSupportNullParameterException(element.getSimpleName().toString(), enclosingElement.getQualifiedName().toString(), clazz);
        }

//        throw new RuntimeException(parameters.get(0).getSimpleName().toString());

        if (parameters.size() > 1 || !parameters.get(0).asType().toString().equals(ParameterizedTypeName.get(List.class, String.class).toString())) {
            throw new UnSupportPermissionDenyParameterException(element.getSimpleName().toString(), enclosingElement.getQualifiedName().toString(), clazz);
        }
    }

    public static void checkMethod(ExecutableElement element, Class<? extends Annotation> clazz) {
        checkPrivateMethod(element, clazz);
        checkMethodThrownType(element, clazz);
        checkMethodReturnType(element, clazz);
        checkEnclosingElement(element, clazz);
        checkMethodParameters(element, clazz);
    }

    public static void checkMethodHaveParameters(ExecutableElement element, Class<? extends Annotation> clazz) {
        checkPrivateMethod(element, clazz);
        checkMethodThrownType(element, clazz);
        checkMethodReturnType(element, clazz);
        checkEnclosingElement(element, clazz);
        checkPermissionDenyMethodParameters(element, clazz);
    }

    public static boolean checkSubOfType(TypeMirror typeMirror, String otherType) {
        if (isTypeEqual(typeMirror, otherType)) {
            return true;
        }
        if (typeMirror.getKind() != TypeKind.DECLARED) {
            return false;
        }
        DeclaredType declaredType = (DeclaredType) typeMirror;
        List<? extends TypeMirror> typeMirrors = declaredType.getTypeArguments();
        if (typeMirrors != null && typeMirrors.size() > 0) {
            StringBuffer typeString = new StringBuffer(declaredType.asElement().toString());
            typeString.append("<");
            for (int i = 0; i < typeMirrors.size(); i++) {
                if (i > 0) {
                    typeString.append(",");
                }
                typeString.append("?");
            }
            typeString.append(">");
            if (typeString.toString().equals(otherType)) {
                return true;
            }
        }
        Element element = declaredType.asElement();
        if (!(element instanceof TypeElement)) {
            return false;
        }
        TypeElement typeElement = (TypeElement) element;
        TypeMirror superType = typeElement.getSuperclass();
        if (checkSubOfType(superType, otherType)) {
            return true;
        }
        for (TypeMirror interfaceType : typeElement.getInterfaces()) {
            if (checkSubOfType(interfaceType, otherType)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isTypeEqual(TypeMirror typeMirror, String otherType) {
        return otherType.equals(typeMirror.toString());
    }

}
