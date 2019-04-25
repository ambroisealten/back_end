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
	public HttpException createMobility(JsonNode jMobility) {
		Mobility newMobility = new Mobility();
		String geographicType = jMobility.get("type").textValue();
		switch (geographicType) {
		case "city":
			try{
				City city = cityEntityController.getCity(jMobility.get("place").textValue());
				newMobility.setPlaceName(city.getName());
			}catch(ResourceNotFoundException rnfe) {}
			break;
		case "departement":
			Departement departement = departementEntityController
					.getDepartement(jMobility.get("place").textValue());
			try {
				newMobility.setPlaceName(departement.getName());
			}catch(ResourceNotFoundException rnfe) {}
			break;
		case "postalCode":
			PostalCode postalCode = postalCodeEntityController
					.getPostalCode(jMobility.get("place").textValue());
			try {
				newMobility.setPlaceName(postalCode.getName());
			}catch(ResourceNotFoundException rnfe) {}
			break;
		case "region":
			Region region = regionEntityController.getRegion(jMobility.get("place").textValue());
			try {
				newMobility.setPlaceName(region.getName());
			}catch(ResourceNotFoundException rnfe) {}
			break;
		default:
			break;
		}
		newMobility.setRadius(Integer.parseInt(jMobility.get("radius").textValue()));
		newMobility.setUnit(jMobility.get("unit").textValue());

		try {
			mobilityRepository.save(newMobility);
		} catch (Exception e) {
			return new ConflictException();
		}
		return new CreatedException();
	}

	public Optional<Mobility> getMobility(Mobility mobilityToFind) {
		// TODO Revoir le code pour optimiser les appels
		return mobilityRepository.findByPlaceNameAndPlaceTypeAndRadiusAndUnit(mobilityToFind.getPlaceName(),
				mobilityToFind.getPlaceType(), mobilityToFind.getRadius(), mobilityToFind.getUnit());
	}

	public Optional<Mobility> getMobilityByName(String placeName) {
		return mobilityRepository.findByPlaceName(placeName);
	}

	public Optional<Mobility> getMobilityByNameAndRadius(String placeName, int radius) {
		return mobilityRepository.findByPlaceNameAndRadius(placeName, radius);
	}

}
