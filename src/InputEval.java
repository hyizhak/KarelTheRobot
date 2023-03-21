public class InputEval {

    private String methodName, argString, argType, argValue;

    public InputEval(String input) {
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

    public String getMethod() {
        return methodName;
    }

    public String getArgString() {
        return argString;
    }

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

    public Object getArgValue() {
        return parseArgValue(argType, argValue);
    }

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
        String test = "move(int 10)";
        InputEval evaledInput = new InputEval(test);
        System.out.println(evaledInput.getMethod());
        System.out.println(evaledInput.argString);
        System.out.println(evaledInput.getArgValue());
    }
}
