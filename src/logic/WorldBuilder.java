package logic;

public class WorldBuilder {
    private int width, height;
    private int[][] wall, rock, trap;
    private int[] robotLoc;
    private KarelRobot.Direction robotDir;

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setWall(int[][] wall) {
        this.wall = wall;
    }

    public void setRock(int[][] rock) {
        this.rock = rock;
    }

    public void setTrap(int[][] trap) {
        this.trap = trap;
    }

    public KarelMap buildMap() {
        return new KarelMap(width, height, wall, rock, trap);
    }

    public void setRobotLoc(int[] loc) {
        this.robotLoc = loc;
    }

    public void setRobotDir(KarelRobot.Direction dir) {
        this.robotDir = dir;
    }

    public KarelRobot buildRobot() {
        return new KarelRobot(buildMap(), robotLoc, robotDir);
    }

    public boolean validate() {
        if (width < 1 || height < 1) {
            return false;
        } else if (robotLoc == null || robotDir == null ||
                robotLoc[0] > height - 1 || robotLoc[1] > width - 1) {
            return false;
        } else if (rock == null) {
            return false;
        }
        return true;
    }

    public static void main(String[] args) throws IllegalAccessException {
        WorldBuilder builder = new WorldBuilder();
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
        System.out.println(builder.validate());
    }
}
