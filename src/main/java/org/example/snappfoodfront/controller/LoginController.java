package org.example.snappfoodfront.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.snappfoodfront.Service.AuthApiService;
import org.example.snappfoodfront.model.UserLoginDto;
import org.example.snappfoodfront.Utils.SceneManager;

import java.io.IOException;

public class LoginController {

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


    @FXML
    private void handleLoginButton(ActionEvent event) {

        String phone = phoneField.getText();
        String password = passwordField.getText();

        if (phone.isEmpty() || password.isEmpty()) {
            errorLabel.setVisible(true);
            errorLabel.setText("Invalid Phone or Password");
            return;
        }

        try {
            errorLabel.setVisible(false);
            UserLoginDto.Response loginResponse = authApiService.login(phone, password);
            errorLabel.setVisible(true);
            errorLabel.setText("Login Successful");
            String token = loginResponse.getToken();

            SceneManager.switchScene(event, "dashboard-view.fxml");

        } catch (IOException | InterruptedException e) {
            errorLabel.setVisible(true);
            errorLabel.setText("Internal Server Error");
            throw new RuntimeException("Failed to login : " + e.getMessage(), e);
        } catch (AuthApiService.LoginException e) {
            errorLabel.setVisible(true);
            errorLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void handleSignUpLink(ActionEvent event) {
        try {
            SceneManager.switchScene(event, "sign-up-view.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}