/**
 * 
 */
package fr.alten.ambroiseJEE.controller.business.geographic;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;

import fr.alten.ambroiseJEE.model.beans.Geographic;
import fr.alten.ambroiseJEE.security.Roles;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.RessourceNotFoundException;

/**
 * @author Andy Chabalier
 *
 */
@Service
public class GeographicBusinessController {

	@Autowired
	private CityBusinessController cityBusinessController;

	@Autowired
	private RegionBusinessController regionBusinessController;

	@Autowired
	private DepartementBusinessController departementBusinessController;

	@Autowired
	private PostalCodeBusinessController postalCodeBusinessController;
	
	private Gson gson = (new GsonBuilder()).create();


	/**
	 * Get the wanted Geographic object. Depending of the placeType, we get the
	 * corresponding BusinessController if the object don't exist in base, we throw
	 * the RessourceNotFoundException
	 * 
	 * @param placeName the place's name
	 * @param placeType the place's type
	 * @return the geographic corresponding to placeName and placeType
	 * @throws RessourceNotFoundException()
	 * @author Andy Chabalier
	 */
	public Optional<Geographic> getPlace(String placeName, String placeType) {
		Geographic place;
		try {
			switch (placeType.toLowerCase()) {
			case "city":
				place = cityBusinessController.getCity(placeName).get();
				break;
			case "region":
				place = regionBusinessController.getRegion(placeName).get();
				break;
			case "departement":
				place = departementBusinessController.getDepartement(placeName).get();
				break;
			case "postalCode":
				place = postalCodeBusinessController.getPostalCode(placeName).get();
				break;
			default:
				throw new RessourceNotFoundException();
			}
		} catch (NoSuchElementException nsee) {
			throw new RessourceNotFoundException();
		}
		return Optional.ofNullable(place);
	}

	/**
	 * 
	 * @param role the current logged user role
	 * @return
	 * @author Andy Chabalier
	 */
	public HttpException synchronise(int role) {
		if (Roles.ADMINISTRATOR_USER_ROLE.getValue() == role) {
			HashMap<String, ArrayList<LinkedTreeMap>> data = fetchData();

			createRegion(data.get("region"),role);
			createDepartement(data.get("departement"),role);
			
			return new OkException();
		}
		throw new ForbiddenException();
	}

	/**
	 * @param arrayList ArrayList of LinkedTreeMap with regions data
	 * @author Andy Chabalier
	 */
	private void createRegion(ArrayList<LinkedTreeMap> regionData, int role) {
		for(LinkedTreeMap region : regionData) {
			try {
				JsonNode jRegion = toJsonNode(gson.toJsonTree(region).getAsJsonObject());
				regionBusinessController.createRegion(jRegion, role);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * @param arrayList ArrayList of LinkedTreeMap with regions data
	 * @author Andy Chabalier
	 */
	private void createDepartement(ArrayList<LinkedTreeMap> departementData, int role) {
		for(LinkedTreeMap departement : departementData) {
			try {
				JsonNode jDepartement = toJsonNode(gson.toJsonTree(departement).getAsJsonObject());
				departementBusinessController.createDepartement(jDepartement, role);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private HashMap<String, ArrayList<LinkedTreeMap>> fetchData() {
		HashMap<String, ArrayList<LinkedTreeMap>> data = new HashMap<String, ArrayList<LinkedTreeMap>>();
		
		Thread dataFetchingThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					
					RestTemplate restTemplate = new RestTemplate();
					
					
					URI urlDepartement = new URI("https://geo.api.gouv.fr/departements?fields=");
					URI urlRegion = new URI("https://geo.api.gouv.fr/regions?fields=");	

					data.put("region", gson.fromJson(restTemplate.getForObject(urlRegion, String.class), ArrayList.class));
					
					ArrayList<LinkedTreeMap> departementsFetched = gson.fromJson(restTemplate.getForObject(urlDepartement, String.class), ArrayList.class);
					data.put("departement", departementsFetched);
					
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
			}
		});
		
		dataFetchingThread.run();

		return data;
	}

	
	private JsonNode toJsonNode(JsonObject jsonObj) throws IOException {
	    ObjectMapper objectMapper = new ObjectMapper();
	    return objectMapper.readTree(jsonObj.toString());
	}
}
