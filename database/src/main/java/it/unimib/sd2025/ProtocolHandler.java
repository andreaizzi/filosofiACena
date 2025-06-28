package it.unimib.sd2025;

import java.util.Arrays;

import it.unimib.sd2025.db.Collection;
import it.unimib.sd2025.db.Database;
import it.unimib.sd2025.db.Document;

/**
 * Handles requests to the database based on a simple protocol. The protocol
 * supports operations like GET, PUT, POST, DELETE, CREATE, and SAVE. Each
 * operation is defined by a specific command followed by parameters.
 */
public class ProtocolHandler {

    private final Database database;

    public ProtocolHandler(Database database) {
        this.database = database;
    }

    /**
     * Handles a request based on the protocol defined.
     *
     * @param request the request string to handle
     * @return a response string which contains either the result of the
     * operation or an error message
     */
    public String handle(String request) {
        String[] parts = request.split(" ");
        if (parts.length < 2) {
            return "ERR 400 Invalid request format";
        }

        String command = parts[0].toUpperCase();
        String collectionName = parts[1];
        String documentId = (parts.length > 2) ? parts[2] : null;
        String documentData = (parts.length > 3) ? String.join(" ", Arrays.copyOfRange(parts, 3, parts.length)) : null;

        return switch (command) {
            case "GET" ->
                doGet(collectionName, documentId);
            case "PUT" ->
                doPut(collectionName, documentId, documentData);
            case "POST" ->
                doPost(collectionName, documentId, documentData);
            case "DELETE" ->
                doDelete(collectionName, documentId);
            case "CREATE" ->
                doCreate(collectionName);
            case "SAVE" ->
                database.saveStatus(collectionName);
            default ->
                "ERR 400 Invalid request format";
        };
    }

    /**
     * Handles a GET request for a collection or a specific document.
     *
     * @param collectionName the name of the collection
     * @param documentId the ID of the document (can be null for all documents
     * in the collection)
     * @return a response string which contains either the document content or
     * an error message
     */
    private String doGet(String collectionName, String documentId) {
        Collection collection = database.getCollection(collectionName);

        if (collection == null) {
            return "ERR 404 Collection not found";
        }

        if (documentId == null) {
            return collection.documentsList().stream()
                    .map(Document::getContent)
                    .toList()
                    .toString();
        }

        Document document = collection.getDocument(documentId);

        if (document == null) {
            return "ERR 404 Document not found";
        }

        return document.getContent();
    }

    /**
     * Handles a PUT request to update a document in a collection.
     *
     * @param collectionName the name of the collection
     * @param documentId the ID of the document
     * @param document the content of the document
     * @return a response string indicating success or failure
     */
    private String doPut(String collectionName, String documentId, String documentData) {
        Collection collection = database.getCollection(collectionName);

        if (collection == null) {
            return "ERR 404 Collection not found";
        }

        Document document = collection.getDocument(documentId);

        if (document == null) {
            return "ERR 404 Document not found";
        }

        document.setContent(documentData);

        return "Document updated";
    }

    /**
     * Handles a POST request to create a new document in a collection.
     *
     * @param collectionName the name of the collection, this will be created if
     * it does not exist
     * @param documentId the ID of the document
     * @param documentData the content of the document
     * @return a response string indicating success or failure
     */
    private String doPost(String collectionName, String documentId, String documentData) {
        Collection collection = database.getCollection(collectionName);

        if (collection == null) {
            collection = new Collection(collectionName);
            database.putCollection(collection);
        }

        if (documentId == null || documentData == null) {
            return "ERR 400 Document ID and data must be provided for POST";
        }

        if (collection.containsDocument(documentId)) {
            return "ERR 409 Document already exists";
        }

        Document document = new Document(documentId, documentData);
        collection.putDocument(document);

        return "Document created";
    }

    /**
     * Handles a DELETE request to remove a document from a collection.
     *
     * @param collectionName the name of the collection
     * @param documentId the ID of the document to delete
     * @return a response string indicating success or failure
     */
    private String doDelete(String collectionName, String documentId) {
        Collection collection = database.getCollection(collectionName);

        if (collection == null) {
            return "ERR 404 Collection not found";
        }

        Document document = collection.getDocument(documentId);

        if (document == null) {
            return "ERR 404 Document not found";
        }

        collection.removeDocument(documentId);
        return "Document deleted";
    }

    /**
     * Handles a CREATE request to create a new collection.
     *
     * @param collectionName the name of the collection to create
     * @return a response string indicating success or failure
     */
    private String doCreate(String collectionName) {
        if (database.containsCollection(collectionName)) {
            return "ERR 409 Collection already exists";
        }

        Collection collection = new Collection(collectionName);
        database.putCollection(collection);

        return "Collection created";
    }
}
