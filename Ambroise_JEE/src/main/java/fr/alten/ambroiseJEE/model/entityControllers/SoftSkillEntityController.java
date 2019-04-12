package fr.alten.ambroiseJEE.model.entityControllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.beans.SoftSkill;
import fr.alten.ambroiseJEE.model.dao.SoftSkillRepository;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;

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
	 * @return the list of all softSkills
	 * @author Lucas Royackkers, Thomas Decamp
	 */
	public List<SoftSkill> getSoftSkills() {
		return softSkillRepository.findAll();
	}
	
	/**
	 * Method to create a softSkill.
	 * 
	 * @param jSoftSkill JsonNode with all softSkill parameters
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database and {@link CreatedException} if the softSkill is created
	 * @author Lucas Royackkers, Thomas Decamp
	 */
	public HttpException createSoftSkillAndGrade(JsonNode jSoftSkill) {

		SoftSkill newSoftSkill = new SoftSkill();
		newSoftSkill.setName(jSoftSkill.get("name").textValue());
		if(jSoftSkill.get("grade").floatValue() >= 1 && jSoftSkill.get("grade").floatValue() <= 4) {
			newSoftSkill.setGrade(jSoftSkill.get("grade").floatValue());
		}

		try {
			softSkillRepository.save(newSoftSkill);
		} catch (Exception e) {
			return new ConflictException();
		}
		return new CreatedException();
	}

}
