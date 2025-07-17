module org.example.snappfoodfront {
    requires javafx.controls;
    requires javafx.fxml;
    requires lombok;
    requires java.net.http;
    requires com.google.gson;
    requires java.prefs;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;
    requires org.controlsfx.controls;
    requires com.jfoenix;


    opens org.example.snappfoodfront to javafx.fxml;
    opens org.example.snappfoodfront.controller to javafx.fxml, javafx.graphics, com.google.gson;
    opens org.example.snappfoodfront.model to javafx.fxml, javafx.graphics, com.google.gson;
    opens org.example.snappfoodfront.Service to javafx.fxml, javafx.graphics, com.google.gson;
    opens org.example.snappfoodfront.Utils to javafx.fxml, javafx.graphics, com.google.gson;

    exports org.example.snappfoodfront;
}