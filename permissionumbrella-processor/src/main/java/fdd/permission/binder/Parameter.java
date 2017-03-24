package fdd.permission.binder;

import com.squareup.javapoet.TypeName;

/**
 * Created by hanxu on 2017/3/10.
 */

public final class Parameter {
    public static final Parameter[] NONE = new Parameter[0];

    private final int position;
    private final TypeName type;
    private final String name;

    public Parameter(int position, TypeName type, String name) {
        this.position = position;
        this.type = type;
        this.name = name;
    }

    int getPosition() {
        return position;
    }

    TypeName getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public boolean requiresCast(String toType) {
        return !type.toString().equals(toType);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Parameter))
            return false;
        Parameter other = (Parameter) obj;
        if (other.position == this.position && other.type.equals(this.type))
            return true;
        return false;
    }
}
