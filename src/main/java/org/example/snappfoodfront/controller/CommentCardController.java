package org.example.snappfoodfront.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.example.snappfoodfront.Service.AuthApiService;
import org.example.snappfoodfront.Service.OrderApiService;
import org.example.snappfoodfront.Service.ProfileApiService;
import org.example.snappfoodfront.Utils.Methods;
import org.example.snappfoodfront.model.RatingDTO;
import org.kordamp.ikonli.javafx.FontIcon;

import java.awt.*;
import java.io.IOException;

public class CommentCardController {

    @FXML private Label userNameLabel;
    @FXML private HBox starsContainer;
    @FXML private Label dateLabel;
    @FXML private Label commentTextLabel;
    @FXML private HBox imagesContainer;

    private final ProfileApiService profileService = new ProfileApiService();

    public void setData(RatingDTO.ItemRatings.Comments comment) {

        userNameLabel.setText(comment.getUsername());
        dateLabel.setText(comment.getCreated_at());
        commentTextLabel.setText(comment.getComment());

        starsContainer.getChildren().clear();
        for (int i = 0; i < comment.getRating(); i++) {
            FontIcon starIcon = new FontIcon("fas-star");
            starIcon.setIconColor(Color.web("#ffe100"));
            starsContainer.getChildren().add(starIcon);
        }

        imagesContainer.getChildren().clear();
        if (comment.getImageBase64() != null) {
            for (String base64 : comment.getImageBase64()) {
                Image image = Methods.convertToImage(base64);
                ImageView imageView = new ImageView(image);
                imageView.setFitHeight(80);
                imageView.setFitWidth(80);
                imageView.setPreserveRatio(false);
                imagesContainer.getChildren().add(imageView);
            }
        }
    }

}