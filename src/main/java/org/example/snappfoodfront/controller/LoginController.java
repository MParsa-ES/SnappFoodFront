package org.example.snappfoodfront.controller;

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

        try {
            errorLabel.setVisible(false);
            UserLoginDto.Response loginResponse = authApiService.login(phone, password);
            errorLabel.setVisible(true);
            errorLabel.setText("Login Successful");
            String token = loginResponse.getToken();
            TokenManager.clearToken();
            TokenManager.saveToken(token);

            if (loginResponse.getUser().getRole().equals("SELLER")) {
                // will add the seller dashboard here
                SceneManager.switchScene(event, "dashboard-view.fxml", 1024, 720);
            } else {
                SceneManager.switchScene(event, "dashboard-view.fxml", 1024, 720);
            }


        } catch (IOException | InterruptedException e) {
            errorLabel.setVisible(true);
            errorLabel.setText("Unable to connect to server");
            throw new RuntimeException("Failed to login : " + e.getMessage(), e);
        } catch (AuthApiService.AuthException e) {
            errorLabel.setVisible(true);
            errorLabel.setText("Wrong Phone or Password");
        }
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