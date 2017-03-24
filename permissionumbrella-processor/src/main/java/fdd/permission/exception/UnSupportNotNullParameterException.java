package fdd.permission.exception;

import java.lang.annotation.Annotation;

import javax.lang.model.element.Element;

/**
 * Created by hanxu on 2017/3/10.
 */

public class UnSupportNotNullParameterException extends RuntimeException {
    public UnSupportNotNullParameterException(String methodName, String enclosingElementName, Class<? extends Annotation> clazz) {
        super("The method '" + methodName + "' annotated with @" + clazz.getName()
                + "in " + enclosingElementName + " must not hava any parameters.");
    }
}
