package fr.alten.ambroiseJEE.controller.business;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.beans.Job;
import fr.alten.ambroiseJEE.model.entityControllers.JobEntityController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.security.UserRoleLists;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;

/**
 * Job controller for business rules
 *
 * @author Lucas Royackkers
 *
 */
@Service
public class JobBusinessController {

	private final UserRoleLists roles = UserRoleLists.getInstance();

	@Autowired
	private JobEntityController jobEntityController;

	/**
	 * Method to delegate Job creation
	 *
	 * @param params the JsonNode containing all Job parameters (only its title, e.g
	 *               title = "Ingénieur système")
	 * @param role   the current logged user's role
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database and {@link CreatedException} if the job is created
	 * @throws {@link ForbiddenException} if the current logged user hasn't the
	 *         rights to perform this action
	 * @author Lucas Royackkers
	 */
	public HttpException createJob(final JsonNode params, final UserRole role) {
		return isAdmin(role) ? this.jobEntityController.createJob(params) : new ForbiddenException();
	}

	/**
	 * Method to delegate Job deletion
	 *
	 * @param params the JsonNode containing all Job parameters (only its title, e.g
	 *               title = "Ingénieur système")
	 * @param role   the user's role
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ResourceNotFoundException} if the resource hasn't
	 *         been found in the database and {@link OkException} if the job is
	 *         deleted
	 * @throws {@link ForbiddenException} if the current logged user hasn't the
	 *         rights to perform this action
	 * @author Lucas Royackkers
	 */
	public HttpException deleteJob(final JsonNode params, final UserRole role) {
		return isAdmin(role) ? this.jobEntityController.deleteJob(params) : new ForbiddenException();
	}

	/**
	 * Get all Jobs
	 *
	 * @param role the user's role
	 * @return a List of Job objects (can be empty)
	 * @throws {@link ForbiddenException} if the current logged user hasn't the
	 *         rights to perform this action
	 * @author Lucas Royackkers
	 */
	public List<Job> getJobs(final UserRole role) {
		if (isNotConsultantOrDeactivated(role)) {
			return this.jobEntityController.getJobs();
		}
		throw new ForbiddenException();
	}

	public boolean isAdmin(final UserRole role) {
		return this.roles.isAdmin(role);
	}

	public boolean isNotConsultantOrDeactivated(final UserRole role) {
		return this.roles.isNot_ConsultantOrDeactivated(role);
	}

	/**
	 * Method to delegate Job update
	 *
	 * @param params the JsonNode containing all Job parameters
	 * @param role   the user's role
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ResourceNotFoundException} if the resource hasn't
	 *         been found in the database and {@link OkException} if the job is
	 *         updated
	 * @throws {@link ForbiddenException} if the current logged user hasn't the
	 *         rights to perform this action
	 * @author Lucas Royackkers
	 */
	public HttpException updateJob(final JsonNode params, final UserRole role) {
		return isAdmin(role) ? this.jobEntityController.updateJob(params) : new ForbiddenException();
	}

}
