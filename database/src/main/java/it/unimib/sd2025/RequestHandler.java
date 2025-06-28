package it.unimib.sd2025;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import it.unimib.sd2025.db.Database;

/**
 * Handles incoming requests from clients. Each request is processed in a
 * separate thread to allow concurrent handling of multiple clients.
 */
class RequestHandler extends Thread {

    private final Socket clientSocket;
    private final ProtocolHandler protocolHandler;

    public RequestHandler(Socket clientSocket, Database database) {
        this.clientSocket = clientSocket;
        this.protocolHandler = new ProtocolHandler(database);
    }

    /**
     * Handles incoming requests from the client. It reads input from the
     * client, processes it using the ProtocolHandler, and sends back the
     * response.
     */
    @Override
    public void run() {
        try (clientSocket; PrintWriter outputStream = new PrintWriter(clientSocket.getOutputStream(), true); BufferedReader inputStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));) {
            String inputLine;

            while ((inputLine = inputStream.readLine()) != null) {
                if (inputLine.startsWith("CLOSE")) {
                    outputStream.println("Connection closed");
                    break;
                }

                String response = protocolHandler.handle(inputLine);

                outputStream.println(response);
            }

        } catch (IOException e) {
            System.err.println(e);
        }
    }
}
