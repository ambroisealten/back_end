/**
 *
 */
package fr.alten.ambroiseJEE.model.entityControllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.beans.Sector;
import fr.alten.ambroiseJEE.model.dao.SectorRepository;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.RessourceNotFoundException;

/**
 * User controller for entity gestion rules
 *
 * @author Andy Chabalier
 *
 */
@Service
public class SectorEntityController {

	@Autowired
	private SectorRepository sectorRepository;

	/**
	 * Method to create an sector.
	 *
	 * @param jSector JsonNode with all sector parameters
	 * @return the @see {@link HttpException} corresponding to the statut of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database and {@link CreatedException} if the sector is created
	 * @author Andy Chabalier
	 */
	public HttpException createSector(JsonNode jSector) {

		Sector newSector = new Sector();

		newSector.setName(jSector.get("name").textValue());
		try {
			sectorRepository.save(newSector);
		} catch (Exception e) {
			return new ConflictException();
		}
		return new CreatedException();
	}

	/**
	 * @param jSector JsonNode with the sector name to delete
	 * @return {@link HttpException} corresponding to the statut of the request
	 *         ({@link RessourceNotFoundException} if the ressource is not found and
	 *         {@link OkException} if the sector is deleted *
	 * @author Andy Chabalier
	 */
	public HttpException deleteSector(JsonNode jSector) {
		Optional<Sector> sectorOptionnal = sectorRepository.findByName(jSector.get("name").textValue());

		if (sectorOptionnal.isPresent()) {
			sectorRepository.delete(sectorOptionnal.get());
			;
		} else {
			throw new RessourceNotFoundException();
		}
		return new OkException();
	}

	/**
	 * fetch the list of sectors
	 * 
	 * @return the list of all sectors
	 * @author Andy Chabalier
	 */
	public List<Sector> getSectors() {
		return sectorRepository.findAll();
	}

	/**
	 *
	 * @param jSector JsonNode with all sector parameters and the oldName to perform
	 *                the update even if the name is changed
	 * @return the @see {@link HttpException} corresponding to the statut of the
	 *         request ({@link RessourceNotFoundException} if the ressource is not
	 *         found and {@link OkException} if the sector is updated
	 * @author Andy Chabalier
	 */
	public HttpException updateSector(JsonNode jSector) {
		Optional<Sector> sectorOptionnal = sectorRepository.findByName(jSector.get("oldName").textValue());

		if (sectorOptionnal.isPresent()) {
			Sector sector = sectorOptionnal.get();
			sectorRepository.save(sector);
		} else {
			throw new RessourceNotFoundException();
		}
		return new OkException();
	}

}
