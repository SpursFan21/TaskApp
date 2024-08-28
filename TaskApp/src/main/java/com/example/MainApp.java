package com.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Login");

        // Create UI elements
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button loginButton = new Button("Login");
        Button registerButton = new Button("Register");

        // Create a layout and add UI elements
        VBox layout = new VBox(10, usernameField, passwordField, loginButton, registerButton);
        layout.setStyle("-fx-padding: 20;");

        // Handle button clicks
        loginButton.setOnAction(e -> handleLogin(usernameField.getText(), passwordField.getText(), primaryStage));
        registerButton.setOnAction(e -> handleRegistration(usernameField.getText(), passwordField.getText()));

        // Set up the scene and stage
        Scene scene = new Scene(layout, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handleLogin(String username, String password, Stage loginStage) {
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
                            TaskPage taskPage = new TaskPage(username);
                            Stage taskStage = new Stage();
                            taskPage.show(taskStage);
                            loginStage.close(); // Close the login stage
                        } else {
                            System.out.println("Invalid username or password.");
                        }
                    } else {
                        System.out.println("Invalid username or password.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    

    private void handleRegistration(String username, String password) {
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
    

    public static void main(String[] args) {
        launch(args);
    }
}
