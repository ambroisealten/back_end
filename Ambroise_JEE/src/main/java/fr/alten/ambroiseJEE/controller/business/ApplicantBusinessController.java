package fr.alten.ambroiseJEE.controller.business;

import java.text.ParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.beans.Person;
import fr.alten.ambroiseJEE.model.entityControllers.PersonEntityController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.security.UserRoleLists;
import fr.alten.ambroiseJEE.utils.PersonRole;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;

/**
 * Applicant controller for business rules.
 *
 * @author Lucas Royackkers
 *
 */
@Service
public class ApplicantBusinessController {
	private final UserRoleLists roles = UserRoleLists.getInstance();
	
	@Autowired
	private PersonEntityController personEntityController;

	/**
	 * Method to delegate applicant creation
	 *
	 * @param jApplicant JsonNode with all applicant(person) parameters
	 * @param role       the user's role
	 * @param personInChargeMail TODO
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ForbiddenException} if the current user hasn't the
	 *         rights to perform this action and {@link CreatedException} if the
	 *         applicant is sucessfully created
	 * @author Lucas Royackkers
	 * @throws ParseException, ForbiddenException
	 */
	public HttpException createApplicant(final JsonNode jApplicant, final UserRole role, String personInChargeMail) throws ParseException {
		if (this.isManagerOrCdrAdmin(role)) {
			return this.personEntityController.createPerson(jApplicant, PersonRole.APPLICANT, personInChargeMail);
		}
		return new ForbiddenException();
	}

	/**
	 * Method to delegate applicant's deletion
	 *
	 * @param params JsonNode containing all the parameters
	 * @param role   the user's role
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ForbiddenException} if the current user hasn't the
	 *         rights to perform this action and {@link OkException} if the
	 *         applicant is deleted
	 * @author Lucas Royackkers
	 * @throws ForbiddenException
	 */
	public HttpException deleteApplicant(final JsonNode params, final UserRole role) {
		if (this.isManagerOrCdrAdmin(role)) {
			return this.personEntityController.deletePerson(params, PersonRole.APPLICANT);
		}
		return new ForbiddenException();
	}

	/**
	 * Try to fetch an applicant given its mail
	 *
	 * @param mail the applicant's mail
	 * @param role the user's role
	 * @return an Optional with the corresponding applicant (or not)
	 * @author Lucas Royackkers
	 * @throws ForbiddenException (if the user hasn't the right to do so)
	 */
	public Person getApplicant(final String mail, final UserRole role) {
		if (this.isConnected(role)) {
			return this.personEntityController.getPersonByMailAndType(mail, PersonRole.APPLICANT);
		}
		throw new ForbiddenException();
	}

	/**
	 * @param role the user's role
	 * @return the list of all applicants or ({@link ForbiddenException} if the
	 *         current user hasn't the rights to perform this action
	 * @author Lucas Royackkers
	 * @throws ForbiddenException
	 */
	public List<Person> getApplicants(final UserRole role) {
		if (this.isConnected(role)) {
			return this.personEntityController.getPersonsByRole(PersonRole.APPLICANT);
		}
		throw new ForbiddenException();
	}

	/**
	 * Method to delegate applicant's update
	 *
	 * @param params JsonNode containing all the parameters
	 * @param role   the user's role
	 * @param personInChargeMail TODO
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ForbiddenException} if the current user hasn't the
	 *         rights to perform this action and {@link OkException} if the
	 *         applicant is updated
	 * @author Lucas Royackkers
	 * @throws ParseException, ForbiddenException
	 */
	public HttpException updateApplicant(final JsonNode params, final UserRole role, String personInChargeMail) throws ParseException {
		if (this.isManagerOrCdrAdmin(role)) {
			return this.personEntityController.updatePerson(params, PersonRole.APPLICANT, personInChargeMail);
		}
		return new ForbiddenException();
	}
	
	/**
	 * Method to test if the user is a Manager (Admin or not) or a CDR_Admin
	 * 
	 * @param role the current logged user's role
	 * @return true if it's a CDR or a Manager (Admin or not), otherwise false
	 * @author Lucas Royackkers
	 */
	public boolean isManagerOrCdrAdmin(final UserRole role) {
		return this.roles.isManagerOrCdrAdmin(role);
	}

	
	/**
	 * Method to test if the user is connected (not an consultant or a deactivated user)
	 * 
	 * @param role the current logged user's role
	 * @return true if the user is connected, otherwise false
	 * @author Lucas Royackkers
	 */
	public boolean isConnected(final UserRole role) {
		return this.roles.isNotConsultantOrDeactivated(role);
	}

}
