package org.example.snappfoodfront.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.example.snappfoodfront.Utils.SceneManager;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SignUpController implements Initializable {

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // اضافه کردن گزینه‌ها به لیست ComboBox
        roleBox.getItems().addAll(
                "BUYER",
                "SELLER",
                "COURIER"
        );

    }

    @FXML
    private void handleLoginLink(ActionEvent event) {
        try {
            SceneManager.switchScene(event, "login-view.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
