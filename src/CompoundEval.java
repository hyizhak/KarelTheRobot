import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CompoundEval {

    public String prefix, condition, ifTrue, ifFalse;
    public String funcName;
    public String[] funcBody;
    public SingleEval single;
    public boolean isSingle;

    public CompoundEval(String input) {
        String[] parts = input.split("\\(", 2);
        prefix = parts[0];
        Pattern pattern = Pattern.compile("\\{([^}]+)\\}");
        Matcher matcher = pattern.matcher(input);
        if (!matcher.find()) {
            single = new SingleEval(input);
            isSingle = true;
        } else {
            matcher.reset();
            if (prefix.equals("if")) {
                condition = parts[1].split("\\)+", 2)[0];
                String[] matches = new String[2];
                int i = 0;
                while (matcher.find()) {
                    String s = matcher.group(1);
                    matches[i] = s;
                    i++;
                }
                ifTrue = matches[0];
                ifFalse = matches[1];
            } else {
                funcName = prefix;
                matcher.find();
                funcBody = matcher.group(1).split(";");
            }
        }
    }

    public void ifInvoke(KarelRobot rob) {
        SingleEval cond = new SingleEval(condition);
        SingleEval trueEval = new SingleEval(ifTrue);
        SingleEval falseEval = new SingleEval(ifFalse);
        cond.invoke(rob);
        if ((boolean) cond.result) {
            trueEval.invoke(rob);
        } else {
            falseEval.invoke(rob);
        }
    }

    public void funcInvoke(KarelRobot rob) {
        rob.customFuncName = funcName;
        rob.customFuncBody = funcBody;
        System.out.println("You have got " + funcName + "() function.");
    }

    public static void main(String[] args) {

    }
}
