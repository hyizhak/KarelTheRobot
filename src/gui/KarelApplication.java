package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class KarelApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("StartScreen.fxml"));
        primaryStage.setTitle("Karel the Robot");
        primaryStage.getIcons().add(new Image(getClass().getResource("/image/icon.png").toExternalForm()));
        primaryStage.setScene(new Scene(root, 1280, 720));
        primaryStage.show();
    }
}
