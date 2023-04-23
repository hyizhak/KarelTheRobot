public class WorldDirector {

    public void stage1(WorldBuilder builder) {
        builder.setWidth(6);
        builder.setHeight(3);
        int[][] rock = new int[][]{{1, 5}};
        builder.setRock(rock);
        builder.setRobotLoc(new int[]{1, 0});
        builder.setRobotDir(KarelRobot.Direction.RIGHT);
    }

    public void stage2(WorldBuilder builder) {
        builder.setWidth(6);
        builder.setHeight(3);
        int[][] rock = new int[][]{{1, 5}};
        int[][] wall = new int[][]{{0, 0}, {1, 0}, {2, 2}, {2, 3}, {2, 4}, {2, 5}};
        builder.setRock(rock);
        builder.setWall(wall);
        builder.setRobotLoc(new int[]{2, 0});
        builder.setRobotDir(KarelRobot.Direction.RIGHT);
    }

    public void stage3(WorldBuilder builder) {
        builder.setWidth(8);
        builder.setHeight(5);
        int[][] rock = new int[][]{{1, 2}, {0, 7}};
        int[][] wall = new int[][]{{0, 4}, {1, 4}, {3, 4}, {4, 4}, {3, 5},
                {4, 5}, {3, 6}, {4, 6}, {3, 7}, {4, 7}};
        int[][] trap = new int[][]{{2, 4}};
        builder.setRock(rock);
        builder.setWall(wall);
        builder.setTrap(trap);
        builder.setRobotLoc(new int[]{4, 0});
        builder.setRobotDir(KarelRobot.Direction.RIGHT);
    }

    public void newMap(WorldBuilder builder) {
        System.out.println("Welcome to the Karel Map Builder! Type ? for help and E to exit.");
        while (true) {
            String input = KarelInteraction.scanner.nextLine();

            if (input.equalsIgnoreCase("E")) {
                if (builder.validate()) {
                    System.out.println("New Map Set.");
                    break;
                } else {
                    System.out.println("Map not fully set yet. Please set all fields.");
                    continue;
                }
            }
            try {
                switch (input.toUpperCase()) {
                    case "?":
                        System.out.print("Commands:" + "\n" + "Width, Height, " + "\n" +
                                "Item = {Rock, Wall, Trap}, Robot" + "\n" +
                                "Compulsory: Width, Height, Rock, Robot" + "\n"
                        );
                        break;
                    case "WIDTH":
                        System.out.println("Please enter the width of the map: int");
                        int width = Integer.parseInt(KarelInteraction.scanner.nextLine());
                        builder.setWidth(width);
                        break;
                    case "HEIGHT":
                        System.out.println("Please enter the height of the map: int");
                        int height = Integer.parseInt(KarelInteraction.scanner.nextLine());
                        builder.setHeight(height);
                        break;
                    case "ROCK":
                        System.out.println("Please enter the location of the rock(s): row1 column1, row2 " +
                                "column2, ...");
                        int[][] rockLoc = parseLoc(KarelInteraction.scanner.nextLine());
                        builder.setRock(rockLoc);
                        break;
                    case "WALL":
                        System.out.println("Please enter the location of the wall(s): row1 column1, row2 " +
                                "column2, ...");
                        int[][] wallLoc = parseLoc(KarelInteraction.scanner.nextLine());
                        builder.setWall(wallLoc);
                        break;
                    case "TRAP":
                        System.out.println("Please enter the location of the trap(s): row1 column1, row2 " +
                                "column2, ...");
                        int[][] trapLoc = parseLoc(KarelInteraction.scanner.nextLine());
                        builder.setTrap(trapLoc);
                        break;
                    case "ROBOT":
                        System.out.println("Please enter the location of the Robot: row column");
                        int[] robotLoc = parseLoc(KarelInteraction.scanner.nextLine())[0];
                        System.out.println("Please enter the direction of the Robot: RIGHT, UP, LEFT, DOWN");
                        KarelRobot.Direction robotDir = KarelRobot.Direction.valueOf(
                                KarelInteraction.scanner.nextLine());
                        builder.setRobotLoc(robotLoc);
                        builder.setRobotDir(robotDir);
                        break;
                    default:
                        System.out.println("Type ? for help.");
                }
            } catch (Exception e) {
                System.out.println("Invalid input.");
            }
        }
    }

    private int[][] parseLoc(String input) {
        return parseLoc(input, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    private int[][] parseLoc(String input, int width, int height) {
        String[] loc = input.split(",");
        int[][] locs = new int[loc.length][2];
        for (int i = 0; i < loc.length; i++) {
            String[] rowcol = loc[i].trim().split("\\s+");
            if (rowcol.length != 2) {
                throw new IllegalArgumentException("Invalid input.");
            } else if (Integer.parseInt(rowcol[0]) > height || Integer.parseInt(rowcol[1]) > width) {
                throw new IllegalArgumentException("Invalid input.");
            }
            locs[i][0] = Integer.parseInt(rowcol[0]) - 1;
            locs[i][1] = Integer.parseInt(rowcol[1]) - 1;
        }
        return locs;
    }

    public static void main(String[] args) {

    }
}
