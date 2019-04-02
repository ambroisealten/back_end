/**
 * 
 */
package fr.alten.ambroiseJEE.controller.business;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.alten.ambroiseJEE.model.beans.Geographic;
import fr.alten.ambroiseJEE.utils.httpStatus.RessourceNotFoundException;

/**
 * @author Andy Chabalier
 *
 */
@Service
public class GeographicBusinessController {

	@Autowired
	private CityBusinessController cityBusinessController;

	@Autowired
	private RegionBusinessController regionBusinessController;

	@Autowired
	private DepartementBusinessController departementBusinessController;

	@Autowired
	private PostalCodeBusinessController postalCodeBusinessController;

	/**
	 * Get the wanted Geographic object. Depending of the placeType, we get the corresponding BusinessController
	 * if the object don't exist in base, we throw the RessourceNotFoundException
	 * @param placeName the place's name
	 * @param placeType the place's type
	 * @return the geographic corresponding to placeName and placeType
	 * @throws RessourceNotFoundException()
	 * @author Andy Chabalier
	 */
	public Optional<Geographic> getPlace(String placeName, String placeType) {
		Geographic place;
		try {
			switch (placeType.toLowerCase()) {
			case "city":
				place = cityBusinessController.getCity(placeName).get();
				break;
			case "region" :
				place = regionBusinessController.getRegion(placeName).get();
				break;
			case "departement" :
				place = departementBusinessController.getDepartement(placeName).get();
				break;
			case "postalCode":
				place = postalCodeBusinessController.getPostalCode(placeName).get();
				break;
			default:
				throw new RessourceNotFoundException();
			}
		} catch (NoSuchElementException nsee) {
			throw new RessourceNotFoundException();
		}
		return Optional.ofNullable(place);
	}

}
