package org.example.snappfoodfront.controller;

import javafx.application.Platform;
import javafx.fxml.Initializable;
import org.example.snappfoodfront.Service.RestaurantApiService;
import org.example.snappfoodfront.Utils.TokenManager;
import org.example.snappfoodfront.model.ErrorResponseDto;
import org.example.snappfoodfront.model.RestaurantDto;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class SellerMainController implements Initializable {

    private final RestaurantApiService restaurantApiService;
    private final String token;

    public SellerMainController() {
        this.restaurantApiService = new RestaurantApiService();
        this.token = TokenManager.getToken();
    }

    public void initialize(URL location, ResourceBundle resources) {

        new Thread(() -> {
            try {


                List<RestaurantDto.Response> restaurants = restaurantApiService.getMyRestaurants(token);

                Platform.runLater(() -> {
                    if (!restaurants.isEmpty()) {
                        // show the restaurants in the center pane
                    } else {
                        // showing a add restaurant button and direct to restaurant creation page upon clicking
                    }
                });

            } catch (RestaurantApiService.RestaurantException e) {

                // creating a label for the error and setting that label text to the error
                ErrorResponseDto error = e.getErrorResponseDto();
                // errorLabel.setText(error);

            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Error in getting restaurant list");
                // errorLabel.setText("Failed to load restaurants");
            }

        }

        ).start();
    }
}
