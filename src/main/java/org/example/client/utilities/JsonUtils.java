package org.example.client.utilities;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class JsonUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        // Prevent crashes if JSON contains fields not present in the target POJO
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private JsonUtils() {}

    /**
     * Resolves a file path relative to the testdata directory or project root.
     */
    public static String resolvePath(String fileName) {
        File file = new File(fileName);
        if (file.isAbsolute() && file.exists()) {
            return fileName;
        }
        return Paths.get(System.getProperty("user.dir"), "testdata", fileName).toAbsolutePath().toString();
    }

    /**
     * Deserializes a JSON file into any POJO class.
     * E.g.: User user = JsonUtils.readAs(new File("user.json"), User.class);
     */
    public static <T> T readAs(String fileName, Class<T> targetClass) {
        File jsonFile = getValidatedFile(fileName);
        try {
            return MAPPER.readValue(jsonFile, targetClass);
        } catch (IOException e) {
            throw new RuntimeException("Failed to deserialize JSON to " + targetClass.getSimpleName() + ": " + e.getMessage(), e);
        }
    }

    /**
     * Reads a JSON file directly into a Map<String, Object>.
     */
    public static Map<String, Object> readAsMap(String fileName) {
        File jsonFile = getValidatedFile(fileName);
        try {
            return MAPPER.readValue(jsonFile, new TypeReference<Map<String, Object>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse JSON file into Map: " + jsonFile.getPath() + " | " + e.getMessage(), e);
        }
    }

    /**
     * Reads a JSON array into a List of Maps.
     */
    public static List<Map<String, Object>> readAsListOfMap(String fileName) {
        File jsonFile = getValidatedFile(fileName);
        try {
            return MAPPER.readValue(jsonFile, new TypeReference<List<Map<String, Object>>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse JSON array into List<Map>: " + jsonFile.getPath() + " | " + e.getMessage(), e);
        }
    }

    /**
     * Converts a JSON array of objects into a 2D Object array for TestNG @DataProvider.
     * Each element in the outer array represents one test iteration passing a single Map<String, Object>.
     */
    public static Object[][] getDataProviderData(String fileName) {
        List<Map<String, Object>> list = readAsListOfMap(fileName);
        Object[][] data = new Object[list.size()][1];
        for (int i = 0; i < list.size(); i++) {
            data[i][0] = list.get(i);
        }
        return data;
    }

    /**
     * Reads a nested value using dot-notation (e.g., "user.address.zipcode" or "items[0].id").
     */
    public static String getJsonValue(String fileName, String jsonPath) {
        File jsonFile = getValidatedFile(fileName);
        try {
            JsonNode root = MAPPER.readTree(jsonFile);
            String[] tokens = jsonPath.split("\\.");
            JsonNode current = root;

            for (String token : tokens) {
                if (token.contains("[") && token.endsWith("]")) {
                    String field = token.substring(0, token.indexOf("["));
                    int index = Integer.parseInt(token.substring(token.indexOf("[") + 1, token.length() - 1));
                    current = current.path(field).path(index);
                } else {
                    current = current.path(token);
                }

                if (current.isMissingNode() || current.isNull()) {
                    return "";
                }
            }
            return current.isValueNode() ? current.asText() : current.toString();

        } catch (IOException e) {
            throw new RuntimeException("Failed to read value at path '" + jsonPath + "' in file: " + fileName, e);
        }
    }

    /**
     * Serializes any Java object to a formatted, pretty-printed JSON file.
     */
    public static void writeToFile(String fileName, Object data) {
        String resolvedPath = resolvePath(fileName);
        try {
            MAPPER.writerWithDefaultPrettyPrinter().writeValue(new File(resolvedPath), data);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write data to JSON file: " + resolvedPath + " | " + e.getMessage(), e);
        }
    }

    // --- Private Helper ---
    private static File getValidatedFile(String fileName) {
        String path = resolvePath(fileName);
        File file = new File(path);
        if (!file.exists() || file.isDirectory()) {
            throw new IllegalArgumentException("JSON file does not exist at path: " + path);
        }
        return file;
    }
}