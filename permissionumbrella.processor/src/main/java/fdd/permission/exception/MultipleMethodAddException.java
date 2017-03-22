package fdd.permission.exception;

import java.lang.annotation.Annotation;

/**
 * Created by hanxu on 2017/3/10.
 */

public class MultipleMethodAddException extends RuntimeException {
    public MultipleMethodAddException(String methodName, String enclosingElementName, Class<? extends Annotation> clazz) {
        super("There are multiple method has the same methodflag '" + methodName + "' annotated with @" + clazz.getName()
                + " in '" + enclosingElementName + "'");
    }
}
