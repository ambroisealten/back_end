/**
 *
 */
package fr.alten.ambroiseJEE.model.entityControllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.beans.City;
import fr.alten.ambroiseJEE.model.dao.CityRepository;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;

/**
 * @author Andy Chabalier
 *
 */

@Service
public class CityEntityController {

	@Autowired
	private CityRepository cityRepository;

	/**
	 * Method to create a city.
	 *
	 * @param jCity JsonNode with all city parameters
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database and {@link CreatedException} if the city is created
	 * @author Andy Chabalier
	 */
	public HttpException createCity(JsonNode jCity) {

		City newCity = new City();
		newCity.setName(jCity.get("name").textValue());
		newCity.setCode(jCity.get("code").textValue());
		newCity.setCodeDepartement(jCity.get("codeDepartement").textValue());
		newCity.setCodeRegion(jCity.get("codeRegion").textValue());
		newCity.setCodePostaux(jCity.get("codesPostaux").textValue());

		try {
			cityRepository.save(newCity);
		} catch (Exception e) {
			return new ConflictException();
		}
		return new CreatedException();
	}

	/**
	 *
	 * @param name the city name to fetch
	 * @return {@link HttpException} corresponding to the status of the request
	 *         ({@link ResourceNotFoundException} if the resource is not found and
	 *         {@link OkException} if the city is deactivated
	 * @author Andy Chabalier
	 */
	public HttpException deleteCity(String name) {
		Optional<City> cityOptionnal = cityRepository.findByName(name);

		if (cityOptionnal.isPresent()) {
			City city = cityOptionnal.get();
			city.setName("deactivated" + System.currentTimeMillis());
			cityRepository.save(city);
		} else {
			throw new ResourceNotFoundException();
		}
		return new OkException();
	}

	/**
	 * @return the list of all cities
	 * @author Andy Chabalier
	 */
	public List<City> getCities() {
		return cityRepository.findAll();
	}

	public Optional<City> getCity(String name) {
		return cityRepository.findByName(name);
	}

	/**
	 *
	 * @param jCity JsonNode with all city parameters and the old name to perform
	 *              the update even if the name is changed
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ResourceNotFoundException} if the resource is not
	 *         found and {@link CreatedException} if the city is updated
	 * @author Andy Chabalier
	 */
	public HttpException updateCity(JsonNode jCity) {
		Optional<City> cityOptionnal = cityRepository.findByName(jCity.get("oldName").textValue());

		if (cityOptionnal.isPresent()) {
			City city = cityOptionnal.get();
			city.setName(jCity.get("name").textValue());

			cityRepository.save(city);
		} else {
			throw new ResourceNotFoundException();
		}
		return new OkException();
	}

}
