package org.example.snappfoodfront;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class SnappFoodApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        URL url = new File("src/main/resources/view/login-view.fxml").toURI().toURL();

        FXMLLoader fxmlLoader = new FXMLLoader(url);
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setHeight(400);
        stage.setWidth(600);
        stage.setTitle("Snapp Food");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}