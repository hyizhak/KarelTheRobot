package logic;

import java.io.Serializable;
import java.util.Arrays;

public class KarelMap implements Serializable {

    public int[] map;
    public final int width, height;
    private int[][] rock;


    public enum Site {
        GROUND(0),
        KAREL(1),
        WALL(2),
        ROCK(3),
        TRAP(4),
        TRAPLEVELED(5);

        public final int typeValue;

        Site(int typeValue) {
            this.typeValue = typeValue;
        }

        public static Site intToSite(int i) {
            return Site.values()[i];
        }
    }


    /**
     * construct an array to represent the map
     */
    public KarelMap(int width, int height, int[][] wall, int[][] rock, int[][] trap) {
        this.width = width;
        this.height = height;
        this.rock = rock;
        map = new int[width * height];
        if (wall != null) setSites(wall, Site.WALL);
        if (rock != null) setSites(rock, Site.ROCK);
        if (trap != null) setSites(trap, Site.TRAP);
    }

    public KarelMap(int[] map, int width, int height, int[][] rock) {
        this.map = map;
        this.width = width;
        this.height = height;
        this.rock = rock;
    }

    public KarelMap mapClone() {
        int[] mapClone = new int[map.length];
        int[][] rockClone = new int[rock.length][2];
        System.arraycopy(map, 0, mapClone, 0, map.length);
        System.arraycopy(rock, 0, rockClone, 0, rock.length);
        return new KarelMap(mapClone, width, height, rockClone);
    }

    /**
     * get the index from a set of row and col, both starting from 0, helper function
     *
     * @return the index of the site in the map
     */
    public int index(int[] loc) {
        return loc[0] * width + loc[1];
    }

    /**
     * get the site type
     *
     * @return the type of the site in int
     */
    public int getType(int[] loc) {
        int index = index(loc);
        return map[index];
    }

    /**
     * set the specified site to a certain type
     *
     * @param loc  the coordinate of the site
     * @param type the type of the site
     */
    public void setSite(int[] loc, Site type) {
        int i = index(loc);
        map[i] = type.typeValue;
    }

    /**
     * set the specified sites to a certain type
     *
     * @param locs the coordinates of the sites
     * @param type the type of the sites
     */
    public void setSites(int[][] locs, Site type) {
        for (int[] loc : locs) {
            setSite(loc, type);
        }
    }

    /**
     * check if the site is passable
     *
     * @param loc the coordinate of the site
     * @return true if the site is passable
     */
    public boolean isPassable(int[] loc) {
        if (loc[0] >= height || loc[1] >= width || loc[0] <= -1 || loc[1] <= -1) {
            return false;
        }
        int i = index(loc);
        return map[i] == Site.GROUND.typeValue ||
                map[i] == Site.TRAPLEVELED.typeValue ||
                map[i] == Site.TRAP.typeValue;
    }

    /**
     * change the states when a rock is picked
     *
     * @param loc the coordinate of the rock picked
     */
    public void rockPicked(int[] loc) {
        setSite(loc, Site.GROUND);
        int[][] newRock = new int[rock.length - 1][2];
        int i = 0;
        for (int[] rockLoc : rock) {
            if (rockLoc[0] != loc[0] || rockLoc[1] != loc[1]) {
                newRock[i++] = rockLoc;
            }
        }
        rock = newRock;
    }

    /**
     * change the states when a rock is put to level a trap
     *
     * @param loc the coordinate of the rock put
     */
    public void rockPut(int[] loc) {
        setSite(loc, Site.TRAPLEVELED);
    }

    /**
     * get the number of rocks in the map
     *
     * @return the number of rocks in the map
     */
    public int numMapRock() {
        return rock.length;
    }

    /**
     * get the steps to the nearest rock
     *
     * @param loc the coordinate of the robot
     * @return the steps to the nearest rock
     */
    public int nearRockStep(int[] loc) {
        int[] distance = new int[numMapRock()];
        int i = 0;
        for (int[] rockLoc : rock) {
            distance[i++] = Math.abs(loc[0] - rockLoc[0]) + Math.abs(loc[1] - rockLoc[1]);
        }
        int minDistance = distance[0];
        for (int steps : distance) {
            if (steps < minDistance) minDistance = steps;
        }
        return minDistance;
    }

    //test
    public static void main(String[] args) {
        KarelMap m = new KarelMap(10, 5, null, null, null);
        System.out.println(m.width);
        int[] loc = new int[]{2, 0};
        m.setSite(loc, Site.KAREL);
        int[] loc2 = new int[]{2, 1};
        m.setSite(loc2, Site.WALL);
        System.out.println(m.isPassable(loc2));
        System.out.print(Arrays.toString(m.map));
    }
}
