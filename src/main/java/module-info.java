module org.example.snappfoodfront {
    requires javafx.controls;
    requires javafx.fxml;
    requires lombok;


    opens org.example.snappfoodfront to javafx.fxml;
    opens org.example.snappfoodfront.controller to javafx.fxml;

    exports org.example.snappfoodfront;
}