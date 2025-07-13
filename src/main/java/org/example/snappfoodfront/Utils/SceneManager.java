package org.example.snappfoodfront.Utils;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class SceneManager {


    private static final String LOGIN_VIEW_PATH = "/view/login-view.fxml";

    public static void showLoginWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(SceneManager.class.getResource(LOGIN_VIEW_PATH));
            Parent root = fxmlLoader.load();

            Stage loginStage = new Stage();
            loginStage.setTitle("Snapp Food");
            loginStage.setScene(new Scene(root));
            loginStage.setResizable(false);
            loginStage.show();

        } catch (IOException e) {
            System.err.println("Error loading login window");
            e.printStackTrace();
        }
    }


    public static void closeCurrentStage(Node node) {
        Stage currentStage = (Stage) node.getScene().getWindow();
        currentStage.close();
    }

    public static void switchScene(ActionEvent event, String fxmlFileName, double width, double height) throws IOException {

        Parent newRoot;
        try {
            URL resourceUrl = SceneManager.class.getResource("/view/" + fxmlFileName);
            newRoot = FXMLLoader.load(Objects.requireNonNull(resourceUrl));
        } catch (IOException e) {
            System.err.println("Error loading view " + fxmlFileName);
            e.printStackTrace();
            throw e;
        }

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        Scene newScene = new Scene(newRoot);

        newRoot.setOpacity(0.0);
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), newRoot);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();

        stage.setScene(newScene);
        stage.setWidth(width);
        stage.setHeight(height);

        stage.centerOnScreen();
        stage.show();


    }

    public static void switchCenterPane(BorderPane borderPane, String fxmlFileName) throws IOException {

        Parent newCenter = FXMLLoader.load(Objects.requireNonNull(SceneManager.class.getResource("/view/" + fxmlFileName)));

        borderPane.setCenter(newCenter);
    }

}