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
public interface SkillRepository {

	/**
	 * @param name the skill name
	 * @return An Optional with the corresponding skill or not. 
	 * @author Thomas Decamp
	 */
	Optional<Skill> findByName(String name);
}
