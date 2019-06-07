/**
 *
 */
package fr.alten.ambroiseJEE.controller.business;

import java.text.ParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.beans.ApplicantForum;
import fr.alten.ambroiseJEE.model.entityControllers.ApplicantForumEntityController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.security.UserRoleLists;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;
import fr.alten.ambroiseJEE.utils.httpStatus.UnprocessableEntityException;

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
	
	private final UserRoleLists roles = UserRoleLists.getInstance();

	/**
	 * Method to delegate applicant creation
	 *
	 * @param jUser JsonNode with all applicant parameters
	 * @param role  the current logged user's role
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database, {@link UnprocessableEntityException} if the mail of the
	 *         applicant isn't in a good format, {@link ResourceNotFoundException}
	 *         if a required resource in the object can't be found,
	 *         {@link CreatedException} if the Applicant is created or
	 *         {@link ForbiddenException} if the logged user hasn't the rights to
	 *         perform this action
	 * @author Andy Chabalier
	 * @throws ParseException
	 */
	public HttpException createApplicant(final JsonNode jApplicant, final UserRole role) throws ParseException {
		if (isNot_ConsultantOrDeactivated(role)) {
			return this.applicantForumEntityController.createApplicant(jApplicant);
		}
		return new ForbiddenException();
	}

	/**
	 * Method to delegate applicant's deletion
	 *
	 * @param params JsonNode containing all the parameters
	 * @param role   the current logged user's role
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ResourceNotFoundException} if the resource isn't in
	 *         the database, {@link OkException} if the applicant is deleted or
	 *         {@link ForbiddenException} if the logged user hasn't the rights to
	 *         perform this action
	 * @author Andy Chabalier
	 */
	public HttpException deleteApplicant(final JsonNode params, final UserRole role) {
		if (isNot_ConsultantOrDeactivated(role)) {
			return this.applicantForumEntityController.deleteApplicant(params);
		}
		return new ForbiddenException();
	}
	
	public boolean isAdmin(final UserRole role) {
		return this.roles.isAdmin(role);
	}

	public boolean isNot_ConsultantOrDeactivated(final UserRole role) {
		return this.roles.isNot_ConsultantOrDeactivated(role);
	}
	
	public boolean isManagerOrCdrAdmin(final UserRole role) {
		return this.roles.isManagerOrCdrAdmin(role);
	}

	/**
	 * Try to fetch an applicant given its name
	 *
	 * @param mail the applicant's mail
	 * @param role the current logged user's role
	 * @return An ApplicantForum if it's been found (or not)
	 * @author Andy Chabalier
	 * @throws {@link ForbiddenException} if the logged user hasn't the rights to
	 */
	public ApplicantForum getApplicant(final String mail, final UserRole role) {
		if (isNot_ConsultantOrDeactivated(role)) {
			return this.applicantForumEntityController.getApplicantByMail(mail);
		}
		throw new ForbiddenException();
	}

	/**
	 * @param role the current logged user's role
	 * @return the list of all applicants (can be empty)
	 * @author Andy Chabalier
	 * @throws {@link ForbiddenException} if the logged user hasn't the rights to
	 */
	public List<ApplicantForum> getApplicants(final UserRole role) {
		if (isNot_ConsultantOrDeactivated(role)) {
			return this.applicantForumEntityController.getApplicants();
		}
		throw new ForbiddenException();
	}

	/**
	 * Method to delegate applicant's update
	 *
	 * @param params JsonNode containing all the parameters
	 * @param role   the current logged user's role
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ResourceNotFoundException} if the resource isn't in
	 *         the database, {@link OkException} if the applicant is updated and
	 *         {@link ForbiddenException} if the logged user hasn't the rights to
	 *         perform this action
	 * @author Andy Chabalier
	 * @throws ParseException
	 */
	public HttpException updateApplicant(final JsonNode params, final UserRole role) throws ParseException {
		if (isNot_ConsultantOrDeactivated(role)) {
			return this.applicantForumEntityController.updateApplicant(params);
		}
		return new ForbiddenException();
	}
}
