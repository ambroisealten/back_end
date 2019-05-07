package fr.alten.ambroiseJEE.model.entityControllers;

import java.util.List;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.mongodb.DuplicateKeyException;

import fr.alten.ambroiseJEE.model.beans.Employer;
import fr.alten.ambroiseJEE.model.dao.EmployerRepository;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.InternalServerErrorException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;

/**
 * Entity controller for the Employer
 *
 * @author Lucas Royackkers
 *
 */
@Service
public class EmployerEntityController {

	@Autowired
	private EmployerRepository employerRepository;

	/**
	 * Method to create an employer
	 *
	 * @param jEmployer JsonNode with all employer parameters
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database and {@link CreatedException} if the employer is created
	 * @author Lucas Royackkers
	 */
	public HttpException createEmployer(final JsonNode jEmployer) {
		final Employer newEmployer = new Employer();
		newEmployer.setName(jEmployer.get("name").textValue());
		try {
			this.employerRepository.save(newEmployer);
		} catch (final Exception e) {
			return new ConflictException();
		}
		return new CreatedException();
	}

	/**
	 * Supplier for employer creation
	 *
	 * @param name the amployer name
	 * @return the supplier of employer
	 * @author Andy Chabalier
	 */
	public Supplier<? extends Employer> createEmployer(final String name) {
		return () -> {
			Employer newEmployer = new Employer();
			newEmployer.setName(name);
			try {
				newEmployer = this.employerRepository.save(newEmployer);
			} catch (final DuplicateKeyException dke) {
				throw new ConflictException();
			} catch (final Exception e) {
				throw new InternalServerErrorException();
			}
			return newEmployer;
		};
	}

	/**
	 * Method to delete an Employer
	 *
	 * @param jEmployer JsonNode with all employer parameters
	 * @return {@link HttpException} corresponding to the status of the request
	 *         ({@link ResourceNotFoundException} if the resource is not found and
	 *         {@link OkException} if the employer is deactivated
	 * @author Lucas Royackkers
	 */
	public HttpException deleteEmployer(final JsonNode jEmployer) {
		try {
			final Employer employer = this.employerRepository.findByName(jEmployer.get("name").textValue())
					.orElseThrow(ResourceNotFoundException::new);
			employer.setName("deactivated" + System.currentTimeMillis());
			this.employerRepository.save(employer);
		} catch (final ResourceNotFoundException rnfe) {
			return rnfe;
		} catch (final Exception e) {
			return new ConflictException();
		}
		return new OkException();
	}

	/**
	 * Get an Employer by its name
	 *
	 * @param name the employer's name
	 * @return an Optional with the matched employer (can be empty)
	 * @author Lucas Royackkers
	 */
	public Employer getEmployer(final String name) {
		return this.employerRepository.findByName(name).orElseThrow(ResourceNotFoundException::new);
	}

	/**
	 * Get all Employers in the database
	 *
	 * @return a List containing all Employers in the database (can be empty)
	 * @author Lucas Royackkers
	 */
	public List<Employer> getEmployers() {
		return this.employerRepository.findAll();
	}

	/**
	 * Method to update an Employer
	 *
	 * @param jEmployer JsonNode with all employer parameters and the old name to
	 *                  perform the update even if the name is changed
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ResourceNotFoundException} if the resource is not
	 *         found, {@link ConflictException} if there is a conflict in the
	 *         database and {@link OkException} if the employer is updated
	 * @author Lucas Royackkers
	 */
	public HttpException updateEmployer(final JsonNode jEmployer) {
		try {
			final Employer employer = this.employerRepository.findByName(jEmployer.get("oldName").textValue())
					.orElseThrow(ResourceNotFoundException::new);
			employer.setName(jEmployer.get("name").textValue());
			this.employerRepository.save(employer);
		} catch (final ResourceNotFoundException rnfe) {
			return rnfe;
		} catch (final Exception e) {
			return new ConflictException();
		}
		return new OkException();
	}

}
