package org.example.snappfoodfront.Utils;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.function.UnaryOperator;

public class Methods {

    public static void filterPhoneField(TextField field) {
        UnaryOperator<TextFormatter.Change> filter = change -> {

            String newText = change.getControlNewText();

            if (newText.matches("\\d*") && newText.length() <= 11) {
                return change;
            }
            return null;
        };

        TextFormatter<String> textFormatter = new TextFormatter<>(filter);
        field.setTextFormatter(textFormatter);
    }

    public static Image convertToImage(String base64String) {
        if (base64String == null || base64String.isEmpty()) {
            return null;
        }

        try {
            byte[] decodedBytes = Base64.getDecoder().decode(base64String);
            InputStream inputStream = new ByteArrayInputStream(decodedBytes);
            return new Image(inputStream);
        } catch (IllegalArgumentException e) {
            System.err.println("invalid" + e.getMessage());
            return null;
        }
    }

}
