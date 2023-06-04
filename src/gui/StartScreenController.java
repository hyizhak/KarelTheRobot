package gui;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Pair;
import logic.KarelMap;
import logic.KarelRobot;

import java.io.*;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StartScreenController {
    @FXML
    private AnchorPane parentPane;
    @FXML
    private VBox menuBox;
    @FXML
    private Button newMapButton;
    @FXML
    private ComboBox<String> stageComboBox;
    @FXML
    private ObservableList<String> stageList;
    @FXML
    private Button selectStageButton;

    private MediaPlayer openingPlayer;
    private int stageNo;
    private String gameStageLogs = "./save/stagelogs.csv";

    @FXML
    public void initialize() {
        stageComboBox.setValue("Stage 1");
        menuBox.prefWidthProperty().bind(parentPane.widthProperty().multiply(0.3));
        menuBox.prefHeightProperty().bind(parentPane.heightProperty().multiply(0.3));


        File dir = Paths.get("customstage").toFile();
        int maxStageNumber = 3;
        File[] customStages = dir.listFiles();
        if (customStages != null) {
            for (File customStage : customStages) {
                String fileName = customStage.getName();
                if (fileName.startsWith("stage") && fileName.endsWith(".ser")) {
                    String stageNumberStr = fileName.substring(5, fileName.length() - 4);
                    int stageNumber = Integer.parseInt(stageNumberStr);
                    if (stageNumber > maxStageNumber) {
                        maxStageNumber = stageNumber;
                    }
                }
            }
        }
        stageNo = maxStageNumber;

        Media media = new Media(new File("./src/resources/audio/AdAstra.mp3").toURI().toString());
        openingPlayer = new MediaPlayer(media);
        openingPlayer.setCycleCount(MediaPlayer.INDEFINITE); // This will make the track loop indefinitely
        openingPlayer.play();
        setStageComboBox();
    }

    @FXML
    public void handleNewMap(ActionEvent event) {
        // Create the custom dialog.
        Dialog<Pair<Integer, Integer>> dialog = new Dialog<>();
        dialog.setTitle("New Map Size");
        dialog.setHeaderText("Enter the dimensions for the new map:");

        // Set the button types.
        ButtonType loginButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        // Create the width and height labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField width = new TextField();
        width.setPromptText("Width");
        TextField height = new TextField();
        height.setPromptText("Height");

        grid.add(new Label("Width:"), 0, 0);
        grid.add(width, 1, 0);
        grid.add(new Label("Height:"), 0, 1);
        grid.add(height, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // Convert the result to a width-height-pair when the OK button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new Pair<>(Integer.valueOf(width.getText()), Integer.valueOf(height.getText()));
            }
            return null;
        });

        Optional<Pair<Integer, Integer>> result = dialog.showAndWait();

        result.ifPresent(widthHeight -> {
            System.out.println("Width=" + widthHeight.getKey() + ", Height=" + widthHeight.getValue());
            int[][] mapGrid = createMap(widthHeight);
            KarelMap map = new KarelMap(mapGrid);
            KarelRobot rob = new KarelRobot(map);
            stageNo++;
            try {
                FileOutputStream fileOutputStream = new FileOutputStream("customstage/stage" + stageNo +
                        ".ser");
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
                objectOutputStream.writeObject(rob);
                objectOutputStream.close();
                fileOutputStream.close();
                System.out.println("Custom Stage " + stageNo + " has been saved.");
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(gameStageLogs, true))) {
                    writer.write(String.valueOf(stageNo));
                    writer.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        setStageComboBox();
    }

    public int[][] createMap(Pair<Integer, Integer> dimensions) {
        int width = dimensions.getKey();
        int height = dimensions.getValue();

        Dialog<int[][]> dialog = new Dialog<>();
        dialog.setTitle("New Map");
        dialog.setHeaderText("Enter the numbers for each grid in your custom map:\n" +
                "0 = ground, 1 = Karel, 2 = wall, 3 = rock, 4 = trap\n" +
                "You need a Karel and at least one rock to play the game.");

        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField[][] fields = new TextField[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                fields[i][j] = new TextField();
                fields[i][j].setPrefWidth(40);
                fields[i][j].setPrefHeight(40);
                fields[i][j].setMaxWidth(40);
                fields[i][j].setMaxHeight(40);
                fields[i][j].setMinWidth(40);
                fields[i][j].setMinHeight(40);

                fields[i][j].setTextFormatter(new TextFormatter<>(change -> {
                    if (change.getControlNewText().matches("[0-4]?")) {
                        return change;
                    } else {
                        return null;
                    }
                }));

                // Add a listener to the text property
                fields[i][j].textProperty().addListener((observable, oldValue, newValue) -> {
                    // Enable or disable the OK button based on the conditions
                    dialog.getDialogPane().lookupButton(okButtonType).setDisable(!validateFields(fields));
                });

//                fields[i][j].setText("0");
//                fields[i][j].clear();

                grid.add(fields[i][j], j, i);
            }
        }

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                int[][] result = new int[height][width];
                for (int i = 0; i < height; i++) {
                    for (int j = 0; j < width; j++) {
                        result[i][j] = Integer.parseInt(fields[i][j].getText());
                    }
                }
                return result;
            }
            return null;
        });

        Optional<int[][]> result = dialog.showAndWait();
        return result.orElse(null);
    }

    private boolean validateFields(TextField[][] fields) {
        int count1 = 0;
        int count3 = 0;

        for (TextField[] row : fields) {
            for (TextField field : row) {
                String text = field.getText();
                if ("1".equals(text)) {
                    count1++;
                }
                if ("3".equals(text)) {
                    count3++;
                }
                if (text.isEmpty()) {
                    return false;
                }
            }
        }

        return count1 == 1 && count3 >= 1;  // Check if there is exactly one 1 and at least one 3
    }

    public void setStageList(ObservableList<String> stageList) {
        this.stageList = stageList;
    }

    private void setStageComboBox() {
        while (stageList.size() < stageNo) {
            stageList.add("Stage " + (stageList.size() + 1));
        }

        String csvFile = gameStageLogs;

        File file = new File(csvFile);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        String line;
        String csvSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((line = br.readLine()) != null) {
                String[] stageInfo = line.split(csvSplitBy);
                String stageNum = stageInfo[0];
                if (stageInfo.length == 1) {
                    stageList.set(Integer.parseInt(stageNum) - 1, "Stage " + stageNum +
                            " ( Not played)");
                } else {
                    if (stageInfo[1].equals("0")) {
                        String stageState = "Played";
                        stageList.set(Integer.parseInt(stageNum) - 1, "Stage " + stageNum +
                                " (" + stageState + ")");
                    } else if (stageInfo[1].equals("1")) {
                        String stageState = "Cleared";
                        stageList.set(Integer.parseInt(stageNum) - 1, "Stage " + stageNum +
                                " (" + stageState + ")");
                    } else if (stageInfo[1].equals("-1")) {
                        String stageState = "Once failed";
                        stageList.set(Integer.parseInt(stageNum) - 1, "Stage " + stageNum +
                                " (" + stageState + ")");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleSelectStage(ActionEvent event) {
        openingPlayer.stop();
        String str = stageComboBox.getValue();
        Pattern pattern = Pattern.compile("\\d+");  // Match one or more digits
        Matcher matcher = pattern.matcher(str);
        StringBuilder numbers = new StringBuilder();
        while (matcher.find()) {
            numbers.append(matcher.group());
        }
        int stageChosen = Integer.parseInt(numbers.toString());
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("KarelStage.fxml"));
            Parent root = loader.load();
            KarelStageController controller = loader.getController();
            controller.setStageList(stageList);
            if (stageChosen < 4) {
                controller.setup(stageChosen);
            } else {
                controller.setupCustomStage(stageChosen);
            }

            // Get the source of the event, cast it to a Node
            Node source = (Node) event.getSource();

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
}
