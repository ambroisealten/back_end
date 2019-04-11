package fr.alten.ambroiseJEE.model.entityControllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.alten.ambroiseJEE.model.beans.SoftSkill;
import fr.alten.ambroiseJEE.model.dao.SoftSkillRepository;

/**
 * Soft skill controller for entity gestion rules
 * 
 * @author Lucas Royackkers
 *
 */
@Service
public class SoftSkillEntityController {
	@Autowired
	private SoftSkillRepository softSkillRepository;

	/**
	 * Try to fetch a soft skill by its name
	 * 
	 * @param name the soft skill's name to fetch
	 * @return An Optional with the corresponding soft skill or not.
	 * @author Lucas Royackkers
	 */
	public Optional<SoftSkill> getSoftSkillByName(String name) {
		return softSkillRepository.findSoftSkillByName(name);
	}
	
	/**
	 * Try to fetch a soft skill by its name and grade
	 * 
	 * @param name the soft skill's name to fetch
	 * @param grade the soft skill's grade to fetch
	 * @return An Optional with the corresponding soft skill or not.
	 * @author Lucas Royackkers
	 */
	public Optional<SoftSkill> getSoftSkillByNameAndGrade(String name,float grade) {
		return softSkillRepository.findSoftSkillByNameAndGrade(name,grade);
	}
	
	/**
	 * Method to create a couple between a grade and a SoftSkill (for skills sheet)
	 * 
	 * @param name the soft skill's name
	 * @param grade the soft skill's grade (int)
	 * @return a SoftSkill object if a corresponding name is found, null if not
	 * @author Lucas Royackkers
	 */
	public SoftSkill createSoftSkillAndGrade(String name,float grade) {
		Optional<SoftSkill> optionalSoftSkill = this.getSoftSkillByNameAndGrade(name,grade);
		if(!optionalSoftSkill.isPresent()){
			SoftSkill softSkill = new SoftSkill();
			softSkill.setName(name);
			//The grade has to be between 1 and 4
			if(grade >= 1 && grade <= 4) {
				softSkill.setGrade(grade);
			}
			return softSkill;
		}
		else{
			return null;
		}
	}
	

}
