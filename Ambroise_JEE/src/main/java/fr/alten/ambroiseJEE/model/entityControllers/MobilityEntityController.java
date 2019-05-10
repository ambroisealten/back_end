package fr.alten.ambroiseJEE.model.entityControllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.beans.City;
import fr.alten.ambroiseJEE.model.beans.Departement;
import fr.alten.ambroiseJEE.model.beans.Mobility;
import fr.alten.ambroiseJEE.model.beans.PostalCode;
import fr.alten.ambroiseJEE.model.beans.Region;
import fr.alten.ambroiseJEE.model.dao.MobilityRepository;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;

/**
 * @author Lucas Royackkers
 *
 */
@Service
public class MobilityEntityController {
	@Autowired
	private MobilityRepository mobilityRepository;

	@Autowired
	private CityEntityController cityEntityController;

	@Autowired
	private DepartementEntityController departementEntityController;

	@Autowired
	private PostalCodeEntityController postalCodeEntityController;

	@Autowired
	private RegionEntityController regionEntityController;

	/**
	 * Method to create a mobility.
	 *
	 * @param jCity JsonNode with all mobility parameters
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database and {@link CreatedException} if the mobility is created
	 * @author Lucas Royackkers
	 */
	public HttpException createMobility(final JsonNode jMobility) {
		final Mobility newMobility = new Mobility();
		final String geographicType = jMobility.get("type").textValue();
		switch (geographicType) {
		case "city":
			try {
				final City city = this.cityEntityController.getCity(jMobility.get("place").textValue());
				newMobility.setPlaceName(city.getNom());
			} catch (final ResourceNotFoundException rnfe) {
			}
			break;
		case "departement":
			final Departement departement = this.departementEntityController
					.getDepartement(jMobility.get("place").textValue());
			try {
				newMobility.setPlaceName(departement.getNom());
			} catch (final ResourceNotFoundException rnfe) {
			}
			break;
		case "postalCode":
			final PostalCode postalCode = this.postalCodeEntityController
					.getPostalCode(jMobility.get("place").textValue());
			try {
				newMobility.setPlaceName(postalCode.getName());
			} catch (final ResourceNotFoundException rnfe) {
			}
			break;
		case "region":
			final Region region = this.regionEntityController.getRegion(jMobility.get("place").textValue());
			try {
				newMobility.setPlaceName(region.getNom());
			} catch (final ResourceNotFoundException rnfe) {
			}
			break;
		default:
			break;
		}
		newMobility.setRadius(Integer.parseInt(jMobility.get("radius").textValue()));
		newMobility.setUnit(jMobility.get("unit").textValue());

		try {
			this.mobilityRepository.save(newMobility);
		} catch (final Exception e) {
			return new ConflictException();
		}
		return new CreatedException();
	}

	public Optional<Mobility> getMobility(final Mobility mobilityToFind) {
		// TODO Revoir le code pour optimiser les appels
		return this.mobilityRepository.findByPlaceNameAndPlaceTypeAndRadiusAndUnit(mobilityToFind.getPlaceName(),
				mobilityToFind.getPlaceType(), mobilityToFind.getRadius(), mobilityToFind.getUnit());
	}

	public Mobility getMobilityByName(final String placeName) {
		return this.mobilityRepository.findByPlaceName(placeName).orElseThrow(ResourceNotFoundException::new);
	}

	public Mobility getMobilityByNameAndRadius(final String placeName, final int radius) {
		return this.mobilityRepository.findByPlaceNameAndRadius(placeName, radius)
				.orElseThrow(ResourceNotFoundException::new);
	}

}
