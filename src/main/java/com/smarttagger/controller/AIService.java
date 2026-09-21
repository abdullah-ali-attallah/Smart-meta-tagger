package com.smarttagger.controller;
import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.util.Base64;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class AIService {
    private static final String API_KEY = "AIzaSyDPLhaz_FRraszZOMzP6_jXzKsttseiS1I"; 
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + API_KEY;

    public String analyzeImage(File mediaFile) throws Exception {
        long fileSizeInMB = mediaFile.length() / (1024 * 1024);
        if (fileSizeInMB > 15) return "Error: Video is too large (" + fileSizeInMB + " MB). Please select a file under 15 MB.";

        byte[] fileContent = Files.readAllBytes(mediaFile.toPath());
        if (fileContent.length == 0) return "Error: The selected file is empty!";

        String base64Data = Base64.getEncoder().encodeToString(fileContent).replace("\n", "").replace("\r", "");
        String fileName = mediaFile.getName().toLowerCase();
        String mimeType = "image/jpeg"; 
        
        if (fileName.endsWith(".png")) mimeType = "image/png";
        else if (fileName.endsWith(".webp")) mimeType = "image/webp";
        else if (fileName.endsWith(".mp4") || fileName.endsWith(".avi") || fileName.endsWith(".mov")) mimeType = "video/mp4";

        String jsonBody = "{\"contents\": [{\"parts\": [{\"inline_data\": {\"mime_type\": \"" + mimeType + "\", \"data\": \"" + base64Data + "\"}},{\"text\": \"Analyze this media file and provide a brief description and 5 tags.\"}]}]}";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(API_URL)).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(jsonBody)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 200) return "API Error (" + response.statusCode() + "): " + response.body();

        JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
        return jsonObject.getAsJsonArray("candidates").get(0).getAsJsonObject().getAsJsonObject("content").getAsJsonArray("parts").get(0).getAsJsonObject().get("text").getAsString().trim();
    }
}
