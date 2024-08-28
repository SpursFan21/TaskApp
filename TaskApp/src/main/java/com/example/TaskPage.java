package com.example;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TaskPage {

    private String username; // Username of the logged-in user

    public TaskPage(String username) {
        this.username = username;
    }

    public void show(Stage primaryStage) {
        primaryStage.setTitle("Task Page");

        // UI elements
        TextField taskField = new TextField();
        taskField.setPromptText("Enter a new task");

        Button addButton = new Button("Add Task");
        Button removeButton = new Button("Remove Task");
        ListView<String> taskListView = new ListView<>();

        // Load tasks from the database
        loadTasks(taskListView);

        // Add functionality to buttons
        addButton.setOnAction(e -> {
            String task = taskField.getText();
            if (!task.isEmpty()) {
                addTask(task);
                taskField.clear();
                loadTasks(taskListView); // Refresh task list
            }
        });

        removeButton.setOnAction(e -> {
            String selectedTask = taskListView.getSelectionModel().getSelectedItem();
            if (selectedTask != null) {
                removeTask(selectedTask);
                loadTasks(taskListView); // Refresh task list
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

    private void loadTasks(ListView<String> taskListView) {
        taskListView.getItems().clear();
        try (Connection conn = DatabaseUtils.getConnection()) {
            String query = "SELECT task FROM tasks WHERE username = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        taskListView.getItems().add(rs.getString("task"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addTask(String task) {
        try (Connection conn = DatabaseUtils.getConnection()) {
            String query = "INSERT INTO tasks (username, task) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                stmt.setString(2, task);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void removeTask(String task) {
        try (Connection conn = DatabaseUtils.getConnection()) {
            String query = "DELETE FROM tasks WHERE username = ? AND task = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                stmt.setString(2, task);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
