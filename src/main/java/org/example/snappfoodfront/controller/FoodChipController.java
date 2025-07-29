package org.example.snappfoodfront.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import org.example.snappfoodfront.model.FoodItemDto;

import java.io.ByteArrayInputStream;
import java.util.Base64;

public class FoodChipController {

    @FXML private HBox rootPane;
    @FXML private ImageView foodImageView;
    @FXML private Label foodNameLabel;

    private FoodItemDto.Response foodItem;

    public void setData(FoodItemDto.Response foodItem) {
        this.foodItem = foodItem;
        foodNameLabel.setText(foodItem.getName());

        if (foodItem.getImageBase64() != null && !foodItem.getImageBase64().isEmpty()) {
            byte[] imageBytes = Base64.getDecoder().decode(foodItem.getImageBase64());
            foodImageView.setImage(new Image(new ByteArrayInputStream(imageBytes)));
        }
    }

    @FXML
    public void initialize() {

        rootPane.setOnDragDetected(event -> {
            Dragboard db = rootPane.startDragAndDrop(TransferMode.COPY);
            ClipboardContent content = new ClipboardContent();

            content.putString(String.valueOf(foodItem.getId()));
            db.setContent(content);

            event.consume();
        });
    }
}