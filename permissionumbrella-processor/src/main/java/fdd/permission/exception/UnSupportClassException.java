package fdd.permission.exception;

import javax.lang.model.element.TypeElement;

/**
 * Created by hanxu on 2017/3/6.
 */

public class UnSupportClassException extends RuntimeException {
    public UnSupportClassException(TypeElement typeElement) {
        super("This SDK dont support to allocation permission on class '" + typeElement.getQualifiedName().toString() + "'");
    }
}
