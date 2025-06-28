package it.unimib.sd2025;

import java.io.IOException;
import java.net.ServerSocket;

import it.unimib.sd2025.db.Database;

/**
 * Represents a database server that listens for incoming connections on a
 * specified port. It uses RequestHandler to process requests from clients.
 */
class DatabaseServer {

    private final Database database;

    private final int port;

    public DatabaseServer(int port, Database database) {
        this.port = port;
        this.database = database;
    }

    /**
     * Starts the database server on the specified port. Listens for incoming
     * connections and handles requests using RequestHandler.
     *
     * @throws IOException if an I/O error occurs when opening the socket
     */
    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);

        System.out.println("⚡️ Database Server listening on port " + port);

        try {
            while (true) {
                new RequestHandler(serverSocket.accept(), database).start();
            }
        } finally {
            serverSocket.close();
        }
    }
}
