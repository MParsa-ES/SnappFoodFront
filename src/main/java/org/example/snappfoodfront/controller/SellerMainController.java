package org.example.snappfoodfront.controller;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.example.snappfoodfront.Service.RestaurantApiService;
import org.example.snappfoodfront.Utils.SceneManager;
import org.example.snappfoodfront.Utils.TokenManager;
import org.example.snappfoodfront.model.RestaurantDto;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class SellerMainController implements Initializable {

    private static final String PROFILE_VIEW_PATH = "/view/profile-view.fxml";

    private final RestaurantApiService restaurantApiService;
    private final String token;

    @FXML public JFXButton logoutButton;
    @FXML public JFXButton profileButton;
    @FXML private ScrollPane contentScrollPane;


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


                        VBox restaurantContainer = new VBox(20);
                        restaurantContainer.setPadding(new Insets(20));
                        restaurantContainer.setAlignment(Pos.TOP_LEFT);

                        HBox headerBox = new HBox(10);
                        headerBox.setAlignment(Pos.CENTER);

                        FontIcon headerIcon = new FontIcon("fas-store-alt");
                        headerIcon.setIconSize(25);
                        headerIcon.setIconColor(Color.web("#2c3e50"));

                        Label headerLabel = new Label("My Restaurant");
                        headerLabel.getStyleClass().add("dashboard-header");

                        headerBox.getChildren().addAll(headerIcon, headerLabel);

                        restaurantContainer.getChildren().add(headerBox);

                        for (RestaurantDto.Response restaurant : restaurants) {

                            try {
                                FXMLLoader loader = new FXMLLoader(SellerMainController.class.getResource("/view/SellerViews/restaurant-card.fxml"));
                                Node restaurantCard = loader.load();

                                RestaurantCardController controller = loader.getController();
                                controller.setData(restaurant);

                                restaurantContainer.getChildren().add(restaurantCard);

                            } catch (IOException e){
                                e.printStackTrace();
                                System.err.println("Error loading restaurant card");
                            }

                        }

                        contentScrollPane.setContent(restaurantContainer);


                    } else {

                        Node emptyNode = createEmptyStateView();
                        contentScrollPane.setContent(emptyNode);

                    }
                });

            } catch (RestaurantApiService.RestaurantException e) {

                Platform.runLater(() -> {

                    Node errorView = createErrorView(e.getErrorResponseDto().getError(), location, resources);
                    contentScrollPane.setContent(errorView);

                });
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Error in getting restaurant list");
            }

        }

        ).start();
    }

    private Node createEmptyStateView() {
        try {

            FXMLLoader loader = new FXMLLoader(SellerMainController.class.getResource("/view/SellerViews/empty-state-view.fxml"));
            Node emptyStateNode = loader.load();


            Button createButton = (Button) loader.getNamespace().get("actionButton");


            createButton.setOnAction(event -> {
                try {
                    SceneManager.switchScene(event, "SellerViews/create-restaurant-view.fxml",700,490);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.err.println("Error in switching to creating restaurant view");
                }
            });

            return emptyStateNode;
        } catch (IOException e) {
            e.printStackTrace();
            // Return a simple error label if the FXML can't be loaded
            System.err.println("Error in creating empty state view");
            return new Label("Error: Could not load UI component.");
        }
    }

    private Node createErrorView(String errorMessage, URL location, ResourceBundle resources) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SellerViews/error-state-view.fxml"));
            Node errorNode = loader.load();


            Label messageLabel = (Label) loader.getNamespace().get("errorMessageLabel");
            Button actionButton = (Button) loader.getNamespace().get("errorActionButton");


            messageLabel.setText(errorMessage);
            actionButton.setText("Retry");
            actionButton.setOnAction(event -> initialize(location, resources));

            return errorNode;
        } catch (IOException e) {
            e.printStackTrace();
            return new Label("Fatal Error: Could not load error screen.");
        }
    }

    @FXML
    protected void goToProfile(ActionEvent event) throws IOException {
        SceneManager.closeCurrentStage(profileButton);
        SceneManager.showWindow(PROFILE_VIEW_PATH, "Profile", "profile", 1050, 720);
    }

    @FXML
    protected void logout(ActionEvent event) throws IOException {
        SceneManager.logout(logoutButton);
    }

}
