/**
 *
 */
package fr.alten.ambroiseJEE.model.entityControllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
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

	private Logger logger = LoggerFactory.getLogger(CityEntityController.class);
	
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
	public HttpException createCity(final JsonNode jCity) {

		final City newCity = new City();
		newCity.setNom(jCity.get("nom").textValue());
		newCity.setCode(jCity.get("code").textValue());
		newCity.setCodeDepartement(jCity.get("codeDepartement").textValue());
		newCity.setCodeRegion(jCity.get("codeRegion").textValue());
		newCity.setCodePostaux(jCity.get("codesPostaux").textValue());

		try {
			this.cityRepository.save(newCity);
		} catch (final DuplicateKeyException dke) {
			return new ConflictException();
		} catch (final Exception e) {
			logger.error("New kind of Exception caught in CityEntityController.createCity : "+e);
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
	public HttpException deleteCity(final JsonNode jCity) {
		return this.cityRepository.findByNom(jCity.get("nom").textValue())
				// optional is present
				.map(city -> {
					city.setNom("deactivated" + System.currentTimeMillis());
					try {
						this.cityRepository.save(city);
					} catch (final DuplicateKeyException dke) {
						return new ConflictException();
					} catch (final Exception e) {
						e.printStackTrace();
					}
					return new OkException();

					// optional isn't present
				}).orElse(new ResourceNotFoundException());

	}

	/**
	 * @return the list of all cities
	 * @author Andy Chabalier
	 */
	public List<City> getCities() {
		return this.cityRepository.findAll();
	}

	public City getCity(final String name) {
		return this.cityRepository.findByNom(name).orElseThrow(ResourceNotFoundException::new);
	}

	/**
	 *
	 * @param jCity JsonNode with all city parameters and the old name to perform
	 *              the update even if the name is changed
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request {@link ResourceNotFoundException} if the resource is not
	 *         found, {@link ConflictException} if a duplicate unique field is
	 *         trying to be saved by updating and {@link CreatedException} if the
	 *         city is updated
	 * @author Andy Chabalier
	 * @author Kylian Gehier
	 */
	public HttpException updateCity(final JsonNode jCity) {
		return this.cityRepository.findByNom(jCity.get("oldName").textValue())
				// optional is present
				.map(city -> {
					city.setNom(jCity.get("nom").textValue());
					try {
						this.cityRepository.save(city);
					} catch (final DuplicateKeyException dke) {
						return new ConflictException();
					} catch (final Exception e) {
						e.printStackTrace();
					}
					return new OkException();

					// optional isn't present
				}).orElse(new ResourceNotFoundException());
	}

}
