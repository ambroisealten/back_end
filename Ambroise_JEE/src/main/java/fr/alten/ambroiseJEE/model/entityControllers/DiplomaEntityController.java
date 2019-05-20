package fr.alten.ambroiseJEE.model.entityControllers;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.mongodb.DuplicateKeyException;

import fr.alten.ambroiseJEE.model.beans.Diploma;
import fr.alten.ambroiseJEE.model.dao.DiplomaRepository;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.InternalServerErrorException;
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
	 * @author Thomas Decamp
	 */
	public HttpException createDiploma(final JsonNode jDiploma) {
		final String diplomaName = jDiploma.get("name").textValue();
		final String diplomaYearOfResult = jDiploma.get("yearOfResult").textValue();
		if (this.diplomaRepository.findByNameAndYearOfResult(diplomaName, diplomaYearOfResult).isPresent()) {
			return new ConflictException();
		}

		final Diploma newDiploma = new Diploma();
		newDiploma.setName(diplomaName);
		newDiploma.setYearOfResult(diplomaYearOfResult);
		try {
			this.diplomaRepository.save(newDiploma);
		} catch (final DuplicateKeyException dke) {
			return new ConflictException();
		} catch (final Exception e) {
			LoggerFactory.getLogger(DiplomaEntityController.class).info(e.getStackTrace().toString());
			return new InternalServerErrorException();
		}
		return new CreatedException();
	}

	/**
	 * Supplier for diploma creation
	 *
	 * @param name         the diploma name
	 * @param yearOfResult the diploma year
	 * @return the supplier of diploma
	 * @author Andy Chabalier
	 */
	public Diploma createDiploma(final String name, final String yearOfResult) {
		if (this.diplomaRepository.findByNameAndYearOfResult(name, yearOfResult).isPresent()) {
			throw new ConflictException();
		}
		Diploma newDiploma = new Diploma();
		newDiploma.setName(name);
		newDiploma.setYearOfResult(yearOfResult);
		try {
			newDiploma = this.diplomaRepository.save(newDiploma);
		} catch (final DuplicateKeyException dke) {
			throw new ConflictException();
		} catch (final Exception e) {
			LoggerFactory.getLogger(DiplomaEntityController.class).info(e.getStackTrace().toString());
			throw new InternalServerErrorException();
		}
		return newDiploma;
	}

	/**
	 * Method to delete a Diploma
	 *
	 * @param jDiploma the JsonNode containing all diploma parameters (name,
	 *                 yearOfResult)
	 * @return {@link HttpException} corresponding to the status of the request
	 *         ({@link ResourceNotFoundException} if the resource is not found and
	 *         {@link OkException} if the diploma is deactivated
	 * @author Lucas Royackkers
	 */
	public HttpException deleteDiploma(final JsonNode jDiploma) {
		try {
			final Diploma diploma = this.diplomaRepository.findByNameAndYearOfResult(jDiploma.get("name").textValue(),
					jDiploma.get("yearOfResult").textValue()).orElseThrow(ResourceNotFoundException::new);
			diploma.setName("deactivated" + System.currentTimeMillis());
			diploma.setYearOfResult(null);
			this.diplomaRepository.save(diploma);
		} catch (final ResourceNotFoundException rnfe) {
			return rnfe;
		} catch (final Exception e) {
			return new ConflictException();
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
	 * @return the corresponding Diploma
	 * @throws {@link ResourceNotFoundException} when the Diploma hasn't been found
	 * @author Lucas Royackkers
	 */
	public Diploma getDiplomaByNameAndYearOfResult(final String name, final String yearOfResult) {
		return this.diplomaRepository.findByNameAndYearOfResult(name, yearOfResult)
				.orElseThrow(ResourceNotFoundException::new);
	}

	/**
	 * Get all Diplomas in the database
	 *
	 * @return a List of Diplomas (can be empty)
	 * @author Lucas Royackkers
	 */
	public List<Diploma> getDiplomas() {
		List<Diploma> results = this.diplomaRepository.findAll();
		List<String> allNames = new ArrayList<String>();
		List<Diploma> finalResults = new ArrayList<Diploma>();
		
		for(Diploma result : results) {
			if(!allNames.contains(result.getName())) {
				finalResults.add(result);
				allNames.add(result.getName());
			}
		}
		
		return finalResults;
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
	 */
	public HttpException updateDiploma(final JsonNode jDiploma) {
		return this.diplomaRepository
				.findByNameAndYearOfResult(jDiploma.get("oldName").textValue(),
						jDiploma.get("yearOfResult").textValue())
				// optional is present
				.map(diploma -> {
					diploma.setName(jDiploma.get("name").textValue());
					try {
						this.diplomaRepository.save(diploma);
					} catch (final DuplicateKeyException dke) {
						return new ConflictException();
					} catch (final Exception e) {
						LoggerFactory.getLogger(DiplomaEntityController.class).info(e.getStackTrace().toString());
						return new InternalServerErrorException();
					}
					return (HttpException) new OkException();
				})
				// optional isn't present
				.orElse(new ResourceNotFoundException());
	}

}
