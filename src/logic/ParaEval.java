package logic;

import java.util.ArrayList;
import java.util.List;

public class ParaEval {
    private final String code;
    private final List<String> segments = new ArrayList<>();
    public int commandsCount;

    public ParaEval(String code) {
        this.code = code.replaceAll("\n", "").replaceAll(" ", "").trim();
    }

    public List<String> split() {
        int bracketCount = 0;
        int lastIndex = 0;
        for (int i = 0; i < code.length(); i++) {
            char c = code.charAt(i);
            if (c == '{') {
                bracketCount++;
            } else if (c == '}') {
                bracketCount--;
                if (bracketCount == 0) {
                    segments.add(code.substring(lastIndex, i + 1));
                    lastIndex = i + 1;
                } else if (bracketCount < 0) {
                    throw new RuntimeException("Syntax error: " + code.substring(lastIndex, i + 1));
                }
            } else if (c == ';' && bracketCount == 0) {
                // Trim the ";" at the end of a simple line to compatibilize with the SingleEval
                segments.add(code.substring(lastIndex, i));
                lastIndex = i + 1;
            }
        }
        if (lastIndex == 0) {
            throw new RuntimeException("Syntax error: " + code);
        }
        commandsCount = segments.size();
        return segments;
    }

    private void invokeSingleRobotMethod(String input, KarelRobot rob) {
        CompoundEval evaledInput = new CompoundEval(input);
        if (evaledInput.isSingle) {
            evaledInput.single.invoke(rob);
        } else if (evaledInput.prefix.equals("if")) {
            evaledInput.ifInvoke(rob);
        } else {
            evaledInput.funcInvoke(rob);
        }
    }

    public int invoke(KarelRobot rob) {
        for (String segment : segments) {
            invokeSingleRobotMethod(segment, rob);
            if (rob.trapped) {
                System.out.println("The robot is trapped. Game over...");
                return -1;
            } else if (rob.map.numMapRock() == 0) {
                System.out.println("There is no rock on the map. You win!");
                return 1;
            }
        }
        return 0;
    }

    public static void main(String[] args) {
        ParaEval eval = new ParaEval("turnLeft();\n" +
                "move();\n" +
                "if (noRockPresent()) {\n" +
                "move();\n" +
                "}\n" +
                "turnRight() {\n" +
                "turnLeft();\n" +
                "turnLeft();\n" +
                "turnLeft();\n" +
                "}\n" +
                "turnRight();\n");
        System.out.println(eval.split().get(2));
    }
}
