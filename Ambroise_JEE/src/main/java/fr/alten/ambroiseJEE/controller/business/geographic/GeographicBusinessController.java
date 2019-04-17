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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

import fr.alten.ambroiseJEE.model.beans.Geographic;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.JsonUtils;
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
	 * Create cities from a provided departement arrayList. This method will ask the
	 * geo.api.gouv.fr API to fetch cities from each departements
	 *
	 * @param arrayList ArrayList of {@link LinkedTreeMap} departements data
	 * @param role      the current logged user role
	 * @author Andy Chabalier
	 */
	private void createCities(ArrayList<LinkedTreeMap> departements, UserRole role) {

		RestTemplate restTemplate = new RestTemplate();
		for (LinkedTreeMap departement : departements) {
			try {
				ArrayList<LinkedTreeMap> citiesByDepartement = new ArrayList<LinkedTreeMap>();
				String code = (String) departement.get("code");
				URI urlCities = new URI("https://geo.api.gouv.fr/departements/" + code + "/communes");
				citiesByDepartement = gson.fromJson(restTemplate.getForObject(urlCities, String.class),
						ArrayList.class);
				for (LinkedTreeMap city : citiesByDepartement) {
					try {
						JsonNode jCity = JsonUtils.toJsonNode(gson.toJsonTree(city).getAsJsonObject());
						cityBusinessController.createCity(jCity, role);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * Create departement from the provided departementData.
	 *
	 * @param arrayList ArrayList of {@link LinkedTreeMap} with departements data
	 * @author Andy Chabalier
	 */
	private void createDepartement(ArrayList<LinkedTreeMap> departementData, UserRole role) {
		for (LinkedTreeMap departement : departementData) {
			try {
				JsonNode jDepartement = JsonUtils.toJsonNode(gson.toJsonTree(departement).getAsJsonObject());
				departementBusinessController.createDepartement(jDepartement, role);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Create regions from the provided regionData.
	 *
	 * @param arrayList ArrayList of {@link LinkedTreeMap} with regions data
	 * @author Andy Chabalier
	 * @return
	 */
	private void createRegion(ArrayList<LinkedTreeMap> regionData, UserRole role) {
		for (LinkedTreeMap region : regionData) {
			try {
				JsonNode jRegion = JsonUtils.toJsonNode(gson.toJsonTree(region).getAsJsonObject());
				regionBusinessController.createRegion(jRegion, role);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Fetch data from the geo.api.gouv.fr API. We fetch region and departement
	 * data.
	 *
	 * @return an HashMap with 2 entry: Region with key "region" and Departements
	 *         with key "departement". Theses key have an arrayList of
	 *         {@link LinkedTreeMap}. these LinkedTreeMap contains the data of
	 *         object (Region or departement)
	 * @author Andy Chabalier
	 */
	private HashMap<String, ArrayList<LinkedTreeMap>> fetchData() {
		HashMap<String, ArrayList<LinkedTreeMap>> data = new HashMap<String, ArrayList<LinkedTreeMap>>();

		Thread dataFetchingThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {

					RestTemplate restTemplate = new RestTemplate();

					URI urlDepartement = new URI("https://geo.api.gouv.fr/departements?fields=");
					URI urlRegion = new URI("https://geo.api.gouv.fr/regions?fields=");

					data.put("region",
							gson.fromJson(restTemplate.getForObject(urlRegion, String.class), ArrayList.class));

					ArrayList<LinkedTreeMap> departementsFetched = gson
							.fromJson(restTemplate.getForObject(urlDepartement, String.class), ArrayList.class);
					data.put("departement", departementsFetched);

				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
			}
		});
		dataFetchingThread.run();

		return data;
	}

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
			return Optional.empty();
		}
		return Optional.ofNullable(place);
	}

	/**
	 * Method to delegate the synchronisation of the base with the geo.gouv.fr API.
	 * This method make the creation of cities, region and departements
	 *
	 * @param role the current logged user role
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ForbiddenException} if the role is not allowed to do
	 *         synchronisation and {@link OkException} if the synchronisation is
	 *         done
	 * @author Andy Chabalier
	 */
	public HttpException synchronize(UserRole role) {
		if (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role) {
			HashMap<String, ArrayList<LinkedTreeMap>> data = fetchData();

			createRegion(data.get("region"), role);
			createDepartement(data.get("departement"), role);

			createCities(data.get("departement"), role);

			return new OkException();
		}
		return new ForbiddenException();
	}
}
