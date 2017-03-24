package fdd.permission.exception;

/**
 * Created by hanxu on 2017/3/9.
 */

public class EnclosingElementPrivateException extends RuntimeException {
    public EnclosingElementPrivateException(String methodName, String enclosingElementName, Class clazz) {
        super("The method '" + methodName + "' annotated with @" + clazz.getName()
                + " must not enclosed in a private class, but '" + enclosingElementName + "' is a private class");
    }
}
