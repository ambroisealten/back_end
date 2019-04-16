package fr.alten.ambroiseJEE.utils.routing;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;

/**
 * 
 * @author Kylian Gehier
 *
 */
public class ParseRoutingFile {

	private static Gson gson = new Gson();

	/**
	 * Create a json file containing menu items to display for a particular role
	 * 
	 * @param role current UserRole
	 * @return json containing routing list
	 * @throws FileNotFoundException
	 * @author Kylian Gehier
	 */
	public static String getJsonRoutingItemsByRole(UserRole role, AngularModule module) throws FileNotFoundException {
		BufferedReader jsonFile = null;
		try {
			jsonFile = new BufferedReader(new InputStreamReader(
					new FileInputStream(AngularModules.getInstance().getFileByAngularModule(module)), "ISO-8859-1"));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		JsonElement configElement = new JsonParser().parse(jsonFile);
		JsonObject configObject = configElement.getAsJsonObject();

		// gt Routingitems containing the list of couple (UserRole, Routes[])
		JsonArray routingItems = configObject.get("routing").getAsJsonArray();
		
		for(JsonElement routing : routingItems) {
			// get the routes list of the current UserRole
			if(UserRole.valueOf(((JsonObject) routing).get("role").getAsString()).equals(role)) {
				JsonArray resultArray = (JsonArray) ((JsonObject) routing).get("routes");

				// create result Json
				JsonObject resultjson = new JsonObject();
				resultjson.add("routes", resultArray);
				
				return gson.toJson(resultjson);
			}
		}
		
		throw new ForbiddenException();


	}
	
}
