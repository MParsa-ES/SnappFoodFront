package org.example.snappfoodfront.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import org.example.snappfoodfront.Utils.Methods;
import org.example.snappfoodfront.model.RestaurantDto;

public class RestaurantMainCardController {

    @FXML public ImageView logoImageView;
    @FXML public Circle statusIndicatorCircle;
    @FXML public Label restaurantNameLabel;
    @FXML public Label addressLabel;
    @FXML public Label phoneLabel;


    public void setData(RestaurantDto.Response restaurant) {
        restaurantNameLabel.setText(restaurant.getName());
        addressLabel.setText(restaurant.getAddress());
        phoneLabel.setText(restaurant.getPhone());

        if(restaurant.getLogoBase64() != null && !restaurant.getLogoBase64().isEmpty()) {
            Image logoImage = Methods.convertToImage(restaurant.getLogoBase64());
            logoImageView.setImage(logoImage);
        }

    }

}
