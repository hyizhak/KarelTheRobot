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

    public static void main(String[] args) {

    }
}
