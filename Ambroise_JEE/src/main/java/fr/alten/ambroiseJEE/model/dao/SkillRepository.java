/**
 *
 */
package fr.alten.ambroiseJEE.model.dao;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import fr.alten.ambroiseJEE.model.beans.Skill;

/**
 * @author Thomas Decamp
 *
 */
public interface SkillRepository extends MongoRepository<Skill, Long> {

	public Optional<Skill> findBy_id(String id);

	/**
	 * Fetch skill by name
	 *
	 * @param name
	 * @return An Optional with the corresponding skill or not.
	 * @author Thomas Decamp
	 * @author Lucas Royackkers
	 */
	public Optional<Skill> findByNameIgnoreCase(String name);

}
