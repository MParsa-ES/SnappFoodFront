package org.example.snappfoodfront.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import org.example.snappfoodfront.Service.AuthApiService;
import org.example.snappfoodfront.Utils.SceneManager;
import org.example.snappfoodfront.Utils.TokenManager;

import java.net.URL;
import java.util.ResourceBundle;

public class SplashScreenController implements Initializable {


    @FXML public Label progressNum;
    @FXML public ProgressBar progressBar;
    @FXML public Label statusLabel;

    private final AuthApiService authService = new AuthApiService();

    private static final String LOGIN_VIEW_PATH = "/view/login-view.fxml";
    private static final String DASHBOARD_VIEW_PATH = "/view/dashboard-view.fxml";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Task<Boolean> tokenValidationTask = new Task<>() {

            @Override
            protected Boolean call() throws Exception {

                updateMessage("Checking token...");
                updateProgress(10, 100);
                updateProgressNum(10);
                Thread.sleep(1000);
                String token = TokenManager.getToken();

                if (token == null) {
                    updateMessage("No session found...");
                    updateProgress(100, 100);
                    updateProgressNum(100);
                    Thread.sleep(1000);
                    return false;
                }

                updateMessage("Validating token...");
                updateProgress(50, 100);
                updateProgressNum(50);
                Thread.sleep(1000);

                try {
                    authService.getProfile(token);
                    updateMessage("Welcome back!");
                    updateProgress(100, 100);
                    updateProgressNum(100);
                    Thread.sleep(1000);
                    return true;
                } catch (Exception e) {
                    updateMessage("Token is invalid");
                    updateProgress(100, 100);
                    updateProgressNum(100);
                    Thread.sleep(1000);
                    TokenManager.clearToken();
                    return false;
                }
            }
        };

        progressBar.progressProperty().bind(tokenValidationTask.progressProperty());
        statusLabel.textProperty().bind(tokenValidationTask.messageProperty());

            tokenValidationTask.setOnSucceeded(event -> {

                Platform.runLater(() -> {
                    boolean isTokenValid = tokenValidationTask.getValue();

                    SceneManager.closeCurrentStage(progressBar);

                    // TODO : remove after testing
                    isTokenValid = false;


                    if (isTokenValid) {
                        SceneManager.showWindow(DASHBOARD_VIEW_PATH, "SnappFood", "dashboard", 1024, 720);
                    } else {
                        SceneManager.showWindow(LOGIN_VIEW_PATH, "SnappFood", "login", 600, 400);
                    }
                });
            });

        new Thread(tokenValidationTask).start();
    }

    private void updateProgressNum(int num) {

        Platform.runLater(() -> {
            progressNum.setText(num + "%");
        });

    }

}
