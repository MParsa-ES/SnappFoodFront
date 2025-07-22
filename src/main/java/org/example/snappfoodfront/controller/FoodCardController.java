package org.example.snappfoodfront.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.example.snappfoodfront.model.FoodItemDto;
import org.example.snappfoodfront.model.RestaurantDto;

import java.io.ByteArrayInputStream;
import java.util.Base64;

public class FoodCardController {

    @FXML public ImageView logoImageView;
    @FXML public Label foodNameLabel;
    @FXML public Label priceLabel;
    @FXML public Label supplyLabel;
    @FXML public Label ratingLabel;

    public void setData(FoodItemDto.Response response) {

        foodNameLabel.setText(response.getName());
        priceLabel.setText(String.valueOf(response.getPrice()));
        supplyLabel.setText(String.valueOf(response.getSupply()));
        ratingLabel.setText(String.valueOf(response.getRating()));

        byte[] imageBytes = Base64.getDecoder().decode(response.getImageBase64());
        logoImageView.setImage(new Image(new ByteArrayInputStream(imageBytes)));

        Circle clip = new Circle(60, 60, 60);
        logoImageView.setClip(clip);

    }

}
