package org.example.snappfoodfront.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import org.example.snappfoodfront.Service.OrderApiService;
import org.example.snappfoodfront.Service.RatingApiService;
import org.example.snappfoodfront.Utils.SceneManager;
import org.example.snappfoodfront.Utils.MainViewState;
import org.example.snappfoodfront.Utils.TokenManager;
import org.example.snappfoodfront.model.RatingDTO;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;

public class ReviewController implements Initializable {

    @FXML public JFXComboBox<Integer> ratingBox;
    @FXML public TextArea commentTextArea;
    @FXML public JFXButton submitButton;
    @FXML public Label errorLabel;
    @FXML public HBox imageThumbnailsContainer;
    @FXML private Long orderId;

    private final RatingApiService ratingService = new RatingApiService();

    private Runnable onReviewSubmittedCallback;
    private Set<String> imageBase64Set = new HashSet<>();
    private int selectedRating = 0;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.orderId = MainViewState.getSelectedOrderId();
        ratingBox.getItems().addAll(1, 2, 3, 4, 5);
    }

    @FXML
    private void handleAddImages() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Photos for Your Review");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(imageThumbnailsContainer.getScene().getWindow());

        if (selectedFiles != null && !selectedFiles.isEmpty()) {

            for (File file : selectedFiles) {
                try {
                    byte[] fileContent = Files.readAllBytes(file.toPath());
                    String base64String = Base64.getEncoder().encodeToString(fileContent);
                    imageBase64Set.add(base64String);

                    Image thumbnail = new Image(file.toURI().toString());
                    ImageView thumbnailView = new ImageView(thumbnail);
                    thumbnailView.setFitHeight(80);
                    thumbnailView.setFitWidth(80);
                    thumbnailView.setPreserveRatio(true);
                    imageThumbnailsContainer.getChildren().add(thumbnailView);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    private void handleSubmit() {
        selectedRating = ratingBox.getValue();
        String comment = commentTextArea.getText();

        if (selectedRating == 0 || comment.isEmpty()) {
            errorLabel.setText("Please select a rating.");
            return;
        }

        RatingDTO.Request requestDto = new RatingDTO.Request(orderId, selectedRating, comment, imageBase64Set);

        new Thread(() -> {
            try {
                ratingService.submitRating(TokenManager.getToken(), requestDto);

                Platform.runLater(() -> {
                    if (onReviewSubmittedCallback != null) {
                        onReviewSubmittedCallback.run();
                    }
                    SceneManager.closeCurrentStage(submitButton);
                });
            } catch (Exception e) {
                Platform.runLater(() -> errorLabel.setText("Failed to submit review: " + e.getMessage()));
                e.printStackTrace();
            }
        }).start();
    }

    public void setOnReviewSubmitted(Runnable callback) {
        this.onReviewSubmittedCallback = callback;
    }

}