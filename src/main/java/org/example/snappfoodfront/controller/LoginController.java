package org.example.snappfoodfront.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private Button loginButton;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField phoneField;

    @FXML
    private void handleLoginButton(ActionEvent event) {

        String phone = phoneField.getText();
        String password = passwordField.getText();

        System.out.println("Phone : " + phone);
        System.out.println("Password : " + password);
    }

}