package com.fangdd.mobile;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by hanxu on 2017/3/7.
 */

public class Umbrella {

    private static final String TAG = "ButterKnife";
    private static boolean debug = true;

    private Umbrella() {
        throw new AssertionError("No instances.");
    }

    @VisibleForTesting
    static final Map<Class<?>, Constructor<? extends PermissionUmbrella>> BINDINGS = new LinkedHashMap<>();

    @NonNull
    @UiThread
    public static PermissionUmbrella allocation(@NonNull Activity activity) {
        return createBinding(activity, activity);
    }

    public static void setDebug(boolean debug) {
        Umbrella.debug = debug;
    }

    private static PermissionUmbrella createBinding(@NonNull Object target, @NonNull Context source) {
        Class<?> targetClass = target.getClass();
        if (debug) {
            Log.e(TAG, "Looking up binding for " + targetClass.getName());
        }

        Constructor<? extends PermissionUmbrella> constructor = findBindingConstructorByClass(targetClass);

        if (constructor == null) {
            return PermissionUmbrella.EMPTY;
        }

        try {
            return constructor.newInstance(target);
        } catch (InstantiationException e) {
            throw new RuntimeException("Unable to invoke " + constructor, e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to invoke " + constructor, e);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            if (cause instanceof Error) {
                throw (Error) cause;
            }
            throw new RuntimeException("Unable to create binding instance.", cause);
        }

    }

    private static Constructor<? extends PermissionUmbrella> findBindingConstructorByClass(Class<?> clazz) {
        Constructor<? extends PermissionUmbrella> bindingCstor = BINDINGS.get(clazz);
        if (bindingCstor != null) {
            if (debug) {
                Log.e(TAG, "Cache Hit: find in binding map.");
            }
            return bindingCstor;
        }
        String clsName = clazz.getName();
        if (clsName.startsWith("android.") || clsName.startsWith("java.")) {
            if (debug) {
                Log.e(TAG, "Cache Miss; cant deal with framework class.");
            }
            return null;
        }

        try {
            if (debug) Log.e(TAG, clsName);
            Class<?> bindingClass = Class.forName(clsName + "_PermissionAllocater");
            bindingCstor = (Constructor<? extends PermissionUmbrella>) bindingClass.getConstructor(clazz);
            if (debug) Log.e(TAG, "Cache Hit: Loaded binding class and constructor.");
        } catch (ClassNotFoundException e) {
            if (debug)
                Log.e(TAG, "Not found. Trying superclass " + clazz.getSuperclass().getName());
            bindingCstor = findBindingConstructorByClass(clazz.getSuperclass());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Unable to find binding constructor for " + clsName, e);
        }
        BINDINGS.put(clazz, bindingCstor);
        return bindingCstor;
    }
}
