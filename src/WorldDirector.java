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

    public static void main(String[] args) {

    }
}
