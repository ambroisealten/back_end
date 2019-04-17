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
		JsonObject configObject = new JsonParser().parse(jsonFile).getAsJsonObject();

		// get menuItems info from json
		JsonObject menuElementJsonObject = configObject.get("menuItems").getAsJsonArray().get(0).getAsJsonObject();

		// get access rights String for current UserRole
		JsonArray accessRightsArray = (JsonArray) menuElementJsonObject.get("access");
		String currentRoleRights = accessRightsArray.get(0).getAsJsonObject().get(role.name()).getAsString();

		// get array containing menus for each module
		JsonArray modulesArray = (JsonArray) menuElementJsonObject.get("modules");

		// create result Json
		JsonObject resultJson = new JsonObject();
		JsonArray resultModulesArray = new JsonArray();

		// put menus info into result Json
		for (JsonElement module : modulesArray) {
			String moduleName = module.getAsJsonObject().get("label").getAsString();
			if (currentRoleRights.contains(moduleName)) {
				resultModulesArray.add(module);
			}
		}

		resultJson.add("modules", resultModulesArray);

		return gson.toJson(resultJson);
	}

}
