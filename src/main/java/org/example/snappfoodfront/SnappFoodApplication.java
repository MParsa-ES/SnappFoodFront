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

    private static final String SPLASH_VIEW_PATH = "/view/splash-screen-view.fxml";

    @Override
    public void start(Stage stage) throws IOException {

          SceneManager.showWindow(SPLASH_VIEW_PATH, "", "loading", 600, 400);

    }

    public static void main(String[] args) {
        launch();
    }
}