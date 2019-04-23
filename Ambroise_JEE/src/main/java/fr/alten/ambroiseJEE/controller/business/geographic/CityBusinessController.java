/**
 *
 */
package fr.alten.ambroiseJEE.controller.business.geographic;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.beans.City;
import fr.alten.ambroiseJEE.model.entityControllers.CityEntityController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;

/**
 * City controller for business rules.
 *
 * @author Andy Chabalier
 *
 */
@Service
public class CityBusinessController {

	@Autowired
	private CityEntityController cityEntityController;

	/**
	 * Method to delegate city creation
	 *
	 * @param jUser JsonNode with all city parameters
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database and {@link CreatedException} if the city is created
	 * @author Andy Chabalier
	 */
	public HttpException createCity(JsonNode jCity, UserRole role) {
		return (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role) ? cityEntityController.createCity(jCity)
				: new ForbiddenException();
	}

	/**
	 * Method to delegate city deletion
	 *
	 * @param params the city name to delete
	 * @param role   the user role
	 * @return @see {@link HttpException} corresponding to the status of the request
	 *         ({@link ForbiddenException} if the resource is not found and
	 *         {@link OkException} if the city is deleted
	 * @author Andy Chabalier
	 */
	public HttpException deleteCity(JsonNode params, UserRole role) {
		return (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role)
				? cityEntityController.deleteCity(params.get("name").textValue())
				: new ForbiddenException();
	}

	/**
	 * get the list of all cities
	 *
	 * @param role the user role
	 * @return the list of all cities
	 * @author Andy Chabalier
	 */
	public List<City> getCities(UserRole role) {
		if (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role) {
			return cityEntityController.getCities();
		}
		throw new ForbiddenException();
	}

	/**
	 * Fetch a city
	 *
	 * @param name the city name to fetch
	 * @return an optional with the requested city or empty if not found
	 * @author Andy Chabalier
	 */
	public Optional<City> getCity(String name) {
		return cityEntityController.getCity(name);
	}

	/**
	 * Method to delegate city update
	 *
	 * @param jCity JsonNode with all city parameters and the old name to perform
	 *              the update even if the name is changed
	 * @param role  user role
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ResourceNotFoundException} if the resource is not
	 *         found and {@link OkException} if the city is updated
	 * @author Andy Chabalier
	 */
	public HttpException updateCity(JsonNode jCity, UserRole role) {
		return (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role) ? cityEntityController.updateCity(jCity)
				: new ForbiddenException();
	}

}
