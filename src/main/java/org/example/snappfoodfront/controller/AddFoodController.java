package org.example.snappfoodfront.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
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
import java.util.Base64;


public class AddFoodController {

    // --- FXML Injections for Form Fields ---
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
    private FlowPane keywordsFlowPane;

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

        Circle clip = new Circle(40, 40, 40);
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
            }
        }
    }

    @FXML
    void handleSave(ActionEvent event) {

        FoodItemDto.Request foodItemDto = new FoodItemDto.Request();

        foodItemDto.setName(nameField.getText());
        foodItemDto.setPrice(Integer.parseInt(priceField.getText()));
        foodItemDto.setSupply(Integer.parseInt(supplyField.getText()));
        foodItemDto.setDescription(descriptionArea.getText());
        foodItemDto.setImageBase64(ImageBase64);


        try {
            restaurantApiService.addFoodItem(TokenManager.getToken(), restaurantId, foodItemDto);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error while saving food item:" + e.getMessage());
        }
    }


    @FXML
    void handleCancel(ActionEvent event) {
        Stage currentStage = (Stage) event.getSource();
        currentStage.close();
    }
}