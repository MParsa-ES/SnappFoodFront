package org.example.snappfoodfront.controller;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.example.snappfoodfront.Utils.SceneManager;
import org.example.snappfoodfront.model.RestaurantDto;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

public class RestaurantCardController {

    @FXML
    private Label addressLabel;

    @FXML
    private ImageView logoImageView;

    @FXML
    private Label phoneLabel;

    @FXML
    private Label restaurantNameLabel;

    @FXML
    private Circle statusIndicatorCircle;

    private RestaurantDto.Response restaurant;


    public void setData(RestaurantDto.Response response) {

        restaurantNameLabel.setText(response.getName());
        phoneLabel.setText(response.getPhone());
        addressLabel.setText(response.getAddress());

        statusIndicatorCircle.setFill(Color.GREEN);

        byte[] imageBytes = Base64.getDecoder().decode(response.getLogoBase64());
        logoImageView.setImage(new Image(new ByteArrayInputStream(imageBytes)));

        Circle clip = new Circle(60, 60, 60);
        logoImageView.setClip(clip);


        this.restaurant = response;


    }


    @FXML
    void handleEditButton(ActionEvent event) {

        if (restaurant == null) {
            return;
        }

        RestaurantCreationController.setRestaurantToEdit(this.restaurant);

        try {
            SceneManager.switchScene(event, "SellerViews/create-restaurant-view.fxml", 700, 550);
        } catch (IOException e){
            e.printStackTrace();
            System.err.println("Error opening the edit menu");
        }


    }

    @FXML
    void handleMenuButton(ActionEvent event) {

        try {
            MenuManagementController controller = SceneManager.switchScene(event, "SellerViews/menu-management-view.fxml", 1050, 720);
            controller.initData(restaurant.getId());
        } catch (IOException e){
            e.printStackTrace();
            System.err.println("Error opening the menu management view");
        }

    }

    @FXML
    void handleFoodManagementButton(ActionEvent event) {

        try {
            FoodLibraryController controller = SceneManager.switchScene(event, "SellerViews/food-library.fxml",1050,720);
            controller.initData(restaurant.getId());
        } catch (IOException e){
            e.printStackTrace();
            System.err.println("Error opening the food management view");
        }

    }

    @FXML
    void handleOrdersButton(ActionEvent event) {
        // TODO : add the handle orders view
        try {
            OrderManagementController controller = SceneManager.switchScene(event, "SellerViews/order-management-view.fxml",1050,720);
            controller.initData(restaurant.getId());
        } catch (IOException e){
            e.printStackTrace();
            System.err.println("Error opening the order management view");
        }
    }



}
