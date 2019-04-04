package fr.alten.ambroiseJEE.model.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import fr.alten.ambroiseJEE.model.PersonRole;
import fr.alten.ambroiseJEE.model.beans.Person;

/**
 * @author Lucas Royackkers
 *
 */
public interface PersonRepository extends MongoRepository<Person,Long>{
	
	/**
	 * @param name the person's name
	 * @return An Optional with the corresponding person or not. 
	 * @author Royackkers Lucas
	 */
	Optional<Person> findByName(String name);
	
	/**
	 * @param name the person's name
	 * @param role the person's role (applicant/consultant)
	 * @return An Optional with the corresponding person or not. 
	 * @author Royackkers Lucas
	 */
	Optional<Person> findByNameAndRole(String name,PersonRole role);
	
	/**
	 * @param role the person's role
	 * @return An Optional with the corresponding persons or not. 
	 * @author Royackkers Lucas
	 */
	List<Person> findAllByRole(PersonRole role);

}
