package fr.alten.ambroiseJEE.model.dao;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import fr.alten.ambroiseJEE.model.beans.Employer;

/**
 * @author Lucas Royackkers
 *
 */
public interface EmployerRepository extends MongoRepository<Employer,Long>{
	/**
	 * @param name the employer's name
	 * @return An Optional with the corresponding employer or not. 
	 * @author Royackkers Lucas
	 */
	Optional<Employer> findByName(String name);
	
}
