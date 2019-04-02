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
import fr.alten.ambroiseJEE.security.Roles;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.RessourceNotFoundException;

/**
 * City controller for business rules.
 * @author Andy Chabalier
 *
 */
@Service
public class CityBusinessController {

	@Autowired
	private CityEntityController cityEntityController;
	

	public Optional<City> getCity(String name) {
		return cityEntityController.getCity(name);
	}


	/**
	 * Method to delegate city creation
	 * @param jUser JsonNode with all city parameters
	 * @return the @see {@link HttpException} corresponding to the statut of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database and {@link CreatedException} if the city is created
	 * @author Andy Chabalier
	 */
	public HttpException createCity(JsonNode jCity, int role) {
		return Roles.ADMINISTRATOR_USER_ROLE.getValue()== role ? cityEntityController.createCity(jCity) : new ForbiddenException();
	}


	/**
	 * @param role the user role
	 * @return the list of all cities
	 * @author Andy Chabalier
	 */
	public List<City> getCities(int role) {
		if (Roles.ADMINISTRATOR_USER_ROLE.getValue()== role) {
			return cityEntityController.getCities();
		}
		throw new ForbiddenException();	
	}


	/**
	 * 
	 * @param jCity JsonNode with all city parameters and the old name to perform the update even if the name is changed
	 * @param role user role
	 * @return the @see {@link HttpException} corresponding to the statut of the
	 *         request ({@link RessourceNotFoundException} if the ressource is not found
	 *         and {@link CreatedException} if the city is updated
	 * @author Andy Chabalier
	 */
	public HttpException updateCity(JsonNode jCity, int role) {
		return Roles.ADMINISTRATOR_USER_ROLE.getValue()== role ? cityEntityController.updateCity(jCity) : new ForbiddenException();
	}


	/**
	 * 
	 * @param params the city name to delete 
	 * @param role the user role
	 * @return @see {@link HttpException} corresponding to the statut of the
	 *         request ({@link ForbiddenException} if the ressource is not found
	 *         and {@link CreatedException} if the city is deleted
	 * @author Andy Chabalier
	 */
	public HttpException deleteCity(JsonNode params, int role) {
		return Roles.ADMINISTRATOR_USER_ROLE.getValue()== role ? cityEntityController.deleteCity(params.get("name").textValue()) : new ForbiddenException();
	}

}

