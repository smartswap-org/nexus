package org.smartswap.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.Map;

public class JsonMapUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Converts a Map<String, String> into a JSON-formatted String.
     *
     * @param map               the Map to convert. Can be null or empty.
     * @return                  a JSON String representation of the Map. Returns "{}" if the map is null or empty.
     * @throws RuntimeException if the conversion to JSON fails.
     *
     * Example:
     * Map<String,String> map = Map.of("userId","123", "ip","192.168.0.1");
     * String json = mapToJsonString(map);
     * // json = {"userId":"123","ip":"192.168.0.1"}
     */
    public static String mapToJsonString(Map<String, String> map) {
        if (map == null || map.isEmpty()) return "{}";
        try {
            return objectMapper.writeValueAsString(map);
        } catch (Exception e) {
            throw new RuntimeException("Could not convert Map to JSON", e);
        }
    }

    /**
     * Converts a JSON-formatted String into a Map<String, String>.
     *
     * @param json              the JSON String to convert. Can be null or blank.
     * @return                  a Map<String, String> parsed from the JSON. Returns an empty Map if the input is null or blank.
     * @throws RuntimeException if the JSON cannot be parsed into a Map.
     *
     * Example:
     * String json = "{\"userId\":\"123\",\"ip\":\"192.168.0.1\"}";
     * Map<String,String> map = jsonStringToMap(json);
     * // map = {userId=123, ip=192.168.0.1}
     */
    public static Map<String, String> jsonStringToMap(String json) {
        if (json == null || json.isBlank()) return Map.of();
        try {
            return objectMapper.readValue(json, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("Could not parse JSON to Map", e);
        }
    }
}
