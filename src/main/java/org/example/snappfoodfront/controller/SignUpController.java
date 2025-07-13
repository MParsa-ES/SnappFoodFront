package org.example.snappfoodfront.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import org.example.snappfoodfront.Service.AuthApiService;
import org.example.snappfoodfront.Utils.SceneManager;
import org.example.snappfoodfront.Utils.RegistrationContext;
import org.example.snappfoodfront.Utils.Methods;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

public class SignUpController implements Initializable {

    @FXML public TextField phoneField;
    @FXML public PasswordField passwordField;
    @FXML public ComboBox roleBox;
    @FXML public Label errorLabel;
    @FXML public Hyperlink loginLink;

    private final AuthApiService authService = new AuthApiService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        roleBox.getItems().addAll(
                "BUYER",
                "SELLER",
                "COURIER"
        );
        Methods.filterPhoneField(phoneField);
    }

    @FXML
    private void handleLoginLink(ActionEvent event) {
        try {
            SceneManager.switchScene(event, "login-view.fxml", 600, 400);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void handleContinue(ActionEvent event) {

        String phone = phoneField.getText();
        String password = passwordField.getText();
        String role = roleBox.getValue().toString();

        if (phone.isEmpty() || password.isEmpty() || role.isEmpty()) {
            errorLabel.setTextFill(Color.RED);
            errorLabel.setText("Please complete all fields");
            return;
        }

        new Thread(() -> {

            try {

                authService.validatePhone(phone);

                Platform.runLater(() -> {

                    RegistrationContext.setPhone(phone);
                    RegistrationContext.setPassword(password);
                    RegistrationContext.setRole(role);

                    try {
                        SceneManager.switchScene(event, "profile-completion-view.fxml", 600, 400);
                    } catch (IOException e) {
                        e.printStackTrace();
                        errorLabel.setText("Unable to continue registration");
                    }

                });
            } catch (AuthApiService.AuthException e) {
                Platform.runLater(() -> {
                    if (e.getErrorCode() == 409) {
                        errorLabel.setText("This phone number is already in use");
                    } else {
                        errorLabel.setText("Unable to validate phone number");
                    }
                });
            } catch (IOException | InterruptedException e) {
                Platform.runLater(() -> errorLabel.setText("Unable to connect to server"));
            }
        }).start();
    }

}