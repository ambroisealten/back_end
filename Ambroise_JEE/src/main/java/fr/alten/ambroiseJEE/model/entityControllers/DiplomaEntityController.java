package fr.alten.ambroiseJEE.model.entityControllers;

import java.text.ParseException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.beans.Diploma;
import fr.alten.ambroiseJEE.model.dao.DiplomaRepository;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;

/**
 * User controller for entity diploma gestion rules
 *
 * @author Lucas Royackkers
 *
 */
@Service
public class DiplomaEntityController {
	@Autowired
	private DiplomaRepository diplomaRepository;

	/**
	 * Method to create a diploma
	 *
	 * @param jDiploma JsonNode with all diploma parameters (name, yearOfResult)
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database and {@link CreatedException} if the diploma is created
	 * @author Lucas Royackkers
	 * @throws ParseException
	 */
	public HttpException createDiploma(final JsonNode jDiploma) throws ParseException {
		final Optional<Diploma> diplomaOptional = this.diplomaRepository
				.findByNameAndYearOfResult(jDiploma.get("name").textValue(), jDiploma.get("yearOfResult").textValue());
		if (diplomaOptional.isPresent()) {
			return new ConflictException();
		}

		final Diploma newDiploma = new Diploma();
		newDiploma.setName(jDiploma.get("name").textValue());
		newDiploma.setYearOfResult(jDiploma.get("yearOfResult").textValue());

		this.diplomaRepository.save(newDiploma);

		return new CreatedException();
	}

	/**
	 *
	 * @param jDiploma the JsonNode containing all diploma parameters (name,
	 *                 yearOfResult)
	 * @return {@link HttpException} corresponding to the status of the request
	 *         ({@link ResourceNotFoundException} if the resource is not found and
	 *         {@link OkException} if the diploma is deactivated
	 * @author Lucas Royackkers
	 */
	public HttpException deleteDiploma(final JsonNode jDiploma) {
		final Optional<Diploma> diplomaOptionnal = this.diplomaRepository
				.findByNameAndYearOfResult(jDiploma.get("name").textValue(), jDiploma.get("yearOfResult").textValue());

		if (diplomaOptionnal.isPresent()) {
			final Diploma diploma = diplomaOptionnal.get();
			diploma.setName("deactivated" + System.currentTimeMillis());
			diploma.setYearOfResult(null);
			this.diplomaRepository.save(diploma);
		} else {
			return new ResourceNotFoundException();
		}
		return new OkException();
	}

	/**
	 * Get a List of Diplomas given their name
	 * 
	 * @param name the searched name for this query
	 * @return a List of Diplomas object (can be empty)
	 * @author Lucas Royackkers
	 */
	public List<Diploma> getDiplomaByName(final String name) {
		return this.diplomaRepository.findByName(name);
	}

	/**
	 * Get a specific Diploma
	 * 
	 * @param name         the searched name for this query
	 * @param yearOfResult the searched year of result for this diploma
	 * @return an Optional with the corresponding Diploma or not
	 * @author Lucas Royackkers
	 */
	public Optional<Diploma> getDiplomaByNameAndYearOfResult(final String name, final String yearOfResult) {
		return this.diplomaRepository.findByNameAndYearOfResult(name, yearOfResult);
	}

	/**
	 * Get all Diplomas in the database
	 *
	 * @return a List of Diplomas (can be empty)
	 * @author Lucas Royackkers
	 */
	public List<Diploma> getDiplomas() {
		return this.diplomaRepository.findAll();
	}

	/**
	 * Update a Diploma
	 *
	 * @param jDiploma JsonNode with all diploma parameters
	 *                 (name,oldName,yearOfResult,oldYearOfResult)
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ResourceNotFoundException} if the diploma is not
	 *         found and {@link CreatedException} if the diploma is updated
	 * @author Lucas Royackkers
	 * @throws ParseException
	 */
	public HttpException updateDiploma(final JsonNode jDiploma) throws ParseException {
		final Optional<Diploma> diplomaOptionnal = this.diplomaRepository.findByNameAndYearOfResult(
				jDiploma.get("oldName").textValue(), jDiploma.get("oldYearOfResult").textValue());

		if (diplomaOptionnal.isPresent()) {
			final Diploma diploma = diplomaOptionnal.get();

			final Optional<Diploma> newDiplomaOptional = this.diplomaRepository.findByNameAndYearOfResult(
					jDiploma.get("name").textValue(), jDiploma.get("yearOfResult").textValue());
			if (newDiplomaOptional.isPresent()) {
				return new ConflictException();
			}

			diploma.setName(jDiploma.get("name").textValue());
			diploma.setYearOfResult(jDiploma.get("yearOfResult").textValue());

			this.diplomaRepository.save(diploma);
		} else {
			return new ResourceNotFoundException();
		}
		return new OkException();
	}

}
