package fr.alten.ambroiseJEE.controller.business;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.beans.Job;
import fr.alten.ambroiseJEE.model.entityControllers.JobEntityController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;

/**
 * Job controller for business rules
 * 
 * @author Lucas Royackkers >>>>>>> origin/JobWebService
 *
 */
@Service
public class JobBusinessController {

	@Autowired
	private JobEntityController jobEntityController;

	/**
	 * Method to delegate Job creation
	 * 
	 * @param params the JsonNode containing all Job parameters
	 * @param role   the user's role
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database and {@link CreatedException} if the job is created
	 * @author Lucas Royackkers
	 */
	public HttpException createJob(JsonNode params, UserRole role) {
		if (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role) {
			return jobEntityController.createJob(params);
		}
		throw new ForbiddenException();
	}

	/**
	 * Method to delegate Job deletion
	 * 
	 * @param params the JsonNode containing all Job parameters
	 * @param role   the user's role
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ResourceNotFoundException} if the resource hasn't
	 *         been found in the database and {@link OkException} if the job is
	 *         desactivated
	 * @author Lucas Royackkers
	 */
	public HttpException deleteJob(JsonNode params, UserRole role) {
		if (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role) {
			return jobEntityController.deleteJob(params);
		}
		throw new ForbiddenException();
	}

	/**
	 *
	 * @param role the user's role
	 * @return a List of Job objects
	 * @author Lucas Royackkers
	 */
	public List<Job> getJobs(UserRole role) {
		if (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role || UserRole.CDR == role
				|| UserRole.MANAGER == role) {
			return jobEntityController.getJobs();
		}
		throw new ForbiddenException();
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
	 * @author Lucas Royackkers
	 */
	public HttpException updateJob(JsonNode params, UserRole role) {
		if (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role) {
			return jobEntityController.updateJob(params);
		}
		throw new ForbiddenException();
	}

}
