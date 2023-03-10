import java.util.Arrays;

//TODO: maybe try abstract factory later
public class KarelMap {

    public int[] map;
    public final int width;
    public final int height;

    public enum Site {
        GROUND(0), KAREL(1);

        private final int typeValue;

        Site(int typeValue) {
            this.typeValue = typeValue;
        }

        public static Site int2Site(int i) {
            return Site.values()[i];
        }
    }


    /**
     * create an array to represent the map
     */
    public KarelMap(int width, int height) {
        this.width = width;
        this.height = height;
        map = new int[width * height];
    }

    /**
     * get the index from a set of row and col, both starting from 0
     */
    private int index(int[] loc) {
        return loc[0] * width + loc[1];
    }

    /**
     * get the site type
     */
    public int getType(int index) {
        return map[index];
    }

    /**
     * set the specified site to a certain type
     */
    public void setSite(int[] loc, Site type) {
        int i = index(loc);
        map[i] = type.typeValue;
    }

    //test
    public static void main(String[] args) {
        KarelMap m = new KarelMap(10, 5);
        System.out.println(m.width);
        int[] loc = new int[]{2, 0};
        m.setSite(loc, Site.KAREL);
        System.out.print(Arrays.toString(m.map));
    }
}
