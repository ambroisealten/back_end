package fr.alten.ambroiseJEE.model.dao;

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
	 * @author Royackkers Lucas
	 */
	Optional<Diploma> findByName(String name);

}
