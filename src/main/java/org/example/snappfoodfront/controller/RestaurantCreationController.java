package org.example.snappfoodfront.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import org.controlsfx.control.textfield.CustomTextField;

public class RestaurantCreationController {

    @FXML
    private CustomTextField additionalFeeTextField;

    @FXML
    private TextArea addressTextField;

    @FXML
    private Button chooseLogoButton;

    @FXML
    private ImageView logoImage;

    @FXML
    private CustomTextField nameTextField;

    @FXML
    private CustomTextField phoneTextField;

    @FXML
    private CustomTextField taxFeeTextField;

    @FXML
    void handleChoosingLogo(ActionEvent event) {

    }

}
