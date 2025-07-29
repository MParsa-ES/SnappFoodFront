package org.example.snappfoodfront.controller;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.example.snappfoodfront.Service.OrderApiService;
import org.example.snappfoodfront.Service.ProfileApiService;
import org.example.snappfoodfront.Service.RatingApiService;
import org.example.snappfoodfront.Utils.*;
import org.example.snappfoodfront.model.FoodItemDto;
import org.example.snappfoodfront.model.RatingDTO;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;

public class FoodMainController implements Initializable {

    @FXML public Label walletLabel;
    @FXML public JFXButton goBackButton;
    @FXML public ImageView logoImageView;
    @FXML public Label foodNameLabel;
    @FXML public Label priceLabel;
    @FXML public Label supplyLabel;
    @FXML public Label descriptionLabel;
    @FXML public JFXButton addButton;

    @FXML Label ratingLabel;
    @FXML public VBox commentContainer;
    @FXML public JFXButton decreaseButton;
    @FXML public JFXButton increaseButton;
    @FXML public TextField numLabel;

    private FoodItemDto.Response foodItem;
    private int totalSupply;
    private int count = 0;

    private static final String RESTAURANT_MAIN_VIEW_PATH = "/view/restaurant-main-view.fxml";
    private static final String CART_VIEW_PATH = "/view/cart-view.fxml";

    private final OrderApiService orderService = new OrderApiService();
    private final RatingApiService ratingService = new RatingApiService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        foodItem = MainViewState.selectedFoodItem;
        loadItemDetails(foodItem);
        loadComments(foodItem.getId());
        setWalletBalance();

    }

    private void loadItemDetails(FoodItemDto.Response foodItem) {

        totalSupply = foodItem.getSupply();

        Platform.runLater(() -> {
            foodNameLabel.setText(foodItem.getName());
            priceLabel.setText(foodItem.getPrice() + " T");
            supplyLabel.setText(String.valueOf(foodItem.getSupply()));
            descriptionLabel.setText(foodItem.getDescription());
            ratingLabel.setText(String.valueOf(foodItem.getRating()));

            Image image = Methods.convertToImage(foodItem.getImageBase64());
            logoImageView.setImage(image);
        });

    }

    private void loadComments(Long itemId) {

        new Thread(() -> {
            try {
                RatingDTO.ItemRatings ratings = ratingService.getComments(TokenManager.getToken(), itemId);
                Set<RatingDTO.ItemRatings.Comments> comments = ratings.getComments();

                if (comments.isEmpty()) {
                    Label errorLabel = new Label("No Comments yet");
                    errorLabel.setStyle("-fx-text-fill: red;");
                    commentContainer.getChildren().add(errorLabel);
                } else {
                    listComments(comments, commentContainer);
                }

            } catch (RatingApiService.RatingException | IOException | InterruptedException e) {
                e.printStackTrace();
            }

        }).start();

    }

    private void listComments(Set<RatingDTO.ItemRatings.Comments> commentList, VBox commentContainer) {

        Platform.runLater(() -> {
            for (RatingDTO.ItemRatings.Comments comment : commentList) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/comment-card.fxml"));
                    Node foodCardNode = loader.load();

                    CommentCardController cardController = loader.getController();
                    cardController.setData(comment);

                    commentContainer.getChildren().add(foodCardNode);
                } catch (IOException e) {
                    System.err.println("error while loading restaurant's card" + e.getMessage());
                    e.printStackTrace();
                }
            }
        });

    }

    @FXML
    private void handleAddFood(ActionEvent event) {
        if (count < totalSupply) {
            count++;
            numLabel.setText(String.valueOf(count));
            if (CartManager.getItemQuantity(foodItem.getId()) == 0) {
                addButton.setText("Add to Cart");
                addButton.setStyle("-fx-background-color:  #ff7600;");
            } else {
                addButton.setText("Update Cart");
                addButton.setStyle("-fx-background-color:  #00ff0c;");
            }
        }
    }

    @FXML
    private void handleRemoveFood(ActionEvent event) {
        if (count > 0) {
            count--;
            numLabel.setText(String.valueOf(count));
            if (count == 0 && CartManager.getItemQuantity(foodItem.getId()) > 0) {
                addButton.setText("Remove from Cart");
                addButton.setStyle("-fx-background-color:  #ff0000;");
            }
        }
    }

    @FXML
    private void handleAddToCart(ActionEvent event) {
        try {
            CartManager.addItem(foodItem, count);

            Platform.runLater(() -> {
                if (count == 0) {
                    addButton.setText("Add to Cart");
                    addButton.setStyle("-fx-background-color:  #ff7600;");
                } else {
                    showSuccessAlert(foodItem.getName() + " " + "Successfully added to your cart");
                    addButton.setText("Update Cart");
                    addButton.setStyle("-fx-background-color:  #00ff0c;");
                }
            });
        } catch (CartManager.DifferentRestaurantException e) {
            showDifferentRestaurantAlert(e.getMessage());
        }
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

    private void showDifferentRestaurantAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Adding from a different restaurant");
        alert.setHeaderText(null);
        alert.setContentText(message);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            CartManager.clearCart();
            handleAddToCart(null);
        }
    }

    private void showSuccessAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleCartShow(ActionEvent event) {
        SceneManager.showWindow(CART_VIEW_PATH, "", "Cart", 400, 600);
    }

    @FXML
    private void goBack(ActionEvent event) {
        SceneManager.closeCurrentStage(goBackButton);
        SceneManager.showWindow(RESTAURANT_MAIN_VIEW_PATH, "SnappFood", "SnappFood", 1024, 720);
    }

}
