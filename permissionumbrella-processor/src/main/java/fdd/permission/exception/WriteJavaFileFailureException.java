package fdd.permission.exception;

/**
 * Created by hanxu on 2017/3/13.
 */

public class WriteJavaFileFailureException extends RuntimeException{
    public WriteJavaFileFailureException(String typeElementName, String e) {
        super("Unable to write binding for type '" + typeElementName + ": " + e);
    }
}
