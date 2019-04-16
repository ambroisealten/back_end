package utils.config;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ParseConfigFile {

	public static String getJsonMenuItems() throws FileNotFoundException {
		Gson gson = new Gson();
		BufferedReader jsonFile = new BufferedReader(new FileReader("src/main/resources/config.json"));
		JsonElement jelement = new JsonParser().parse(jsonFile);
		JsonObject jobject = jelement.getAsJsonObject();
		JsonArray jarray = jobject.getAsJsonArray("menuItems");
		return gson.toJson(jarray.get(0));
	}
	
	
}
