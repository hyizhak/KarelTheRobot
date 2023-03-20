import java.util.Arrays;

public class KarelRobot {

    public int[] loc;
    public int ori;
    public KarelMap map;

    public enum Direction {
        RIGHT, UP, LEFT, DOWN;

        public static Direction intToDir(int i) {
            return Direction.values()[i];
        }
    }


    /**
     * create a karel robot in a certain map
     */
    public KarelRobot(KarelMap map, int[] loc, Direction ori) {
        this.map = map;
        this.loc = loc;
        this.ori = ori.ordinal();
        this.map.setSite(this.loc, KarelMap.Site.KAREL);
    }

    /**
     * move the robot one step forward
     */
    public void move() {
        Direction currentDir = Direction.intToDir(ori);

        if ((currentDir == Direction.RIGHT && loc[1] == map.width - 1) ||
                (currentDir == Direction.UP && loc[0] == 0) ||
                (currentDir == Direction.LEFT && loc[1] == 0) ||
                (currentDir == Direction.DOWN && loc[0] == map.height - 1)) {
            throw new IndexOutOfBoundsException("The robot cannot leave the map!");
        }
        map.setSite(loc, KarelMap.Site.GROUND);
        switch (currentDir) {
            case RIGHT:
                loc[1]++;
                break;
            case UP:
                loc[0]--;
                break;
            case LEFT:
                loc[1]--;
                break;
            case DOWN:
                loc[0]++;
                break;
        }
        map.setSite(loc, KarelMap.Site.KAREL);
    }

    public void move(int n) {
        for (int i = 0; i < n; i++) {
            move();
        }
    }

    /**
     * turn the robot towards the left
     */
    public void turnLeft() {
        ori++;
        if (ori == 4) {
            ori = 0;
        }
    }

    //test
    public static void main(String[] args) {
        KarelMap m = new KarelMap(10, 5, null, null);
        KarelRobot r = new KarelRobot(m, new int[]{2, 0}, Direction.RIGHT);
        System.out.println(r.ori);
        System.out.println(Arrays.toString(m.map));
        r.move();
        r.turnLeft();
        System.out.println(Arrays.toString(m.map));
        System.out.println(r.ori);
    }
}
