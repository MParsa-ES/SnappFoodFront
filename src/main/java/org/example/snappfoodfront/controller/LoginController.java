package org.example.snappfoodfront.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.example.snappfoodfront.Service.AuthApiService;
import org.example.snappfoodfront.Utils.TokenManager;
import org.example.snappfoodfront.model.UserLoginDto;
import org.example.snappfoodfront.Utils.SceneManager;
import org.example.snappfoodfront.Utils.Methods;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

public class LoginController implements Initializable {

    private final AuthApiService authApiService;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField phoneField;
    @FXML
    private Label errorLabel;
    @FXML
    private Hyperlink signUpLink;

    public LoginController() {
        this.authApiService = new AuthApiService();
    }

    private static final String DASHBOARD_VIEW_PATH = "/view/customer-main-view.fxml";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Methods.filterPhoneField(phoneField);
    }


    @FXML
    private void handleLoginButton(ActionEvent event) {

        String phone = phoneField.getText();
        String password = passwordField.getText();

        if (phone.isEmpty() || password.isEmpty()) {
            errorLabel.setVisible(true);
            errorLabel.setText("Invalid Phone or Password");
            return;
        }

        new Thread(() -> {
            try {
                UserLoginDto.Response loginResponse = authApiService.login(phone, password);
                TokenManager.saveToken(loginResponse.getToken());

                Platform.runLater(() -> {
                    errorLabel.setText("Login Successful!");
                    SceneManager.closeCurrentStage(errorLabel);
                    SceneManager.showWindow(DASHBOARD_VIEW_PATH, "SnappFood", "main window", 1024, 720);
                });
            } catch (AuthApiService.AuthException e) {
                Platform.runLater(() -> errorLabel.setText("Wrong phone number or password."));
            } catch (IOException | InterruptedException e) {
                Platform.runLater(() -> errorLabel.setText("Unable to connect to the server."));
            }
        }).start();
    }

    @FXML
    private void handleSignUpLink(ActionEvent event) {
        try {
            SceneManager.switchScene(event, "sign-up-view.fxml", 600, 400);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}