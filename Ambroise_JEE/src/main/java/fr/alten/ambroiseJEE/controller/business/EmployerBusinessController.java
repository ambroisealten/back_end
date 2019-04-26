package fr.alten.ambroiseJEE.controller.business;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.beans.Employer;
import fr.alten.ambroiseJEE.model.entityControllers.EmployerEntityController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;

/**
 * Employer controller for business rules
 *
 * @author Lucas Royackkers
 *
 */
@Service
public class EmployerBusinessController {

	@Autowired
	private EmployerEntityController employerEntityController;

	/**
	 * Method to delegate the employer creation
	 *
	 * @param params the JsonNode containing all Employer parameters
	 * @param role   the user's role
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database and {@link CreatedException} if the employer is created
	 * @throws {@link ForbiddenException} if the current logged user hasn't the
	 *         rights to perform this action
	 * @author Lucas Royackkers
	 */
	public HttpException createEmployer(final JsonNode params, final UserRole role) {
		if (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role) {
			return this.employerEntityController.createEmployer(params);
		}
		throw new ForbiddenException();
	}

	/**
	 * Method to delegate the employer deletion
	 *
	 * @param params the Json containing all Employer parameters
	 * @param role   the user's role
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ResourceNotFoundException} if the resource is not
	 *         found and {@link OkException} if the employer is deactivated
	 * @throws {@link ForbiddenException} if the current logged user hasn't the
	 *         rights to perform this action
	 * @author Lucas Royackkers
	 */
	public HttpException deleteEmployer(final JsonNode params, final UserRole role) {
		if (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role) {
			return this.employerEntityController.deleteEmployer(params);
		}
		throw new ForbiddenException();
	}

	/**
	 * Get all employers within the database
	 *
	 * @param role the user's role
	 * @return a List of Employer objects (can be empty)
	 * @throws {@link ForbiddenException} if the current logged user hasn't the
	 *         rights to perform this action
	 * @author Lucas Royackkers
	 */
	public List<Employer> getEmployers(final UserRole role) {
		if (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role || UserRole.CDR == role
				|| UserRole.MANAGER == role) {
			return this.employerEntityController.getEmployers();
		}
		throw new ForbiddenException();
	}

	/**
	 * Method to delegate the employer update
	 *
	 * @param params the Json containing all Employer parameters
	 * @param role   the user's role
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ResourceNotFoundException} if the resource is not
	 *         found and {@link OkException} if the employer is updated
	 * @throws {@link ForbiddenException} if the current logged user hasn't the
	 *         rights to perform this action
	 * @author Lucas Royackkers
	 */
	public HttpException updateEmployer(final JsonNode params, final UserRole role) {
		if (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role) {
			return this.employerEntityController.updateEmployer(params);
		}
		throw new ForbiddenException();
	}

}
