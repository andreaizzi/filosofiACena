package it.unimib.sd2025.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

public class DatabaseConnection {

    private final String url;
    private final int port;
    private final Jsonb jsonb;

    public DatabaseConnection(String url, int port) {
        this.url = url;
        this.port = port;
        this.jsonb = JsonbBuilder.create();
    }

    public String getUrl() {
        return url;
    }

    public int getPort() {
        return port;
    }

    private String handleCommand(String command) throws DatabaseException {
        try (Socket clientSocket = new Socket(url, port); PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true); BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

            out.println(command);
            String response = in.readLine();

            if (response.startsWith("ERR")) {
                int errorCode = Integer.parseInt(response.split(" ")[1]);
                String errorMessage = response.substring(4);

                throw new DatabaseException(errorMessage, errorCode);
            }

            return response;

        } catch (IOException e) {
            throw new DatabaseException("Error handling command: " + command + " - " + e.getMessage(), 500);
        }
    }

    public <T> T getCollection(String collectionName, Class<T> type) throws DatabaseException {
        String json = handleCommand("GET " + collectionName);
        return jsonb.fromJson(json, type);
    }

    public <T> T getDocument(String collectionName, String documentId, Class<T> type) throws DatabaseException {
        String json = handleCommand("GET " + collectionName + " " + documentId);
        return jsonb.fromJson(json, type);
    }

    public String updateDocument(java.lang.String collectionName, java.lang.String documentId, java.lang.Object documentData) throws DatabaseException {
        String json = jsonb.toJson(documentData);
        return handleCommand("PUT " + collectionName + " " + documentId + " " + json);
    }

    public String createDocument(java.lang.String collectionName, java.lang.String documentId, java.lang.Object documentData) throws DatabaseException {
        String json = jsonb.toJson(documentData);
        return handleCommand("POST " + collectionName + " " + documentId + " " + json);
    }

    public String deleteDocument(String collectionName, String documentId) throws DatabaseException {
        return handleCommand("DELETE " + collectionName + " " + documentId);
    }

    public String createCollection(String collectionName) throws DatabaseException {
        return handleCommand("CREATE " + collectionName);
    }

    public String saveDatabase(String filename) throws DatabaseException {
        return handleCommand("SAVE " + filename);
    }
}
