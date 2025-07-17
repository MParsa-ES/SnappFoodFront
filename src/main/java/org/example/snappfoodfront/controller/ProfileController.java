package org.example.snappfoodfront.controller;


import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

public class ProfileController {

    public Button imageSelect;
    public TextArea addressField;
    public JFXButton confirmButton;
    public Label imageLabel;


    private String profileImageBase64;

    @FXML
    protected void handleImageSelect() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Image");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(imageSelect.getScene().getWindow());

        if (selectedFile != null) {
            imageLabel.setText(selectedFile.getName());

            try {
                byte[] fileContent = Files.readAllBytes(selectedFile.toPath());
                profileImageBase64 = Base64.getEncoder().encodeToString(fileContent);
            } catch (IOException e) {
                imageLabel.setText("Error loading image");
                e.printStackTrace();
            }
        }
    }

}
