/**
 *
 */
package fr.alten.ambroiseJEE.utils;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;

/**
 * @author Andy Chabalier
 *
 */
public class JsonUtils {

	/**
	 *
	 * @param jsonObj JsonObject to convert into JsonNode
	 * @return the corresponding JsonNode
	 * @throws IOException
	 * @author Andy Chabalier
	 */
	public static JsonNode toJsonNode(JsonObject jsonObj) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readTree(jsonObj.toString());
	}
}
