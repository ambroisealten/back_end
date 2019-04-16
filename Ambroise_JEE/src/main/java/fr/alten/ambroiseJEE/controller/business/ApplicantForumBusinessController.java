/**
 * 
 */
package fr.alten.ambroiseJEE.controller.business;

import java.text.ParseException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.beans.ApplicantForum;
import fr.alten.ambroiseJEE.model.entityControllers.ApplicantForumEntityController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;

/**
 * Applicant controller for business rules.
 * 
 * @author Andy Chabalier
 *
 */
@Service
public class ApplicantForumBusinessController {
	@Autowired
	private ApplicantForumEntityController applicantForumEntityController;

	/**
	 * Try to fetch an applicant given its name
	 * 
	 * @param mail the applicant's mail
	 * @param role the user's role
	 * @return an Optional with the corresponding applicant (or not)
	 * @author Andy Chabalier
	 * @throws ForbiddenException (if the user hasn't the right to do so)
	 */
	public Optional<ApplicantForum> getApplicant(String mail, UserRole role) {
		if (UserRole.CDR == role || UserRole.MANAGER == role || UserRole.MANAGER_ADMIN == role
				|| UserRole.CDR_ADMIN == role) {
			return applicantForumEntityController.getApplicantByMail(mail);
		}
		throw new ForbiddenException();
	}

	/**
	 * Method to delegate applicant creation
	 * 
	 * @param jUser JsonNode with all applicant parameters
	 * @param role  the user's role
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ForbiddenException} if the current user hasn't the
	 *         rights to perform this action
	 * @author Andy Chabalier
	 * @throws ParseException, ForbiddenException
	 */
	public HttpException createApplicant(JsonNode jApplicant, UserRole role) throws ParseException {
		if (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role || UserRole.MANAGER == role
				|| UserRole.CDR == role) {
			return applicantForumEntityController.createApplicant(jApplicant);
		}
		throw new ForbiddenException();
	}

	/**
	 * Method to delegate applicant's update
	 * 
	 * @param params JsonNode containing all the parameters
	 * @param role   the user's role
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ForbiddenException} if the current user hasn't the
	 *         rights to perform this action
	 * @author Andy Chabalier
	 * @throws ParseException, ForbiddenException
	 */
	public HttpException updateApplicant(JsonNode params, UserRole role) throws ParseException {
		if (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role || UserRole.MANAGER == role) {
			return applicantForumEntityController.updateApplicant(params);
		}
		throw new ForbiddenException();
	}

	/**
	 * Method to delegate applicant's deletion
	 * 
	 * @param params JsonNode containing all the parameters
	 * @param role   the user's role
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ForbiddenException} if the current user hasn't the
	 *         rights to perform this action
	 * @author Andy Chabalier
	 * @throws ForbiddenException
	 */
	public HttpException deleteApplicant(JsonNode params, UserRole role) {
		if (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role || UserRole.MANAGER == role) {
			return applicantForumEntityController.deleteApplicant(params);
		}
		throw new ForbiddenException();
	}

	/**
	 * @param role the user's role
	 * @return the list of all applicants or ({@link ForbiddenException} if the current user hasn't the rights to perform this action
	 * @author Andy Chabalier
	 * @throws ForbiddenException
	 */
	public List<ApplicantForum> getApplicants(UserRole role) {
		if ((UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role || UserRole.MANAGER == role || UserRole.CDR == role)) {
			return applicantForumEntityController.getApplicants();
		}
		throw new ForbiddenException();	
	}
}
