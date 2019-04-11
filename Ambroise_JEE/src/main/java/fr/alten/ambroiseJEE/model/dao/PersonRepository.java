package fr.alten.ambroiseJEE.model.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import fr.alten.ambroiseJEE.model.beans.Person;
import fr.alten.ambroiseJEE.utils.PersonRole;

/**
 * @author Lucas Royackkers
 *
 */
public interface PersonRepository extends MongoRepository<Person,Long>{
	
	/**
	 * @param mail the person's mail
	 * @return An Optional with the corresponding person or not. 
	 * @author Royackkers Lucas
	 */
	Optional<Person> findByMail(String mail);
	
	/**
	 * @param mail the person's mail
	 * @param role the person's role (applicant/consultant)
	 * @return An Optional with the corresponding person or not. 
	 * @author Royackkers Lucas
	 */
	Optional<Person> findByMailAndRole(String mail,PersonRole role);
	
	/**
	 * @param role the person's role
	 * @return An Optional with the corresponding persons or not. 
	 * @author Royackkers Lucas
	 */
	List<Person> findAllByRole(PersonRole role);

}
