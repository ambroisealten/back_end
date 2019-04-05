package fr.alten.ambroiseJEE.model.entityControllers;

import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import fr.alten.ambroiseJEE.model.beans.City;
import fr.alten.ambroiseJEE.model.beans.Departement;
import fr.alten.ambroiseJEE.model.beans.Geographic;
import fr.alten.ambroiseJEE.model.beans.Mobility;
import fr.alten.ambroiseJEE.model.beans.PostalCode;
import fr.alten.ambroiseJEE.model.beans.Region;
import fr.alten.ambroiseJEE.model.dao.MobilityRepository;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;

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
	
	
	public Optional<Mobility> getMobility(String placeName, int radius){
		return mobilityRepository.findByPlaceNameAndRadius(placeName, radius);
	}
	
	/**
	 * Method to create a mobility.
	 * 
	 * @param jCity JsonNode with all mobility parameters
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database and {@link CreatedException} if the mobility is created
	 * @author Lucas Royackkers
	 */
	public HttpException createMobility(JsonNode jMobility){
		Mobility newMobility = new Mobility();
		String geographicType = jMobility.get("type").textValue();
		switch(geographicType) {
			case "city":
				Optional<City> city = cityEntityController.getCity(jMobility.get("place").textValue());
				if(city.isPresent()) {
					newMobility.setPlace(city.get());
				}
				break;
			case "departement":
				Optional<Departement> departement = departementEntityController.getDepartement(jMobility.get("place").textValue());
				if(departement.isPresent()) {
					newMobility.setPlace(departement.get());
				}
				break;
			case "postalCode":
				Optional<PostalCode> postalCode = postalCodeEntityController.getPostalCode(jMobility.get("place").textValue());
				if(postalCode.isPresent()) {
					newMobility.setPlace(postalCode.get());
				}
				break;
			case "region":
				Optional<Region> region = regionEntityController.getRegion(jMobility.get("place").textValue());
				if(region.isPresent()) {
					newMobility.setPlace(region.get());
				}
				break;
			default:
				break;
		}
		newMobility.setRadius(Integer.parseInt(jMobility.get("radius").textValue()));
		newMobility.setUnit(jMobility.get("unit").textValue());
		
		try {
			mobilityRepository.save(newMobility);
		}
		catch (Exception e){
			return new ConflictException();
		}
		return new CreatedException();
	}

}
