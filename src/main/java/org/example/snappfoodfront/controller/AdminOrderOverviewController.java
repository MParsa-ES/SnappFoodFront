package org.example.snappfoodfront.controller;

import javafx.application.Platform;
import javafx.beans.value.ObservableSetValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import org.example.snappfoodfront.Service.AdminApiService;
import org.example.snappfoodfront.Service.RestaurantApiService;
import org.example.snappfoodfront.Utils.TokenManager;
import org.example.snappfoodfront.model.FoodItemDto;
import org.example.snappfoodfront.model.OrderDto;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class AdminOrderOverviewController implements Initializable {


    @FXML private TableView<OrderDto.OrderResponse> ordersTableView;
    @FXML private TableColumn<OrderDto.OrderResponse, Long> orderIdColumn;
    @FXML private TableColumn<OrderDto.OrderResponse, Long> vendorColumn;
    @FXML private TableColumn<OrderDto.OrderResponse, Long> customerColumn;
    @FXML private TableColumn<OrderDto.OrderResponse, Long> courierColumn;
    @FXML private TableColumn<OrderDto.OrderResponse, Integer> priceColumn;
    @FXML private TableColumn<OrderDto.OrderResponse, String> statusColumn;
    @FXML private TableColumn<OrderDto.OrderResponse, Void> actionsColumn;
    @FXML private ToggleGroup statusToggleGroup;
    @FXML private TextField searchFoodField;
    @FXML private TextField searchVendorField;
    @FXML private TextField searchCourierField;
    @FXML private TextField searchCustomerField;
    @FXML private HBox feedbackBox;
    @FXML private Label feedbackLabel;


    private final AdminApiService adminApiService = new AdminApiService();
    private final RestaurantApiService restaurantApiService = new RestaurantApiService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        vendorColumn.setCellValueFactory(new PropertyValueFactory<>("vendor_id"));
        customerColumn.setCellValueFactory(new PropertyValueFactory<>("customer_id"));
        courierColumn.setCellValueFactory(new PropertyValueFactory<>("courier_id"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("pay_price"));

        setupStatusColumn();
        setupActionsColumn();

        loadOrders();
    }

    @FXML
    void handleFilter(ActionEvent event) {
        loadOrders();
    }

    private void loadOrders() {

        ToggleButton selectedToggle = (ToggleButton) statusToggleGroup.getSelectedToggle();

        String statusFilter;
        
        if (selectedToggle != null && !selectedToggle.getText().equals("All")) {
            
            if (selectedToggle.getText().equals("Pending")){
                statusFilter = "WAITING_VENDOR";
            } else if (selectedToggle.getText().equals("Accepted")) {
                statusFilter = "ACCEPTED";
            } else if (selectedToggle.getText().equals("In Transit")) {
                statusFilter = "ON_THE_WAY";
            } else if (selectedToggle.getText().equals("Completed")) {
                statusFilter = "COMPLETED";
            } else {
                statusFilter = null;
            }
        } else {
            statusFilter = null;
        }

        String foodFilter = searchFoodField.getText();
        String vendorFilter = searchVendorField.getText();
        String courierFilter = searchCourierField.getText();
        String customerFilter = searchCustomerField.getText();

        new Thread(() -> {
            try {
                List<OrderDto.OrderResponse> orders = adminApiService.getAllOrders(
                        TokenManager.getToken(),
                        foodFilter,
                        vendorFilter,
                        courierFilter,
                        customerFilter,
                        statusFilter
                );
                Platform.runLater(() -> {
                    ordersTableView.setItems(FXCollections.observableArrayList(orders));
                });
            } catch (AdminApiService.AdminException e) {

                Platform.runLater(() -> showFeedback(e.getMessage(), true));

            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> showFeedback("Error while Loading all orders", true));
            }
        }).start();
    }

    private void setupStatusColumn() {
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setGraphic(null);
                } else {
                    Label statusBadge = new Label(status.replace("_", " ").toUpperCase());
                    statusBadge.getStyleClass().add("status-badge");
                    switch (status.toLowerCase()) {
                        case "waiting_vendor":
                            statusBadge.getStyleClass().add("status-pending");
                            break;
                        case "accepted":
                            statusBadge.getStyleClass().add("status-accepted");
                            break;
                        case "finding_courier":
                            statusBadge.getStyleClass().add("status-ready");
                            break;
                        case "completed":
                        case "delivered":
                            statusBadge.getStyleClass().add("status-completed");
                            break;
                        case "cancelled":
                        case "unpaid_and_cancelled":
                        case "rejected":
                            statusBadge.getStyleClass().add("status-cancelled");
                            break;
                    }
                    setGraphic(statusBadge);
                    setAlignment(Pos.CENTER);
                }
            }
        });
    }

    private void setupActionsColumn() {
        Callback<TableColumn<OrderDto.OrderResponse, Void>, TableCell<OrderDto.OrderResponse, Void>> cellFactory = param -> new TableCell<>() {
            private final Button detailsBtn = new Button("Details");

            {
                detailsBtn.getStyleClass().add("details-button");
                detailsBtn.setOnAction(event -> showOrderDetails());
            }

            private void showOrderDetails() {
                OrderDto.OrderResponse order = getTableView().getItems().get(getIndex());

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Order Details");
                alert.setHeaderText("Items for order " + order.getId());
                ListView<String> items = new ListView<>();
                items.getItems().add("Loading items...");
                alert.getDialogPane().setContent(items);

                new Thread(() -> {
                    try {
                        List<FoodItemDto.Response> allFoodItems = restaurantApiService.getAllFoodItems(TokenManager.getToken(), order.getVendor_id());
                        Map<Long, String> itemsMap = new HashMap<>();

                        for (FoodItemDto.Response foodItem : allFoodItems  ) {
                            itemsMap.put(foodItem.getId(), foodItem.getName());
                        }

                        ObservableList<String> itemNames = FXCollections.observableArrayList();

                        for (Long itemId : order.getItem_ids()) {
                            itemNames.add(itemsMap.get(itemId));
                        }

                        Platform.runLater(() -> {
                            items.setItems(itemNames);
                        });


                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("Error while displaying food items for this order :" + e.getMessage());
                        Platform.runLater(() -> {
                            items.getItems().setAll("Failed to Load Food...");
                        });
                    }
                }).start();

                alert.showAndWait();
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(detailsBtn);
                    setAlignment(Pos.CENTER);
                }
            }
        };
        actionsColumn.setCellFactory(cellFactory);
    }

    private void showFeedback(String message, boolean isError) {
        feedbackLabel.setText(message);
        feedbackBox.getStyleClass().removeAll("feedback-box-success", "feedback-box-error");
        feedbackBox.getStyleClass().add(isError ? "feedback-box-error" : "feedback-box-success");
        feedbackBox.setVisible(true);
        feedbackBox.setManaged(true);
    }
}
