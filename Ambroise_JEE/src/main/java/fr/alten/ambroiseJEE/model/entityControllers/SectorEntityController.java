/**
 *
 */
package fr.alten.ambroiseJEE.model.entityControllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.beans.Sector;
import fr.alten.ambroiseJEE.model.dao.SectorRepository;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;

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
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database and {@link CreatedException} if the sector is created
	 * @author Andy Chabalier
	 */
	public HttpException createSector(final JsonNode jSector) {

		final Sector newSector = new Sector();

		newSector.setName(jSector.get("name").textValue());
		try {
			this.sectorRepository.save(newSector);
		} catch (final Exception e) {
			return new ConflictException();
		}
		return new CreatedException();
	}

	/**
	 * @param jSector JsonNode with the sector name to delete
	 * @return {@link HttpException} corresponding to the status of the request
	 *         ({@link ResourceNotFoundException} if the resource is not found and
	 *         {@link OkException} if the sector is deleted *
	 * @author Andy Chabalier
	 */
	public HttpException deleteSector(final JsonNode jSector) {
		try {
			final Sector sector = this.sectorRepository.findByName(jSector.get("name").textValue())
					.orElseThrow(ResourceNotFoundException::new);
			this.sectorRepository.delete(sector);
		} catch (final ResourceNotFoundException rnfe) {
			return rnfe;
		}
		return new OkException();
	}

	/**
	 * fetch a sector
	 *
	 * @param name name of the sector to fetch
	 * @author Andy Chabalier
	 * @return the sector
	 * @throws {@link ResourceNotFoundException} if the ressource is not found
	 */
	public Sector getSector(final String name) {
		return this.sectorRepository.findByName(name).orElseThrow(ResourceNotFoundException::new);
	}

	/**
	 * fetch the list of sectors
	 *
	 * @return the list of all sectors
	 * @author Andy Chabalier
	 */
	public List<Sector> getSectors() {
		return this.sectorRepository.findAll();
	}

	/**
	 *
	 * @param jSector JsonNode with all sector parameters and the oldName to perform
	 *                the update even if the name is changed
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ResourceNotFoundException} if the resource is not
	 *         found and {@link OkException} if the sector is updated
	 * @author Andy Chabalier
	 */
	public HttpException updateSector(final JsonNode jSector) {
		try {
			final Sector sector = this.sectorRepository.findByName(jSector.get("oldName").textValue())
					.orElseThrow(ResourceNotFoundException::new);
			sector.setName(jSector.get("name").textValue());
			this.sectorRepository.save(sector);
		} catch (final ResourceNotFoundException rnfe) {
			return rnfe;
		} catch (final Exception e) {
			return new ConflictException();
		}
		return new OkException();
	}

}
