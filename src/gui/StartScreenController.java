package gui;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

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

    @FXML
    public void initialize() {
        menuBox.prefWidthProperty().bind(parentPane.widthProperty().multiply(0.3));
        menuBox.prefHeightProperty().bind(parentPane.heightProperty().multiply(0.3));
        setStageComboBox();

        Media media = new Media(new File("./src/resources/audio/AdAstra.mp3").toURI().toString());
        openingPlayer = new MediaPlayer(media);
        openingPlayer.setCycleCount(MediaPlayer.INDEFINITE); // This will make the track loop indefinitely
        openingPlayer.play();
    }

    @FXML
    public void handleNewMap(ActionEvent event) {
        //TODO: 处理创建新地图的逻辑
    }

    protected void setStageComboBox() {
        String csvFile = "./save/stagelogs.csv";

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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleSelectStage(ActionEvent event) {
        openingPlayer.stop();
        String str = stageComboBox.getValue();
        int endIndex = str.indexOf("(", 6);
        int stageNo = Integer.parseInt(str.substring(6, endIndex).trim());
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("KarelStage.fxml"));
            Parent root = loader.load();
            KarelStageController controller = loader.getController();
            controller.setStageList(stageList);
            controller.setup(stageNo);

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
