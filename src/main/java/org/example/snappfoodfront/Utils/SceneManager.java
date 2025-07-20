package org.example.snappfoodfront.Utils;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.example.snappfoodfront.Service.AuthApiService;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class SceneManager {


    private static final String LOGIN_VIEW_PATH = "/view/login-view.fxml";
    private static final String DASHBOARD_VIEW_PATH = "/view/customer-main-view.fxml";

    public static void showWindow(String path, String title, String error, int width, int height) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(SceneManager.class.getResource(path));
            Parent root = fxmlLoader.load();

            Stage stage = new Stage();

            if (title.isEmpty()) {
                stage.initStyle(StageStyle.UNDECORATED);
            }

            stage.setTitle(title);
            stage.setScene(new Scene(root, width, height));
            stage.setResizable(false);
            stage.show();

        } catch (IOException e) {
            System.err.println("Error loading " + error + "window");
            e.printStackTrace();
        }
    }

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

    public static void showMainWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(SceneManager.class.getResource(DASHBOARD_VIEW_PATH));
            Parent root = fxmlLoader.load();

            Stage loginStage = new Stage();
            loginStage.setTitle("Snapp Food");
            loginStage.setScene(new Scene(root));
            loginStage.setResizable(false);
            loginStage.show();

        } catch (IOException e) {
            System.err.println("Error loading dashboard");
            e.printStackTrace();
        }
    }

    public static void closeCurrentStage(Node node) {
        Stage currentStage = (Stage) node.getScene().getWindow();
        currentStage.close();
    }

    public static <T> T switchScene(ActionEvent event, String fxmlFileName, double width, double height) throws IOException {

        URL resourceUrl = SceneManager.class.getResource("/view/" + fxmlFileName);
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(resourceUrl));

        Parent newRoot;
        try {
            newRoot = loader.load();
        } catch (IOException e) {
            System.err.println("Error loading view " + fxmlFileName);
            e.printStackTrace();
            throw e;
        }

        T controller = loader.getController();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        Scene scene = stage.getScene();
        scene.setRoot(newRoot);

        newRoot.setOpacity(0.0);
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), newRoot);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();


        stage.setWidth(width);
        stage.setHeight(height);
        stage.centerOnScreen();
        stage.show();


        return controller;
    }

    public static void logout(Node node) {
        AuthApiService authService = new AuthApiService();
        String token = TokenManager.getToken();

        new Thread(() -> {
            try {
                if (token != null) {
                    authService.logout(token);
                }
            } catch (Exception e) {
                System.err.println("error while logging out" + e.getMessage());
            } finally {
                TokenManager.clearToken();
                Platform.runLater(() -> {
                    closeCurrentStage(node);
                    showWindow(LOGIN_VIEW_PATH, "SnappFood", "login", 600, 400);
                });
            }
        }).start();
    }

    public static void switchCenterPane(BorderPane borderPane, String fxmlFileName) throws IOException {

        Parent newCenter = FXMLLoader.load(Objects.requireNonNull(SceneManager.class.getResource("/view/" + fxmlFileName)));

        borderPane.setCenter(newCenter);
    }

}