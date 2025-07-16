package org.example.snappfoodfront.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.paint.Color;
import org.example.snappfoodfront.Service.RestaurantApiService;
import org.example.snappfoodfront.Utils.TokenManager;
import org.example.snappfoodfront.model.ErrorResponseDto;
import org.example.snappfoodfront.model.RestaurantDto;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class SellerMainController implements Initializable {

    private final RestaurantApiService restaurantApiService;
    private final String token;


    @FXML
    private ScrollPane contentScrollPane;




    public SellerMainController() {
        this.restaurantApiService = new RestaurantApiService();
        this.token = TokenManager.getToken();
    }

    public void initialize(URL location, ResourceBundle resources) {

        contentScrollPane.setContent(new ProgressBar());

        new Thread(() -> {
            try {

                List<RestaurantDto.Response> restaurants = restaurantApiService.getMyRestaurants(token);

                Platform.runLater(() -> {
                    if (!restaurants.isEmpty()) {
                        // show the restaurants in the center pane
                        System.out.println("Restaurants found");
                    } else {

                        Node emptyNode = createEmptyStateView();
                        contentScrollPane.setContent(emptyNode);

                    }
                });

            } catch (RestaurantApiService.RestaurantException e) {

                Platform.runLater(() -> {

                    Node errorNode = createErrorNode(location, resources, e.getErrorResponseDto());
                    contentScrollPane.setContent(errorNode);

                    });
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Error in getting restaurant list");
                // errorLabel.setText("Failed to load restaurants");
            }

        }

        ).start();
    }

    private Node createEmptyStateView() {
        try {
            // Create an FXMLLoader to load your component FXML
            FXMLLoader loader = new FXMLLoader(SellerMainController.class.getResource("/view/SellerViews/empty-state-view.fxml"));
            Node emptyStateNode = loader.load();


            Button createButton = (Button) loader.getNamespace().get("actionButton");


            createButton.setOnAction(event -> {
                System.out.println("Go to create restaurant page...");
                // SceneManager.switchScene(...)
            });

            return emptyStateNode;
        } catch (IOException e) {
            e.printStackTrace();
            // Return a simple error label if the FXML can't be loaded
            System.err.println("Error in creating empty state view");
            return new Label("Error: Could not load UI component.");
        }
    }

    private Node createErrorNode(URL location, ResourceBundle resources, ErrorResponseDto errorResponseDto) {
        try {
            // Create an FXMLLoader to load your component FXML
            FXMLLoader loader = new FXMLLoader(SellerMainController.class.getResource("/view/SellerViews/empty-state-view.fxml"));
            Node errorNode = loader.load();


            Button retryButton = (Button) loader.getNamespace().get("actionButton");
            retryButton.setText("Retry");



            Label errorLabel = (Label) loader.getNamespace().get("messageLabel");

            errorLabel.setText(errorResponseDto.getError());
            errorLabel.setTextFill(new Color(0,0,0,1));


            retryButton.setOnAction(event -> {
                initialize(location, resources);
            });

            return errorNode;
        } catch (IOException e) {
            e.printStackTrace();
            // Return a simple error label if the FXML can't be loaded
            System.err.println("Error in creating error state view");
            return new Label("Error: Could not load UI component.");
        }
    }


}
