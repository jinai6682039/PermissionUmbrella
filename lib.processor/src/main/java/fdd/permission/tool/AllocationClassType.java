package fdd.permission.tool;

import fdd.permission.provider.ConstantsProvider;

/**
 * Created by hanxu on 2017/3/6.
 */

public enum  AllocationClassType {

    ACTIVITY("target"),
    FRAGMENT("target.getActivity()");

    private final String activity;

    AllocationClassType(String activity) {
        this.activity = activity;
    }

    public static AllocationClassType getAllocationClassType(String className, TypeResolver typeResolver) {
        if (typeResolver.isSubTypeOf(className, ConstantsProvider.ACTIVITY)) {
            return ACTIVITY;
        } else if (typeResolver.isSubTypeOf(className, ConstantsProvider.FRAGMENT)){
            return FRAGMENT;
        }
        return null;
    }
}
