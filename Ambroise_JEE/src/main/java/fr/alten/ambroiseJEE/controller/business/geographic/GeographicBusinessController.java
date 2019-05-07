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
import fr.alten.ambroiseJEE.security.UserRoleLists;
import fr.alten.ambroiseJEE.utils.JsonUtils;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;

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

	private final Gson gson = new GsonBuilder().create();

	public boolean isAdmin(final UserRole role) {
		return UserRoleLists.getInstance().isAdmin(role);
	}

	/**
	 * Create cities from a provided departement arrayList. This method will ask the
	 * geo.api.gouv.fr API to fetch cities from each departements
	 *
	 * @param arrayList ArrayList of {@link LinkedTreeMap} departements data
	 * @param role      the current logged user role
	 * @author Andy Chabalier
	 */
	private void createCities(final ArrayList<LinkedTreeMap> departements, final UserRole role) {

		final RestTemplate restTemplate = new RestTemplate();
		for (final LinkedTreeMap departement : departements) {
			try {
				ArrayList<LinkedTreeMap> citiesByDepartement = new ArrayList<LinkedTreeMap>();
				final String code = (String) departement.get("code");
				final URI urlCities = new URI("https://geo.api.gouv.fr/departements/" + code + "/communes");
				citiesByDepartement = this.gson.fromJson(restTemplate.getForObject(urlCities, String.class),
						ArrayList.class);
				for (final LinkedTreeMap city : citiesByDepartement) {
					try {
						final JsonNode jCity = JsonUtils.toJsonNode(this.gson.toJsonTree(city).getAsJsonObject());
						this.cityBusinessController.createCity(jCity, role);
					} catch (final IOException e) {
						e.printStackTrace();
					}
				}
			} catch (final URISyntaxException e) {
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
	private void createDepartement(final ArrayList<LinkedTreeMap> departementData, final UserRole role) {
		for (final LinkedTreeMap departement : departementData) {
			try {
				final JsonNode jDepartement = JsonUtils.toJsonNode(this.gson.toJsonTree(departement).getAsJsonObject());
				this.departementBusinessController.createDepartement(jDepartement, role);
			} catch (final IOException e) {
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
	private void createRegion(final ArrayList<LinkedTreeMap> regionData, final UserRole role) {
		for (final LinkedTreeMap region : regionData) {
			try {
				final JsonNode jRegion = JsonUtils.toJsonNode(this.gson.toJsonTree(region).getAsJsonObject());
				this.regionBusinessController.createRegion(jRegion, role);
			} catch (final IOException e) {
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
		final HashMap<String, ArrayList<LinkedTreeMap>> data = new HashMap<String, ArrayList<LinkedTreeMap>>();

		final Thread dataFetchingThread = new Thread(() -> {
			try {

				final RestTemplate restTemplate = new RestTemplate();

				final URI urlDepartement = new URI("https://geo.api.gouv.fr/departements?fields=");
				final URI urlRegion = new URI("https://geo.api.gouv.fr/regions?fields=");

				data.put("region", GeographicBusinessController.this.gson
						.fromJson(restTemplate.getForObject(urlRegion, String.class), ArrayList.class));

				final ArrayList<LinkedTreeMap> departementsFetched = GeographicBusinessController.this.gson
						.fromJson(restTemplate.getForObject(urlDepartement, String.class), ArrayList.class);
				data.put("departement", departementsFetched);

			} catch (final URISyntaxException e) {
				e.printStackTrace();
			}
		});
		dataFetchingThread.run();

		return data;
	}

	/**
	 * Get the wanted Geographic object. Depending of the placeType, we get the
	 * corresponding BusinessController if the object don't exist in base, we throw
	 * the ResourceNotFoundException
	 *
	 * @param placeName the place's name
	 * @param placeType the place's type
	 * @return the geographic corresponding to placeName and placeType
	 * @throws ResourceNotFoundException()
	 * @author Andy Chabalier
	 */
	public Optional<Geographic> getPlace(final String placeName, final String placeType) {
		Geographic place;
		try {
			switch (placeType.toLowerCase()) {
			case "city":
				place = this.cityBusinessController.getCity(placeName);
				break;
			case "region":
				place = this.regionBusinessController.getRegion(placeName);
				break;
			case "departement":
				place = this.departementBusinessController.getDepartement(placeName);
				break;
			case "postalCode":
				place = this.postalCodeBusinessController.getPostalCode(placeName);
				break;
			default:
				throw new ResourceNotFoundException();
			}
		} catch (final NoSuchElementException nsee) {
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
	public HttpException synchronize(final UserRole role) {
		if (isAdmin(role)) {
			final HashMap<String, ArrayList<LinkedTreeMap>> data = fetchData();

			createRegion(data.get("region"), role);
			createDepartement(data.get("departement"), role);

			createCities(data.get("departement"), role);

			return new OkException();
		}
		return new ForbiddenException();
	}
}
