/**
 *
 */
package fr.alten.ambroiseJEE.utils;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;

/**
 * @author Andy Chabalier
 *
 */
public class JsonUtils {

	/**
	 * Util method to convert JsonObject to JsonNode
	 * 
	 * @param jsonObj JsonObject to convert into JsonNode
	 * @return the corresponding JsonNode
	 * @throws IOException If a low-level I/O problem (missing input, network error)
	 *                     occurs
	 * @author Andy Chabalier
	 */
	public static JsonNode toJsonNode(JsonObject jsonObj) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readTree(jsonObj.toString());
	}

	/**
	 * Util method to convert map to JsonNode
	 * 
	 * @param map Map to convert into JsonNode
	 * @return the corresponding JsonNode
	 * @throws IOException If a low-level I/O problem (missing input, network error)
	 *                     occurs
	 * @author Andy Chabalier
	 */
	public static JsonNode toJsonNode(Map<?, ?> map) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.convertValue(map, JsonNode.class);
	}
}
