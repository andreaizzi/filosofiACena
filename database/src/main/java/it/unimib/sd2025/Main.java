package it.unimib.sd2025;

import java.io.IOException;

import it.unimib.sd2025.db.Database;

public class Main {

    public static void main(String[] args) throws IOException {
        Database database = Database.fromFile("database.json");

        new DatabaseServer(3030, database)
                .start();
    }
}
