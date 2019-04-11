package fr.alten.ambroiseJEE.model.dao;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import fr.alten.ambroiseJEE.model.beans.SoftSkill;

/**
 * @author Lucas Royackkers
 *
 */
public interface SoftSkillRepository extends MongoRepository<SoftSkill,Long>{
	
	/**
	 * Fetch soft skill by name
	 * 
	 * @param name
	 * @return An Optional with the corresponding soft skill or not. 
	 * @author Lucas Royackkers
	 */
	public Optional<SoftSkill> findSoftSkillByName(String name);
	
	/**
	 * Fetch soft skill by name and grade
	 * 
	 * @param name
	 * @param grade
	 * @return An Optional with the corresponding soft skill or not. 
	 * @author Lucas Royackkers
	 */
	public Optional<SoftSkill> findSoftSkillByNameAndGrade(String name,float grade);

}
