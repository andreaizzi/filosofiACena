package it.unimib.sd2025.resources;

import java.util.Map;

import it.unimib.sd2025.utils.DatabaseConnection;
import it.unimib.sd2025.utils.DatabaseException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

@Path("/save-database")
public class SaveDatabaseResource {

    private final DatabaseConnection databaseConnection = new DatabaseConnection("localhost", 3030);

    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Response saveDatabase(Map<String, String> request) throws DatabaseException {
        String message = databaseConnection.saveDatabase(request.getOrDefault("filename", "database.json"));
        return Response.ok("{\"message\": \"" + message + "\"}").build();
    }
}
