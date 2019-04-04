package fr.alten.ambroiseJEE.utils.config;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ParseConfigFile {

	private static Gson gson = new Gson();
	
	public static String getJsonMenuItemsByRole(int role) throws FileNotFoundException {		

		BufferedReader jsonFile = new BufferedReader(new FileReader("src/main/resources/config.json"));
		JsonElement configElement = new JsonParser().parse(jsonFile);
		JsonObject configObject = configElement.getAsJsonObject();
		
		JsonArray menuItemsArray = configObject.get("menuItems").getAsJsonArray();
		JsonElement menuElement = menuItemsArray.get(0);
		
		JsonArray accessRightsArray = (JsonArray) menuElement.getAsJsonObject().get("access");
		String currentRoleRights = accessRightsArray.get(0).getAsJsonObject().get(Integer.toString(role)).toString();
		
		JsonArray modulesArray = (JsonArray) menuElement.getAsJsonObject().get("modules");
		
		// create result Json
		JsonObject resultJson = new JsonObject();
		JsonArray resultModulesArray = new JsonArray();
				
		for(int i = 0; i < modulesArray.size(); i++) {
			String module = modulesArray.get(i).getAsJsonObject().get("label").toString();
			if(currentRoleRights.contains(module.subSequence(1, module.length()-1)))
				resultModulesArray.add(modulesArray.get(i));
		}
		
		resultJson.add("modules", resultModulesArray);
		
		return gson.toJson(resultJson);
	}
	
	
	public static void main(String args[]) throws FileNotFoundException {
		System.out.println(getJsonMenuItemsByRole(3));
	}
}
