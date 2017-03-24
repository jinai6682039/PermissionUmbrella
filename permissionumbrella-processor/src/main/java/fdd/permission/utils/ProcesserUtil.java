package fdd.permission.utils;

import com.fangdd.annotation.PermissionAllocation;
import com.fangdd.annotation.PermissionDeny;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

import static java.util.Arrays.asList;
import static java.util.Arrays.deepEquals;
import static java.util.Collections.emptyList;

/**
 * Created by hanxu on 2017/3/6.
 */

public class ProcesserUtil {

    // 拿到对应注解的值
    public static List<String> getAnnotationPermissions(ExecutableElement element, Class<? extends Annotation> clazz) {
        if (clazz.getName().equals(PermissionAllocation.class.getName())) {
            return asList(element.getAnnotation(PermissionAllocation.class).permissions());
        } else {
            return emptyList();
        }
    }

    public static int getAnnotationFlag(ExecutableElement element, Class<? extends Annotation> clazz) {

        if (clazz.getName().equals(PermissionAllocation.class.getName())) {
            return element.getAnnotation(PermissionAllocation.class).methodFlag();
        } else  if (clazz.getName().equals(PermissionDeny.class.getName())) {
            return element.getAnnotation(PermissionDeny.class).methodFlag();
        } else {
            return Integer.MAX_VALUE;
        }
    }

    // 拿到对应注解修饰的所有方法
    public static List<ExecutableElement> getMethods(Element element, Class<? extends Annotation> clazz) {
        List<ExecutableElement> methods = new ArrayList<>();
        for (Element encloseElement : element.getEnclosedElements()) {
            Annotation annotation = encloseElement.getAnnotation(clazz);
            if (annotation != null) {
                methods.add((ExecutableElement) encloseElement);
            }
        }
        return methods;
    }

    // 拿到对应注解修饰的申请了对应权限的方法
    public static ExecutableElement getPermissionAllocationMethode(String[] permissions, List<ExecutableElement> elements) {
        for (ExecutableElement executableElement : elements) {

            String[] annotationPermissions = executableElement.getAnnotation(PermissionAllocation.class).permissions();

            if (deepEquals(permissions, annotationPermissions)) {
                return executableElement;
            }
        }

        return null;
    }

    public static String getPackageName(String name) {
        return name.substring(0, name.lastIndexOf("."));
    }

    public static String getClassName(String name) {
        return name.substring(name.lastIndexOf(".") + 1);
    }


}
