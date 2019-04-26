/**
 *
 */
package fr.alten.ambroiseJEE.model.entityControllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.mongodb.DuplicateKeyException;

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
		return this.cityRepository.findByName(jCity.get("name").textValue())
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
		return this.cityRepository.findByName(name).orElseThrow(ResourceNotFoundException::new);
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
	public HttpException updateCity(final JsonNode jCity) {
		return this.cityRepository.findByName(jCity.get("oldName").textValue())
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
