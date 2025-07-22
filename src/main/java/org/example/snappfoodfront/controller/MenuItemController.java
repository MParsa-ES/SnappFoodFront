package org.example.snappfoodfront.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.example.snappfoodfront.Service.RestaurantApiService;
import org.example.snappfoodfront.Utils.TokenManager;
import org.example.snappfoodfront.model.FoodItemDto;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class MenuItemController {

    @FXML private Label itemNameLabel;
    @FXML private Button removeButton;

    private Long restaurantId;
    private String menuTitle;
    private FoodItemDto.Response foodItem;
    private Runnable refreshCallback;
    private final RestaurantApiService restaurantApiService = new RestaurantApiService();

    public void setData(Long restaurantId, String menuTitle, FoodItemDto.Response foodItem, Runnable refreshCallback) {
        this.restaurantId = restaurantId;
        this.menuTitle = menuTitle;
        this.foodItem = foodItem;
        this.refreshCallback = refreshCallback;
        itemNameLabel.setText(foodItem.getName());
    }

    @FXML
    void handleRemoveItem(ActionEvent event) {
        new Thread(() -> {
            try {
                // FIX: URL-encode the title and replace '+' with '%20'.
                String encodedMenuTitle = URLEncoder.encode(menuTitle, StandardCharsets.UTF_8).replace("+", "%20");
                restaurantApiService.deleteFoodFromMenu(TokenManager.getToken(), restaurantId, encodedMenuTitle, foodItem.getId());
                Platform.runLater(refreshCallback);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
