public class WorldBuilder {
    private int width, height;
    private int[][] wall, stone;
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

    public void setStone(int[][] stone) {
        this.stone = stone;
    }

    public KarelMap buildMap() {
        return new KarelMap(width, height, wall, stone);
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
