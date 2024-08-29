package com.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;

import java.io.FileNotFoundException;
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
            // Verify the URL for the FXML file
            URL fxmlUrl = getClass().getResource("/com/example/MainApp.fxml");
            if (fxmlUrl == null) {
                throw new FileNotFoundException("FXML file not found: /com/example/MainApp.fxml");
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            
            // Load the FXML file and get the root node
            Parent root = loader.load();
            
            // Create a new scene with the loaded root node
            Scene scene = new Scene(root, 800, 600);
            
            // Set the scene on the primary stage
            if (primaryStage == null) {
                primaryStage = new Stage();
            }
            primaryStage.setTitle("Main Page");
            primaryStage.setScene(scene);
            
            // Load and apply CSS
            URL cssUrl = getClass().getResource("/com/example/styles.css");
            if (cssUrl == null) {
                throw new FileNotFoundException("CSS file not found: /com/example/styles.css");
            }
            scene.getStylesheets().add(cssUrl.toExternalForm());
            
            // Show the primary stage
            primaryStage.show();
        } catch (IOException e) {
            System.err.println("Failed to load FXML or CSS file:");
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
