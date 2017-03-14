package fdd.permission.exception;

import java.lang.annotation.Annotation;

/**
 * Created by hanxu on 2017/3/14.
 */

public class UnSupportNullParameterException extends RuntimeException {
    public UnSupportNullParameterException(String methodName, String enclosingElementName, Class<? extends Annotation> clazz) {
        super("The method '" + methodName + "' annotated with @" + clazz.getName()
                + "in " + enclosingElementName + " must hava at least one parameter.");
    }
}
