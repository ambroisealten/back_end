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
public interface PersonRepository extends MongoRepository<Person, Long> {

	/**
	 * Get a List of all Person given a role
	 * 
	 * @param role the person's role
	 * @return A List of Persons with the corresponding persons or not (can be
	 *         empty).
	 * @author Royackkers Lucas
	 */
	List<Person> findAllByRole(PersonRole role);

	/**
	 * Get a List of Person given a diploma
	 * 
	 * @param highestDiploma the person's diploma
	 * @return A List of Person that match the given diploma (can be empty)
	 * @author Lucas Royackkers
	 */
	List<Person> findByHighestDiploma(String highestDiploma);

	/**
	 * Get a List of Person given a Job
	 * 
	 * @param job the person's Job
	 * @return A List of Person that match the given job (can be empty)
	 * @author Lucas Royackkers
	 */
	List<Person> findByJob(String job);

	/**
	 * Get a List of all Person given a mail
	 *
	 * @param mail the person's mail
	 * @return A List of Persons with the corresponding persons or not (can be
	 *         empty).
	 * @author Royackkers Lucas
	 */
	Optional<Person> findByMail(String mail);

	/**
	 * Try to fetch a Person by its mail and role
	 *
	 * @param mail the person's mail
	 * @param role the person's role (applicant/consultant)
	 * @return An Optional with the corresponding person or not.
	 * @author Royackkers Lucas
	 */
	Optional<Person> findByMailAndRole(String mail, PersonRole role);

	/**
	 * Get a List of Person given a name
	 *
	 * @param name the person's name
	 * @return A List of Person that match the given name (can be empty)
	 * @author Lucas Royackkers
	 */
	List<Person> findByName(String name);

	/**
	 * Get a List of Person given a surname
	 *
	 * @param surname the person's surname
	 * @return A List of Person that match the given surname (can be empty)
	 * @author Lucas Royackkers
	 */
	List<Person> findBySurname(String surname);

}
