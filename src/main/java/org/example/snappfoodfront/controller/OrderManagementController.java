package org.example.snappfoodfront.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import org.example.snappfoodfront.Service.RestaurantApiService;
import org.example.snappfoodfront.Utils.SceneManager;
import org.example.snappfoodfront.Utils.TokenManager;
import org.example.snappfoodfront.model.FoodItemDto;
import org.example.snappfoodfront.model.OrderDto;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.stream.Collectors;

public class OrderManagementController implements Initializable {

    @FXML
    private TableView<OrderDto.OrderResponse> ordersTableView;

    @FXML
    private TableColumn<OrderDto.OrderResponse, Long> orderIdColumn;

    @FXML
    private TableColumn<OrderDto.OrderResponse, Long> customerColumn;

    @FXML
    private TableColumn<OrderDto.OrderResponse, String> timeColumn;

    @FXML
    private TableColumn<OrderDto.OrderResponse, Integer> priceColumn;

    @FXML
    private TableColumn<OrderDto.OrderResponse, String> statusColumn;

    @FXML
    private TableColumn<OrderDto.OrderResponse, Void> actionsColumn;

    @FXML
    private ToggleGroup statusToggleGroup;

    @FXML
    private TextField searchTextField;


    private Long restaurantId;
    private final RestaurantApiService restaurantApiService = new RestaurantApiService();
    private Map<Long, FoodItemDto.Response> allFoodsMap;

    public void initData(Long restaurantId) {
        this.restaurantId = restaurantId;
        loadOrders();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        customerColumn.setCellValueFactory(new PropertyValueFactory<>("customer_id"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("created_at"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("pay_price"));


        setupStatusColumn();
        setupActionsColumn();

        statusToggleGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> loadOrders());
    }

    private void loadOrders() {

        ToggleButton selectedToggle = (ToggleButton) statusToggleGroup.getSelectedToggle();
        String statusFilter;

        if (selectedToggle != null && !selectedToggle.getText().equals("All")) {

            if (selectedToggle.getText().equals("Pending")) {
                statusFilter = "WAITING_VENDOR";
            } else if (selectedToggle.getText().equals("Accepted")) {
                statusFilter = "ACCEPTED";
            } else if (selectedToggle.getText().equals("Ready for Courier")) {
                statusFilter = "FINDING_COURIER";
            } else {
                statusFilter = null;
            }

        } else {
            statusFilter = null;
        }

        String searchFilter = searchTextField.getText();

        new Thread(() -> {
            try {

                List<OrderDto.OrderResponse> orders = restaurantApiService.getAllOrders(
                        TokenManager.getToken(),
                        restaurantId,
                        statusFilter,
                        searchFilter,
                        searchFilter,
                        searchFilter
                );

                List<FoodItemDto.Response> allFoods = restaurantApiService.getAllFoodItems(TokenManager.getToken(), restaurantId);

                this.allFoodsMap = new HashMap<>();
                for (FoodItemDto.Response food : allFoods) {
                    this.allFoodsMap.put(food.getId(), food);
                }

                Platform.runLater(() -> {
                    ordersTableView.setItems(FXCollections.observableArrayList(orders));
                });
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Error getting the list of orders :" + e.getMessage());
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
                    setText(null);
                    setGraphic(null);
                } else {
                    Label statusBadge = new Label(status);
                    statusBadge.getStyleClass().add("status-badge");

                    switch (status.toLowerCase()) {
                        case "waiting_vendor":
                            statusBadge.getStyleClass().add("status-pending");
                            break;
                        case "accepted":
                            statusBadge.getStyleClass().add("status-accepted");
                            break;
                        case "completed":
                            statusBadge.getStyleClass().add("status-ready");
                            break;
                        case "cancelled":
                        case "unpaid_and_cancelled":
                            statusBadge.getStyleClass().add("status-cancelled");
                            break;
                    }
                    setGraphic(statusBadge);
                }
            }
        });
    }


    private void setupActionsColumn() {
        Callback<TableColumn<OrderDto.OrderResponse, Void>, TableCell<OrderDto.OrderResponse, Void>> cellFactory = param -> new TableCell<>() {
            private final Button acceptBtn = new Button("Accept");
            private final Button rejectBtn = new Button("Reject");
            private final Button readyBtn = new Button("Mark as Ready");
            private final Button detailsBtn = new Button("Details");
            private final HBox pane = new HBox(10);

            {

                acceptBtn.getStyleClass().add("accept-button");
                rejectBtn.getStyleClass().add("reject-button");
                readyBtn.getStyleClass().add("ready-button");
                detailsBtn.getStyleClass().add("details-button");

                acceptBtn.setOnAction(event -> handleUpdateStatus("accepted"));
                rejectBtn.setOnAction(event -> handleUpdateStatus("cancelled"));
                readyBtn.setOnAction(event -> handleUpdateStatus("finding_courier"));
                detailsBtn.setOnAction(event -> showOrderDetails());
            }

            private void handleUpdateStatus(String newStatus) {
                OrderDto.OrderResponse order = getTableView().getItems().get(getIndex());
                new Thread(() -> {
                    try {
                        OrderDto.OrderStatusChangeRequest statusChangeRequest = new OrderDto.OrderStatusChangeRequest(newStatus.toUpperCase());
                        restaurantApiService.updateOrderStatus(TokenManager.getToken(), order.getId(), statusChangeRequest);
                        Platform.runLater(() -> loadOrders());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            }

            private void showOrderDetails() {
                OrderDto.OrderResponse order = getTableView().getItems().get(getIndex());
                if (order.getItem_ids() == null || allFoodsMap == null) {
                    return;
                }

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Order Details");
                alert.setHeaderText("Items for Order #" + order.getId());

                ListView<String> itemsListView = new ListView<>();
                ObservableList<String> itemNames = FXCollections.observableArrayList();

                // Loop through the item IDs and look up their names in our map
                for (Long itemId : order.getItem_ids()) {
                    if (allFoodsMap.containsKey(itemId)) {
                        itemNames.add(allFoodsMap.get(itemId).getName());
                    } else {
                        itemNames.add("Unknown Item (ID: " + itemId + ")");
                    }
                }
                itemsListView.setItems(itemNames);

                alert.getDialogPane().setContent(itemsListView);
                alert.showAndWait();
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    OrderDto.OrderResponse order = getTableView().getItems().get(getIndex());
                    pane.getChildren().clear();
                    pane.setAlignment(Pos.CENTER);
                    pane.getChildren().add(detailsBtn);


                    if ("waiting_vendor".equalsIgnoreCase(order.getStatus())) {
                        pane.getChildren().addAll(acceptBtn, rejectBtn);
                    } else if ("accepted".equalsIgnoreCase(order.getStatus())) {
                        pane.getChildren().add(readyBtn);
                    }
                    setGraphic(pane);
                }
            }
        };
        actionsColumn.setCellFactory(cellFactory);
    }

    @FXML
    private void handleSearch(MouseEvent event) {
        loadOrders();
    }

    @FXML
    void handleBackButton(ActionEvent event) {
        try {
            SceneManager.switchScene(event, "SellerViews/seller-main-view.fxml", 1050, 720);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading seller view");
        }
    }
}
