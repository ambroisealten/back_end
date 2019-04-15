package fr.alten.ambroiseJEE.utils.config;

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
 * Parse information from config.json file
 * 
 * @author Camille Schnell
 *
 */
public class ParseConfigFile {

	private static Gson gson = new Gson();

	/**
	 * Create a json file containing menu items to display for a particular role
	 * 
	 * @param role current UserRole
	 * @return json containing menu items
	 * @throws FileNotFoundException
	 * @author Camille Schnell
	 * @author Kylian Gehier
	 */
	public static String getJsonMenuItemsByRole(UserRole role) throws FileNotFoundException {

		// read json config file
		BufferedReader jsonFile = null;
		try {
			jsonFile = new BufferedReader(
					new InputStreamReader(new FileInputStream("src/main/resources/modules.json"), "ISO-8859-1"));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		JsonElement configElement = new JsonParser().parse(jsonFile);
		JsonObject configObject = configElement.getAsJsonObject();

		// get menuItems info from json
		JsonArray menuItemsArray = configObject.get("menuItems").getAsJsonArray();
		JsonElement menuElement = menuItemsArray.get(0);

		// get access rights String for current UserRole
		JsonArray accessRightsArray = (JsonArray) menuElement.getAsJsonObject().get("access");
		String currentRoleRights = accessRightsArray.get(0).getAsJsonObject().get(role.toString()).toString();

		// get array containing menus for each module
		JsonArray modulesArray = (JsonArray) menuElement.getAsJsonObject().get("modules");

		// create result Json
		JsonObject resultJson = new JsonObject();
		JsonArray resultModulesArray = new JsonArray();

		// put menus info into result Json
		for (int i = 0; i < modulesArray.size(); i++) {
			String moduleName = modulesArray.get(i).getAsJsonObject().get("label").getAsString();
			if (currentRoleRights.contains(moduleName))
				resultModulesArray.add(modulesArray.get(i));
		}

		resultJson.add("modules", resultModulesArray);

		return gson.toJson(resultJson);
	}

	// TO-DO
	public static String getJsonRoutingItemsByRole(UserRole role) throws FileNotFoundException {
		BufferedReader jsonFile = null;
		try {
			jsonFile = new BufferedReader(
					new InputStreamReader(new FileInputStream("src/main/resources/routing.json"), "ISO-8859-1"));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		JsonElement configElement = new JsonParser().parse(jsonFile);
		JsonObject configObject = configElement.getAsJsonObject();

		// get routingItems info from json
		JsonArray routingItemsArray = configObject.get("routingItems").getAsJsonArray();

		// get the routingItem for current UserRole
		for (JsonElement e : routingItemsArray) {
			if (UserRole.valueOf(e.getAsJsonObject().get("role").getAsString()) == role) {
				JsonObject currentRoleRoutingItem = (JsonObject) e;

				// get the routing list for current UserRole
				JsonArray currentRoleRoutingArray = (JsonArray) currentRoleRoutingItem.getAsJsonObject().get("routes");
				
				// transforming the routing list JsonArray to a JsonObject
				JsonObject resultJson = new JsonObject();
				resultJson.add("routes", currentRoleRoutingArray);
				
				return gson.toJson(resultJson);
				
				
			}
		}
		
		// Exception in case UserRole doesn't exist in routing.json or UserRole
		throw new ForbiddenException();
		 

	}

}
