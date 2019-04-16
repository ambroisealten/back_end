/**
 * 
 */
package fr.alten.ambroiseJEE.controller.business;

import java.util.List;
import java.util.Optional;

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
import fr.alten.ambroiseJEE.utils.httpStatus.RessourceNotFoundException;

/**
 * Job controller for business rules.
 * @author Thomas Decamp
 *
 */
@Service
public class JobBusinessController {

	@Autowired
	private JobEntityController jobEntityController;
	

	public Optional<Job> getJob(String title) {
		return jobEntityController.getJob(title);
	}


	/**
	 * Method to delegate job creation
	 * @param jUser JsonNode with all job parameters
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database and {@link CreatedException} if the job is created
	 * @author Thomas Decamp
	 */
	public HttpException createJob(JsonNode jJob, UserRole role) {
		return (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role) ? jobEntityController.createJob(jJob) : new ForbiddenException();
	}


	/**
	 * @param role the user role
	 * @return the list of all jobs
	 * @author Thomas Decamp
	 */
	public List<Job> getJobs(UserRole role) {
		if (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role) {
			return jobEntityController.getJobs();
		}
		throw new ForbiddenException();	
	}


	/**
	 * 
	 * @param jJob JsonNode with all job parameters and the old title to perform the update even if the title is changed
	 * @param role user role
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link RessourceNotFoundException} if the resource is not found
	 *         and {@link CreatedException} if the job is updated
	 * @author Thomas Decamp
	 */
	public HttpException updateJob(JsonNode jJob, UserRole role) {
		return (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role) ? jobEntityController.updateJob(jJob) : new ForbiddenException();
	}


	/**
	 * 
	 * @param params the job title to delete 
	 * @param role the user role
	 * @return @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ForbiddenException} if the resource is not found
	 *         and {@link CreatedException} if the job is deleted
	 * @author Thomas Decamp
	 */
	public HttpException deleteJob(JsonNode params, UserRole role) {
		return (UserRole.CDR_ADMIN == role || UserRole.MANAGER_ADMIN == role) ? jobEntityController.deleteJob(params.get("title").textValue()) : new ForbiddenException();
	}

}
