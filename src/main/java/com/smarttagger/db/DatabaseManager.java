package com.smarttagger.db;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String URL = "jdbc:sqlite:smart_tagger.db";

    public DatabaseManager() {
        try (Connection conn = DriverManager.getConnection(URL); Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS saved_tags (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, file_name TEXT, tags TEXT)");
            stmt.execute("CREATE TABLE IF NOT EXISTS users (username TEXT PRIMARY KEY, password TEXT NOT NULL)");
        } catch (SQLException e) { System.out.println("DB Error: " + e.getMessage()); }
    }

    public boolean registerUser(String username, String password) {
        String sql = "INSERT INTO users(username, password) VALUES(?, ?)";
        try (Connection conn = DriverManager.getConnection(URL); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username); pstmt.setString(2, password); pstmt.executeUpdate(); return true;
        } catch (SQLException e) { return false; }
    }

    public boolean loginUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DriverManager.getConnection(URL); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username); pstmt.setString(2, password); return pstmt.executeQuery().next();
        } catch (SQLException e) { return false; }
    }

    public List<String> getAllUsers() {
        List<String> users = new ArrayList<>();
        String sql = "SELECT username FROM users";
        try (Connection conn = DriverManager.getConnection(URL); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) users.add("👤 " + rs.getString("username"));
        } catch (SQLException e) { System.out.println("Error: " + e.getMessage()); }
        return users;
    }

    public void saveTags(String username, String fileName, String tags) {
        String sql = "INSERT INTO saved_tags(username, file_name, tags) VALUES(?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username); pstmt.setString(2, fileName); pstmt.setString(3, tags); pstmt.executeUpdate();
        } catch (SQLException e) { System.out.println("Save Error: " + e.getMessage()); }
    }

    public List<String> getHistory(String username) {
        List<String> history = new ArrayList<>();
        String sql = "SELECT * FROM saved_tags WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(URL); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username); ResultSet rs = pstmt.executeQuery();
            while (rs.next()) history.add(rs.getString("file_name") + " -> " + rs.getString("tags"));
        } catch (SQLException e) { System.out.println("Read Error: " + e.getMessage()); }
        return history;
    }
}
