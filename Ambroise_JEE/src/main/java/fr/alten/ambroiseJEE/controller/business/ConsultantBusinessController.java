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
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.UnprocessableEntityException;
import fr.alten.ambroiseJEE.utils.personRole.PersonRole;

/**
 * Consultant controller for business rules.
 *
 * @author Lucas Royackkers
 *
 */
@Service
public class ConsultantBusinessController {
	private final UserRoleLists roles = UserRoleLists.getInstance();

	@Autowired
	private PersonEntityController personEntityController;

	@Autowired
	private SkillsSheetBusinessController skillsSheetBusinessController;

	/**
	 * Method to delegate consultant creation
	 *
	 * @param role               the user's role
	 * @param personInChargeMail TODO
	 * @param jUser              JsonNode with all consultant(person) parameters
	 *
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ForbiddenException} if the current user hasn't the
	 *         rights to perform this action
	 * @author Lucas Royackkers
	 * @throws ParseException
	 */
	public HttpException createConsultant(final JsonNode jConsultant, final UserRole role,
			final String personInChargeMail) throws ParseException {
		if (isManager(role)) {
			return this.personEntityController.createPerson(jConsultant, PersonRole.CONSULTANT, personInChargeMail);
		} else {
			return new ForbiddenException();
		}

	}

	/**
	 * Method to create an Consultant and a Skills Sheet (with the created
	 * Consultant in it)
	 *
	 * @param params the JsonNode containing all informations about the Person and
	 *               the Skills Sheet
	 * @param role   the current logged user's role
	 * @param mail   the current logged user's mail
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ForbiddenException} if the current user hasn't the
	 *         rights to perform this action, {@link UnprocessableEntityException}
	 *         if there is a problem during the creation of one of the element, and
	 *         {@link CreatedException} if the consultant and the Skills sheet are
	 *         sucessfully created
	 * @author Lucas Royackkers
	 */
	public HttpException createConsultantAndSkillsSheet(final JsonNode params, final UserRole role,
			final String personInChargeMail) {
		if (isManager(role)) {
			final JsonNode jConsultant = params.get("person");
			final HttpException createResult = this.personEntityController.createPerson(jConsultant,
					PersonRole.CONSULTANT, personInChargeMail);
			if (!(createResult instanceof CreatedException)) {
				return createResult;
			} else {
				final JsonNode jSkillsSheet = params.get("skillsSheet");
				final HttpException createSkillsSheetResult = this.skillsSheetBusinessController
						.createSkillsSheet(jSkillsSheet, role, personInChargeMail);
				if (!(createSkillsSheetResult instanceof CreatedException)) {
					final HttpException deleteResult = this.personEntityController.deletePersonByRole(jConsultant,
							PersonRole.APPLICANT);
					if (!(createSkillsSheetResult instanceof OkException)) {
						return deleteResult;
					}
				} else {
					return createSkillsSheetResult;
				}
			}
		}
		return new ForbiddenException();
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
	public HttpException deleteConsultant(final JsonNode params, final UserRole role) {
		if (isManager(role)) {
			return this.personEntityController.deletePersonByRole(params, PersonRole.CONSULTANT);
		}
		return new ForbiddenException();
	}

	/**
	 * Try to fetch a consultant given its mail
	 *
	 * @param mail the consultant's mail
	 *
	 * @return an Optional with the corresponding consultant (or not)
	 * @author Lucas Royackkers
	 * @throws ForbiddenException (if the user hasn't the right to do so)
	 */
	public Person getConsultant(final String mail, final UserRole role) {
		if (isConnected(role)) {
			return this.personEntityController.getPersonByMailAndType(mail, PersonRole.CONSULTANT);
		}
		throw new ForbiddenException();
	}

	/**
	 * Get all Consultants
	 *
	 * @param role the user's role
	 * @return the list of all consultants
	 * @author Lucas Royackkers
	 */
	public List<Person> getConsultants(final UserRole role) {
		if (isConnected(role)) {
			return this.personEntityController.getPersonsByRole(PersonRole.CONSULTANT);
		}
		throw new ForbiddenException();
	}

	/**
	 * Method to test if the user is connected (not an consultant or a deactivated
	 * user)
	 *
	 * @param role the current logged user's role
	 * @return true if the user is connected, otherwise false
	 * @author Lucas Royackkers
	 */
	public boolean isConnected(final UserRole role) {
		return this.roles.isNot_ConsultantOrDeactivated(role);
	}

	/**
	 * Method to test if the user is manager
	 *
	 * @param role {@link UserRole} the current logged user's role
	 * @return true if it's manager or manager admin, otherwise false
	 * @author Andy Chabalier
	 */
	public boolean isManager(final UserRole role) {
		return this.roles.isManager(role);
	}

	/**
	 * Method to delegate consultant update
	 *
	 * @param params             JsonNode with all parameters
	 * @param role               the user's role
	 * @param personInChargeMail TODO
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ForbiddenException} if the current user hasn't the
	 *         rights to perform this action
	 * @author Lucas Royackkers
	 * @throws ParseException
	 */
	public HttpException updateConsultant(final JsonNode params, final UserRole role, final String personInChargeMail)
			throws ParseException {
		if (isManager(role)) {
			return this.personEntityController.updatePerson(params, PersonRole.CONSULTANT, personInChargeMail);
		}
		return new ForbiddenException();
	}

	/**
	 * Method to delegate consultant update
	 *
	 * @param params             JsonNode with all parameters
	 * @param role               the user's role
	 * @param personInChargeMail TODO
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ForbiddenException} if the current user hasn't the
	 *         rights to perform this action
	 * @author Lucas Royackkers
	 * @throws ParseException
	 */
	public HttpException updateArchive(final JsonNode params, final UserRole role, final String personInChargeMail)
			throws ParseException {
		if (isManager(role)) {
			return this.personEntityController.updatePerson(params, PersonRole.DEMISSIONNAIRE, personInChargeMail);
		}
		return new ForbiddenException();
	}
}
