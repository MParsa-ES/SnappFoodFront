package org.example.snappfoodfront.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import org.example.snappfoodfront.Service.RestaurantApiService;
import org.example.snappfoodfront.Utils.TokenManager;
import org.example.snappfoodfront.model.FoodItemDto;
import org.example.snappfoodfront.model.MenuDto;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class MenuCategoryController {

    @FXML private VBox rootPane;
    @FXML private Label categoryNameLabel;
    @FXML private VBox itemsContainer;

    private MenuManagementController mainController;
    private Long restaurantId;
    private String menuTitle;
    private final RestaurantApiService restaurantApiService = new RestaurantApiService();


    public void setData(MenuManagementController mainController, Long restaurantId, String title, List<FoodItemDto.Response> items) {
        this.mainController = mainController;
        this.restaurantId = restaurantId;
        this.menuTitle = title;
        categoryNameLabel.setText(title);
        itemsContainer.getChildren().clear();
        if (items != null) {
            for (FoodItemDto.Response item : items) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SellerViews/menu-item-view.fxml"));
                    Node menuItemNode = loader.load();
                    MenuItemController controller = loader.getController();
                    controller.setData(mainController, restaurantId, menuTitle, item);
                    itemsContainer.getChildren().add(menuItemNode);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    public void initialize() {
        rootPane.setOnDragOver(event -> {
            if (event.getGestureSource() != rootPane && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });

        rootPane.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasString()) {
                Long foodItemId = Long.parseLong(db.getString());
                new Thread(() -> {
                    try {
                        String encodedMenuTitle = URLEncoder.encode(menuTitle, StandardCharsets.UTF_8).replace("+", "%20");
                        MenuDto.AddItemRequest addItemRequest = new MenuDto.AddItemRequest(foodItemId);
                        restaurantApiService.addFoodToMenu(TokenManager.getToken(), restaurantId, encodedMenuTitle, addItemRequest);
                        Platform.runLater(() -> mainController.refreshDataAndUI());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
                event.setDropCompleted(true);
            } else {
                event.setDropCompleted(false);
            }
            event.consume();
        });
    }

    @FXML
    void handleDeleteMenuButton() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete the '" + menuTitle + "' menu?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                new Thread(() -> {
                    try {
                        String encodedMenuTitle = URLEncoder.encode(menuTitle, StandardCharsets.UTF_8).replace("+", "%20");
                        restaurantApiService.deleteMenu(TokenManager.getToken(), restaurantId, encodedMenuTitle);
                        Platform.runLater(() -> mainController.refreshDataAndUI());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        });
    }
}
