package com.example;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MainAppController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Button registerButton;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        Stage stage = (Stage) loginButton.getScene().getWindow();  // Get the current stage

        try (Connection conn = DatabaseUtils.getConnection()) {
            String query = "SELECT password FROM users WHERE username = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String storedHash = rs.getString("password");
                        if (BCrypt.checkpw(password, storedHash)) {
                            System.out.println("Login successful!");
                            // Open the Task Page
                            try {
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("TaskPage.fxml")); // Ensure the correct path
                                Parent root = loader.load();
                                TaskPageController controller = loader.getController();
                                controller.setUsername(username); // Pass the username to the controller if needed

                                Stage taskStage = new Stage();
                                taskStage.setScene(new Scene(root));
                                taskStage.setTitle("Task Page");
                                taskStage.show();

                                stage.close(); // Close the login stage
                            } catch (IOException e) {
                                System.err.println("Failed to load TaskPage: " + e.getMessage());
                                e.printStackTrace();
                            }
                        } else {
                            System.out.println("Invalid username or password.");
                        }
                    } else {
                        System.out.println("Invalid username or password.");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @FXML
    private void handleRegistration() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try (Connection conn = DatabaseUtils.getConnection()) {
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            String query = "INSERT INTO users (username, password) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                stmt.setString(2, hashedPassword); // Store hashed password
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Registration successful!");
                } else {
                    System.out.println("Registration failed.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
