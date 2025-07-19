package org.example.snappfoodfront.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.example.snappfoodfront.Service.RestaurantApiService;
import org.example.snappfoodfront.Utils.SceneManager;
import org.example.snappfoodfront.model.RestaurantDto;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CustomerMainController implements Initializable {

    private static final String PROFILE_VIEW_PATH = "/view/profile-view.fxml";

    private final RestaurantApiService restaurantService = new RestaurantApiService();

    @FXML public JFXButton profileButton;
    @FXML public JFXButton logoutButton;
    @FXML public Label messageLabel;
    @FXML public FontIcon messageIcon;
    @FXML public VBox restaurantContainer;
    @FXML public JFXHamburger hamburger;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        loadAllRestaurants();

    }

    private void loadAllRestaurants() {

        new Thread(() -> {
            try {

                final List<RestaurantDto.Response> restaurantList = restaurantService.getAllRestaurants();

                Platform.runLater(() -> {

                    restaurantContainer.getChildren().clear();

                    if(restaurantList.isEmpty()) {
                        messageLabel.setTextFill(Color.RED);
                        messageLabel.setFont(Font.font(18));
                        messageLabel.setText("No restaurants found");
                        messageIcon.setIconLiteral("fas-store-alt-slash");
                    }else {
                        messageLabel.setTextFill(Color.BLACK);
                        messageLabel.setFont(Font.font(14));
                        messageLabel.setText("Found " + restaurantList.size() + " restaurants");
                        messageIcon.setIconLiteral("fas-store-alt");
                    }

                    for (RestaurantDto.Response restaurant : restaurantList) {
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/restaurant-card.fxml"));
                            Node restaurantCardNode = loader.load();

                            RestaurantMainCardController cardController = loader.getController();
                            cardController.setData(restaurant);

                            restaurantContainer.getChildren().add(restaurantCardNode);

                        } catch (IOException e) {
                            System.err.println("error while loading restaurant's card" + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                });

            } catch (Exception e) {
                System.err.println("error while loading restaurants from server" + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    protected void goToProfile(ActionEvent event) throws IOException {
        SceneManager.closeCurrentStage(profileButton);
        SceneManager.showWindow(PROFILE_VIEW_PATH, "Profile", "profile", 1024, 720);
    }

    @FXML
    protected void logout(ActionEvent event) throws IOException {
        SceneManager.logout(logoutButton);
    }

}
