package com.smarttagger.view;

import com.smarttagger.controller.TagController;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.util.List;

public class MainApp {
    private TagController controller;
    private String lastTags = "";
    private File selectedFile;

    // Modern Colors
    private final String PRIMARY = "#3498db";
    private final String SECONDARY = "#2ecc71";
    private final String BG_COLOR = "#f0f2f5";
    private final String SIDEBAR_COLOR = "#2c3e50";

    public MainApp(TagController controller) { this.controller = controller; }

    public void start(Stage primaryStage) {
        primaryStage.setTitle("Smart Meta Tagger | Dashboard");

        // --- Main Layout ---
        HBox mainLayout = new HBox();
        mainLayout.setStyle("-fx-background-color: " + BG_COLOR + ";");

        // --- Left Sidebar ---
        VBox sidebar = new VBox(20);
        sidebar.setPrefWidth(200);
        sidebar.setPadding(new Insets(30, 15, 30, 15));
        sidebar.setStyle("-fx-background-color: " + SIDEBAR_COLOR + ";");
        sidebar.setAlignment(Pos.TOP_CENTER);

        Label userIcon = new Label("👤");
        userIcon.setStyle("-fx-font-size: 40px; -fx-text-fill: white;");
        Label userLabel = new Label(controller.getCurrentUser());
        userLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");

        Button historyBtn = createSidebarButton("📊 My History");
        Button logoutBtn = createSidebarButton("🚪 Logout");
        
        sidebar.getChildren().addAll(userIcon, userLabel, new Separator(), historyBtn, logoutBtn);
        VBox.setVgrow(logoutBtn, Priority.ALWAYS); // Push logout to bottom

        // --- Right Content Area ---
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setPrefWidth(600);

        Label header = new Label("Generate Metadata");
        header.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #34495e;");

        // --- Input Section (Card Style) ---
        VBox inputCard = createCard();
        
        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Image", "Video");
        typeBox.setValue("Image");
        typeBox.setPrefWidth(150);

        TextField fileNameField = new TextField();
        fileNameField.setPromptText("No file selected...");
        fileNameField.setEditable(false);
        fileNameField.setPrefWidth(300);
        styleTextField(fileNameField);

        Button browseBtn = new Button("Browse");
        styleSecondaryButton(browseBtn, PRIMARY);

        HBox fileRow = new HBox(10, fileNameField, browseBtn);
        fileRow.setAlignment(Pos.CENTER_LEFT);

        Button autoAiBtn = new Button("✨ Analyze with AI");
        autoAiBtn.setDisable(true);
        stylePrimaryButton(autoAiBtn, "#9b59b6"); // Purple for AI
        autoAiBtn.setMaxWidth(Double.MAX_VALUE);

        TextArea descArea = new TextArea();
        descArea.setPromptText("Visual description will appear here...");
        descArea.setPrefRowCount(3);
        descArea.setWrapText(true);
        styleTextArea(descArea);

        inputCard.getChildren().addAll(new Label("Media Type"), typeBox, new Label("Select File"), fileRow, autoAiBtn, descArea);

        // --- Output Section (Card Style) ---
        VBox outputCard = createCard();
        
        TextField extraField = new TextField("1080p, High Quality, Cinematic");
        styleTextField(extraField);

        Button generateBtn = new Button("🚀 Generate Tags");
        stylePrimaryButton(generateBtn, SECONDARY);
        generateBtn.setPrefWidth(200);

        Button saveBtn = new Button("💾 Save Result");
        saveBtn.setDisable(true);
        styleSecondaryButton(saveBtn, "#e67e22");

        HBox actionRow = new HBox(15, generateBtn, saveBtn);
        
        TextArea resultArea = new TextArea();
        resultArea.setPromptText("Final tags will appear here...");
        resultArea.setPrefRowCount(4);
        resultArea.setEditable(false);
        styleTextArea(resultArea);

        outputCard.getChildren().addAll(new Label("Additional Context"), extraField, actionRow, resultArea);

        content.getChildren().addAll(header, inputCard, outputCard);
        mainLayout.getChildren().addAll(sidebar, content);

        // --- Logic Hooks ---
        browseBtn.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            selectedFile = fc.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                fileNameField.setText(selectedFile.getName());
                autoAiBtn.setDisable(false);
            }
        });

        autoAiBtn.setOnAction(e -> {
            descArea.setText("🤖 AI is looking at your file...");
            controller.analyzeFileRealAI(selectedFile, new TagController.ResultCallback() {
                @Override public void onSuccess(String result) { Platform.runLater(() -> descArea.setText(result)); }
                @Override public void onError(String error) { Platform.runLater(() -> descArea.setText(error)); }
            });
        });

        generateBtn.setOnAction(e -> {
            resultArea.setText("Processing...");
            controller.processMedia(typeBox.getValue(), fileNameField.getText(), descArea.getText(), extraField.getText(), new TagController.ResultCallback() {
                @Override public void onSuccess(String result) {
                    Platform.runLater(() -> { lastTags = result; resultArea.setText(result); saveBtn.setDisable(false); });
                }
                @Override public void onError(String error) { Platform.runLater(() -> resultArea.setText(error)); }
            });
        });

        saveBtn.setOnAction(e -> {
            controller.getDb().saveTags(controller.getCurrentUser(), fileNameField.getText(), lastTags);
            resultArea.setText("✅ Saved successfully to history!");
        });

        historyBtn.setOnAction(e -> showHistoryPopup());
        logoutBtn.setOnAction(e -> { primaryStage.close(); /* Logic to reopen LoginApp */ });

        primaryStage.setScene(new Scene(mainLayout, 850, 700));
        primaryStage.show();
    }

    // --- Styling Helpers ---
    private VBox createCard() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
        DropShadow ds = new DropShadow(10, Color.rgb(0,0,0,0.05));
        card.setEffect(ds);
        return card;
    }

    private Button createSidebarButton(String text) {
        Button btn = new Button(text);
        btn.setPrefWidth(170);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #bdc3c7; -fx-alignment: CENTER_LEFT; -fx-font-size: 13px; -fx-cursor: hand;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-alignment: CENTER_LEFT; -fx-cursor: hand;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #bdc3c7; -fx-alignment: CENTER_LEFT;"));
        return btn;
    }

    private void stylePrimaryButton(Button btn, String color) {
        btn.setStyle("-fx-background-color: "+color+"; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 5; -fx-cursor: hand;");
    }

    private void styleSecondaryButton(Button btn, String color) {
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: "+color+"; -fx-border-color: "+color+"; -fx-border-radius: 5; -fx-padding: 8 16; -fx-cursor: hand;");
    }

    private void styleTextField(TextField tf) {
        tf.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #e0e0e0; -fx-border-radius: 5; -fx-padding: 8;");
    }

    private void styleTextArea(TextArea ta) {
        ta.setStyle("-fx-control-inner-background: #f9f9f9; -fx-viewport-background: #f9f9f9; -fx-border-color: #e0e0e0; -fx-border-radius: 5;");
    }

    private void showHistoryPopup() {
        Stage stage = new Stage();
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: white;");

        ListView<String> listView = new ListView<>();
        List<String> history = controller.getDb().getHistory(controller.getCurrentUser());
        listView.getItems().addAll(history);
        listView.setStyle("-fx-background-radius: 5;");

        root.getChildren().addAll(new Label("Your Saved Metadata Records"), listView);
        stage.setScene(new Scene(root, 400, 500));
        stage.show();
    }
}