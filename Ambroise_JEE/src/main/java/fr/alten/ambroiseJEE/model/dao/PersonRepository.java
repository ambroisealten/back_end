package fr.alten.ambroiseJEE.model.dao;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

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

}
