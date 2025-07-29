package org.example.snappfoodfront.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import org.example.snappfoodfront.Service.AuthApiService;
import org.example.snappfoodfront.Service.OrderApiService;
import org.example.snappfoodfront.Service.ProfileApiService;
import org.example.snappfoodfront.Utils.CartManager;
import org.example.snappfoodfront.Utils.SceneManager;
import org.example.snappfoodfront.Utils.TokenManager;
import org.example.snappfoodfront.model.ProfileDto;

import java.net.URL;
import java.util.ResourceBundle;

public class SplashScreenController implements Initializable {


    @FXML public Label progressNum;
    @FXML public ProgressBar progressBar;
    @FXML public Label statusLabel;

    private final ProfileApiService profileService = new ProfileApiService();

    private static final String LOGIN_VIEW_PATH = "/view/login-view.fxml";
    private static final String DASHBOARD_VIEW_PATH = "/view/customer-main-view.fxml";

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
                    profileService.getProfile(token);
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

                    if (isTokenValid) {
                        try {
                            ProfileDto profile = profileService.getProfile(TokenManager.getToken());

                            switch (profile.getRole()) {
                                case "BUYER" ->
                                        SceneManager.showWindow("/view/customer-main-view.fxml", "SnappFood", "customer", 1024, 720);
                                case "SELLER" ->
                                        SceneManager.showWindow("/view/SellerViews/seller-main-view.fxml", "SnappFood", "seller", 1050, 720);
                                case "ADMIN" ->
                                        SceneManager.showWindow("/view/AdminViews/admin-dashboard-view.fxml", "SnappFood", "admin dashboard", 1050, 720);
                                case "COURIER" ->
                                    SceneManager.showWindow("/view/CourierViews/courier-dashboard-view.fxml", "SnappFood", "courier dashboard", 1050, 720);

                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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
