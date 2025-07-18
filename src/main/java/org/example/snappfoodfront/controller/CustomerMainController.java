package org.example.snappfoodfront.controller;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import org.example.snappfoodfront.Utils.SceneManager;

import java.io.IOException;

public class CustomerMainController {

    private static final String PROFILE_VIEW_PATH = "/view/profile-view.fxml";

    @FXML public JFXButton profileButton;
    @FXML public JFXButton logoutButton;

    @FXML
    protected void goToProfile(ActionEvent event) throws IOException {
        SceneManager.closeCurrentStage(profileButton);
        SceneManager.showWindow(PROFILE_VIEW_PATH, "Profile", "profile", 1024, 720);
    }

    @FXML
    protected void logout(ActionEvent event) throws IOException {
        SceneManager.logout(logoutButton);
    }

}
