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

	/**
	 * Fetch skill by name
	 *
	 * @param name
	 * @return An Optional with the corresponding skill or not.
	 * @author Thomas Decamp
	 */
	public Optional<Skill> findByName(String name);
	
	/**
	 * Fetch skill by name and grade
	 *
	 * @param name
	 * @param grade
	 * @return An Optional with the corresponding skill or not.
	 * @author Thomas Decamp
	 */
	public Optional<Skill> findByNameAndGrade(String name, float grade);
}
