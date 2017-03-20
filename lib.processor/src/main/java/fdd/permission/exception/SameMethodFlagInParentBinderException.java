package fdd.permission.exception;

import java.lang.annotation.Annotation;

import javax.lang.model.element.ExecutableElement;

import fdd.permission.binder.BinderSet;

/**
 * Created by hanxu on 2017/3/20.
 */

public class SameMethodFlagInParentBinderException extends RuntimeException {
    public SameMethodFlagInParentBinderException(BinderSet binderSet) {
        super("The BinderSet '" + binderSet.getClassName() + "' has same method flag in the super class, please fix it!" );
    }
}
