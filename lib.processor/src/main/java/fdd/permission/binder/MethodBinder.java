package fdd.permission.binder;

import java.util.Collections;
import java.util.List;

/**
 * Created by hanxu on 2017/3/10.
 */

public class MethodBinder implements BaseBinder {

    private final String methodName;
    private final List<Parameter> parameters;
    private final int methodFlag;
    private final List<String> permissions;

    public MethodBinder(String methodName, List<Parameter> parameters, int methodFlag, List<String> permissions) {
        this.methodName = methodName;
        this.parameters = parameters;
        this.methodFlag = methodFlag;
        this.permissions = permissions;
    }

    @Override
    public String binderDescription() {
        return "method '" + methodName + "'";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MethodBinder))
            return false;
        MethodBinder other = (MethodBinder) obj;

        if (other.methodFlag == this.methodFlag)
            return true;

        if (other.methodName.equals(this.methodName) && this.equalsParameters(other.parameters))
            return true;
        else
            return false;
    }

    @Override
    public int hashCode() {
        return this.getMethodFlag();
    }

    private boolean equalsParameters(List<Parameter> otherParameters) {
        if (this.parameters.size() != otherParameters.size())
            return false;

        for (int i = 0; i < this.parameters.size(); i++) {
            if (!(this.parameters.get(i).equals(otherParameters.get(i)))) {
                return false;
            }
        }
        return true;
    }

    public String getMethodName() {
        return methodName;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public int getMethodFlag() {
        return methodFlag;
    }

    public List<String> getPermissions() {
        return permissions;
    }
}
