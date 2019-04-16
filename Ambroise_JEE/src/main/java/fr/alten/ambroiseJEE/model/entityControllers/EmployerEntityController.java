package fr.alten.ambroiseJEE.model.entityControllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.beans.Employer;
import fr.alten.ambroiseJEE.model.dao.EmployerRepository;
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
	public HttpException createEmployer(JsonNode jEmployer) {

		Optional<Employer> employerOptional = employerRepository.findByName(jEmployer.get("name").textValue());
		if (employerOptional.isPresent()) {
			return new ConflictException();
		}

		Employer newEmployer = new Employer();
		newEmployer.setName(jEmployer.get("name").textValue());

		try {
			employerRepository.save(newEmployer);
		} catch (Exception e) {
			return new ConflictException();
		}
		return new CreatedException();
	}

	/**
	 *
	 * @param jEmployer JsonNode with all employer parameters
	 * @return {@link HttpException} corresponding to the status of the request
	 *         ({@link RessourceNotFoundException} if the resource is not found and
	 *         {@link OkException} if the employer is deactivated
	 * @author Lucas Royackkers
	 */
	public HttpException deleteEmployer(JsonNode jEmployer) {
		Optional<Employer> employerOptionnal = employerRepository.findByName(jEmployer.get("name").textValue());

		if (employerOptionnal.isPresent()) {
			Employer employer = employerOptionnal.get();
			employer.setName("deactivated" + System.currentTimeMillis());
			employerRepository.save(employer);
		} else {
			throw new RessourceNotFoundException();
		}
		return new OkException();
	}

	public Optional<Employer> getEmployer(String name) {
		return employerRepository.findByName(name);
	}

	public List<Employer> getEmployers() {
		return employerRepository.findAll();
	}

	/**
	 *
	 * @param jEmployer JsonNode with all employer parameters and the old name to
	 *                  perform the update even if the name is changed
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link RessourceNotFoundException} if the resource is not
	 *         found and {@link OkException} if the employer is updated
	 * @author Lucas Royackkers
	 */
	public HttpException updateEmployer(JsonNode jEmployer) {
		Optional<Employer> employerOptionnal = employerRepository.findByName(jEmployer.get("oldName").textValue());

		if (employerOptionnal.isPresent()) {
			Employer employer = employerOptionnal.get();
			employer.setName(jEmployer.get("name").textValue());

			employerRepository.save(employer);
		} else {
			throw new RessourceNotFoundException();
		}
		return new OkException();
	}

}
