package fdd.permission.exception;

import java.lang.annotation.Annotation;

import javax.lang.model.element.ExecutableElement;

/**
 * Created by hanxu on 2017/3/6.
 */

public class PrivateMethodException extends RuntimeException {
    public PrivateMethodException(ExecutableElement element, Class<? extends Annotation> clazz) {
        super("This SDK dont support private method '" +  element.getSimpleName().toString() + "' annotated with @"
                + clazz.getSimpleName() + ", the method must not be private");
    }
}
