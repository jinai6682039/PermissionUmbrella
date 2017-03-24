package fdd.permission.tool;

/**
 * Created by hanxu on 2017/3/6.
 */

public interface TypeResolver {
    boolean isSubTypeOf(String clazz, String superTypeClazz);
}
