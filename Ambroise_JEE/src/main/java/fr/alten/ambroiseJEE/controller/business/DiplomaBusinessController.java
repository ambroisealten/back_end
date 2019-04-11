package fr.alten.ambroiseJEE.controller.business;

import java.text.ParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.beans.Diploma;
import fr.alten.ambroiseJEE.model.entityControllers.DiplomaEntityController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;

/**
 * Diploma controller for business rules
 * @author Lucas Royackkers
 *
 */
@Service
public class DiplomaBusinessController {
	
	@Autowired
	private DiplomaEntityController diplomaEntityController;

	/**
	 * Method to delegate diploma creation
	 * @param params the JsonNode containing all diploma parameters
	 * @param role the user's role
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database and {@link CreatedException} if the diploma is created
	 * @author Lucas Royackkers
	 * @throws ParseException 
	 */
	public HttpException createDiploma(JsonNode params, UserRole role) throws ParseException {
		if(UserRole.MANAGER_ADMIN == role || UserRole.CDR_ADMIN == role) {
			return diplomaEntityController.createDiploma(params);
		}
		throw new ForbiddenException();
	}

	/**
	 * Method to get all diplomas
	 * @param role the user's role
	 * @return a List of Diploma objects
	 * @author Lucas Royackkers
	 */
	public List<Diploma> getDiplomas(UserRole role) {
		if(UserRole.MANAGER_ADMIN == role || UserRole.CDR_ADMIN == role) {
			return diplomaEntityController.getDiplomas();
		}
		throw new ForbiddenException();
	}

	/**
	 * Method to delegate diploma update
	 * @param params the JsonNode containing all diploma parameters
	 * @param role the user's role
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database and {@link CreatedException} if the diploma is updated
	 * @throws ParseException
	 * @author Lucas Royackkers
	 */
	public HttpException updateDiploma(JsonNode params, UserRole role) throws ParseException {
		if(UserRole.MANAGER_ADMIN == role || UserRole.CDR_ADMIN == role) {
			return diplomaEntityController.updateDiploma(params);
		}
		throw new ForbiddenException();
	}

	/**
	 * Method to delegate diploma deletion 
	 * @param params the JsonNode containing all diploma parameters
	 * @param role the user's role
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database and {@link CreatedException} if the diploma is desactivated
	 * @author Lucas Royackkers
	 */
	public HttpException deleteDiploma(JsonNode params, UserRole role) {
		if(UserRole.MANAGER_ADMIN == role || UserRole.CDR_ADMIN == role) {
			return diplomaEntityController.deleteDiploma(params);
		}
		throw new ForbiddenException();
	}

}
