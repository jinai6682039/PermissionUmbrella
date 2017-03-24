package fdd.permission.exception;

import java.lang.annotation.Annotation;

/**
 * Created by hanxu on 2017/3/9.
 */

public class EnclosingElementClassUnSupportException extends RuntimeException {
    public EnclosingElementClassUnSupportException(String methodName, String enclosingElementName, Class<? extends Annotation> clazz) {
        super("The method '" + methodName + "' annotated with @" + clazz.getName()
                + " must not enclosed in a framework class, but '" + enclosingElementName + "' is a framework class");
    }
}
