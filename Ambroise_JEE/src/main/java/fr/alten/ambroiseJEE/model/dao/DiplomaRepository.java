package fr.alten.ambroiseJEE.model.dao;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import fr.alten.ambroiseJEE.model.beans.Diploma;

/**
 * @author Lucas Royackkers
 *
 */
public interface DiplomaRepository extends MongoRepository<Diploma,Long>{
	/**
	 * @param name the diploma's name
	 * @return An Optional with the corresponding diploma or not. 
	 * @author Lucas Royackkers
	 */
	List<Diploma> findByName(String name);

	/**
	 * 
	 * @param name the diploma's name
	 * @param yearOfResult the diploma's year of result
	 * @return An Optional with the corresponding diploma or not
	 * @author Lucas Royackkers
	 */
	Optional<Diploma> findByNameAndYearOfResult(String name, String yearOfResult);

}
