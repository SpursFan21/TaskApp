package com.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;

public class MainApp extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("TaskApp");

        showMainPage();
    }

    public void showMainPage() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/com/example/MainApp.fxml"));
            VBox mainPage = (VBox) loader.load();

            // Create and set the scene
            Scene scene = new Scene(mainPage);
            primaryStage.setScene(scene);

            // Load and apply CSS
            scene.getStylesheets().add(MainApp.class.getResource("/com/example/styles.css").toExternalForm());

            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showTaskPage() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/com/example/TaskPage.fxml"));
            VBox taskPage = (VBox) loader.load();
    
            // Set the controller
            TaskPageController controller = loader.getController();
            controller.setMainApp(this);
    
            Scene scene = new Scene(taskPage);
            primaryStage.setScene(scene);

            // Load and apply CSS
            scene.getStylesheets().add(MainApp.class.getResource("/com/example/styles.css").toExternalForm());
            
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
