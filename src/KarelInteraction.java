import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class KarelInteraction {

    private KarelMap map;
    private KarelRobot rob;
    private static final String QUIT_PROMPT = "You can use 'Q' to quit the game";
    private static final String GOODBYE = "See you next time.";

    public KarelInteraction(int width, int height) {
        map = new KarelMap(width, height);
        rob = new KarelRobot(map, new int[]{2, 0}, KarelRobot.Direction.RIGHT);
    }

    /**
     * draw a corresponding site
     */
    private void drawSite(int type) {
        String icon = "";
        KarelMap.Site siteType = KarelMap.Site.int2Site(type);
        KarelRobot.Direction currentDir = KarelRobot.Direction.int2Dir(rob.ori);
        switch (siteType) {
            case GROUND:
                icon = "·";
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
     */
    private void drawRow(int row) {
        int first = row * map.width;
        int last = (row + 1) * map.width - 1;
        for (int i = first; i <= last; i++) {
            drawSite(map.getType(i));
        }
        System.out.print("\n");
    }

    /**
     * draw the whole map out, and under the map the quit prompt
     */
    public void drawMap() {
        for (int i = 0; i < map.height; i++) {
            drawRow(i);
        }
        System.out.println(QUIT_PROMPT);
    }

    /**
     * an auxiliary centering method to format the output
     */
    private static String center(String str, int len) {
        int leftSpace = (len - str.length()) / 2;
        int rightSpace = len - str.length() - leftSpace;
        return " ".repeat(leftSpace) + str + " ".repeat(rightSpace);
    }

    /**
     * draw the opening page
     */
    private static void drawOp() {
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

    /**
     * waiting for prompt for the robot
     */
    private void gameLoop() {
        Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);

        while (true) {
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
    }

    /**
     * map the input to the robot method with the same name
     */
    private void invokeRobotMethod(String methodName) {
        try {
            Method method = KarelRobot.class.getDeclaredMethod(methodName.replace("()", ""));
            method.invoke(rob);
        } catch (NoSuchMethodException e) {
            System.out.println("Error: The robot cannot '" + methodName + "'");
        } catch (Exception e) {
            Throwable cause = e.getCause();
            System.out.println("Error: " + cause.getMessage());
        }
    }

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8)) {
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
                            game = new KarelInteraction(5, 5);
                            break;
                        case "STAGE2":
                            game = new KarelInteraction(5, 5);
                            break;
                        case "STAGE3":
                            game = new KarelInteraction(5, 5);
                            break;
                        case "NEW MAP":
                            game = new KarelInteraction(5, 5);
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

            if (game == null) {
                return;
            } else {
                game.gameLoop();
            }
        }
    }
}
