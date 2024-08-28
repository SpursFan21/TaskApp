package com.example;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class TaskPage {

    private String username;

    public TaskPage(String username) {
        this.username = username;
    }

    public void show(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("TaskPage.fxml"));
            VBox layout = loader.load();

            TaskPageController controller = loader.getController();
            controller.setUsername(username);

            primaryStage.setTitle("Task Page");
            Scene scene = new Scene(layout, 600, 400);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
