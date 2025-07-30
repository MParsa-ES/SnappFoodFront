package org.example.snappfoodfront.controller;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.example.snappfoodfront.Service.RestaurantApiService;
import org.example.snappfoodfront.Utils.SceneManager;
import org.example.snappfoodfront.Utils.TokenManager;
import org.example.snappfoodfront.model.FoodItemDto;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;



public class FoodLibraryController implements Initializable {

    @FXML private TableView<FoodItemDto.Response> foodTableView;
    @FXML private TableColumn<FoodItemDto.Response, String> nameColumn;
    @FXML private TableColumn<FoodItemDto.Response, Integer> priceColumn;
    @FXML private TableColumn<FoodItemDto.Response, Integer> supplyColumn;
    @FXML private TableColumn<FoodItemDto.Response, Void> actionsColumn;

    private Long restaurantId;
    private final String token = TokenManager.getToken();

    private final RestaurantApiService restaurantApiService = new RestaurantApiService();


    @FXML
    private void handleBackButton(ActionEvent event) {
        try {
            SceneManager.switchScene(event,"SellerViews/seller-main-view.fxml",1050,720);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading seller view");
        }
    }

    @FXML
    private void handleAddButton(ActionEvent event) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SellerViews/food-creation-view.fxml"));
            Parent root = loader.load();


            AddFoodController addFoodController = loader.getController();


            addFoodController.setRestaurantId(this.restaurantId);
            addFoodController.initForAdd(this.restaurantId);


            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add New Food Item");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(root));
            dialogStage.showAndWait();

            loadFoodItems();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void initData(Long restaurantId) {
        this.restaurantId = restaurantId;
        loadFoodItems();
    }


    @Override
    public void initialize(URL url, ResourceBundle resources) {

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        supplyColumn.setCellValueFactory(new PropertyValueFactory<>("supply"));

        setupActionsColumn();

    }

    private void loadFoodItems() {

        new Thread(() -> {
            try {

                List<FoodItemDto.Response> foodItems = restaurantApiService.getAllFoodItems(this.token, this.restaurantId);

                Platform.runLater(() -> {

                    foodTableView.setItems(FXCollections.observableArrayList(foodItems));
                });
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Error while loading food items:" + e.getMessage());
            }

        }).start();
    }

    private void setupActionsColumn() {
        Callback<TableColumn<FoodItemDto.Response, Void>, TableCell<FoodItemDto.Response, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<FoodItemDto.Response, Void> call(final TableColumn<FoodItemDto.Response, Void> param) {
                final TableCell<FoodItemDto.Response, Void> cell = new TableCell<>() {

                    private final JFXButton editBtn = new JFXButton("Edit");
                    private final JFXButton deleteBtn = new JFXButton("Delete");
                    private final HBox pane = new HBox(10, editBtn, deleteBtn);

                    {

                        pane.setSpacing(15);
                        pane.setAlignment(Pos.CENTER);

                        editBtn.getStyleClass().add("edit-button");
                        deleteBtn.getStyleClass().add("delete-button");


                        editBtn.setGraphic(new FontIcon("fas-pencil-alt"));
                        deleteBtn.setGraphic(new FontIcon("fas-trash-alt"));

                        editBtn.setPrefWidth(80);
                        deleteBtn.setPrefWidth(80);

                    }

                    {

                        editBtn.setOnAction((ActionEvent event) -> {

                            FoodItemDto.Response foodItemToEdit = getTableView().getItems().get(getIndex());

                            try {

                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SellerViews/food-creation-view.fxml"));
                                Parent root = loader.load();


                                AddFoodController addFoodController = loader.getController();


                                addFoodController.initForEdit(foodItemToEdit, restaurantId);


                                Stage dialogStage = new Stage();
                                dialogStage.setTitle("Edit Food Item");
                                dialogStage.initModality(Modality.APPLICATION_MODAL);
                                dialogStage.initOwner(((Node) event.getSource()).getScene().getWindow());
                                dialogStage.setScene(new Scene(root));


                                dialogStage.showAndWait();


                                loadFoodItems();

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        });


                        deleteBtn.setOnAction((ActionEvent event) -> {
                            FoodItemDto.Response foodItem = getTableView().getItems().get(getIndex());


                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete " + foodItem.getName() + "?", ButtonType.YES, ButtonType.NO);
                            alert.showAndWait();

                            if (alert.getResult() == ButtonType.YES) {
                                try {
                                    restaurantApiService.deleteFoodItem(TokenManager.getToken(), restaurantId, foodItem.getId());
                                    loadFoodItems();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    System.err.println("Error while deleting food item: " + e.getMessage());
                                }

                            }
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(pane);
                        }
                    }
                };
                return cell;
            }
        };

        actionsColumn.setCellFactory(cellFactory);
    }
}