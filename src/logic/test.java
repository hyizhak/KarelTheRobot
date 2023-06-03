package logic;

import java.io.File;
import java.nio.file.Paths;

public class test {
    public static void main(String[] args) {
        File saveDir = Paths.get("save").toFile();
        File[] savedGames = saveDir.listFiles();
        if (savedGames != null) {
            for (File savedGame : savedGames) {
                System.out.println(savedGame.getName());
            }
        }
    }
}
