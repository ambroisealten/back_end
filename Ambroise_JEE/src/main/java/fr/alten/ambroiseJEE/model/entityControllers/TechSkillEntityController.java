package fr.alten.ambroiseJEE.model.entityControllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import fr.alten.ambroiseJEE.model.beans.TechSkill;
import fr.alten.ambroiseJEE.model.dao.TechSkillRepository;

/**
 * Tech skill controller for entity gestion rules
 * 
 * @author Lucas Royackkers
 *
 */
@Service
public class TechSkillEntityController {
	@Autowired
	private TechSkillRepository techSkillRepository;
	
	/**
	 * Try to fetch a tech skill by its name
	 * 
	 * @param name the tech skill's name to fetch
	 * @return An Optional with the corresponding tech skill or not.
	 * @author Lucas Royackkers
	 */
	public Optional<TechSkill> getTechSkill(String name) {
		return techSkillRepository.findTechSkillByName(name);
	}
	
	/**
	 * Method to create a couple between a grade and a TechSkill (for skills sheet)
	 * 
	 * @param name the tech skill's name
	 * @param grade the tech skill's grade (int)
	 * @return a TechSkill object if a corresponding name is found, null if not
	 * @author Lucas Royackkers
	 */
	public TechSkill createTechSkillAndGrade(String name,int grade) {
		Optional<TechSkill> optionalTechSkill = this.getTechSkill(name);
		if(optionalTechSkill.isPresent()){
			TechSkill techSkill = optionalTechSkill.get();
			//The grade has to be between 1 and 4
			if(grade >= 1 && grade <= 4) {
				techSkill.setGrade(grade);
			}
			return techSkill;
		}
		else{
			return null;
		}
	}

}
