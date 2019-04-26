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
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;

/**
 * @author Lucas Royackkers
 *
 */

@Service
public class JobEntityController {
	@Autowired
	private JobRepository jobRepository;

	/**
	 * Method to create a job.
	 *
	 * @param jJob JsonNode with all job parameters (only the title)
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request, {@link ConflictException} if there is a conflict in the
	 *         database and {@link CreatedException} if the job is created
	 * @author Lucas Royackkers
	 */
	public HttpException createJob(final JsonNode jJob) {
		final Job newJob = new Job();
		newJob.setTitle(jJob.get("title").textValue());

		try {
			this.jobRepository.save(newJob);
		} catch (final Exception e) {
			return new ConflictException();
		}
		return new CreatedException();
	}

	/**
	 * Method to delete a Job
	 *
	 * @param jJob JsonNode with all Job parameters (only the title)
	 * @return {@link HttpException} corresponding to the status of the request,
	 *         {@link ResourceNotFoundException} if the resource is not found and
	 *         {@link OkException} if the Job is deactivated
	 * @author Lucas Royackkers
	 */
	public HttpException deleteJob(final JsonNode jJob) {
		final Optional<Job> JobOptionnal = this.jobRepository.findByTitle(jJob.get("title").textValue());

		if (JobOptionnal.isPresent()) {
			final Job job = JobOptionnal.get();
			job.setTitle("deactivated" + System.currentTimeMillis());
			this.jobRepository.save(job);
		} else {
			return new ResourceNotFoundException();
		}
		return new OkException();
	}

	/**
	 * Fetch a Job given its title
	 * 
	 * @param title the Job's title
	 * @return an Optional with the corresponding Job or not
	 * @author Lucas Royackkers
	 */
	public Optional<Job> getJob(final String title) {
		return this.jobRepository.findByTitle(title);
	}

	/**
	 * Get a List of all the jobs
	 * 
	 * @return the list of all jobs (can be empty)
	 * @author Lucas Royackkers
	 */
	public List<Job> getJobs() {
		return this.jobRepository.findAll();
	}

	/**
	 * Method to update a Job
	 *
	 * @param jJob JsonNode with all Job parameters (the old title and the new one)
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request, {@link ResourceNotFoundException} if the resource is not
	 *         found, {@link ConflictException} if there is a conflict in the
	 *         database and {@link OkException} if the Job is updated
	 * @author Lucas Royackkers
	 */
	public HttpException updateJob(final JsonNode jJob) {
		final Optional<Job> JobOptionnal = this.jobRepository.findByTitle(jJob.get("oldTitle").textValue());

		if (JobOptionnal.isPresent()) {
			final Job job = JobOptionnal.get();
			job.setTitle(jJob.get("title").textValue());

			try {
				this.jobRepository.save(job);
			} catch (final Exception e) {
				return new ConflictException();
			}
		} else {
			return new ResourceNotFoundException();
		}
		return new OkException();
	}
}
