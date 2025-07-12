package org.example.snappfoodfront.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.snappfoodfront.Utils.SceneManager;

import java.io.IOException;

public class SignUpController {

    @FXML
    public TextField phoneField;

    @FXML
    public PasswordField passwordField;

    @FXML
    public ComboBox roleBox;

    @FXML
    public Button SignUpButton;

    @FXML
    public Label errorLabel;

    @FXML
    public Hyperlink LoginLink;


    @FXML
    private void handleLoginLink(ActionEvent event) {
        try {
            SceneManager.switchScene(event, "login-view.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
