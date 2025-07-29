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
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;


public class AddFoodController {

    @FXML private TextField nameField;
    @FXML private TextField priceField;
    @FXML private TextField supplyField;
    @FXML private TextArea descriptionArea;
    @FXML private JFXButton chooseImageButton;
    @FXML private ImageView foodImageView;
    @FXML private GridPane keywordsGridPane;
    @FXML private HBox feedbackBox;
    @FXML private Label feedbackLabel;
    @FXML private JFXButton cancelButton;
    @FXML private Label titleLabel;
    @FXML private JFXButton saveButton;


    private final RestaurantApiService restaurantApiService = new RestaurantApiService();

    @Setter
    private Long restaurantId;

    private String ImageBase64;

    private FoodItemDto.Response foodToEdit = null;


    public void initForEdit(FoodItemDto.Response foodToEdit, Long restaurantId) {
        this.foodToEdit = foodToEdit;
        this.restaurantId = restaurantId;


        nameField.setText(foodToEdit.getName());
        priceField.setText(String.valueOf(foodToEdit.getPrice()));
        supplyField.setText(String.valueOf(foodToEdit.getSupply()));
        descriptionArea.setText(foodToEdit.getDescription());

        byte[] imageBytes = Base64.getDecoder().decode(foodToEdit.getImageBase64());
        foodImageView.setImage(new Image(new ByteArrayInputStream(imageBytes)));

        saveButton.setText("Save Changes");
        titleLabel.setText("Edit Food");


        Set<String> keywords = foodToEdit.getKeywords();
        for (Node node : keywordsGridPane.getChildren()) {
            if (node instanceof JFXCheckBox checkBox && keywords.contains(checkBox.getText())) {
                checkBox.setSelected(true);
            }
        }

    }


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

        if (foodToEdit == null) {
            try {
                ImageBase64 = loadDefaultLogoBase64();
            } catch (IOException e) {
                e.printStackTrace();
                ImageBase64 = "";
                System.err.println("Failed to load default food picture");
            }
        }

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
        foodItemDto.setKeywords(keywords);

        if (this.ImageBase64 == null && this.foodToEdit != null) {
            foodItemDto.setImageBase64(foodToEdit.getImageBase64());
        } else {
            foodItemDto.setImageBase64(this.ImageBase64);
        }

        new Thread(() -> {
            try {
                if (foodToEdit == null) {
                    restaurantApiService.addFoodItem(TokenManager.getToken(), restaurantId, foodItemDto);
                    Platform.runLater(() -> {
                        showFeedback("Successfully added food item.", false);
                        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        currentStage.close();
                    });
                } else {
                    restaurantApiService.editFoodItem(TokenManager.getToken(), restaurantId, foodToEdit.getId(), foodItemDto);
                    Platform.runLater(() -> {
                        showFeedback("Successfully edited food item.", false);
                        foodToEdit = null;
                        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        currentStage.close();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Error saving or editing food item :" + e.getMessage());
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

    private String loadDefaultLogoBase64() throws IOException {

        String defaultImagePath = "/images/default-food.jpg";

        try (InputStream inputStream = getClass().getResourceAsStream(defaultImagePath)) {

            if (inputStream == null) {
                throw new IOException("Cannot find default avatar image at path: " + defaultImagePath);
            }

            byte[] fileContent = inputStream.readAllBytes();
            foodImageView.setImage(new Image(new ByteArrayInputStream(fileContent)));
            return Base64.getEncoder().encodeToString(fileContent);
        }
    }
}