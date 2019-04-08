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
	
	public Optional<Diploma> getDiplomaByName(String name){
		return diplomaRepository.findByName(name);
	}
	
	public List<Diploma> getDiplomas(){
		return diplomaRepository.findAll();
	}
	
	/**
	 * Method to create a diploma
	 * 
	 * @param jCity JsonNode with all diploma parameters
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database and {@link CreatedException} if the diploma is created
	 * @author Lucas Royackkers
	 * @throws ParseException 
	 */
	public HttpException createDiploma(JsonNode jDiploma) throws ParseException {

		Diploma newDiploma = new Diploma();
		newDiploma.setName(jDiploma.get("name").textValue());
		newDiploma.setYearOfResult(null);

		try {
			diplomaRepository.save(newDiploma);
		} catch (Exception e) {
			return new ConflictException();
		}
		return new CreatedException();
	}

	/**
	 * 
	 * @param jDiploma JsonNode with all diploma parameters, the old name and the school who gave the diploma to perform the update even if the name is changed
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link RessourceNotFoundException} if the diploma is not found
	 *         and {@link CreatedException} if the diploma is updated
	 * @author Lucas Royackkers
	 * @throws ParseException 
	 */
	public HttpException updateDiploma(JsonNode jDiploma) throws ParseException {
		Optional<Diploma> diplomaOptionnal = diplomaRepository.findByName(jDiploma.get("oldName").textValue());
		
		if (diplomaOptionnal.isPresent()) {
			Diploma diploma = diplomaOptionnal.get();
			diploma.setName(jDiploma.get("name").textValue());
			diploma.setYearOfResult(null);
			diplomaRepository.save(diploma);
		}
		else {
			throw new RessourceNotFoundException();
		}		
		return new OkException();
	}

	/**
	 * 
	 * @param name the diploma name to fetch 
	 * @return {@link HttpException} corresponding to the status of the
	 *         request ({@link RessourceNotFoundException} if the resource is not found
	 *         and {@link OkException} if the diploma is desactivated
	 * @author Lucas Royackkers
	 */
	public HttpException deleteDiploma(String name) {
		Optional<Diploma> diplomaOptionnal = diplomaRepository.findByName(name);
		
		if (diplomaOptionnal.isPresent()) {
			Diploma diploma = diplomaOptionnal.get();
			diploma.setName("desactivated" + System.currentTimeMillis());
			diploma.setYearOfResult(null);
			diplomaRepository.save(diploma);
		}
		else {
			throw new RessourceNotFoundException();
		}		
		return new OkException();
	}

}
