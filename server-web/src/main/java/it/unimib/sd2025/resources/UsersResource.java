package it.unimib.sd2025.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import it.unimib.sd2025.models.User;
import it.unimib.sd2025.models.Voucher;
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

    /**
     * Retrieves a list of users, optionally filtered by query parameters.
     * Filtering can be done by name, surname, email, or CF (Codice Fiscale).
     *
     * @param uriInfo The URI information containing query parameters.
     * @return A response containing the list of users or an error message.
     */
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
                    .entity(e.getMessage())
                    .build();
        }
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param userId The ID of the user to retrieve.
     * @return A response containing the user or an error message.
     */
    @GET
    @Path("/{userId}")
    @Produces("application/json")
    public Response getUser(@PathParam("userId") String userId) {
        try {
            User user = databaseConnection.getDocument("users", userId, User.class);

            return Response.ok(user).build();
        } catch (DatabaseException e) {
            return Response.status(e.getErrorCode())
                    .entity(e.getMessage())
                    .build();
        }
    }

    /**
     * Updates a user by their ID with the provided updates. Updates can be done
     * on name, surname, email, and CF (Codice Fiscale).
     *
     * @param userId The ID of the user to update.
     * @param updates A map containing the fields to update.
     * @return A response indicating the success or failure of the operation.
     */
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
                String email = updates.get("email");
                if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("Email non valida")
                            .build();
                }

                existingUser.setEmail(email);
            }
            if (updates.containsKey("cf")) {
                existingUser.setCf(updates.get("cf"));
            }

            databaseConnection.updateDocument("users", userId, existingUser);

            return Response.ok(existingUser).build();

        } catch (DatabaseException e) {
            return Response.status(e.getErrorCode())
                    .entity(e.getMessage())
                    .build();
        }
    }

    /**
     * Creates a new user with the provided details. The user is initialized
     * with a default amount of money (500).
     *
     * @param user The user object containing the details to create.
     * @return A response indicating the success or failure of the operation.
     */
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Response createUser(User user) {
        try {
            if (user.getName() == null || user.getName().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Il nome è obbligatorio")
                        .build();
            }

            if (user.getSurname() == null || user.getSurname().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Il cognome è obbligatorio")
                        .build();
            }

            if (user.getEmail() == null || user.getEmail().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("L'email è obbligatoria")
                        .build();
            }

            String email = user.getEmail();
            if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Email non valida")
                        .build();
            }

            if (user.getCf() == null || user.getCf().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Il CF è obbligatorio")
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
                    .entity(e.getMessage())
                    .build();
        }
    }

    /**
     * Deletes a user by their ID. This operation also deletes all vouchers
     * associated with the user.
     *
     * @param userId The ID of the user to delete.
     * @return A response indicating the success or failure of the operation.
     */
    @DELETE
    @Path("/{userId}")
    @Produces("application/json")
    public Response deleteUser(@PathParam("userId") String userId) {
        try {
            User user = databaseConnection.getDocument("users", userId, User.class);

            Voucher[] vouchers = databaseConnection.getCollection("vouchers", Voucher[].class);

            for (Voucher voucher : vouchers) {
                if (voucher.getUserId().equals(user.getId())) {
                    databaseConnection.deleteDocument("vouchers", voucher.getId());
                }
            }

            databaseConnection.deleteDocument("users", userId);

            return Response.status(Response.Status.NO_CONTENT)
                    .entity("Utente eliminato con successo")
                    .build();
        } catch (DatabaseException e) {
            return Response.status(e.getErrorCode())
                    .entity(e.getMessage())
                    .build();
        }
    }
}
