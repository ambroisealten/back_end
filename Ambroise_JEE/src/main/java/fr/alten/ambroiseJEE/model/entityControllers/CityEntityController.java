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
import fr.alten.ambroiseJEE.utils.httpStatus.RessourceNotFoundException;

/**
 * @author Andy Chabalier
 *
 */

@Service
public class CityEntityController {
	
	@Autowired
	private CityRepository cityRepository;

	public Optional<City> getCity(String name) {
		return cityRepository.findByName(name);
	}

	/**
	 * Method to create a city.
	 * 
	 * @param jCity JsonNode with all city parameters
	 * @return the @see {@link HttpException} corresponding to the statut of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database and {@link CreatedException} if the city is created
	 * @author Andy Chabalier
	 */
	public HttpException createCity(JsonNode jCity) {

		City newCity = new City();
		newCity.setName(jCity.get("name").textValue());

		try {
			cityRepository.save(newCity);
		} catch (Exception e) {
			return new ConflictException();
		}
		return new CreatedException();
	}

	/**
	 * @return the list of all cities
	 * @author Andy Chabalier
	 */
	public List<City> getCities() {
		return cityRepository.findAll();
	}

	/**
	 * 
	 * @param jCity JsonNode with all city parameters and the old name to perform the update even if the name is changed
	 * @return the @see {@link HttpException} corresponding to the statut of the
	 *         request ({@link RessourceNotFoundException} if the ressource is not found
	 *         and {@link CreatedException} if the city is updated
	 * @author Andy Chabalier
	 */
	public HttpException updateCity(JsonNode jCity) {
		Optional<City> cityOptionnal = cityRepository.findByName(jCity.get("oldName").textValue());
		
		if (cityOptionnal.isPresent()) {
			City city = cityOptionnal.get();
			city.setName(jCity.get("name").textValue());
			
			cityRepository.save(city);
		}
		else {
			throw new RessourceNotFoundException();
		}		
		return new OkException();
	}

	/**
	 * 
	 * @param name the city name to fetch 
	 * @return {@link HttpException} corresponding to the statut of the
	 *         request ({@link RessourceNotFoundException} if the ressource is not found
	 *         and {@link OkException} if the city is desactivated
	 * @author Andy Chabalier
	 */
	public HttpException deleteCity(String name) {
		Optional<City> cityOptionnal = cityRepository.findByName(name);
		
		if (cityOptionnal.isPresent()) {
			City city = cityOptionnal.get();
			city.setName("desactivated" + System.currentTimeMillis());
			cityRepository.save(city);
		}
		else {
			throw new RessourceNotFoundException();
		}		
		return new OkException();
	}

}

