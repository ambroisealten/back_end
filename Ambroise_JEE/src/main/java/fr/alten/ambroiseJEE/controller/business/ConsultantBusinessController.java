package fr.alten.ambroiseJEE.controller.business;

import java.text.ParseException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.beans.Person;
import fr.alten.ambroiseJEE.model.entityControllers.PersonEntityController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.PersonRole;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;

/**
 * Consultant controller for business rules.
 * 
 * @author Lucas Royackkers
 *
 */
@Service
public class ConsultantBusinessController {
	@Autowired
	private PersonEntityController personEntityController;

	/**
	 * Method to delegate consultant creation
	 * 
	 * @param jUser JsonNode with all consultant(person) parameters
	 * @param role  the user's role
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ForbiddenException} if the current user hasn't the
	 *         rights to perform this action
	 * @author Lucas Royackkers
	 * @throws ParseException
	 */
	public HttpException createConsultant(JsonNode jConsultant, UserRole role) throws ParseException {
		if (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role || UserRole.MANAGER == role) {
			return personEntityController.createPerson(jConsultant, PersonRole.CONSULTANT);
		} else {
			throw new ForbiddenException();
		}

	}

	/**
	 * Method to delegate consultant deletion
	 * 
	 * @param params JsonNode with parameters (name)
	 * @param role   the user's role
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ForbiddenException} if the current user hasn't the
	 *         rights to perform this action
	 * @author Lucas Royackkers
	 */
	public HttpException deleteConsultant(JsonNode params, UserRole role) {
		if (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role || UserRole.MANAGER == role) {
			return personEntityController.deletePerson(params, PersonRole.CONSULTANT);
		}
		throw new ForbiddenException();
	}

	/**
	 * Try to fetch a consultant given its mail
	 * 
	 * @param mail the applicant's mail
	 *
	 * @return an Optional with the corresponding consultant (or not)
	 * @author Lucas Royackkers
	 * @throws ForbiddenException (if the user hasn't the right to do so)
	 */
	public Optional<Person> getConsultant(String mail, UserRole role) {
		if (UserRole.MANAGER == role || UserRole.MANAGER_ADMIN == role || UserRole.CDR_ADMIN == role
				|| UserRole.CDR == role) {
			return personEntityController.getPersonByMailAndType(mail, PersonRole.CONSULTANT);
		}
		throw new ForbiddenException();
	}

	/**
	 * @param role the user's role
	 * @return the list of all consultants
	 * @author Lucas Royackkers
	 */
	public List<Person> getConsultants(UserRole role) {
		if ((UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role || UserRole.MANAGER == role)) {
			return personEntityController.getPersonsByRole(PersonRole.CONSULTANT);
		}
		throw new ForbiddenException();
	}

	/**
	 * Method to delegate consultant update
	 * 
	 * @param params JsonNode with all parameters
	 * @param role   the user's role
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ForbiddenException} if the current user hasn't the
	 *         rights to perform this action
	 * @author Lucas Royackkers
	 * @throws ParseException
	 */
	public HttpException updateConsultant(JsonNode params, UserRole role) throws ParseException {
		if (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role || UserRole.MANAGER == role) {
			return personEntityController.updatePerson(params, PersonRole.CONSULTANT);
		}
		throw new ForbiddenException();
	}
}
