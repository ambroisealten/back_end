package fr.alten.ambroiseJEE.model.entityControllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.beans.Job;
import fr.alten.ambroiseJEE.model.dao.JobRepository;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.RessourceNotFoundException;

/**
 * @author Lucas Royackkers
 *
 */

@Service
public class JobEntityController {
	@Autowired
	private JobRepository jobRepository;
	
	public Optional<Job> getJob(String title) {
		return jobRepository.findByTitle(title);
	}

	/**
	 * Method to create a job.
	 * 
	 * @param jJob JsonNode with all job parameters
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database and {@link CreatedException} if the job is created
	 * @author Lucas Royackkers
	 */
	public HttpException createJob(JsonNode jJob) {

		Job newJob = new Job();
		newJob.setTitle(jJob.get("title").textValue());

		try {
			jobRepository.save(newJob);
		} catch (Exception e) {
			return new ConflictException();
		}
		return new CreatedException();
	}

	/**
	 * @return the list of all jobs
	 * @author Lucas Royackkers
	 */
	public List<Job> getJobs() {
		return jobRepository.findAll();
	}

	/**
	 * 
	 * @param jJob JsonNode with all Job parameters and the old name to perform the update even if the name is changed
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link RessourceNotFoundException} if the resource is not found
	 *         and {@link OkException} if the Job is updated
	 * @author Lucas Royackkers
	 */
	public HttpException updateJob(JsonNode jJob) {
		Optional<Job> JobOptionnal = jobRepository.findByTitle(jJob.get("oldTitle").textValue());
		
		if (JobOptionnal.isPresent()) {
			Job job = JobOptionnal.get();
			job.setTitle(jJob.get("title").textValue());
			
			jobRepository.save(job);
		}
		else {
			throw new RessourceNotFoundException();
		}		
		return new OkException();
	}

	/**
	 * 
	 * @param jJob JsonNode with all Job parameters 
	 * @return {@link HttpException} corresponding to the status of the
	 *         request ({@link RessourceNotFoundException} if the resource is not found
	 *         and {@link OkException} if the Job is deactivated
	 * @author Lucas Royackkers
	 */
	public HttpException deleteJob(JsonNode jJob) {
		Optional<Job> JobOptionnal = jobRepository.findByTitle(jJob.get("title").textValue());
		
		if (JobOptionnal.isPresent()) {
			Job job = JobOptionnal.get();
			job.setTitle("deactivated" + System.currentTimeMillis());
			jobRepository.save(job);
		}
		else {
			throw new RessourceNotFoundException();
		}		
		return new OkException();
	}
}
