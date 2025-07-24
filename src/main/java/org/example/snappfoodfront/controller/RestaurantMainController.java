package org.example.snappfoodfront.controller;

import com.jfoenix.controls.JFXButton;
import com.sun.tools.javac.Main;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.*;
import javafx.scene.shape.Circle;
import org.example.snappfoodfront.Service.OrderApiService;
import org.example.snappfoodfront.Service.RestaurantApiService;
import org.example.snappfoodfront.Utils.*;
import org.example.snappfoodfront.model.FoodItemDto;
import org.example.snappfoodfront.model.MenuDto;
import org.example.snappfoodfront.model.RestaurantDto;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class RestaurantMainController implements Initializable {


    @FXML public Label walletLabel;
    @FXML public JFXButton goBackButton;
    @FXML public JFXButton logoutButton;
    @FXML public ImageView logoImageView;
    @FXML public Circle statusIndicatorCircle;
    @FXML public Label restaurantNameLabel;
    @FXML public Label addressLabel;
    @FXML public Label phoneLabel;
    @FXML public HBox menuContainer;
    @FXML public VBox foodContainer;
    @FXML public JFXButton favoriteButton;

    private static final String CUSTOMER_MAIN_VIEW_PATH = "/view/customer-main-view.fxml";
    private static final String FOOD_MAIN_VIEW_PATH = "/view/food-main-view.fxml";

    private RestaurantDto.Response restaurant;
    private boolean isFavorite;

    private final RestaurantApiService restaurantService = new RestaurantApiService();
    private final OrderApiService orderService = new OrderApiService();

    public class MenuButton extends JFXButton {

        private final BooleanProperty selected = new SimpleBooleanProperty(false);

        RadialGradient orangeToPink = new RadialGradient(
                0, 0, 0.5, 0.5, 0.5, true, CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.web("#ff00f6")),
                new Stop(1.0, Color.WHITE));

        RadialGradient pinkToOrange = new RadialGradient(
                0, 0, 0.5, 0.5, 0.5, true, CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.web("#ff7600")),
                new Stop(1.0, Color.WHITE));

        private final String orangeStyle = "-fx-background-radius: 5; -fx-background-color: #ff7600;";
        private final String pinkStyle = "-fx-background-radius: 5; -fx-background-color: #ff00f6;";

        public MenuButton(String text) {
            super(text);

            this.setButtonType(JFXButton.ButtonType.RAISED);
            this.setCursor(Cursor.HAND);
            this.setTextFill(Color.WHITE);
            this.setStyle(
                    "-fx-background-radius: 5;" +
                    "-fx-background-color:  #ff7600"
            );

            this.setRipplerFill(orangeToPink);

            selected.addListener((obs, oldVal, isSelected) -> {
                if (isSelected) {
                    this.setStyle(pinkStyle);
                    this.setRipplerFill(pinkToOrange);
                } else {
                    this.setStyle(orangeStyle);
                    this.setRipplerFill(orangeToPink);
                }
            });

        }

        public final void setSelected(boolean selected) { this.selected.set(selected); }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        setWalletBalance();
        restaurant = MainViewState.getSelectedRestaurant();
        try {
            loadRestaurantDetails(restaurant);
            loadRestaurantMenus(restaurant.getId());
        } catch (IOException | RestaurantApiService.RestaurantException | InterruptedException e) {
            e.printStackTrace();

        }

    }

    protected void loadRestaurantDetails(RestaurantDto.Response restaurant) throws IOException, RestaurantApiService.RestaurantException {

        isFavorite = MainViewState.isCameFromFavorites();
        MainViewState.setCameFromFavorites(false);
        Platform.runLater(() -> {
            restaurantNameLabel.setText(restaurant.getName());
            addressLabel.setText(restaurant.getAddress());
            phoneLabel.setText(restaurant.getPhone());

            Image logo = Methods.convertToImage(restaurant.getLogoBase64());
            logoImageView.setImage(logo);

            if (isFavorite) {
                favoriteButton.setStyle("-fx-background-color: #ff0000;");
                favoriteButton.setText("Remove from Favorites");
            } else {
                favoriteButton.setStyle("-fx-background-color: #00ff05;");
                favoriteButton.setText("Add to Favorites");
            }

        });
    }

    protected void loadRestaurantMenus(Long restaurantId) throws IOException, RestaurantApiService.RestaurantException, InterruptedException {

        new Thread(() -> {

            try {
                List<MenuDto.Response> menuList = restaurantService.getRestaurantMenus(restaurantId);
                List<MenuButton> menuButtons = new ArrayList<>();

                for (MenuDto.Response menu : menuList) {
                    MenuButton menuButton = new MenuButton(menu.getTitle());
                    menuButtons.add(menuButton);
                    final MenuDto.Response currentMenu = menu;
                    menuButton.setOnAction(event -> {

                        for (MenuButton btn : menuButtons) {
                            if (btn != menuButton) {
                                btn.setSelected(false);
                            }
                        }

                        menuButton.setSelected(true);
                        try {
                            loadMenuItems(currentMenu.getId());
                        } catch (IOException | RestaurantApiService.RestaurantException | InterruptedException e) {
                            e.printStackTrace();
                        }

                    });
                }

                if(menuList.isEmpty()) {
                    Platform.runLater(() -> {
                        menuContainer.getChildren().clear();
                        menuContainer.setMinWidth(1020);
                        Label errorLabel = new Label("No restaurant menus found");
                        errorLabel.setStyle("-fx-text-fill: red;");
                        menuContainer.getChildren().add(errorLabel);
                    });
                } else {
                    Platform.runLater(() -> {
                        menuContainer.getChildren().clear();
                        menuContainer.getChildren().addAll(menuButtons);
                    });
                }
            } catch (IOException | RestaurantApiService.RestaurantException | InterruptedException e) {
                e.printStackTrace();
            }

        }).start();

    }

    @FXML
    protected void toggleFavorite(ActionEvent event) throws IOException, RestaurantApiService.RestaurantException, InterruptedException {

        String token = TokenManager.getToken();

        Platform.runLater(() -> {
            if (!isFavorite) {
                favoriteButton.setStyle("-fx-background-color: #ff0000;");
                favoriteButton.setText("Remove from favorites");
                isFavorite = true;
                try {
                    restaurantService.addFavoriteRestaurant(token, restaurant.getId());
                } catch (IOException | RestaurantApiService.RestaurantException | InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                favoriteButton.setStyle("-fx-background-color: #00ff05;");
                favoriteButton.setText("Add to favorites");
                isFavorite = false;
                try {
                    restaurantService.removeFavoriteRestaurant(token, restaurant.getId());
                    MainViewState.favoriteRestaurantIds.remove(restaurant.getId());
                } catch (IOException | RestaurantApiService.RestaurantException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void loadMenuItems(Long menuId) throws IOException, RestaurantApiService.RestaurantException, InterruptedException {

        new Thread(() -> {

            try{

                final List<FoodItemDto.Response> itemList = restaurantService.getMenuItems(menuId);

                Platform.runLater(() -> {

                    foodContainer.getChildren().clear();

                    if (itemList.isEmpty()) {
                        Label errorLabel = new Label("No items found");
                        errorLabel.setStyle("-fx-text-fill: red;");
                        foodContainer.getChildren().add(errorLabel);
                    } else {
                        listItems(itemList, foodContainer);
                    }

                });

            } catch (IOException | RestaurantApiService.RestaurantException | InterruptedException e) {
                e.printStackTrace();
            }

        }).start();

    }

    private void listItems(List<FoodItemDto.Response> itemList, VBox foodContainer) {

        Platform.runLater(() -> {
            for (FoodItemDto.Response item : itemList) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/food-card.fxml"));
                    Node foodCardNode = loader.load();
                    final FoodItemDto.Response currentItem = item;

                    FoodCardController cardController = loader.getController();
                    cardController.setData(currentItem);

                    foodContainer.getChildren().add(foodCardNode);
                    foodCardNode.setOnMouseClicked(event -> {
                        foodItemClicked(currentItem);
                    });
                } catch (IOException e) {
                    System.err.println("error while loading restaurant's card" + e.getMessage());
                    e.printStackTrace();
                }
            }
        });

    }

    private void foodItemClicked(FoodItemDto.Response foodItem) {

        MainViewState.selectedFoodItem = foodItem;
        MainViewState.cameFromFavorites = isFavorite;
        SceneManager.closeCurrentStage(foodContainer);
        SceneManager.showWindow(FOOD_MAIN_VIEW_PATH, "SnappFood", foodItem.getName(), 1024, 720);
    }

    private void setWalletBalance() {
        try {
            CartManager.setWalletBalance(orderService.getWalletBalance(TokenManager.getToken()));
            Platform.runLater(() -> {
                walletLabel.setText("Wallet Balance: " + CartManager.getWalletBalance() + " T");
            });
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            Platform.runLater(() -> walletLabel.setText(e.getMessage()));
        }
    }

    @FXML
    protected void goBack(ActionEvent event) throws IOException {
        SceneManager.closeCurrentStage(goBackButton);
        SceneManager.showWindow(CUSTOMER_MAIN_VIEW_PATH, "SnappFood", "dashboard", 1024, 720);
    }

    @FXML
    protected void logout(ActionEvent event) throws IOException {
        SceneManager.logout(logoutButton);
    }

}
