package com.example;

import javafx.scene.Scene;
import javafx.scene.control.*;
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

        ComboBox<String> statusComboBox = new ComboBox<>();
        statusComboBox.getItems().addAll("Not Started", "In Progress", "Complete");
        statusComboBox.setValue("Not Started");

        Button addButton = new Button("Add Task");
        Button removeButton = new Button("Remove Task");
        Button updateButton = new Button("Update Task");

        ListView<String> taskListView = new ListView<>();

        // Load tasks from the database
        loadTasks(taskListView);

        // Add functionality to buttons
        addButton.setOnAction(e -> {
            String task = taskField.getText();
            String status = statusComboBox.getValue();
            if (!task.isEmpty() && status != null) {
                addTask(task, status);
                taskField.clear();
                statusComboBox.setValue("Not Started");
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

        updateButton.setOnAction(e -> {
            String selectedTask = taskListView.getSelectionModel().getSelectedItem();
            String newStatus = statusComboBox.getValue();
            if (selectedTask != null && newStatus != null) {
                updateTaskStatus(selectedTask, newStatus);
                loadTasks(taskListView); // Refresh task list
            }
        });

        // Layout
        VBox layout = new VBox(10, taskField, statusComboBox, addButton, removeButton, updateButton, taskListView);
        layout.setStyle("-fx-padding: 20;");

        // Scene and Stage
        Scene scene = new Scene(layout, 500, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void loadTasks(ListView<String> taskListView) {
        taskListView.getItems().clear();
        try (Connection conn = DatabaseUtils.getConnection()) {
            String query = "SELECT task, status FROM tasks WHERE username = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String task = rs.getString("task");
                        String status = rs.getString("status");
                        if (status == null) {
                            status = "Unknown"; // Default status if null
                        }
                        taskListView.getItems().add(task + " (" + status + ")");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    

    private void addTask(String task, String status) {
        try (Connection conn = DatabaseUtils.getConnection()) {
            String query = "INSERT INTO tasks (username, task, status) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                stmt.setString(2, task);
                stmt.setString(3, status);
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

    private void updateTaskStatus(String task, String status) {
        try (Connection conn = DatabaseUtils.getConnection()) {
            String query = "UPDATE tasks SET status = ? WHERE username = ? AND task = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, status);
                stmt.setString(2, username);
                stmt.setString(3, task);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
