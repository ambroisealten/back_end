package fr.alten.ambroiseJEE.model.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import fr.alten.ambroiseJEE.model.beans.TechSkill;
import fr.alten.ambroiseJEE.utils.TechSkillGrade;

/**
 * @author Lucas Royackkers
 *
 */
public interface TechSkillRepository extends MongoRepository<TechSkill,Long>{
	
	/**
	 * Fetch tech skill by name
	 * 
	 * @param name
	 * @return An Optional with the corresponding tech skill or not. 
	 * @author Lucas Royackkers
	 */
	public List<TechSkill> findByName(String name);
	
	/**
	 * Fetch tech skill by name and grade
	 * 
	 * @param name
	 * @return An Optional with the corresponding tech skill or not. 
	 * @author Lucas Royackkers
	 */
	public Optional<TechSkill> findByNameAndGrade(String name,TechSkillGrade grade);

}
