package org.example.snappfoodfront.controller;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import org.example.snappfoodfront.Utils.SceneManager;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminDashboardController implements Initializable {

    @FXML
    private JFXButton profileButton;

    @FXML
    private JFXButton logoutButton;

    @FXML
    private BorderPane contentArea;

    @FXML
    private JFXButton couponsNavButton;

    @FXML
    private JFXButton ordersNavButton;

    @FXML
    private JFXButton transactionsNavButton;

    @FXML
    private JFXButton usersNavButton;


    public void initialize(URL url, ResourceBundle rb) {
        handleStatisticsNav(null);
    }



    @FXML
    void handleCouponsNav(ActionEvent event) {
        try {
            SceneManager.switchCenterPane(contentArea, "AdminViews/coupon-management-view.fxml");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading coupon list view :" + e.getMessage());
        }
    }

    @FXML
    void handleOrdersNav(ActionEvent event) {
        try {
            SceneManager.switchCenterPane(contentArea, "AdminViews/admin-order-view.fxml");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading order list view :" + e.getMessage());
        }
    }

    @FXML
    void handleTransactionsNav(ActionEvent event) {
        try {
            SceneManager.switchCenterPane(contentArea, "AdminViews/transaction-management-view.fxml");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading order list view :" + e.getMessage());
        }
    }

    @FXML
    void handleUsersNav(ActionEvent event) {

        try {
            SceneManager.switchCenterPane(contentArea, "AdminViews/user-management-view.fxml");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading user management view :" + e.getMessage());
        }

    }

    @FXML
    void handleStatisticsNav(ActionEvent event) {
        try {
            SceneManager.switchCenterPane(contentArea, "AdminViews/admin-statistics-view.fxml");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading statistics view :" + e.getMessage());
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

}
