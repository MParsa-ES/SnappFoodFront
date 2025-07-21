package org.example.snappfoodfront.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Setter;
import org.example.snappfoodfront.Service.RestaurantApiService;
import org.example.snappfoodfront.Utils.Methods;
import org.example.snappfoodfront.Utils.TokenManager;
import org.example.snappfoodfront.model.FoodItemDto;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;


public class AddFoodController {

    @FXML
    private TextField nameField;
    @FXML
    private TextField priceField;
    @FXML
    private TextField supplyField;
    @FXML
    private TextArea descriptionArea;

    @FXML
    private JFXButton chooseImageButton;
    @FXML
    private ImageView foodImageView;
    @FXML
    private GridPane keywordsGridPane;

    @FXML
    private HBox feedbackBox;

    @FXML
    private Label feedbackLabel;

    @FXML
    private JFXButton cancelButton;

    @FXML
    private JFXButton saveButton;


    private final RestaurantApiService restaurantApiService = new RestaurantApiService();

    @Setter
    private Long restaurantId;

    private String ImageBase64;


    @FXML
    public void initialize() {

        Methods.filterPhoneField(priceField);
        Methods.filterPhoneField(supplyField);

        Circle clip = new Circle();

        clip.centerXProperty().bind(foodImageView.fitWidthProperty().divide(2));
        clip.centerYProperty().bind(foodImageView.fitHeightProperty().divide(2));
        clip.radiusProperty().bind(foodImageView.fitWidthProperty().divide(2));
        foodImageView.setClip(clip);


        BooleanBinding formIsInvalid =
                nameField.textProperty().isEmpty()
                        .or(priceField.textProperty().isEmpty())
                        .or(supplyField.textProperty().isEmpty())
                        .or(descriptionArea.textProperty().isEmpty());

        saveButton.disableProperty().bind(formIsInvalid);
    }

    @FXML
    void handleChooseImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Food Image");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(chooseImageButton.getScene().getWindow());

        if (selectedFile != null) {
            try {
                byte[] fileContent = Files.readAllBytes(selectedFile.toPath());
                ImageBase64 = Base64.getEncoder().encodeToString(fileContent);
                foodImageView.setImage(new Image(new ByteArrayInputStream(fileContent)));
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Error choosing image" + e.getMessage());
                showFeedback(e.getMessage(), true);
            }
        }
    }

    @FXML
    void handleSave(ActionEvent event) {

        Set<String> keywords = new HashSet<>();

        for (Node node : keywordsGridPane.getChildren()) {
            if (node instanceof JFXCheckBox checkBox) {
                if (checkBox.isSelected()) {
                    keywords.add(checkBox.getText());
                }
            }
        }

        if (keywords.isEmpty()) {
            showFeedback("Please select at least one keyword.", true);
            return;
        }

        FoodItemDto.Request foodItemDto = new FoodItemDto.Request();

        foodItemDto.setName(nameField.getText());
        foodItemDto.setPrice(Integer.parseInt(priceField.getText()));
        foodItemDto.setSupply(Integer.parseInt(supplyField.getText()));
        foodItemDto.setDescription(descriptionArea.getText());
        foodItemDto.setImageBase64(ImageBase64);
        foodItemDto.setKeywords(keywords);


        new Thread(() -> {
            try {
                restaurantApiService.addFoodItem(TokenManager.getToken(), restaurantId, foodItemDto);
                Platform.runLater(() -> {
                    showFeedback("Successfully added food item.", false);
                    Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    currentStage.close();
                });
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Error saving food item :" + e.getMessage());
                Platform.runLater(() -> {
                    showFeedback(e.getMessage(), true);
                });
            }
        }).start();
    }


    @FXML
    void handleCancel(ActionEvent event) {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.close();
    }


    private void showFeedback(String message, boolean isError) {
        feedbackLabel.setText(message);
        feedbackBox.getStyleClass().removeAll("feedback-box-success", "feedback-box-error");
        feedbackBox.getStyleClass().add(isError ? "feedback-box-error" : "feedback-box-success");
        feedbackBox.setVisible(true);
        feedbackBox.setManaged(true);
    }
}