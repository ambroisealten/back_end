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
 * @author Thomas Decamp
 * @author Kylian Gehier
 *
 */
public class JsonUtils {

	/**
	 *
	 *
	 * @param params {@link JsonNode} which integrity has to be checked
	 * @param fields all String fields that has to be checked in the
	 *               {@link JsonNode}
	 * @return true if all fields are present false otherwise
	 * @author Thomas Decamp
	 * @author Kylian Gehier
	 */
	public static boolean checkJsonIntegrity(final JsonNode params, final String... fields) {
		for (final String f : fields) {
			if (!params.has(f)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Util method to convert JsonObject to JsonNode
	 *
	 * @param jsonObj JsonObject to convert into JsonNode
	 * @return the corresponding JsonNode
	 * @throws IOException If a low-level I/O problem (missing input, network error)
	 *                     occurs
	 * @author Andy Chabalier
	 */
	public static JsonNode toJsonNode(final JsonObject jsonObj) throws IOException {
		final ObjectMapper objectMapper = new ObjectMapper();
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
	public static JsonNode toJsonNode(final Map<?, ?> map) throws IOException {
		final ObjectMapper mapper = new ObjectMapper();
		return mapper.convertValue(map, JsonNode.class);
	}
}
