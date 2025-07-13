package org.example.snappfoodfront.Utils;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

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

}
