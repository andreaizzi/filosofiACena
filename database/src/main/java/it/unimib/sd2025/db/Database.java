package it.unimib.sd2025.db;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Represents a database that contains multiple collections. Each collection can
 * be accessed by its unique identifier.
 */
public class Database {

    private final ConcurrentHashMap<String, Collection> collections;

    public Database() {
        this.collections = new ConcurrentHashMap<>();
    }

    /**
     * Constructs a Database instance with the given collections. Used for
     * deserialization.
     *
     * @param collections A map of collection IDs to Collection objects.
     */
    private Database(ConcurrentHashMap<String, Collection> collections) {
        this.collections = collections;
    }

    public void putCollection(Collection collection) {
        this.collections.put(collection.getId(), collection);
    }

    public Collection getCollection(String name) {
        return this.collections.get(name);
    }

    public boolean containsCollection(String name) {
        return this.collections.containsKey(name);
    }

    public void removeCollection(String name) {
        this.collections.remove(name);
    }

    /**
     * Loads a Database instance from a JSON file.
     *
     * @param filename The name of the JSON file to load.
     * @return A Database instance populated with the data from the file.
     * @throws IOException If an error occurs while reading the file.
     */
    public static Database fromFile(String filename) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        String path = "src/main/resources/" + filename;
        String json = Files.readString(Paths.get(path));

        ConcurrentHashMap<String, Collection> collectionsMap = mapper.readValue(
                json,
                mapper.getTypeFactory().constructMapType(ConcurrentHashMap.class, String.class, Collection.class)
        );

        return new Database(collectionsMap);
    }

    /**
     * Saves the current state of the database to a JSON file.
     *
     * @param filename The name of the file where the database status will be
     * saved.
     * @return A message indicating the result of the save operation.
     */
    public String saveStatus(String filename) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this.collections);

            String filePath = "src/main/resources/" + filename;

            Files.writeString(Paths.get(filePath), json, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            return "Database status saved successfully";
        } catch (IOException e) {
            return "ERR 500 Failed to save database";
        }
    }
}
