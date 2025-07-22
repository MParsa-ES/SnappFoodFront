package org.example.snappfoodfront.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.example.snappfoodfront.Service.RestaurantApiService;
import org.example.snappfoodfront.Utils.TokenManager;
import org.example.snappfoodfront.model.BuyerDto;
import org.example.snappfoodfront.model.FoodItemDto;
import org.example.snappfoodfront.model.MenuDto;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class MenuManagementController {

    @FXML private VBox availableFoodsContainer;
    @FXML private VBox menuCategoriesContainer;
    @FXML private TextField newCategoryTextField;
    @FXML private Button addCategoryButton;

    private Long restaurantId;
    private final RestaurantApiService restaurantApiService = new RestaurantApiService();

    public void initData(Long restaurantId) {
        this.restaurantId = restaurantId;
        loadAllData();
    }

    private void loadAllData() {
        new Thread(() -> {
            try {
                List<FoodItemDto.Response> allFoods = restaurantApiService.getAllFoodItems(TokenManager.getToken(), restaurantId);
                BuyerDto.ItemList allMenusWithItems = restaurantApiService.getMenusWithItems(TokenManager.getToken(), restaurantId);

                Platform.runLater(() -> {
                    populateAvailableFoods(allFoods);
                    populateMenuCategories(allMenusWithItems);
                });
            } catch (Exception e) {
                e.printStackTrace();
                // TODO: Show an error message on the UI
            }
        }).start();
    }

    private void populateAvailableFoods(List<FoodItemDto.Response> foods) {
        availableFoodsContainer.getChildren().clear();
        for (FoodItemDto.Response food : foods) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SellerViews/food-chip-view.fxml"));
                Node foodChip = loader.load();
                FoodChipController controller = loader.getController();
                controller.setData(food);
                availableFoodsContainer.getChildren().add(foodChip);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void populateMenuCategories(BuyerDto.ItemList allMenus) {
        menuCategoriesContainer.getChildren().clear();
        List<String> titles = allMenus.getMenu_titles();
        Map<String, List<FoodItemDto.Response>> itemsMap = allMenus.getMenu_title();

        for (String title : titles) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SellerViews/menu-category-view.fxml"));
                Node menuCategoryNode = loader.load();
                MenuCategoryController controller = loader.getController();
                List<FoodItemDto.Response> itemsForThisMenu = itemsMap.get(title);

                controller.setData(restaurantId, title, itemsForThisMenu, this::loadAllData);

                menuCategoriesContainer.getChildren().add(menuCategoryNode);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void handleAddMenuButton() {
        String newTitle = newCategoryTextField.getText();
        if (newTitle == null || newTitle.isBlank()) return;

        new Thread(() -> {
            try {
                // For adding a menu, the title is in the request body, so no encoding is needed here.
                MenuDto.Request menuRequest = new MenuDto.Request(newTitle);
                restaurantApiService.addMenu(TokenManager.getToken(), restaurantId, menuRequest);
                Platform.runLater(() -> {
                    newCategoryTextField.clear();
                    loadAllData(); // Refresh everything
                });
            } catch (Exception e) {
                e.printStackTrace();
                // TODO: Show error feedback
            }
        }).start();
    }
}