/**
 *
 */
package fr.alten.ambroiseJEE.model.dao;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import fr.alten.ambroiseJEE.model.beans.SkillForum;

/**
 * @author Thomas Decamp
 *
 */
public interface SkillForumRepository extends MongoRepository<SkillForum, Long> {

	/**
	 * fetch a Skill forum by is id
	 *
	 * @param _id the id to fetch
	 * @return An Optional with the corresponding skill or empty.
	 * @author Andy Chabalier
	 */
	Optional<SkillForum> findBy_id(String _id);

	/**
	 * @param name the skill name
	 * @return An Optional with the corresponding skill or not.
	 * @author Thomas Decamp
	 */
	Optional<SkillForum> findByName(String name);
}
