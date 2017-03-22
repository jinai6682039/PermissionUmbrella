package fdd.permission.exception;

import java.lang.annotation.Annotation;

/**
 * Created by hanxu on 2017/3/14.
 */

public class UnSupportPermissionDenyParameterException extends RuntimeException {
    public UnSupportPermissionDenyParameterException(String methodName, String enclosingElementName, Class<? extends Annotation> clazz) {
        super("The method '" + methodName + "' annotated with @" + clazz.getName()
                + "in " + enclosingElementName + " must hava one and only one List<String> parameter.");
    }
}
