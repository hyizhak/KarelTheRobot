package gui;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import logic.*;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class KarelStageController implements Initializable {

    @FXML
    public BorderPane parentPane;
    @FXML
    public Menu loadMenu;
    public MenuItem switchbutton;
    public MenuItem helpbutton;
    public MenuItem restartbutton;
    @FXML
    private TextArea codeArea;
    @FXML
    private TextArea errorMessagesArea;
    @FXML
    private TextArea logsArea;
    @FXML
    private GridPane mapGrid;
    @FXML
    private Button exitButton;


    private ImageView[][] imageViewGrid;
    private KarelRobot rob;
    private KarelRobot clonedRobot;
    private int stageNo;
    private int gameState;
    private int count;
    private int numTimes;
    private String gameStageLogs = "./save/stagelogs.csv";
    private ObservableList<String> stageList;
    private MediaPlayer normalBGMPlayer;
    private MediaPlayer loseBGMPlayer;
    private MediaPlayer winBGMPlayer;
    private MediaPlayer mediaPlayer;
    private MediaPlayer endingPlayer;
    private MediaPlayer linePlayer;
    private final Image groundImage = new Image("/image/ground.png");
    private final Image robotRightImage = new Image("/image/karel0.png");
    private final Image robotUpImage = new Image("/image/karel1.png");
    private final Image robotLeftImage = new Image("/image/karel2.png");
    private final Image robotDownImage = new Image("/image/karel3.png");
    private final Image wallImage = new Image("/image/wall.png");
    private final Image rockImage = new Image("/image/rock.png");
    private final Image trapImage = new Image("/image/trap.png");
    private final Image trapLeveledImage = new Image("/image/trapleveled.png");
    private List<String> audioFiles = Arrays.asList(
            "./src/resources/audio/Deploy1.wav",
            "./src/resources/audio/Deploy2.wav",
            "./src/resources/audio/Battle1.wav",
            "./src/resources/audio/Battle2.wav",
            "./src/resources/audio/Battle3.wav",
            "./src/resources/audio/Battle4.wav",
            "./src/resources/audio/Chosen1.wav",
            "./src/resources/audio/Chosen2.wav",
            "./src/resources/audio/MissionDepart.wav",
            "./src/resources/audio/MissionStart.wav"
    );

    private Random random = new Random();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        codeArea.prefWidthProperty().bind(parentPane.widthProperty().multiply(0.4));
        codeArea.prefHeightProperty().bind(parentPane.heightProperty().multiply(0.7));
        errorMessagesArea.prefWidthProperty().bind(parentPane.widthProperty().multiply(0.4));
        errorMessagesArea.prefHeightProperty().bind(parentPane.heightProperty().multiply(0.2));
        logsArea.prefWidthProperty().bind(parentPane.widthProperty().multiply(0.55));
//        logsArea.prefHeightProperty().bind(parentPane.heightProperty().multiply(0.3));
        mapGrid.prefHeightProperty().bind(parentPane.heightProperty().multiply(0.7));

        // Redirect System.out to the outputArea
        PrintStream ps = new PrintStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                // append the data in it to text area
                Platform.runLater(() -> logsArea.appendText(String.valueOf((char) b)));
            }
        });
        System.setOut(ps);

        File file = new File(gameStageLogs);
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                if (file.createNewFile()) {
                    System.out.println("File created: " + file.getName());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        Media media1 = new Media(new File("./src/resources/audio/KaltsitTeam.mp3").toURI().toString());
        normalBGMPlayer = new MediaPlayer(media1);
        normalBGMPlayer.setCycleCount(MediaPlayer.INDEFINITE); // This will make the track loop indefinitely

        Media media2 = new Media(new File("./src/resources/audio/ItsaSin.mp3").toURI().toString());
        loseBGMPlayer = new MediaPlayer(media2);
        loseBGMPlayer.setCycleCount(MediaPlayer.INDEFINITE);

        Media media3 = new Media(new File("./src/resources/audio/SeeYouJosephine.mp3").toURI().toString());
        winBGMPlayer = new MediaPlayer(media3);
        winBGMPlayer.setCycleCount(MediaPlayer.INDEFINITE);

        handleShortcut();
        setLoadMenu();

//        // Redirect System.in to the inputArea
//        PipedInputStream inPipe = new PipedInputStream();
//        PipedOutputStream outPipe = null;
//        try {
//            outPipe = new PipedOutputStream(inPipe);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        System.setIn(inPipe);
//        PipedOutputStream finalOutPipe = outPipe;
//        codeArea.setOnKeyPressed(event -> {
//            if (event.getCode() == KeyCode.ENTER && event.isControlDown()) {
//                String text = codeArea.getText();
//                try {
//                    finalOutPipe.write((text + "\n").getBytes());
//                    finalOutPipe.flush();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                event.consume();
//            }
//        });

    }

    public void setup(int stageNo) {
        this.stageNo = stageNo;

        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }


        initStage(stageNo);

        mapGrid.getChildren().clear();

        imageViewGrid = new ImageView[rob.map.height][rob.map.width];
        double size = Math.min(1280 * 0.5 / rob.map.width, 720 * 0.65 / rob.map.height);
        for (int i = 0; i < rob.map.height; i++) {
            for (int j = 0; j < rob.map.width; j++) {
                imageViewGrid[i][j] = new ImageView();
                imageViewGrid[i][j].setFitWidth(size);
                imageViewGrid[i][j].setFitHeight(size);
                // Note that GridPane uses column-major order
                mapGrid.add(imageViewGrid[i][j], j, i);
            }
        }

        String saveDir = "save";
        String fileName = "stage" + stageNo + ".ser";
        Path filePath = Paths.get(saveDir, fileName);

        if (Files.exists(filePath)) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Would you like to load the existent save?",
                    ButtonType.YES, ButtonType.NO);
            alert.setHeaderText(null);
            alert.showAndWait();

            if (alert.getResult() == ButtonType.YES) {
                loadGame(fileName);
            }
        }

        updateImages(rob.map.map);

        mediaPlayer = normalBGMPlayer;

        try (BufferedReader reader = new BufferedReader(new FileReader(gameStageLogs))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data[0].equals(String.valueOf(stageNo))) {
                    switch (data[1]) {
                        case "1":
                            mediaPlayer = winBGMPlayer;
                            break;
                        case "-1":
                            mediaPlayer = loseBGMPlayer;
                            break;
                    }
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.play();
    }

    public void setupCustomStage(int stageNo) {
        this.stageNo = stageNo;

        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }

        try {
            FileInputStream fileInputStream = new FileInputStream("customstage/stage" + stageNo + ".ser");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            KarelRobot deserializedKarelRobot = (KarelRobot) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
            rob = deserializedKarelRobot;
            clonedRobot = rob.clone();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            System.out.println("Class not found");
            throw new RuntimeException(e);
        }

        mapGrid.getChildren().clear();

        imageViewGrid = new ImageView[rob.map.height][rob.map.width];
        double size = Math.min(1280 * 0.5 / rob.map.width, 720 * 0.65 / rob.map.height);
        for (int i = 0; i < rob.map.height; i++) {
            for (int j = 0; j < rob.map.width; j++) {
                imageViewGrid[i][j] = new ImageView();
                imageViewGrid[i][j].setFitWidth(size);
                imageViewGrid[i][j].setFitHeight(size);
                // Note that GridPane uses column-major order
                mapGrid.add(imageViewGrid[i][j], j, i);
            }
        }

        String saveDir = "save";
        String fileName = "stage" + stageNo + ".ser";
        Path filePath = Paths.get(saveDir, fileName);

        if (Files.exists(filePath)) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Would you like to load the existent save?",
                    ButtonType.YES, ButtonType.NO);
            alert.setHeaderText(null);
            alert.showAndWait();

            if (alert.getResult() == ButtonType.YES) {
                loadGame(fileName);
            }
        }

        updateImages(rob.map.map);

        mediaPlayer = normalBGMPlayer;

        try (BufferedReader reader = new BufferedReader(new FileReader(gameStageLogs))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data[0].equals(String.valueOf(stageNo)) && data.length > 1) {
                    switch (data[1]) {
                        case "1":
                            mediaPlayer = winBGMPlayer;
                            break;
                        case "-1":
                            mediaPlayer = loseBGMPlayer;
                            break;
                    }
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.play();
    }

    public void setStageList(ObservableList<String> stageList) {
        this.stageList = stageList;
    }

    public void playRandomLines(int numTimes) {
        this.count = 0;
        this.numTimes = numTimes;
        playNext();
    }

    private void playNext() {
        if (count < numTimes) {
            int randomIndex = random.nextInt(audioFiles.size());
            String filePath = audioFiles.get(randomIndex);
            Media hit = new Media(Paths.get(filePath).toUri().toString());
            linePlayer = new MediaPlayer(hit);

            linePlayer.setOnEndOfMedia(new Runnable() {
                @Override
                public void run() {
                    Platform.runLater(() -> {
                        linePlayer.stop();
                        count++;
                        playNext();
                    });
                }
            });

            linePlayer.play();
        }
    }

    private void saveGame() {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream("save/stage" + stageNo + ".ser");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            StageSave stageSave = new StageSave(codeArea.getText(), logsArea.getText(),
                    errorMessagesArea.getText(), stageNo, gameState, rob);
            objectOutputStream.writeObject(stageSave);
            objectOutputStream.close();
            fileOutputStream.close();
            updateStageLogs(gameState);
            System.out.println("Stage " + stageNo + " has been saved.");
            setLoadMenu();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setLoadMenu() {
        // Create the directory File object
        File saveDir = Paths.get("save").toFile();
        // Clear the current menu items
        loadMenu.getItems().clear();

        File[] saveFiles = saveDir.listFiles();

        if (saveFiles != null) {
            // Add each file as a menu item
            for (File file : saveFiles) {
                if (file.getName().endsWith(".ser")) {
                    MenuItem menuItem = new MenuItem(file.getName().replace(".ser", ""));
                    menuItem.setOnAction(event -> loadGame(file.getName()));
                    loadMenu.getItems().add(menuItem);
                }
            }
        }

    }

    private void loadGame(String fileName) {
        try {
            FileInputStream fileInputStream = new FileInputStream("save/" + fileName);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            StageSave deserializedStageSave = (StageSave) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
            codeArea.setText(deserializedStageSave.code);
            logsArea.setText(deserializedStageSave.logs);
            errorMessagesArea.setText(deserializedStageSave.errorMessages);
            stageNo = deserializedStageSave.stageNo;
            gameState = deserializedStageSave.gameState;
            rob = deserializedStageSave.rob;

            mapGrid.getChildren().clear();

            imageViewGrid = new ImageView[rob.map.height][rob.map.width];
            double size = Math.min(1280 * 0.5 / rob.map.width, 720 * 0.65 / rob.map.height);
            for (int i = 0; i < rob.map.height; i++) {
                for (int j = 0; j < rob.map.width; j++) {
                    imageViewGrid[i][j] = new ImageView();
                    imageViewGrid[i][j].setFitWidth(size);
                    imageViewGrid[i][j].setFitHeight(size);
                    // Note that GridPane uses column-major order
                    mapGrid.add(imageViewGrid[i][j], j, i);
                }
            }

            updateImages(rob.map.map);
            System.out.println("Save " + fileName + " has been loaded.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            System.out.println("Class not found");
            throw new RuntimeException(e);
        }
    }

    private void updateStageLogs(int gameState) {
        Map<String, String> map = new LinkedHashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(gameStageLogs))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                map.put(data[0], line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        map.put(String.valueOf(stageNo), stageNo + "," + Integer.toString(gameState));

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(gameStageLogs))) {
            for (String line : map.values()) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initStage(int stage) {
        WorldDirector director = new WorldDirector();
        WorldBuilder builder = new WorldBuilder();
        switch (stage) {
            case 1:
                director.stage1(builder);
                break;
            case 2:
                director.stage2(builder);
                break;
            case 3:
                director.stage3(builder);
                break;
        }
        rob = builder.buildRobot();
        clonedRobot = rob.clone();
    }

    private void initStage(KarelRobot rob) {
        this.rob = rob;
        clonedRobot = rob.clone();
    }

    private void updateImages(int[] grid) {
        for (int i = 0; i < rob.map.height; i++) {
            for (int j = 0; j < rob.map.width; j++) {
                imageViewGrid[i][j].setImage(getImageForState(grid[i * rob.map.width + j]));
            }
        }
    }

    private Image getImageForState(int state) {
        KarelMap.Site siteType = KarelMap.Site.intToSite(state);
        KarelRobot.Direction currentDir = KarelRobot.Direction.intToDir(rob.ori);
        switch (siteType) {
            case WALL:
                return wallImage;
            case ROCK:
                return rockImage;
            case TRAP:
                return trapImage;
            case TRAPLEVELED:
                return trapLeveledImage;
            case KAREL:
                return switch (currentDir) {
                    case RIGHT -> robotRightImage;
                    case UP -> robotUpImage;
                    case LEFT -> robotLeftImage;
                    case DOWN -> robotDownImage;
                };
            default:
                return groundImage;
        }
    }

    private void showHelp() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Help");
        alert.setHeaderText("Karel needs to collect all the rocks, whether it's to collect them in a bag " +
                "or to fill a trap.\n" + "Here are the commands and an example of input:\n");
        alert.setContentText(
                "move(int x)                Karel moves x steps forward\n" +
                        "turnLeft()                   Karel turns left\n" +
                        "pickRock()                 Karel picks the rock in front\n" +
                        "putRock()                  Karel puts a rock in front to level a trap if " +
                        "existent\n" +
                        "noRockPresent()       Karel checks if there is a rock in front\n" +
                        "noRockInBag()          Check if there is a rock in the bag\n" +
                        "showInformation()    Print the current state of the game\n" +
                        "if(){}else{}                   If-else syntax\n" +
                        "functionName(){}      Define a new custom function\n\n" +
                        "turnLeft();\n" +
                        "move();\n" +
                        "if (noRockPresent()) {\n" +
                        "move();\n" +
                        "}\n" +
                        "turnRight() {\n" +
                        "turnLeft();\n" +
                        "turnLeft();\n" +
                        "turnLeft();\n" +
                        "}\n" +
                        "turnRight();\n");
        alert.showAndWait();
    }

    private void handleShortcut() {
        // Create KeyCodeCombinations
        KeyCodeCombination ctrlEnterCombination = new KeyCodeCombination(KeyCode.ENTER, KeyCombination.CONTROL_DOWN);
        KeyCodeCombination ctrlSCombination = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
        KeyCodeCombination ctrlQCombination = new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN);
        KeyCodeCombination ctrlHCombination = new KeyCodeCombination(KeyCode.H, KeyCombination.CONTROL_DOWN);
        KeyCodeCombination ctrlshiftSCombination = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN);
        KeyCodeCombination F5Combination = new KeyCodeCombination(KeyCode.F5);
        // Handle key pressed event for inputArea namely run code when Ctrl+Enter is pressed
        codeArea.setOnKeyPressed(event -> {
            if (ctrlEnterCombination.match(event)) {
                runCode(null);  // Trigger the button's action
                event.consume();
            }
            //Shortcut for save: Ctrl+S
            if (ctrlSCombination.match(event)) {
                saveGame();
                event.consume();
            }
            //shortcut for exit: Ctrl+Q
            if (ctrlQCombination.match(event)) {
                exitButton.fire();
                event.consume();
            }
            //shortcut for help: Ctrl+H
            if (ctrlHCombination.match(event)) {
                showHelp();
                event.consume();
            }
            //shortcut for switching stages: Ctrl+Shift+S
            if (ctrlshiftSCombination.match(event)) {
                switchbutton.fire();
                event.consume();
            }
            //shortcut for restarting the game: F5
            if (F5Combination.match(event)) {
                restartbutton.fire();
                event.consume();
            }
        });
    }

    @FXML
    protected void runCode(ActionEvent actionEvent) {
        String code = codeArea.getText();
        ParaEval codePage = new ParaEval(code);
        try {
            codePage.split();
            int commandsCount = codePage.commandsCount;
            playRandomLines(commandsCount);
            gameState = codePage.invoke(rob);
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            errorMessagesArea.appendText(errorMessage + "\n");
        }
        updateImages(rob.map.map);
        if (gameState != 0) {
            updateStageLogs(gameState);
            mediaPlayer.stop();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            if (gameState == 1) {
                Media media = new Media(new File("./src/resources/audio/Kaluski.mp3").toURI().toString());
                endingPlayer = new MediaPlayer(media);
                endingPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                endingPlayer.play();
                alert.setTitle("There is no rock on the map. You win!");
            } else if (gameState == -1) {
                Media media = new Media(new File("./src/resources/audio/LoveTrap.mp3").toURI().toString());
                endingPlayer = new MediaPlayer(media);
                endingPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                endingPlayer.play();
                alert.setTitle("The robot is trapped. You loose...");
            }
            alert.setHeaderText(null);
            alert.setContentText("Do you want to restart the stage?");

            ButtonType buttonTypeYes = new ButtonType("Yes");
            ButtonType buttonTypeNo = new ButtonType("No");

            // Removing default ButtonTypes
            alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

            Optional<ButtonType> result = alert.showAndWait();
            endingPlayer.stop();
            if (result.isPresent() && result.get() == buttonTypeYes) {
                // user chose YES
                initStage(clonedRobot);
                codeArea.clear();
                logsArea.clear();
                errorMessagesArea.clear();
                updateImages(rob.map.map);
                mediaPlayer.play();
            } else {
                // user chose NO or closed the dialog
                exitButton.fire();
            }
        }
    }

    @FXML
    protected void exitLevel(ActionEvent actionEvent) {
        mediaPlayer.stop();

        String saveDir = "save";
        String fileName = "stage" + stageNo + ".ser";
        Path filePath = Paths.get(saveDir, fileName);

        if (!Files.exists(filePath)) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Would you like to save the stage?",
                    ButtonType.YES, ButtonType.NO);
            alert.setHeaderText(null);
            alert.showAndWait();

            if (alert.getResult() == ButtonType.YES) {
                saveGame();
            }
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("StartScreen.fxml"));
            Parent root = loader.load();
            StartScreenController controller = loader.getController();
            controller.setStageList(stageList);

            // Get the source of the event, cast it to a Node
            Node source = (Node) actionEvent.getSource();

            // Get the scene from the source
            Scene scene = source.getScene();

            // Get the stage from the scene
            Stage stage = (Stage) scene.getWindow();

            // Now you have the stage and can use it
            stage.setScene(new Scene(root, 1280, 720));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void switchLevel(ActionEvent actionEvent) {
        String saveDir = "save";
        String fileName = "stage" + stageNo + ".ser";
        Path filePath = Paths.get(saveDir, fileName);

        if (!Files.exists(filePath)) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Would you like to save the stage?",
                    ButtonType.YES, ButtonType.NO);
            alert.setHeaderText(null);
            alert.showAndWait();

            if (alert.getResult() == ButtonType.YES) {
                saveGame();
            }
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(stageList.get(0), stageList);
        dialog.setTitle("Switch Level");
        dialog.setHeaderText("Choose a level to switch to:");

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(stage -> {
            int endIndex = stage.indexOf("(", 6);
            int stageNo = Integer.parseInt(stage.substring(6, endIndex).trim());
            setup(stageNo);
        });
    }

    @FXML
    protected void saveGame(ActionEvent actionEvent) {
        saveGame();
    }

    @FXML
    protected void gameHelp(ActionEvent actionEvent) {
        showHelp();
    }

    @FXML
    protected void gameRestart(ActionEvent actionEvent) {
        initStage(clonedRobot);
        codeArea.clear();
        logsArea.clear();
        errorMessagesArea.clear();
        updateImages(rob.map.map);
        System.out.println("Restarted");
    }

    public static void main(String[] args) {

    }


}
