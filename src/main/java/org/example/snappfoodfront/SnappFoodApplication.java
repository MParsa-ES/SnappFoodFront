package org.example.snappfoodfront;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.snappfoodfront.Utils.SceneManager;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class SnappFoodApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

          SceneManager.showLoginWindow();

    }

    public static void main(String[] args) {
        launch();
    }
}