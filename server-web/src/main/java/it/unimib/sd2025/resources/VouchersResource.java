package it.unimib.sd2025.resources;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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

@Path("/vouchers")
public class VouchersResource {

    private final DatabaseConnection databaseConnection = new DatabaseConnection("localhost", 3030);

    private final ConcurrentHashMap<String, Object> userLocks = new ConcurrentHashMap<>();

    /**
     * Retrieves a list of vouchers based on query parameters. Allows filtering
     * by userId, type, and used status.
     *
     * @param uriInfo the URI information containing query parameters
     * @return a Response containing the list of vouchers or an error message
     */
    @GET
    @Produces("application/json")
    public Response getVouchers(@Context UriInfo uriInfo) {
        MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();

        try {
            Voucher[] vouchers = databaseConnection.getCollection("vouchers", Voucher[].class);

            List<Voucher> filteredVouchers = new ArrayList<>();

            for (Voucher voucher : vouchers) {
                boolean matches = true;

                if (queryParams.containsKey("userId") && !voucher.getUserId().equalsIgnoreCase(queryParams.getFirst("userId"))) {
                    matches = false;
                }
                if (queryParams.containsKey("type") && !voucher.getType().toString().equalsIgnoreCase(queryParams.getFirst("type"))) {
                    matches = false;
                }
                if (queryParams.containsKey("used")) {
                    boolean used = Boolean.parseBoolean(queryParams.getFirst("used"));
                    if (voucher.isUsed() != used) {
                        matches = false;
                    }
                }

                if (matches) {
                    filteredVouchers.add(voucher);
                }
            }

            return Response.ok(filteredVouchers).build();
        } catch (DatabaseException e) {
            return Response.status(e.getErrorCode())
                    .entity(e.getMessage())
                    .build();
        }
    }

    /**
     * Retrieves a voucher by its ID.
     *
     * @param voucherId the ID of the voucher to retrieve
     * @return a Response containing the voucher or an error message
     */
    @GET
    @Path("/{voucherId}")
    @Produces("application/json")
    public Response getVoucherById(@PathParam("voucherId") String voucherId) {
        try {
            Voucher voucher = databaseConnection.getDocument("vouchers", voucherId, Voucher.class);

            return Response.ok(voucher).build();
        } catch (DatabaseException e) {
            return Response.status(e.getErrorCode())
                    .entity(e.getMessage())
                    .build();
        }
    }

    /**
     * Updates a voucher by its ID. Allows updating the type and used status of
     * the voucher. If the voucher is already used, it cannot be updated. If the
     * used status is set to true, the consume date is also set to the current
     * date.
     *
     * @param voucherId the ID of the voucher to update
     * @param updates a map containing the fields to update
     * @return a Response indicating success or failure
     */
    @PUT
    @Path("/{voucherId}")
    @Consumes("application/json")
    @Produces("application/json")
    public Response updateVoucher(@PathParam("voucherId") String voucherId, Map<String, Object> updates) {
        try {
            Voucher existingVoucher = databaseConnection.getDocument("vouchers", voucherId, Voucher.class);

            if (existingVoucher.isUsed()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Impossibile aggiornare un buono già utilizzato")
                        .build();
            }

            if (updates.containsKey("type")) {
                existingVoucher.setType(Voucher.VoucherType.fromString((String) updates.get("type")));
            }
            if (updates.containsKey("used") && (Boolean) updates.get("used")) {
                existingVoucher.setUsed(true);
                existingVoucher.setConsumeDate(new Date());
            }

            databaseConnection.updateDocument("vouchers", voucherId, existingVoucher);

            return Response.ok(existingVoucher).build();

        } catch (DatabaseException e) {
            return Response.status(e.getErrorCode())
                    .entity(e.getMessage())
                    .build();
        }
    }

    /**
     * Creates a new voucher. Requires userId, value, and type parameters. The
     * value must be a positive integer and the user must have sufficient funds.
     *
     * @param voucherData a map containing the voucher data
     * @return a Response indicating success or failure
     */
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Response createVoucher(Map<String, String> voucherData) {
        try {
            Voucher voucher = new Voucher();

            if (!voucherData.containsKey("userId") || !voucherData.containsKey("value") || !voucherData.containsKey("type")) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("userId, value e type sono obbligatori")
                        .build();
            }

            String userId = voucherData.get("userId");

            Object userLock = userLocks.computeIfAbsent(userId, k -> new Object());

            synchronized (userLock) {
                User user = databaseConnection.getDocument("users", userId, User.class);

                int value;

                try {
                    value = Integer.parseInt(voucherData.get("value"));
                    if (value <= 0) {
                        return Response.status(Response.Status.BAD_REQUEST)
                                .entity("Il valore deve essere un intero positivo")
                                .build();
                    }

                    if (user.getMoneyLeft() < value) {
                        return Response.status(Response.Status.BAD_REQUEST)
                                .entity("Fondi insufficienti per l'utente")
                                .build();
                    }
                } catch (NumberFormatException e) {
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("Valore non valido, deve essere un intero positivo")
                            .build();
                }

                voucher.setUserId(userId);
                voucher.setValue(value);

                String typeParam = voucherData.get("type");
                Voucher.VoucherType type;

                try {
                    type = Voucher.VoucherType.fromString(typeParam);
                    voucher.setType(type);
                } catch (IllegalArgumentException e) {
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("Tipo di buono non valido: " + typeParam)
                            .build();
                }

                String voucherId = UUID.randomUUID().toString();
                voucher.setId(voucherId);
                voucher.setCreationDate(new Date());
                voucher.setUsed(false);

                databaseConnection.createDocument("vouchers", voucherId, voucher);

                user.setMoneyLeft(user.getMoneyLeft() - voucher.getValue());
                databaseConnection.updateDocument("users", user.getId(), user);

                return Response.status(Response.Status.CREATED)
                        .entity(voucher)
                        .build();
            }
        } catch (DatabaseException e) {
            return Response.status(e.getErrorCode())
                    .entity(e.getMessage())
                    .build();
        }
    }

    /**
     * Deletes a voucher by its ID. If the voucher is already used, it cannot be
     * deleted. The user's money is refunded if the voucher is successfully
     * deleted.
     *
     * @param voucherId the ID of the voucher to delete
     * @return a Response indicating success or failure
     */
    @DELETE
    @Path("/{voucherId}")
    @Produces("application/json")
    public Response deleteVoucher(@PathParam("voucherId") String voucherId) {
        try {
            Voucher existingVoucher = databaseConnection.getDocument("vouchers", voucherId, Voucher.class);

            if (existingVoucher.isUsed()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Impossibile eliminare un buono già utilizzato")
                        .build();
            }

            databaseConnection.deleteDocument("vouchers", voucherId);

            User user = databaseConnection.getDocument("users", existingVoucher.getUserId(), User.class);
            user.setMoneyLeft(user.getMoneyLeft() + existingVoucher.getValue());
            databaseConnection.updateDocument("users", user.getId(), user);

            return Response.status(Response.Status.NO_CONTENT)
                    .entity("Buono eliminato con successo")
                    .build();

        } catch (DatabaseException e) {
            return Response.status(e.getErrorCode())
                    .entity(e.getMessage())
                    .build();
        }
    }
}
