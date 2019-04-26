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

		final Agency newAgency = new Agency();
		newAgency.setName(jAgency.get("name").textValue());

		final String placeType = jAgency.get("placeType").textValue();
		final Optional<Geographic> place = this.geographicBusinessController.getPlace(jAgency.get("place").textValue(),
				placeType);
		if (place.isPresent()) {
			newAgency.setPlace(place.get().getIdentifier());
			newAgency.setPlaceType(placeType);
		}

		try {
			this.agencyRepository.save(newAgency);
		} catch (final Exception e) {
			return new ConflictException();
		}
		return new CreatedException();
	}

	/**
	 *
	 * @param name the agency name to fetch
	 * @return {@link HttpException} corresponding to the status of the request
	 *         ({@link ResourceNotFoundException} if the resource is not found and
	 *         {@link OkException} if the agency is deactivated
	 * @author MAQUINGHEN MAXIME
	 */
	public HttpException deleteAgency(final String name) {
		final Optional<Agency> agencyOptionnal = this.agencyRepository.findByName(name);

		if (agencyOptionnal.isPresent()) {
			final Agency agency = agencyOptionnal.get();
			agency.setName("deactivated" + System.currentTimeMillis());
			agency.setPlace(null);
			this.agencyRepository.save(agency);
		} else {
			throw new ResourceNotFoundException();
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

	public Optional<Agency> getAgency(final String name) {
		return this.agencyRepository.findByName(name);
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
		final Optional<Agency> agencyOptionnal = this.agencyRepository.findByName(jAgency.get("oldName").textValue());

		if (agencyOptionnal.isPresent()) {
			final Agency agency = agencyOptionnal.get();
			agency.setName(jAgency.get("name").textValue());
			final String placeType = jAgency.get("placeType").textValue();
			final Optional<Geographic> place = this.geographicBusinessController
					.getPlace(jAgency.get("place").textValue(), placeType);
			if (place.isPresent()) {
				agency.setPlace(place.get().getIdentifier());
				agency.setPlaceType(placeType);
			}
			this.agencyRepository.save(agency);
		} else {
			throw new ResourceNotFoundException();
		}
		return new OkException();
	}

}
