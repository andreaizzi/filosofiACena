package it.unimib.sd2025.db;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a collection of documents in the database. Each collection has a
 * unique identifier and contains multiple documents.
 */
public class Collection {

    private String id;
    private ConcurrentHashMap<String, Document> documents;

    public Collection(String id) {
        this.id = id;
        this.documents = new ConcurrentHashMap<>();
    }

    public Collection() {
        // Default constructor for deserialization
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ConcurrentHashMap<String, Document> getDocuments() {
        return documents;
    }

    public void setDocuments(ConcurrentHashMap<String, Document> documents) {
        this.documents = documents;
    }

    public Document getDocument(String documentId) {
        return documents.get(documentId);
    }

    public List<Document> documentsList() {
        return new ArrayList<>(documents.values());
    }

    public boolean containsDocument(String documentId) {
        return documents.containsKey(documentId);
    }

    public void putDocument(Document document) {
        documents.put(document.getId(), document);
    }

    public void removeDocument(String documentId) {
        documents.remove(documentId);
    }
}
