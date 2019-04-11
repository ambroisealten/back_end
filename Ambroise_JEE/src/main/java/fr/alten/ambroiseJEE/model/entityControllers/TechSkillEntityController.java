package fr.alten.ambroiseJEE.model.entityControllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

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
	public Optional<List<TechSkill>> getTechSkillsByName(String name) {
		return techSkillRepository.findTechSkillsByName(name);
	}
	
	/**
	 * Try to fetch a tech skill by its name and grade
	 * 
	 * @param name the tech skill's name to fetch
	 * @param grade the tech skill's grade to fetch
	 * @return An Optional with the corresponding tech skill or not.
	 * @author Lucas Royackkers
	 */
	public Optional<TechSkill> getTechSkillByNameAndGrade(String name,float grade) {
		return techSkillRepository.findTechSkillByNameAndGrade(name,grade);
	}
	
	/**
	 * Method to create a couple between a grade and a TechSkill (for skills sheet)
	 * 
	 * @param jTechSkill the JsonNode containing all tech skill parameters 
	 * @return a TechSkill object if a corresponding name is found, null if not
	 * @author Lucas Royackkers
	 */
	public TechSkill createTechSkillAndGrade(JsonNode jTechSkill) {
		Optional<TechSkill> optionalTechSkill = this.getTechSkillByNameAndGrade(jTechSkill.get("name").textValue(),Float.parseFloat(jTechSkill.get("grade").textValue()));
		if(!optionalTechSkill.isPresent()){
			TechSkill techSkill = new TechSkill();
			techSkill.setName(jTechSkill.get("name").textValue());
			//The grade has to be between 1 and 4
			float grade = Float.parseFloat(jTechSkill.get("grade").textValue());
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
