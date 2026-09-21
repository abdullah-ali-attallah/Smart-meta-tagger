package com.smarttagger.controller;
import com.smarttagger.model.*;
import com.smarttagger.db.DatabaseManager;
import com.smarttagger.exception.UnsupportedMediaException;
import com.smarttagger.exception.EmptyDescriptionException;
import java.io.File;
import java.util.List;

public class TagController {
    private DatabaseManager dbManager = new DatabaseManager();
    private AIService aiService = new AIService();
    private String currentUser;

    public void setCurrentUser(String username) { this.currentUser = username; }
    public String getCurrentUser() { return this.currentUser; }
    public DatabaseManager getDb() { return this.dbManager; }

    public void analyzeFileRealAI(File file, ResultCallback callback) {
        new Thread(() -> {
            try {
                // تطبيق الإكسبشن المخصص بتاعك هنا
                String name = file.getName().toLowerCase();
                if (!name.endsWith(".png") && !name.endsWith(".jpg") && !name.endsWith(".jpeg") && !name.endsWith(".webp") && !name.endsWith(".mp4") && !name.endsWith(".avi") && !name.endsWith(".mov")) {
                    throw new UnsupportedMediaException("Error: Unsupported file format! Please upload an Image or Video.");
                }
                String aiDescription = aiService.analyzeImage(file);
                callback.onSuccess(aiDescription);
            } catch (UnsupportedMediaException e) {
                callback.onError(e.getMessage());
            } catch (Exception e) {
                callback.onError("System Error: " + e.getMessage());
            }
        }).start();
    }

    public void processMedia(String type, String fileName, String description, String extraParam, ResultCallback callback) {
        new Thread(() -> {
            try {
                if (description == null || description.trim().isEmpty()) throw new EmptyDescriptionException("Error: Description is empty!");
                MediaItem item;
                if ("Image".equals(type)) {
                    item = new ImageItem(fileName, description, extraParam);
                } else {
                    int duration = 60; 
                    try {
                        String numbersOnly = extraParam.replaceAll("[^0-9]", "");
                        if (!numbersOnly.isEmpty()) duration = Integer.parseInt(numbersOnly);
                    } catch (Exception ex) {} 
                    item = new VideoItem(fileName, description, duration);
                }
                List<String> tags = item.generateTags();
                callback.onSuccess(String.join(" ", tags));
            } catch (Exception e) { callback.onError(e.getMessage()); }
        }).start();
    }

    public interface ResultCallback { void onSuccess(String result); void onError(String error); }
}
