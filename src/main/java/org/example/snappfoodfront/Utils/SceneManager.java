package org.example.snappfoodfront.Utils;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class SceneManager {


    private static final String LOGIN_VIEW_PATH = "/view/login-view.fxml";

    public static void showLoginWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(SceneManager.class.getResource(LOGIN_VIEW_PATH));
            Parent root = fxmlLoader.load();

            Stage loginStage = new Stage();
            loginStage.setTitle("Snapp Food");
            loginStage.setScene(new Scene(root));
            loginStage.setResizable(false);
            loginStage.show();

        } catch (IOException e) {
            System.err.println("خطا در بارگذاری صفحه لاگین:");
            e.printStackTrace();
        }
    }


    // متد کمکی برای بستن پنجره فعلی
    public static void closeCurrentStage(Node node) {
        Stage currentStage = (Stage) node.getScene().getWindow();
        currentStage.close();
    }

    public static void switchScene(ActionEvent event, String fxmlFileName) throws IOException {

        URL url = new File("src/main/resources/view/" + fxmlFileName).toURI().toURL();

        Scene scene = new Scene(FXMLLoader.load(url));

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        stage.setScene(scene);
        stage.show();
    }

}