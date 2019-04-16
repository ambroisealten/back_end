/**
 * 
 */
package fr.alten.ambroiseJEE.model.entityControllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.beans.Region;
import fr.alten.ambroiseJEE.model.dao.RegionRepository;
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
public class RegionEntityController {
	
	@Autowired
	private RegionRepository regionRepository;

	public Optional<Region> getRegion(String name) {
		return regionRepository.findByName(name);
	}

	/**
	 * Method to create a region.
	 * 
	 * @param jRegion JsonNode with all city parameters
	 * @return the @see {@link HttpException} corresponding to the statut of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database and {@link CreatedException} if the region is created
	 * @author Andy Chabalier
	 */
	public HttpException createRegion(JsonNode jRegion) {

		Region newRegion = new Region();
		newRegion.setName(jRegion.get("nom").textValue());
		newRegion.setCode(jRegion.get("code").textValue());

		try {
			regionRepository.save(newRegion);
		} catch (Exception e) {
			return new ConflictException();
		}
		return new CreatedException();
	}

	/**
	 * @return the list of all regions
	 * @author Andy Chabalier
	 */
	public List<Region> getRegions() {
		return regionRepository.findAll();
	}

	/**
	 * 
	 * @param jRegion JsonNode with all region parameters and the old name to perform the update even if the name is changed
	 * @return the @see {@link HttpException} corresponding to the statut of the
	 *         request ({@link RessourceNotFoundException} if the ressource is not found
	 *         and {@link CreatedException} if the region is updated
	 * @author Andy Chabalier
	 */
	public HttpException updateRegion(JsonNode jRegion) {
		Optional<Region> regionOptionnal = regionRepository.findByName(jRegion.get("oldName").textValue());
		
		if (regionOptionnal.isPresent()) {
			Region region = regionOptionnal.get();
			region.setName(jRegion.get("name").textValue());
			
			regionRepository.save(region);
		}
		else {
			throw new RessourceNotFoundException();
		}		
		return new OkException();
	}

	/**
	 * 
	 * @param name the region name to fetch 
	 * @return {@link HttpException} corresponding to the statut of the
	 *         request ({@link RessourceNotFoundException} if the ressource is not found
	 *         and {@link OkException} if the region is desactivated
	 * @author Andy Chabalier
	 */
	public HttpException deleteRegion(String name) {
		Optional<Region> regionOptionnal = regionRepository.findByName(name);
		
		if (regionOptionnal.isPresent()) {
			Region region = regionOptionnal.get();
			region.setName("desactivated" + System.currentTimeMillis());
			regionRepository.save(region);
		}
		else {
			throw new RessourceNotFoundException();
		}		
		return new OkException();
	}

}

