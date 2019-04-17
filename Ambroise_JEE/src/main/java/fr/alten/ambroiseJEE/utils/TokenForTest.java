package fr.alten.ambroiseJEE.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import fr.alten.ambroiseJEE.controller.rest.LoginRestController;
import fr.alten.ambroiseJEE.utils.routing.AngularModules;

/**
 * 
 * @author Kylian Gehier
 *
 */
//Singleton
public class TokenForTest {

	private static TokenForTest INSTANCE = null;
	private String token;
	private LoginRestController loginRestController;
	private JsonNode param = null;

	private TokenForTest() {

		loginRestController = new LoginRestController();
		try {
			this.param = getParams();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("param : "+param);
		try {
			this.token = this.loginRestController.login(param);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static TokenForTest getInstance() {
		if (INSTANCE == null) {
			return new TokenForTest();
		}
		return INSTANCE;
	}
	
	public String getToken() {
		return this.token;
	}

	private JsonNode getParams() throws Exception {

		
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode actualObj = mapper.readValue("{\"mail\":\"tempUserAdminManager@mail.com\",\"pswd\":\"pass\"}", JsonNode.class);
			System.out.println(actualObj);
			return actualObj;
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		throw new Exception();
	}
}
