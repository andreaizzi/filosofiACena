package it.unimib.sd2025.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import it.unimib.sd2025.models.User;
import it.unimib.sd2025.utils.DatabaseConnection;
import it.unimib.sd2025.utils.DatabaseException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

@Path("/users")
public class UsersResource {

    private final DatabaseConnection databaseConnection = new DatabaseConnection("localhost", 3030);

    @GET
    @Produces("application/json")
    public Response getUsers(@Context UriInfo uriInfo) {
        MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();

        try {
            User[] users = databaseConnection.getCollection("users", User[].class);

            List<User> filteredUsers = new ArrayList<>();

            for (User user : users) {
                boolean matches = true;

                if (queryParams.containsKey("name") && !user.getName().equalsIgnoreCase(queryParams.getFirst("name"))) {
                    matches = false;
                }
                if (queryParams.containsKey("surname") && !user.getSurname().equalsIgnoreCase(queryParams.getFirst("surname"))) {
                    matches = false;
                }
                if (queryParams.containsKey("email") && !user.getEmail().equalsIgnoreCase(queryParams.getFirst("email"))) {
                    matches = false;
                }
                if (queryParams.containsKey("cf") && !user.getCf().equalsIgnoreCase(queryParams.getFirst("cf"))) {
                    matches = false;
                }

                if (matches) {
                    filteredUsers.add(user);
                }
            }

            return Response.ok(filteredUsers).build();
        } catch (DatabaseException e) {
            return Response.status(e.getErrorCode())
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @GET
    @Path("/{userId}")
    @Produces("application/json")
    public Response getUser(@PathParam("userId") String userId) {
        try {
            User user = databaseConnection.getDocument("users", userId, User.class);

            return Response.ok(user).build();
        } catch (DatabaseException e) {
            return Response.status(e.getErrorCode())
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @PUT
    @Path("/{userId}")
    @Consumes("application/json")
    @Produces("application/json")
    public Response updateUser(@PathParam("userId") String userId, Map<String, String> updates) {
        try {
            User existingUser = databaseConnection.getDocument("users", userId, User.class);

            if (updates.containsKey("name")) {
                existingUser.setName(updates.get("name"));
            }
            if (updates.containsKey("surname")) {
                existingUser.setSurname(updates.get("surname"));
            }
            if (updates.containsKey("email")) {
                existingUser.setEmail(updates.get("email"));
            }
            if (updates.containsKey("cf")) {
                existingUser.setCf(updates.get("cf"));
            }

            databaseConnection.updateDocument("users", userId, existingUser);

            return Response.ok(existingUser).build();

        } catch (DatabaseException e) {
            return Response.status(e.getErrorCode())
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Response createUser(User user) {
        try {
            if (user.getName() == null || user.getName().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Name is required\"}")
                        .build();
            }

            if (user.getSurname() == null || user.getSurname().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Surname is required\"}")
                        .build();
            }

            if (user.getEmail() == null || user.getEmail().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Email is required\"}")
                        .build();
            }

            if (user.getCf() == null || user.getCf().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"CF is required\"}")
                        .build();
            }

            String userId = UUID.randomUUID().toString();
            user.setId(userId);
            user.setMoneyLeft(500);

            databaseConnection.createDocument("users", userId, user);

            return Response.status(Response.Status.CREATED)
                    .entity(user)
                    .build();
        } catch (DatabaseException e) {
            return Response.status(e.getErrorCode())
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @DELETE
    @Path("/{userId}")
    @Produces("application/json")
    public Response deleteUser(@PathParam("userId") String userId) {
        try {
            databaseConnection.deleteDocument("users", userId);
            return Response.noContent().build();
        } catch (DatabaseException e) {
            return Response.status(e.getErrorCode())
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }
}
