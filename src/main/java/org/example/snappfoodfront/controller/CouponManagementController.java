package org.example.snappfoodfront.controller;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.example.snappfoodfront.Service.AdminApiService;

import org.example.snappfoodfront.Utils.TokenManager;
import org.example.snappfoodfront.model.CouponDto;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CouponManagementController implements Initializable {

    @FXML private TableView<CouponDto.Response> couponsTableView;
    @FXML private TableColumn<CouponDto.Response, String> codeColumn;
    @FXML private TableColumn<CouponDto.Response, String> typeColumn;
    @FXML private TableColumn<CouponDto.Response, Double> valueColumn;
    @FXML private TableColumn<CouponDto.Response, Integer> userCountColumn;
    @FXML private TableColumn<CouponDto.Response, String> startDateColumn;
    @FXML private TableColumn<CouponDto.Response, String> endDateColumn;
    @FXML private TableColumn<CouponDto.Response, Void> actionsColumn;
    @FXML private HBox feedbackBox;
    @FXML private Label feedbackLabel;


    private final AdminApiService adminApiService = new AdminApiService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        codeColumn.setCellValueFactory(new PropertyValueFactory<>("coupon_code"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        userCountColumn.setCellValueFactory(new PropertyValueFactory<>("user_count"));
        startDateColumn.setCellValueFactory(new PropertyValueFactory<>("start_date"));
        endDateColumn.setCellValueFactory(new PropertyValueFactory<>("end_date"));

        setupActionsColumn();

        loadCoupons();
    }

    private void loadCoupons() {
        new Thread(() -> {
            try {

                List<CouponDto.Response> coupons = adminApiService.getAllCoupons(TokenManager.getToken());
                Platform.runLater(() -> {
                    couponsTableView.setItems(FXCollections.observableArrayList(coupons));
                });
            } catch (AdminApiService.AdminException e) {
                Platform.runLater(() -> showFeedback(e.getMessage(), true));
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> showFeedback("Erro Loading Coupons", true));
            }
        }).start();
    }

    private void setupActionsColumn() {
        Callback<TableColumn<CouponDto.Response, Void>, TableCell<CouponDto.Response, Void>> cellFactory = param -> new TableCell<>() {
            private final JFXButton editBtn = new JFXButton("Edit");
            private final JFXButton deleteBtn = new JFXButton("Delete");
            private final HBox pane = new HBox(10, editBtn, deleteBtn);

            {
                editBtn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                deleteBtn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

                editBtn.setGraphic(new FontIcon("fas-pencil-alt"));
                deleteBtn.setGraphic(new FontIcon("fas-trash-alt"));
                pane.setAlignment(Pos.CENTER);

                Tooltip.install(editBtn, new Tooltip("Edit Coupon"));
                Tooltip.install(deleteBtn, new Tooltip("Delete Coupon"));

                editBtn.getStyleClass().add("icon-button");
                deleteBtn.getStyleClass().add("icon-button-danger");

                editBtn.setOnAction(event -> {
                    CouponDto.Response coupon = getTableView().getItems().get(getIndex());
                    try {

                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AdminViews/add-coupon-view.fxml"));
                        Parent root = loader.load();

                        Stage dialogStage = new Stage();
                        dialogStage.setTitle("Edit Coupon");
                        dialogStage.initModality(Modality.APPLICATION_MODAL);
                        dialogStage.setScene(new Scene(root));

                        AddCouponController controller = loader.getController();

                        controller.initDataForEdit(coupon);

                        dialogStage.showAndWait();
                        loadCoupons();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                deleteBtn.setOnAction(event -> {
                    CouponDto.Response coupon = getTableView().getItems().get(getIndex());
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete coupon '" + coupon.getCoupon_code() + "'?", ButtonType.YES, ButtonType.NO);
                    alert.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.YES) {
                            new Thread(() -> {
                                try {
                                    adminApiService.deleteCoupon(TokenManager.getToken(), coupon.getId());
                                    Platform.runLater(() -> loadCoupons());
                                } catch (AdminApiService.AdminException e) {
                                    Platform.runLater(() -> showFeedback(e.getMessage(), true));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Platform.runLater(() -> showFeedback("Error Deleting Coupon", true));
                                }
                            }).start();
                        }
                    });
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(pane);
                }
            }
        };
        actionsColumn.setCellFactory(cellFactory);
    }

    @FXML
    void handleAddCoupon(ActionEvent event) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AdminViews/add-coupon-view.fxml"));
            Parent root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add New Coupon");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(root));
            dialogStage.showAndWait();

            loadCoupons();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showFeedback(String message, boolean isError) {
        feedbackLabel.setText(message);
        feedbackBox.getStyleClass().removeAll("feedback-box-success", "feedback-box-error");
        feedbackBox.getStyleClass().add(isError ? "feedback-box-error" : "feedback-box-success");
        feedbackBox.setVisible(true);
        feedbackBox.setManaged(true);
    }
}
