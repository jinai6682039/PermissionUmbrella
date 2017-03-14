package com.fangdd.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by hanxu on 2017/3/4.
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface PermissionAllocation {

    /**
     * permissions want to be Allocation
     */
    String[] permissions();

    int methodFlag();
}
