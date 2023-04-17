import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class KarelInteraction {

    private KarelRobot rob;
    private static final String QUIT_PROMPT = "You can use 'Q' to quit the game";
    private static final String GOODBYE = "See you next time.";
    public static Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);

    /**
     * construct the game
     *
     * @param stage the user's choice of stage
     */
    public KarelInteraction(int stage) {
        WorldDirector director = new WorldDirector();
        WorldBuilder builder = new WorldBuilder();
        switch (stage) {
            case 1:
                director.stage1(builder);
                break;
            case 2:
                director.stage2(builder);
                break;
            case 3:
                director.stage3(builder);
                break;
        }
        rob = builder.buildRobot();
    }

    /**
     * draw a corresponding site
     *
     * @param type the type of the site to be drawn
     */
    private void drawSite(int type) {
        String icon = "";
        KarelMap.Site siteType = KarelMap.Site.intToSite(type);
        KarelRobot.Direction currentDir = KarelRobot.Direction.intToDir(rob.ori);
        switch (siteType) {
            case GROUND:
                icon = "·";
                break;
            case WALL:
                icon = "■";
                break;
            case ROCK:
                icon = "●";
                break;
            case TRAP:
                icon = "⊙";
                break;
            case TRAPLEVELED:
                icon = "×";
                break;
            case KAREL:
                switch (currentDir) {
                    case RIGHT:
                        icon = "►";
                        break;
                    case UP:
                        icon = "▲";
                        break;
                    case LEFT:
                        icon = "◄";
                        break;
                    case DOWN:
                        icon = "▼";
                        break;
                }
                break;

        }
        System.out.print("  " + icon + "  ");
    }

    /**
     * draw a specified line of the map
     *
     * @param row the row number of the map to be drawn
     */
    private void drawRow(int row) {
        for (int i = 0; i < rob.map.width; i++) {
            int[] site = new int[]{row, i};
            drawSite(rob.map.getType(site));
        }
        System.out.print("\n");
    }

    /**
     * draw the whole map out, and under the map the quit prompt
     */
    public void drawMap() {
        for (int i = 0; i < rob.map.height; i++) {
            drawRow(i);
        }
        System.out.println(QUIT_PROMPT);
    }

    /**
     * an auxiliary centering method to format the output
     *
     * @param str the string to be centered
     * @param len the length of the string after centering
     */
    private static String center(String str, int len) {
        int leftSpace = (len - str.length()) / 2;
        int rightSpace = len - str.length() - leftSpace;
        return " ".repeat(leftSpace) + str + " ".repeat(rightSpace);
    }

    /**
     * draw the opening page
     */
    public static void drawOp() {
        String outerBox = "—".repeat(80);
        String info = "|" + center("Created by: Han, Yizhan", 78) + "|";
        String welcome = "|" + center("Welcome to Karel's World!", 78) + "|";
        String divLine = "|" + center("-".repeat(44), 78) + "|";
        String choosePrompt = "|" + center("Please choose a stage or create a new map:", 78) + "|";
        String divSpace = "|" + " ".repeat(78) + "|";
        String stageChoose = "|" + center("STAGE1       STAGE2      STAGE3      NEW MAP", 78) + "|";
        System.out.print(outerBox + "\n" + info + "\n" + welcome + "\n" + divLine + "\n" + choosePrompt + "\n"
                + divSpace + "\n" + stageChoose + "\n" + divSpace + "\n" + outerBox + "\n" + QUIT_PROMPT + "\n");
    }

    public static KarelInteraction opening() {
        KarelInteraction game = null;
        while (true) {
            drawOp();
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("Q")) {
                System.out.println(GOODBYE);
                break;
            }

            try {
                switch (input) {
                    case "STAGE1":
                        game = new KarelInteraction(1);
                        break;
                    case "STAGE2":
                        game = new KarelInteraction(2);
                        break;
                    case "STAGE3":
                        game = new KarelInteraction(3);
                        break;
                    case "NEW MAP":
                        System.out.println("Not implemented yet. Choose another stage please.");
                        break;
                    default:
                        System.out.println("Choose a stage or create a new map bitte");
                        break;
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
            if (game != null) {
                break;
            }
        }
        return game;
    }

    /**
     * waiting for prompt for the robot, the main game loop
     */
    public void gameLoop() {
        while (rob.map.numMapRock() != 0 && !rob.trapped) {
            drawMap();

            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("Q")) {
                System.out.println(GOODBYE);
                break;
            }

            try {
                invokeRobotMethod(input);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        if (rob.trapped) {
            drawMap();
            System.out.println("The robot is trapped. Game over...");
        }
        if (rob.map.numMapRock() == 0) System.out.println("There is no rock on the map. You win!");

    }

    /**
     * map the input to the robot method with the same name
     *
     * @param input the input string
     */
    private void invokeRobotMethod(String input) {
        SingleEval evaledInput = new SingleEval(input);

        try {
            if (!evaledInput.hasArg()) {
                Method method = KarelRobot.class.getDeclaredMethod(evaledInput.getMethod());
                method.invoke(rob);
            } else {
                Method method = KarelRobot.class.getDeclaredMethod(
                        evaledInput.getMethod(), evaledInput.getArgType());
                method.invoke(rob, evaledInput.getArgValue());
            }
        } catch (NoSuchMethodException e) {
            System.out.println("Error: Not supported the command '" + input + "'");
        } catch (Exception e) {
            Throwable cause = e.getCause();
            System.out.println("Error: " + cause.getMessage());
        }
    }

    public static void main(String[] args) {
        KarelInteraction game = opening();

        if (game == null) {
            return;
        } else {
            game.gameLoop();
        }
    }
}
