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
import fr.alten.ambroiseJEE.utils.httpStatus.RessourceNotFoundException;

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
	 * @param jDiploma JsonNode with all diploma parameters
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database and {@link CreatedException} if the diploma is created
	 * @author Lucas Royackkers
	 * @throws ParseException
	 */
	public HttpException createDiploma(JsonNode jDiploma) throws ParseException {
		Optional<Diploma> diplomaOptional = diplomaRepository.findByNameAndYearOfResult(jDiploma.get("name").textValue(), jDiploma.get("yearOfResult").textValue());
		if(diplomaOptional.isPresent()) {
			return new ConflictException();
		}
		
		Diploma newDiploma = new Diploma();
		newDiploma.setName(jDiploma.get("name").textValue());
		newDiploma.setYearOfResult(jDiploma.get("yearOfResult").textValue());

		try {
			diplomaRepository.save(newDiploma);
		} catch (Exception e) {
			return new ConflictException();
		}
		return new CreatedException();
	}

	/**
	 *
	 * @param jDiploma the JsonNode containing all diploma parameters
	 * @return {@link HttpException} corresponding to the status of the request
	 *         ({@link RessourceNotFoundException} if the resource is not found and
	 *         {@link OkException} if the diploma is deactivated
	 * @author Lucas Royackkers
	 */
	public HttpException deleteDiploma(JsonNode jDiploma) {
		Optional<Diploma> diplomaOptionnal = diplomaRepository
				.findByNameAndYearOfResult(jDiploma.get("name").textValue(), jDiploma.get("yearOfResult").textValue());

		if (diplomaOptionnal.isPresent()) {
			Diploma diploma = diplomaOptionnal.get();
			diploma.setName("deactivated" + System.currentTimeMillis());
			diploma.setYearOfResult(null);
			diplomaRepository.save(diploma);
		} else {
			throw new RessourceNotFoundException();
		}
		return new OkException();
	}

	public List<Diploma> getDiplomaByName(String name) {
		return diplomaRepository.findByName(name);
	}

	public Optional<Diploma> getDiplomaByNameAndYearOfResult(String name, String yearOfResult) {
		return diplomaRepository.findByNameAndYearOfResult(name, yearOfResult);
	}

	public List<Diploma> getDiplomas() {
		return diplomaRepository.findAll();
	}

	/**
	 *
	 * @param jDiploma JsonNode with all diploma parameters, the old name and the
	 *                 school who gave the diploma to perform the update even if the
	 *                 name is changed
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link RessourceNotFoundException} if the diploma is not
	 *         found and {@link CreatedException} if the diploma is updated
	 * @author Lucas Royackkers
	 * @throws ParseException
	 */
	public HttpException updateDiploma(JsonNode jDiploma) throws ParseException {
		Optional<Diploma> diplomaOptionnal = diplomaRepository.findByNameAndYearOfResult(
				jDiploma.get("oldName").textValue(), jDiploma.get("oldYearOfResult").textValue());

		if (diplomaOptionnal.isPresent()) {
			Diploma diploma = diplomaOptionnal.get();
			diploma.setName(jDiploma.get("name").textValue());
			diploma.setYearOfResult(jDiploma.get("yearOfResult").textValue());
			diplomaRepository.save(diploma);
		} else {
			throw new RessourceNotFoundException();
		}
		return new OkException();
	}

}
