package org.example.snappfoodfront.controller;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import org.example.snappfoodfront.Service.AdminApiService;
import org.example.snappfoodfront.Utils.TokenManager;
import org.example.snappfoodfront.model.UserLoginDto;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class UserManagementController implements Initializable {

    @FXML
    private TableColumn<UserLoginDto.UserData, Void> actionsColumn;

    @FXML
    private TableColumn<UserLoginDto.UserData, Long> idColumn;

    @FXML
    private TableColumn<UserLoginDto.UserData, String> nameColumn;

    @FXML
    private TableColumn<UserLoginDto.UserData, String> phoneColumn;

    @FXML
    private TableColumn<UserLoginDto.UserData, String> roleColumn;

    @FXML
    private TableColumn<UserLoginDto.UserData, String> statusColumn;

    @FXML
    private TableView<UserLoginDto.UserData> usersTableView;


    @FXML
    private HBox feedbackBox;

    @FXML
    private Label feedbackLabel;

    private final AdminApiService adminApiService = new AdminApiService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("full_name"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("approval_status"));

        setupActionsColumn();
        loadUsers();
    }

    private void loadUsers() {
        new Thread(() -> {
            try {
                List<UserLoginDto.UserData> users = adminApiService.getAllUsers(TokenManager.getToken());
                Platform.runLater(() -> {
                    usersTableView.setItems(FXCollections.observableArrayList(users));
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void setupActionsColumn() {
        Callback<TableColumn<UserLoginDto.UserData, Void>, TableCell<UserLoginDto.UserData, Void>> cellFactory = param -> new TableCell<>() {
            private final JFXButton approveBtn = new JFXButton("Approve");
            private final JFXButton setToPendingBtn = new JFXButton("Set to Pending");
            private final JFXButton rejectBtn = new JFXButton("Reject");
            private final JFXButton removeBtn = new JFXButton("Remove");
            private final HBox pane = new HBox(10);

            {
                approveBtn.getStyleClass().add("accept-button");
                rejectBtn.getStyleClass().add("reject-button");
                removeBtn.getStyleClass().add("delete-button");
                setToPendingBtn.getStyleClass().add("pending-button");

                approveBtn.setOnAction(event -> handleUpdateStatus("approved"));
                rejectBtn.setOnAction(event -> handleUpdateStatus("rejected"));
                setToPendingBtn.setOnAction(event -> handleUpdateStatus("pending"));
                removeBtn.setOnAction(event -> handleRemoveUser());
            }

            private void handleUpdateStatus(String newStatus) {
                UserLoginDto.UserData user = getTableView().getItems().get(getIndex());
                new Thread(() -> {
                    try {

                        adminApiService.updateUserApprovalStatus(TokenManager.getToken(), user.getId(), newStatus);
                        Platform.runLater(() -> loadUsers());
                    } catch (AdminApiService.AdminException e) {
                        Platform.runLater(() -> showFeedback(e.getMessage(), true));

                    } catch (Exception e) {
                        Platform.runLater(() -> showFeedback("Error: Could not update user status.", true));
                        e.printStackTrace();
                    }
                }).start();
            }

            private void handleRemoveUser() {
                UserLoginDto.UserData user = getTableView().getItems().get(getIndex());

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Permanently remove user '" + user.getFull_name() + "'?", ButtonType.YES, ButtonType.NO);
                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.YES) {
                        new Thread(() -> {
                            try {
                                adminApiService.deleteUser(TokenManager.getToken(), user.getId());
                                Platform.runLater(() -> loadUsers());
                            } catch (AdminApiService.AdminException e) {
                                Platform.runLater(() -> showFeedback(e.getMessage(), true));
                            } catch (Exception e) {
                                Platform.runLater(() -> showFeedback("Error: Could not delete user.", true));
                                e.printStackTrace();
                            }
                        }).start();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    UserLoginDto.UserData user = getTableView().getItems().get(getIndex());
                    pane.getChildren().clear();
                    pane.setAlignment(Pos.CENTER);

                    if ("pending".equalsIgnoreCase(user.getApproval_status())) {
                        pane.getChildren().addAll(approveBtn, rejectBtn);
                    } else if ("approved".equalsIgnoreCase(user.getApproval_status())) {
                        pane.getChildren().add(setToPendingBtn);
                    }

                    pane.getChildren().add(removeBtn);
                    setGraphic(pane);
                }
            }
        };
        actionsColumn.setCellFactory(cellFactory);
    }

    private void showFeedback(String message, boolean isError) {
        feedbackLabel.setText(message);
        feedbackBox.getStyleClass().removeAll("feedback-box-success", "feedback-box-error");
        feedbackBox.getStyleClass().add(isError ? "feedback-box-error" : "feedback-box-error");
        feedbackBox.setVisible(true);
        feedbackBox.setManaged(true);
    }
}
