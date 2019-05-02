/**
 *
 */
package fr.alten.ambroiseJEE.model.entityControllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.beans.Region;
import fr.alten.ambroiseJEE.model.dao.RegionRepository;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;

/**
 * @author Andy Chabalier
 *
 */

@Service
public class RegionEntityController {

	@Autowired
	private RegionRepository regionRepository;

	/**
	 * Method to create a region.
	 *
	 * @param jRegion JsonNode with all city parameters
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database and {@link CreatedException} if the region is created
	 * @author Andy Chabalier
	 */
	public HttpException createRegion(final JsonNode jRegion) {

		final Region newRegion = new Region();
		newRegion.setName(jRegion.get("nom").textValue());
		newRegion.setCode(jRegion.get("code").textValue());

		try {
			this.regionRepository.save(newRegion);
		} catch (final Exception e) {
			return new ConflictException();
		}
		return new CreatedException();
	}

	/**
	 *
	 * @param name the region name to fetch
	 * @return {@link HttpException} corresponding to the status of the request
	 *         ({@link ResourceNotFoundException} if the resource is not found and
	 *         {@link OkException} if the region is deactivated
	 * @author Andy Chabalier
	 */
	public HttpException deleteRegion(final String name) {
		try {
			final Region region = this.regionRepository.findByName(name).orElseThrow(ResourceNotFoundException::new);
			region.setName("deactivated" + System.currentTimeMillis());
			this.regionRepository.save(region);
		} catch (final ResourceNotFoundException rnfe) {
			return rnfe;
		} catch (final Exception e) {
			return new ConflictException();
		}
		return new OkException();
	}

	/**
	 * Get a region
	 *
	 * @param name the region name to fetch
	 * @return the asked region
	 * @author Andy Chabalier
	 * @throws {@link ResourceNotFoundException} if the resource is not found
	 */
	public Region getRegion(final String name) {
		return this.regionRepository.findByName(name).orElseThrow(ResourceNotFoundException::new);
	}

	/**
	 * @return the list of all regions
	 * @author Andy Chabalier
	 */
	public List<Region> getRegions() {
		return this.regionRepository.findAll();
	}

	/**
	 *
	 * @param jRegion JsonNode with all region parameters and the old name to
	 *                perform the update even if the name is changed
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ResourceNotFoundException} if the resource is not
	 *         found and {@link OkException} if the region is updated
	 * @author Andy Chabalier
	 */
	public HttpException updateRegion(final JsonNode jRegion) {
		try {
			final Region region = this.regionRepository.findByName(jRegion.get("oldName").textValue())
					.orElseThrow(ResourceNotFoundException::new);
			region.setName(jRegion.get("name").textValue());
			this.regionRepository.save(region);
		} catch (final ResourceNotFoundException rnfe) {
			throw rnfe;
		} catch (final Exception e) {
			return new ConflictException();
		}
		return new OkException();
	}

}
