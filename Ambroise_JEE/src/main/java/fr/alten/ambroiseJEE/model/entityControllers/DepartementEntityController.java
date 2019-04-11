/**
 * 
 */
package fr.alten.ambroiseJEE.model.entityControllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.beans.Departement;
import fr.alten.ambroiseJEE.model.dao.DepartementRepository;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.RessourceNotFoundException;

/**
 * @author Andy Chabalier
 *
 */

@Service
public class DepartementEntityController {
	
	@Autowired
	private DepartementRepository departementRepository;

	public Optional<Departement> getDepartement(String name) {
		return departementRepository.findByName(name);
	}

	/**
	 * Method to create a departement.
	 * 
	 * @param jDepartement JsonNode with all departement parameters
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database and {@link CreatedException} if the departement is created
	 * @author Andy Chabalier
	 */
	public HttpException createDepartement(JsonNode jDepartement) {

		Departement newDepartement = new Departement();
		newDepartement.setName(jDepartement.get("name").textValue());

		try {
			departementRepository.save(newDepartement);
		} catch (Exception e) {
			return new ConflictException();
		}
		return new CreatedException();
	}

	/**
	 * @return the list of all departements
	 * @author Andy Chabalier
	 */
	public List<Departement> getDepartements() {
		return departementRepository.findAll();
	}

	/**
	 * 
	 * @param jDepartement JsonNode with all departement parameters and the old name to perform the update even if the name is changed
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link RessourceNotFoundException} if the resource is not found
	 *         and {@link CreatedException} if the departement is updated
	 * @author Andy Chabalier
	 */
	public HttpException updateDepartement(JsonNode jDepartement) {
		Optional<Departement> departementOtionnal = departementRepository.findByName(jDepartement.get("oldName").textValue());
		
		if (departementOtionnal.isPresent()) {
			Departement departement = departementOtionnal.get();
			departement.setName(jDepartement.get("name").textValue());
			
			departementRepository.save(departement);
		}
		else {
			throw new RessourceNotFoundException();
		}		
		return new OkException();
	}

	/**
	 * 
	 * @param name the departement name to fetch 
	 * @return {@link HttpException} corresponding to the status of the
	 *         request ({@link RessourceNotFoundException} if the resource is not found
	 *         and {@link OkException} if the departement is deactivated
	 * @author Andy Chabalier
	 */
	public HttpException deleteDepartement(String name) {
		Optional<Departement> departementOptionnal = departementRepository.findByName(name);
		
		if (departementOptionnal.isPresent()) {
			Departement departement = departementOptionnal.get();
			departement.setName("deactivated" + System.currentTimeMillis());
			departementRepository.save(departement);
		}
		else {
			throw new RessourceNotFoundException();
		}		
		return new OkException();
	}

}


