package logic;

import java.lang.reflect.Method;
import java.util.NoSuchElementException;

public class SingleEval {

    private String methodName, argValue, input;
    private Class<?> argType;
    public Object result;

    /**
     * parse the input string to get the method name, argument type and value
     *
     * @param input the input string
     */
    public SingleEval(String input) {
        this.input = input;
        String[] parts = input.split("\\(");
        methodName = parts[0];
        //parts.lenth > 1 means there are arguments and
        //parts[1].length() > 1 means the argument is not the ")" at the end
        if (parts.length > 1 && parts[1].length() > 1) {
            argValue = parts[1].replace(")", "");
//            String[] argParts = argString.split(" ");
//            argType = argParts[0];
//            argValue = argParts[1];
        }
    }

    /**
     * get the method name
     *
     * @return method name
     */
    public String getMethod() {
        return methodName;
    }

    public boolean hasArg() {
        return argValue != null;
    }

    /**
     * get the class of the argument from the input
     *
     * @return primitive classes
     */
    public Class<?> getArgType() {
        if (argValue.matches("\\d+")) {
            argType = int.class;
        } else if (argValue.matches("\\d+\\.\\d+")) {
            argType = double.class;
        } else if (argValue.equalsIgnoreCase("true") || argValue.equalsIgnoreCase("false")) {
            argType = boolean.class;
        } else {
            argType = String.class;
        }
        return argType;
    }

    /**
     * get the class of the argument from the name
     *
     * @return primitive class and other defined class from the name
     */
    public Class<?> getArgType(String type) {
        Class<?> argClass = null;
        switch (type) {
            case "int":
                return int.class;
            case "double":
                return double.class;
            case "boolean":
                return boolean.class;
            case "String":
                return String.class;
            default:
                try {
                    argClass = Class.forName(type);
                } catch (ClassNotFoundException e) {
                    System.out.println("Error: Invalid arg class");
                }
                return argClass;
        }
    }

    /**
     * get the value of the argument
     *
     * @return the parsed value of the argument
     */
    public Object getArgValue() {
        return parseArgValue(argType, argValue);
    }

    /**
     * parse the argument value to the corresponding type
     *
     * @param argType  the String of the argument type
     * @param argValue the value of the argument
     * @return the parsed value of the argument
     */
    private Object parseArgValue(Class<?> argType, String argValue) {
        if (argType == int.class) {
            return Integer.parseInt(argValue);
        } else if (argType == double.class) {
            return Double.parseDouble(argValue);
        } else if (argType == boolean.class) {
            return Boolean.parseBoolean(argValue);
        } else if (argType == String.class) {
            return argValue;
        }
        return null;
    }

    public void invoke(KarelRobot rob) {
        if (getMethod().equals(rob.customFuncName)) {
            rob.customFunc();
        } else {
            try {
                if (!hasArg()) {
                    Method method = KarelRobot.class.getDeclaredMethod(getMethod());
                    result = method.invoke(rob);
                } else {
                    Method method = KarelRobot.class.getDeclaredMethod(
                            getMethod(), getArgType());
                    result = method.invoke(rob, getArgValue());
                }
            } catch (NoSuchMethodException e) {
                throw new NoSuchElementException("Error: Not supported the command '" + input + "'");
            } catch (Exception e) {
                Throwable cause = e.getCause();
                throw new IllegalArgumentException("Error: " + cause.getMessage());
            }
        }
    }

    public static void main(String[] args) throws ClassNotFoundException {
        String test = "move(10)";
        SingleEval evaledInput = new SingleEval(test);
        System.out.println(evaledInput.getMethod());
        System.out.println(evaledInput.getArgType());
        System.out.println(evaledInput.getArgValue());
    }
}
