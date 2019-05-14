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
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;

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

	/**
	 * Method to create an agency.
	 *
	 * @param jAgency JsonNode with all agency parameters (name and place parameter)
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database and {@link CreatedException} if the user is created
	 * @author Andy Chabalier
	 */
	public HttpException createAgency(final JsonNode jAgency) {
		try {
			final Agency newAgency = new Agency();
			newAgency.setName(jAgency.get("name").textValue());

			final String placeType = jAgency.get("placeType").textValue();
			final Optional<Geographic> place = this.geographicBusinessController
					.getPlace(jAgency.get("place").textValue(), placeType);
			if (place.isPresent()) {
				newAgency.setPlace(place.get().getIdentifier());
				newAgency.setPlaceType(placeType);
			}
			newAgency.setPlaceType(placeType);

			this.agencyRepository.save(newAgency);
		} catch (final ResourceNotFoundException rnfe) {
			return rnfe;
		} catch (final Exception e) {
			return new ConflictException();
		}
		return new CreatedException();
	}

	/**
	 *
	 * @param jAgency JsonNode with all agency parameters
	 * @return {@link HttpException} corresponding to the status of the request
	 *         ({@link ResourceNotFoundException} if the resource is not found and
	 *         {@link OkException} if the agency is deactivated
	 * @author MAQUINGHEN MAXIME
	 */
	public HttpException deleteAgency(final JsonNode jAgency) {
		try {
			final Agency agency = this.agencyRepository.findByName(jAgency.get("name").textValue()).orElseThrow(ResourceNotFoundException::new);
			agency.setName("deactivated" + System.currentTimeMillis());
			agency.setPlace(null);
			this.agencyRepository.save(agency);
		} catch (final ResourceNotFoundException rnfe) {
			return rnfe;
		} catch (final Exception e) {
			return new ConflictException();
		}
		return new OkException();
	}

	/**
	 * @return the list of all agencies
	 * @author Andy Chabalier
	 */
	public List<Agency> getAgencies() {
		return this.agencyRepository.findAll();
	}

	/**
	 * Fetch an agency
	 *
	 * @param name the name of the agency to fetch
	 * @return the fetched agency
	 * @throws @{@link ResourceNotFoundException} if the resource is not found
	 * @author Andy Chabalier
	 */
	public Agency getAgency(final String name) {
		return this.agencyRepository.findByName(name).orElseThrow(ResourceNotFoundException::new);
	}

	/**
	 *
	 * @param jAgency JsonNode with all user parameters (name, place) and the old
	 *                name to perform the update even if the name is changed
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ResourceNotFoundException} if the resource is not
	 *         found and {@link CreatedException} if the agency is updated
	 * @author Andy Chabalier
	 */
	public HttpException updateAgency(final JsonNode jAgency) {
		try {
			final Agency agency = this.agencyRepository.findByName(jAgency.get("oldName").textValue())
					.orElseThrow(ResourceNotFoundException::new);
			agency.setName(jAgency.get("name").textValue());
			final String placeType = jAgency.get("placeType").textValue();
			final Optional<Geographic> place = this.geographicBusinessController
					.getPlace(jAgency.get("place").textValue(), placeType);
			if (place.isPresent()) {
				agency.setPlace(place.get().getIdentifier());
				agency.setPlaceType(placeType);
			}
			this.agencyRepository.save(agency);
		} catch (final ResourceNotFoundException rnfe) {
			return rnfe;
		} catch (final Exception e) {
			return new ConflictException();
		}
		return new OkException();
	}

}
