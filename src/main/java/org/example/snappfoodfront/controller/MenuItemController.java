package org.example.snappfoodfront.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.example.snappfoodfront.Service.RestaurantApiService;
import org.example.snappfoodfront.Utils.TokenManager;
import org.example.snappfoodfront.model.FoodItemDto;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class MenuItemController {

    @FXML private Label itemNameLabel;

    private MenuManagementController mainController;
    private Long restaurantId;
    private String menuTitle;
    private FoodItemDto.Response foodItem;
    private final RestaurantApiService restaurantApiService = new RestaurantApiService();

    public void setData(MenuManagementController mainController, Long restaurantId, String menuTitle, FoodItemDto.Response foodItem) {
        this.mainController = mainController;
        this.restaurantId = restaurantId;
        this.menuTitle = menuTitle;
        this.foodItem = foodItem;
        itemNameLabel.setText(foodItem.getName());
    }

    @FXML
    void handleRemoveItem(ActionEvent event) {
        new Thread(() -> {
            try {
                String encodedMenuTitle = URLEncoder.encode(menuTitle, StandardCharsets.UTF_8).replace("+", "%20");
                restaurantApiService.deleteFoodFromMenu(TokenManager.getToken(), restaurantId, encodedMenuTitle, foodItem.getId());
                Platform.runLater(() -> mainController.refreshDataAndUI());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
