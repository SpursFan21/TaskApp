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
    private ListView<TaskItem> taskListView;

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
        Button logOutButton = new Button("Log Out");

        taskListView = new ListView<>();
        taskListView.setCellFactory(param -> new TaskCell(taskListView));

        // Load tasks from the database
        loadTasks();

        // Add functionality to buttons
        addButton.setOnAction(e -> {
            String task = taskField.getText();
            String status = statusComboBox.getValue();
            if (!task.isEmpty() && status != null) {
                addTask(task, status);
                taskField.clear();
                statusComboBox.setValue("Not Started");
                loadTasks(); // Refresh task list
            }
        });

        removeButton.setOnAction(e -> {
            TaskItem selectedItem = taskListView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                removeTask(selectedItem.getTask());
                loadTasks(); // Refresh task list
            }
        });

        logOutButton.setOnAction(e -> {
            // Close current stage and return to MainApp
            primaryStage.close();
            MainApp.show();
        });

        // Layout
        VBox layout = new VBox(10, logOutButton, taskField, statusComboBox, addButton, removeButton, taskListView);
        layout.setStyle("-fx-padding: 20;");

        // Scene and Stage
        Scene scene = new Scene(layout, 500, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void loadTasks() {
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
                        taskListView.getItems().add(new TaskItem(task, status));
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

    private class TaskCell extends ListCell<TaskItem> {
        private final Button statusButton = new Button("Status");
        private final ListView<TaskItem> listView;

        public TaskCell(ListView<TaskItem> listView) {
            this.listView = listView;
            statusButton.setOnAction(e -> {
                TaskItem taskItem = getItem();
                if (taskItem != null) {
                    // Create a context menu for status selection
                    ContextMenu contextMenu = new ContextMenu();
                    MenuItem notStarted = new MenuItem("Not Started");
                    MenuItem inProgress = new MenuItem("In Progress");
                    MenuItem complete = new MenuItem("Complete");

                    notStarted.setOnAction(ev -> updateStatus(taskItem, "Not Started"));
                    inProgress.setOnAction(ev -> updateStatus(taskItem, "In Progress"));
                    complete.setOnAction(ev -> updateStatus(taskItem, "Complete"));

                    contextMenu.getItems().addAll(notStarted, inProgress, complete);
                    contextMenu.show(statusButton, getScene().getWindow().getX() + 100, getScene().getWindow().getY() + 100);
                }
            });
        }

        @Override
        protected void updateItem(TaskItem item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
                setGraphic(null);
            } else {
                setText(item.getTask() + " (" + item.getStatus() + ")");
                setGraphic(statusButton);
            }
        }

        private void updateStatus(TaskItem taskItem, String status) {
            updateTaskStatus(taskItem.getTask(), status);
            loadTasks(); // Refresh task list
        }
    }

    private static class TaskItem {
        private final String task;
        private final String status;

        public TaskItem(String task, String status) {
            this.task = task;
            this.status = status;
        }

        public String getTask() {
            return task;
        }

        public String getStatus() {
            return status;
        }
    }
}
