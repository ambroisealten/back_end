/**
 *
 */
package fr.alten.ambroiseJEE.model.dao;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import fr.alten.ambroiseJEE.model.beans.Departement;

public interface DepartementRepository extends MongoRepository<Departement, Long> {

	/**
	 * @param code the departement code
	 * @return An Optional with the corresponding region or not.
	 * @author Camille Schnell
	 */
	Optional<Departement> findByCode(String code);

	/**
	 * @param nom the departement name
	 * @return An Optional with the corresponding departement or not.
	 * @author Andy Chabalier
	 */
	Optional<Departement> findByNom(String nom);

}
