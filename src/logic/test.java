package logic;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

public class test {
    public static void main(String[] args) {
        File saveDir = Paths.get("save").toFile();
        File[] savedGames = saveDir.listFiles();
        if (savedGames != null) {
            for (File savedGame : savedGames) {
                System.out.println(savedGame.getName());
            }
        }
        ArrayList<int[]> rock1 = new ArrayList<>();
        rock1.add(new int[]{1, 2});
        rock1.add(new int[]{0, 7});
        rock1.add(new int[]{1, 2});
        rock1.add(new int[]{0, 7});
        int[][] rockarray = rock1.toArray(new int[rock1.size()][]);
        int[][] rock = new int[][]{{1, 2}, {0, 7}, {1, 2}, {0, 7}};
        System.out.println(Arrays.deepToString(rockarray));
        System.out.println(Arrays.deepToString(rock));
    }
}
