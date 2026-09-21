package com.smarttagger.view;

import com.smarttagger.controller.TagController;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.util.List;

public class LoginApp extends Application {
    private TagController controller = new TagController();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Smart Meta Tagger | Secure Login");

        // --- Main Container ---
        VBox root = new VBox(25);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #f4f7f6;"); // Light neutral background

        // --- Login Card ---
        VBox card = new VBox(20);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(30));
        card.setMaxWidth(350);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15;");
        
        // Add a subtle drop shadow to the card
        DropShadow shadow = new DropShadow();
        shadow.setRadius(15);
        shadow.setColor(Color.rgb(0, 0, 0, 0.1));
        card.setEffect(shadow);

        // --- Components ---
        Label title = new Label("Smart Meta Tagger");
        title.setStyle("-fx-font-size: 24px; -fx-font-family: 'Segoe UI', sans-serif; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label subtitle = new Label("Please sign in to continue");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #7f8c8d;");

        TextField userField = new TextField();
        userField.setPromptText("Username");
        styleTextField(userField);

        PasswordField passField = new PasswordField();
        passField.setPromptText("Password");
        styleTextField(passField);

        // Main Action Button
        Button loginBtn = new Button("SIGN IN");
        stylePrimaryButton(loginBtn);
        loginBtn.setPrefWidth(250);

        // Secondary Buttons Row
        HBox actionRow = new HBox(15);
        actionRow.setAlignment(Pos.CENTER);
        Button signupBtn = new Button("Create Account");
        styleSecondaryButton(signupBtn);
        
        Button showUsersBtn = new Button("View Users");
        styleSecondaryButton(showUsersBtn);
        
        actionRow.getChildren().addAll(signupBtn, showUsersBtn);

        Label msgLabel = new Label();
        msgLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");

        // --- Logic ---
        loginBtn.setOnAction(e -> {
            if (controller.getDb().loginUser(userField.getText(), passField.getText())) {
                controller.setCurrentUser(userField.getText());
                new MainApp(controller).start(new Stage());
                primaryStage.close();
            } else {
                msgLabel.setText("Invalid username or password");
            }
        });

        signupBtn.setOnAction(e -> {
            if (controller.getDb().registerUser(userField.getText(), passField.getText())) {
                msgLabel.setStyle("-fx-text-fill: #27ae60;");
                msgLabel.setText("Registration successful!");
            } else {
                msgLabel.setStyle("-fx-text-fill: #e74c3c;");
                msgLabel.setText("Username already exists.");
            }
        });

        showUsersBtn.setOnAction(e -> {
            List<String> users = controller.getDb().getAllUsers();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Database Records");
            alert.setHeaderText(null);
            alert.setContentText(users.isEmpty() ? "No users registered yet." : "Registered Users:\n" + String.join(", ", users));
            alert.showAndWait();
        });

        // --- Assemble ---
        card.getChildren().addAll(title, subtitle, userField, passField, loginBtn, actionRow, msgLabel);
        root.getChildren().add(card);

        Scene scene = new Scene(root, 450, 550);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // --- Modern Styling Helpers ---
    private void styleTextField(TextField field) {
        field.setPrefHeight(40);
        field.setStyle("-fx-background-color: #ecf0f1; -fx-background-radius: 5; -fx-border-color: transparent; -fx-padding: 0 10 0 10;");
        
        // Change border color on focus
        field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) field.setStyle("-fx-background-color: #ecf0f1; -fx-background-radius: 5; -fx-border-color: #3498db; -fx-border-radius: 5; -fx-padding: 0 10 0 10;");
            else field.setStyle("-fx-background-color: #ecf0f1; -fx-background-radius: 5; -fx-border-color: transparent; -fx-padding: 0 10 0 10;");
        });
    }

    private void stylePrimaryButton(Button btn) {
        btn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;"));
    }

    private void styleSecondaryButton(Button btn) {
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #34495e; -fx-border-color: #bdc3c7; -fx-border-radius: 5; -fx-cursor: hand;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #eeeeee; -fx-text-fill: #34495e; -fx-border-color: #34495e; -fx-border-radius: 5; -fx-cursor: hand;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #34495e; -fx-border-color: #bdc3c7; -fx-border-radius: 5; -fx-cursor: hand;"));
    }

    public static void main(String[] args) { launch(args); }
}