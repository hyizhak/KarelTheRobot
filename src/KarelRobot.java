import java.util.Arrays;

public class KarelRobot {

    public int[] loc;
    public int ori;
    public int bagRock = 0;
    public boolean trapped = false;
    public KarelMap map;

    public enum Direction {
        RIGHT, UP, LEFT, DOWN;

        public static Direction intToDir(int i) {
            return Direction.values()[i];
        }
    }


    /**
     * construct a karel robot in a certain map
     */
    public KarelRobot(KarelMap map, int[] loc, Direction ori) {
        this.map = map;
        this.loc = loc;
        this.ori = ori.ordinal();
        this.map.setSite(this.loc, KarelMap.Site.KAREL);
    }

    /**
     * get the next site in front of the robot
     *
     * @return the coordinate of the next site
     */
    private int[] nextSite() {
        Direction currentDir = Direction.intToDir(ori);
        int[] nextLoc = new int[]{loc[0], loc[1]};
        switch (currentDir) {
            case RIGHT:
                nextLoc[1]++;
                break;
            case UP:
                nextLoc[0]--;
                break;
            case LEFT:
                nextLoc[1]--;
                break;
            case DOWN:
                nextLoc[0]++;
                break;
        }
        return nextLoc;
    }

    /**
     * move the robot one step forward
     */
    public void move() {
        int[] nextLoc = nextSite();

        if (!map.isPassable(nextLoc)) {
            throw new IllegalArgumentException("The robot cannot move towards this direction anymore!");
        }
        if (map.getType(nextLoc) == KarelMap.Site.TRAP.typeValue) {
            map.setSite(loc, KarelMap.Site.GROUND);
            trapped = true;
        } else {
            map.setSite(loc, KarelMap.Site.GROUND);
            loc = nextLoc;
            map.setSite(loc, KarelMap.Site.KAREL);
        }
    }

    /**
     * move the robot n steps forward
     *
     * @param n the number of steps
     */
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

    public void turnRight() {
        for (int i = 0; i < 3; i++) {
            turnLeft();
        }
    }

    /**
     * pick the stone in front of the robot
     */
    public void pickRock() {
        int[] nextLoc = nextSite();
        if (map.getType(nextLoc) == KarelMap.Site.ROCK.typeValue) {
            bagRock++;
            System.out.println("You have got a rock!");
            System.out.println("Now you have " + bagRock + " in your bag.");
            map.rockPicked(nextLoc);
        } else {
            System.out.println("There is no rock ahead! Please enter again.");
        }
    }

    /**
     * put the stone in front of the robot
     */
    public void putRock() {
        int[] nextLoc = nextSite();
        if (map.getType(nextLoc) == KarelMap.Site.TRAP.typeValue) {
            if (bagRock > 0) {
                bagRock--;
                System.out.println("You have put down a rock!");
                System.out.println("Now you have " + bagRock + " left.");
                map.rockPut(nextLoc);
            } else {
                System.out.println("You have no rock in your bag! Please enter again.");
            }
        } else {
            System.out.println("You cannot put a rock here! Please enter again.");
        }
    }

    /**
     * check if the robot has no rock in the bag
     *
     * @return true if the robot has no rock in the bag
     */
    public boolean noRockInBag() {
        boolean flag = bagRock == 0;
        System.out.println(flag);
        return flag;
    }

    /**
     * check if there is no rock in front of the robot
     *
     * @return true if there is no rock in front of the robot
     */
    public boolean noRockPresent() {
        int[] nextLoc = nextSite();
        boolean flag = map.getType(nextLoc) == KarelMap.Site.ROCK.typeValue ||
                map.getType(nextLoc) == KarelMap.Site.TRAPLEVELED.typeValue;
        System.out.println(!flag);
        return !flag;
    }

    /**
     * print the information of the Karel world, including the number of rocks on the map,
     * the number of rocks in the bag, and the distance to the nearest rock
     */
    public void showInformation() {
        String num;
        if (bagRock == 0) {
            num = "no";
        } else {
            num = Integer.toString(bagRock);
        }
        System.out.println("There is " + map.numMapRock() + " rock(s) on the map that you need to collect.");
        System.out.println("You have " + num + " rock(s) in your bag.");
        System.out.println("You are " + map.nearRockStep(loc) + " step(s) away from the nearest rock.");
    }

    //test
    public static void main(String[] args) {
        KarelMap m = new KarelMap(10, 5, null, null, null);
        KarelRobot r = new KarelRobot(m, new int[]{2, 0}, Direction.RIGHT);
        System.out.println(r.ori);
        System.out.println(Arrays.toString(r.loc));
        System.out.println(Arrays.toString(r.nextSite()));
        //System.out.println(Arrays.toString(m.map));
        r.move();
        System.out.println(Arrays.toString(r.loc));
        r.turnLeft();
        //System.out.println(Arrays.toString(m.map));
        System.out.println(r.ori);
        System.out.println(Arrays.toString(r.loc));
        System.out.println(Arrays.toString(r.nextSite()));
        r.move();
        System.out.println(Arrays.toString(r.loc));
    }
}
