/**
 *
 */
package fr.alten.ambroiseJEE.utils;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import fr.alten.ambroiseJEE.model.beans.Person;

/**
 * @author Andy Chabalier
 * @author Thomas Decamp
 * @author Kylian Gehier
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
	
	/**
	 * 
	 *
	 * @param params {@link JsonNode} which integrity has to be checked
	 * @param fields all String fields that has to be checked in the {@link JsonNode}
	 * @return	true if all fields are present
	 * 			false otherwise
	 * @author Thomas Decamp
	 * @author Kylian Gehier
	 */
	public static boolean checkJsonIntegrity(JsonNode params, String... fields) {

		for (int i = 0; i < fields.length; i++) {
			if (params.get(fields[i]).isNull())
				return false;
		}
		return true;
	}

	public static String toJson(Object object) {
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		return gson.toJson(object);
	}
}
