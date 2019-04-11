package fr.alten.ambroiseJEE.model.dao;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import fr.alten.ambroiseJEE.model.beans.Mobility;

/**
 * @author Lucas Royackkers
 *
 */
public interface MobilityRepository extends MongoRepository<Mobility,Long>{
	/**
	 * @param name the mobility's name (its location)
	 * @param radius the amount of kms (or mins) the person can move (can be null)
	 * @return An Optional with the corresponding mobility or not. 
	 * @author Lucas Royackkers
	 */
	Optional<Mobility> findByPlaceNameAndRadius(String placeName,int radius);

	Optional<Mobility> findByPlaceName(String placeName);
	
	Optional<Mobility> findByPlaceNameAndPlaceTypeAndRadiusAndUnit(String placeName, String placeType, int radius, String unit);
}
