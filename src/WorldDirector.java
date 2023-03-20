public class WorldDirector {

    public void stage1(WorldBuilder builder) {
        builder.setWidth(6);
        builder.setHeight(3);
        int[][] stone = new int[][]{{1, 5}};
        builder.setStone(stone);
        builder.setRobotLoc(new int[]{1, 0});
        builder.setRobotDir(KarelRobot.Direction.RIGHT);
    }

    public void stage2(WorldBuilder builder) {
        builder.setWidth(6);
        builder.setHeight(3);
        int[][] stone = new int[][]{{1, 5}};
        int[][] wall = new int[][]{{0, 0}, {1, 0}, {2, 2}, {2, 3}, {2, 4}, {2, 5}};
        builder.setStone(stone);
        builder.setWall(wall);
        builder.setRobotLoc(new int[]{2, 0});
        builder.setRobotDir(KarelRobot.Direction.RIGHT);
    }

    public static void main(String[] args) {

    }
}
