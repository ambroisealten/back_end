/**
 * 
 */
package fr.alten.ambroiseJEE.model.entityControllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.controller.business.geographic.GeographicBusinessController;
import fr.alten.ambroiseJEE.model.beans.Agency;
import fr.alten.ambroiseJEE.model.beans.Geographic;
import fr.alten.ambroiseJEE.model.dao.AgencyRepository;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.RessourceNotFoundException;

/**
 * @author MAQUINGHEN MAXIME
 *
 */
@Service
public class AgencyEntityController {
	
	@Autowired
	private AgencyRepository agencyRepository;

	@Autowired
	private GeographicBusinessController geographicBusinessController;
	
	
	public Optional<Agency> getAgency(String name) {
		return agencyRepository.findByName(name);
	}

	/**
	 * Method to create an agency.
	 * 
	 * @param jAgency JsonNode with all agency parameters (name and place parameter)
	 * @return the @see {@link HttpException} corresponding to the statut of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database and {@link CreatedException} if the user is created
	 * @author Andy Chabalier
	 */
	public HttpException createAgency(JsonNode jAgency) {

		Agency newAgency = new Agency();
		newAgency.setName(jAgency.get("name").textValue());
		
		String placeType = jAgency.get("placeType").textValue();
		Optional<Geographic> place = geographicBusinessController.getPlace(jAgency.get("place").textValue(),placeType);
		if(place.isPresent()) {
			newAgency.setPlace(place.get().getIdentifier());
			newAgency.setPlaceType(placeType);
		}

		try {
			agencyRepository.save(newAgency);
		} catch (Exception e) {
			return new ConflictException();
		}
		return new CreatedException();
	}

	/**
	 * @return the list of all agencies
	 * @author Andy Chabalier
	 */
	public List<Agency> getAgencies() {
		return agencyRepository.findAll();
	}

	/**
	 * 
	 * @param jAgency JsonNode with all user parameters (name, place) and the old name to perform the update even if the name is changed
	 * @return the @see {@link HttpException} corresponding to the statut of the
	 *         request ({@link RessourceNotFoundException} if the ressource is not found
	 *         and {@link CreatedException} if the agency is updated
	 * @author Andy Chabalier
	 */
	public HttpException updateAgency(JsonNode jAgency) {
		Optional<Agency> agencyOptionnal = agencyRepository.findByName(jAgency.get("oldName").textValue());
		
		if (agencyOptionnal.isPresent()) {
			Agency agency = agencyOptionnal.get();
			agency.setName(jAgency.get("name").textValue());
			String placeType = jAgency.get("placeType").textValue();
			Optional<Geographic> place = geographicBusinessController.getPlace(jAgency.get("place").textValue(),placeType);
			if(place.isPresent()) {
				agency.setPlace(place.get().getIdentifier());
				agency.setPlaceType(placeType);
			}
			agencyRepository.save(agency);
		}
		else {
			throw new RessourceNotFoundException();
		}		
		return new OkException();
	}

	/**
	 * 
	 * @param name the agency name to fetch 
	 * @return {@link HttpException} corresponding to the statut of the
	 *         request ({@link RessourceNotFoundException} if the ressource is not found
	 *         and {@link OkException} if the agency is desactivated
	 * @author MAQUINGHEN MAXIME
	 */
	public HttpException deleteAgency(String name) {
		Optional<Agency> agencyOptionnal = agencyRepository.findByName(name);
		
		if (agencyOptionnal.isPresent()) {
			Agency agency = agencyOptionnal.get();
			agency.setName("desactivated" + System.currentTimeMillis());
			agency.setPlace(null);
			agencyRepository.save(agency);
		}
		else {
			throw new RessourceNotFoundException();
		}		
		return new OkException();
	}

}
