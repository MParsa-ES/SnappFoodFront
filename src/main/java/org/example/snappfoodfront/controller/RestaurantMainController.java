package org.example.snappfoodfront.controller;

import com.jfoenix.controls.JFXButton;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.*;
import javafx.scene.shape.Circle;
import org.example.snappfoodfront.Utils.MainViewState;
import org.example.snappfoodfront.Utils.SceneManager;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class RestaurantMainController implements Initializable {


    @FXML public JFXButton goBackButton;
    @FXML public JFXButton logoutButton;
    @FXML public ImageView logoImageView;
    @FXML public Circle statusIndicatorCircle;
    @FXML public Label restaurantNameLabel;
    @FXML public Label addressLabel;
    @FXML public Label phoneLabel;
    @FXML public HBox menuContainer;
    @FXML public VBox foodContainer;

    private static final String CUSTOMER_MAIN_VIEW_PATH = "/view/customer-main-view.fxml";

    public class MenuButton extends JFXButton {

        private final BooleanProperty selected = new SimpleBooleanProperty(false);

        RadialGradient orangeToPink = new RadialGradient(
                0, 0, 0.5, 0.5, 0.5, true, CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.web("#ff00f6")),
                new Stop(1.0, Color.WHITE));

        RadialGradient pinkToOrange = new RadialGradient(
                0, 0, 0.5, 0.5, 0.5, true, CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.web("#ff7600")),
                new Stop(1.0, Color.WHITE));

        private final String orangeStyle = "-fx-background-radius: 5; -fx-background-color: #ff7600;";
        private final String pinkStyle = "-fx-background-radius: 5; -fx-background-color: #ff00f6;";

        public MenuButton(String text) {
            super(text);

            this.setButtonType(JFXButton.ButtonType.RAISED);
            this.setCursor(Cursor.HAND);
            this.setTextFill(Color.WHITE);
            this.setStyle(
                    "-fx-background-radius: 5;" +
                    "-fx-background-color:  #ff7600"
            );

            this.setRipplerFill(orangeToPink);

            selected.addListener((obs, oldVal, isSelected) -> {
                if (isSelected) {
                    this.setStyle(pinkStyle);
                    this.setRipplerFill(pinkToOrange);
                } else {
                    this.setStyle(orangeStyle);
                    this.setRipplerFill(orangeToPink);
                }
            });

        }

        public final void setSelected(boolean selected) { this.selected.set(selected); }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Long id = MainViewState.getSelectedRestaurantId();
        menuContainer.getChildren().clear();
        List<MenuButton> menuButtons = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            MenuButton menuButton = new MenuButton("Restaurant Menu");
            menuButtons.add(menuButton);

            menuButton.setOnAction(event -> {

                for (MenuButton btn : menuButtons) {
                    if (btn != menuButton) {
                        btn.setSelected(false);
                    }
                }

                menuButton.setSelected(true);

            });

            menuContainer.getChildren().add(menuButton);
        }

    }

    @FXML
    protected void goBack(ActionEvent event) throws IOException {
        SceneManager.closeCurrentStage(goBackButton);
        SceneManager.showWindow(CUSTOMER_MAIN_VIEW_PATH, "SnappFood", "dashboard", 1024, 720);
    }

}
