package org.example.snappfoodfront.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.snappfoodfront.Service.RestaurantApiService;
import org.example.snappfoodfront.Utils.SceneManager;
import org.example.snappfoodfront.Utils.TokenManager;
import org.example.snappfoodfront.model.BuyerDto;
import org.example.snappfoodfront.model.FoodItemDto;
import org.example.snappfoodfront.model.MenuDto;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MenuManagementController {

    @FXML
    private VBox availableFoodsContainer;

    @FXML
    private VBox menuCategoriesContainer;

    @FXML
    private TextField newMenuTextField;

    private Long restaurantId;
    private final RestaurantApiService restaurantApiService = new RestaurantApiService();


    private List<FoodItemDto.Response> allFoodsCache;
    private BuyerDto.ItemList allMenusWithItemsCache;

    public void initData(Long restaurantId) {
        this.restaurantId = restaurantId;
        loadAllData();
    }


    private void loadAllData() {
        new Thread(() -> {
            try {
                this.allFoodsCache = restaurantApiService.getAllFoodItems(TokenManager.getToken(), restaurantId);
                this.allMenusWithItemsCache = restaurantApiService.getMenusWithItems(TokenManager.getToken(), restaurantId);

                Platform.runLater(() -> {
                    populateAvailableFoods();
                    populateMenuCategories();
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }


    private void populateAvailableFoods() {
        availableFoodsContainer.getChildren().clear();
        if (allFoodsCache == null) return;
        for (FoodItemDto.Response food : allFoodsCache) {
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

    private void populateMenuCategories() {
        menuCategoriesContainer.getChildren().clear();
        if (allMenusWithItemsCache == null) return;
        List<String> titles = allMenusWithItemsCache.getMenu_titles();
        Map<String, List<FoodItemDto.Response>> itemsMap = allMenusWithItemsCache.getMenu_title();

        if (titles != null) {
            Collections.sort(titles);
        }

        for (String title : titles) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SellerViews/menu-category-view.fxml"));
                Node menuCategoryNode = loader.load();
                MenuCategoryController controller = loader.getController();
                List<FoodItemDto.Response> itemsForThisMenu = itemsMap.get(title);


                controller.setData(this, restaurantId, title, itemsForThisMenu);
                menuCategoriesContainer.getChildren().add(menuCategoryNode);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void refreshDataAndUI() {
        loadAllData();
    }

    @FXML
    void handleAddMenuButton() {
        String newTitle = newMenuTextField.getText();
        if (newTitle == null || newTitle.isBlank()) return;

        new Thread(() -> {
            try {
                String encodedTitle = URLEncoder.encode(newTitle, StandardCharsets.UTF_8).replace("+", "%20");
                MenuDto.Request menuRequest = new MenuDto.Request(encodedTitle);
                restaurantApiService.addMenu(TokenManager.getToken(), restaurantId, menuRequest);
                Platform.runLater(this::loadAllData);
                Platform.runLater(newMenuTextField::clear);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    void handleBackButton(ActionEvent event) {
        try {
            SceneManager.switchScene(event, "SellerViews/seller-main-view.fxml", 1024, 720);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading seller view");
        }
    }

    @FXML
    void handleAddFoodButton(ActionEvent event) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SellerViews/food-creation-view.fxml"));
            Parent root = loader.load();


            AddFoodController addFoodController = loader.getController();


            addFoodController.setRestaurantId(this.restaurantId);


            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add New Food Item");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(root));
            dialogStage.showAndWait();

            loadAllData();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
