package fdd.permission.exception;

import java.lang.annotation.Annotation;

/**
 * Created by hanxu on 2017/3/9.
 */

public class EnclosingElementNotClassException extends RuntimeException {
    public EnclosingElementNotClassException(String methodName, String enclosingElementName, Class<? extends Annotation> clazz) {
        super("The method '" + methodName + "' annotated with @" + clazz.getName()
                + " must enclosed in a class, but '" + enclosingElementName + "' is not a class");
    }
}
