package org.example.snappfoodfront.controller;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.snappfoodfront.Service.CourierApiService;
import org.example.snappfoodfront.Utils.SceneManager;
import org.example.snappfoodfront.Utils.TokenManager;
import org.example.snappfoodfront.model.OrderDto;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CourierDashboardController implements Initializable {


    @FXML
    private VBox availableDeliveriesContainer;

    @FXML
    private VBox myDeliveriesContainer;

    @FXML
    private JFXButton profileButton;

    @FXML
    private JFXButton logoutButton;



    @FXML
    private TextField searchFoodField;

    @FXML
    private TextField searchUserField;

    @FXML
    private TextField searchVendorField;

    @FXML
    private ToggleGroup deliveryStatusToggleGroup;

    @FXML
    private Label feedbackLabel;

    @FXML
    private HBox feedbackBox;

    private final CourierApiService courierApiService = new CourierApiService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadAvailableDeliveries();
        loadMyDeliveries();
        deliveryStatusToggleGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            loadMyDeliveries();
        });
    }


    private void loadAvailableDeliveries() {
        new Thread(() -> {
            try {
                List<OrderDto.OrderResponse> available = courierApiService.getAvailableDeliveries(TokenManager.getToken());
                Platform.runLater(() -> {
                    populateDeliveries(availableDeliveriesContainer, available);
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> showFeedback(e.getMessage(), true));
            }
        }).start();
    }

    private void loadMyDeliveries() {

        String foodFilter = searchFoodField.getText();
        String userFilter = searchUserField.getText();
        String vendorFilter = searchVendorField.getText();

        ToggleButton selectedToggle = (ToggleButton) deliveryStatusToggleGroup.getSelectedToggle();
        String statusFilter;
        if (selectedToggle != null && !selectedToggle.getText().equals("All")) {
            if (selectedToggle.getText().equals("On The Way")) {
                statusFilter = "ON_THE_WAY";
            } else if (selectedToggle.getText().equals("Delivered")) {
                statusFilter = "COMPLETED";
            } else {
                statusFilter = null;
            }
        } else {
            statusFilter = null;
        }

        new Thread(() -> {
            try {

                List<OrderDto.OrderResponse> history = courierApiService.getDeliveryHistory(
                        TokenManager.getToken(),
                        foodFilter,
                        vendorFilter,
                        userFilter,
                        statusFilter
                );

                Platform.runLater(() -> {
                    populateDeliveries(myDeliveriesContainer, history);
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> showFeedback(e.getMessage(), true));
            }
        }).start();
    }

    @FXML
    void handleFilterMyDeliveries(ActionEvent event) {
        loadMyDeliveries();
    }

    private void populateDeliveries(VBox container, List<OrderDto.OrderResponse> orders) {
        container.getChildren().clear();
        if (orders == null) return;

        for (OrderDto.OrderResponse order : orders) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CourierViews/delivery-card-view.fxml"));
                Node cardNode = loader.load();
                DeliveryCardController controller = loader.getController();

                controller.setData(order, this::loadMyDeliveries, this::loadAvailableDeliveries);

                container.getChildren().add(cardNode);
            } catch (IOException e) {
                e.printStackTrace();
                Platform.runLater(() -> showFeedback(e.getMessage(), true));
            }
        }
    }


    @FXML
    protected void goToProfile(ActionEvent event) throws IOException {
        SceneManager.closeCurrentStage(profileButton);
        SceneManager.showWindow("/view/profile-view.fxml", "Profile", "profile", 1050, 720);
    }

    @FXML
    protected void logout(ActionEvent event) throws IOException {
        SceneManager.logout(logoutButton);
    }

    private void showFeedback(String message, boolean isError) {
        feedbackLabel.setText(message);
        feedbackBox.getStyleClass().removeAll("feedback-box-success", "feedback-box-error");
        feedbackBox.getStyleClass().add(isError ? "feedback-box-error" : "feedback-box-success");
        feedbackBox.setVisible(true);
        feedbackBox.setManaged(true);
    }

}
