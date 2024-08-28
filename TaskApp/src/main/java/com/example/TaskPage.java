package com.example;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TaskPage {

    public void show(Stage primaryStage) {
        primaryStage.setTitle("Task Page");

        // UI elements
        TextField taskField = new TextField();
        taskField.setPromptText("Enter a new task");

        Button addButton = new Button("Add Task");
        Button removeButton = new Button("Remove Task");
        ListView<String> taskListView = new ListView<>();

        // Add functionality to buttons
        addButton.setOnAction(e -> {
            String task = taskField.getText();
            if (!task.isEmpty()) {
                taskListView.getItems().add(task);
                taskField.clear();
            }
        });

        removeButton.setOnAction(e -> {
            String selectedTask = taskListView.getSelectionModel().getSelectedItem();
            if (selectedTask != null) {
                taskListView.getItems().remove(selectedTask);
            }
        });

        // Layout
        VBox layout = new VBox(10, taskField, addButton, removeButton, taskListView);
        layout.setStyle("-fx-padding: 20;");

        // Scene and Stage
        Scene scene = new Scene(layout, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}

