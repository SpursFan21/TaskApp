package com.example;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TaskPageController {

    @FXML
    private TextField taskField;
    @FXML
    private Button addButton;
    @FXML
    private Button removeButton;
    @FXML
    private Button logOutButton;
    @FXML
    private ListView<TaskItem> taskListView;
    @FXML
    private Label welcomeLabel;
    @FXML
    private ComboBox<String> statusComboBox;

    private String username;
    private MainApp mainApp;

    public void setUsername(String username) {
        this.username = username;
        welcomeLabel.setText("Welcome, " + username);
        loadTasks();
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }


    @FXML
    public void initialize() {
        // Initialize statusComboBox with options
        statusComboBox.setItems(FXCollections.observableArrayList("Not Started", "In Progress", "Complete"));
        statusComboBox.setValue("Not Started");
    
        addButton.setOnAction(e -> addTask());
        removeButton.setOnAction(e -> removeTask());
        logOutButton.setOnAction(e -> logOut());
    
        // Customize ListView cell factory
        taskListView.setCellFactory(lv -> new ListCell<TaskItem>() {
            @Override
            protected void updateItem(TaskItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox container = new HBox(10);
                    
                    Button statusButton = new Button("Update Status");
                    
                    ComboBox<String> statusComboBox = new ComboBox<>(FXCollections.observableArrayList("Not Started", "In Progress", "Complete"));
                    statusComboBox.setValue(item.getStatus());

                    Label statusLabel = new Label(item.getStatus());
    
                    Label taskLabel = new Label(item.getTask());
    
                    statusButton.setOnAction(e -> {
                        String newStatus = statusComboBox.getValue();
                        if (newStatus != null) {
                            updateTaskStatus(item.getTask(), newStatus);
                            statusLabel.setText(newStatus);
                        } else {
                            System.err.println("StatusComboBox value is null");
                        }
                    });
    
                    container.getChildren().addAll(statusButton, statusComboBox, statusLabel, taskLabel);
                    
                    setGraphic(container);
                }
            }
        });
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
                            status = "Unknown";
                        }
                        taskListView.getItems().add(new TaskItem(task, status));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addTask() {
        String task = taskField.getText();
        String status = statusComboBox.getValue(); // Get status from ComboBox
    
        if (!task.isEmpty()) {
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
            taskField.clear();
            statusComboBox.setValue("Not Started"); // Reset to default value
            loadTasks();
        }
    }
    

    private void removeTask() {
        TaskItem selectedItem = taskListView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            try (Connection conn = DatabaseUtils.getConnection()) {
                String query = "DELETE FROM tasks WHERE username = ? AND task = ?";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, username);
                    stmt.setString(2, selectedItem.getTask());
                    stmt.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            loadTasks();
        }
    }

    private void logOut() {
        // Close the current window
        Stage currentStage = (Stage) logOutButton.getScene().getWindow();
        currentStage.close();

        // Redirect to MainApp page
        if (mainApp != null) {
            mainApp.showMainPage(); // Ensure this method exists in MainApp
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
        loadTasks(); // Refresh the task list to show the updated status
    }

    private class TaskItem {
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

        @Override
        public String toString() {
            return task + " (" + status + ")";
        }
    }
}
