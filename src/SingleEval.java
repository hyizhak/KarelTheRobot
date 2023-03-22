public class SingleEval {

    private String methodName, argString, argType, argValue;

    /**
     * parse the input string to get the method name, argument type and value
     *
     * @param input the input string
     */
    public SingleEval(String input) {
        String[] parts = input.split("\\(");
        methodName = parts[0];
        //parts.lenth > 1 means there are arguments and
        //parts[1].length() > 1 means the argument is not the ")" at the end
        if (parts.length > 1 && parts[1].length() > 1) {
            argString = parts[1].replace(")", "");
            String[] argParts = argString.split(" ");
            argType = argParts[0];
            argValue = argParts[1];
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

    /**
     * get the argument string
     *
     * @return argument string
     */
    public String getArgString() {
        return argString;
    }

    /**
     * get the class of the argument
     *
     * @return primitive class and other defined class from the name
     */
    public Class<?> getArgType() {
        Class<?> argClass = null;
        switch (argType) {
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
                    argClass = Class.forName(argType);
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
    private Object parseArgValue(String argType, String argValue) {
        switch (argType) {
            case "int":
                return Integer.parseInt(argValue);
            case "double":
                return Double.parseDouble(argValue);
            case "boolean":
                return Boolean.parseBoolean(argValue);
            case "String":
                return argValue;
            default:
                return null;
        }
    }

    public static void main(String[] args) throws ClassNotFoundException {
        String test = "move(KarelMap 10)";
        SingleEval evaledInput = new SingleEval(test);
        System.out.println(evaledInput.getMethod());
        System.out.println(evaledInput.getArgType());
        System.out.println(evaledInput.getArgValue());
    }
}
