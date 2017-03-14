package fdd.permission.exception;

import java.lang.annotation.Annotation;

import javax.lang.model.element.ExecutableElement;

/**
 * Created by hanxu on 2017/3/6.
 */

public class UnSupportThrowTypeException extends RuntimeException {
    public UnSupportThrowTypeException(ExecutableElement element, Class<? extends Annotation> clazz) {
        super("This SDK dont support method '" + element.getSimpleName().toString() + "' annotated with @"
                + clazz.getSimpleName() + "with Throw exception");
    }
}
