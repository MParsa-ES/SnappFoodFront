package org.example.snappfoodfront.Utils;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.snappfoodfront.SnappFoodApplication;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class SceneManager {

    public static void switchScene(ActionEvent event, String fxmlFileName) throws IOException {

        URL url = new File("src/main/resources/view/" + fxmlFileName).toURI().toURL();

        FXMLLoader fxmlLoader = new FXMLLoader(url);

        Scene scene = new Scene(fxmlLoader.load());

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        stage.setScene(scene);
        stage.show();
    }

}
