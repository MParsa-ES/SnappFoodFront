package org.example.snappfoodfront.controller;

import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.snappfoodfront.Service.AuthApiService;
import org.example.snappfoodfront.model.UserLoginDto;

import java.io.IOException;

public class LoginController {

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField phoneField;

    @FXML
    private Label errorLabel;


    @FXML
    private void handleLoginButton(ActionEvent event) {

        String phone = phoneField.getText();
        String password = passwordField.getText();

        if (phone.isEmpty() || password.isEmpty()) {
            errorLabel.setVisible(true);
            errorLabel.setText("Invalid Phone or Password");
            return;
        }

        AuthApiService authApiService = new AuthApiService();
        try {
            errorLabel.setVisible(false);
            UserLoginDto.Response loginResponse = authApiService.login(phone, password);
            errorLabel.setVisible(true);
            errorLabel.setText("Login Successful");
            String token = loginResponse.getToken();

            // TODO : direct to the next page

        } catch (IOException | InterruptedException e) {
            errorLabel.setVisible(true);
            errorLabel.setText("Internal Server Error");
            throw new RuntimeException("Failed to login : " + e.getMessage(), e);
        } catch (AuthApiService.LoginException e) {
            errorLabel.setVisible(true);

            return;
        }
    }

}