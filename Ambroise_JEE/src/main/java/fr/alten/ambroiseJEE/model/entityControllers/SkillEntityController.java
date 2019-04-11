/**
 * 
 */
package fr.alten.ambroiseJEE.model.entityControllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.beans.Skill;
import fr.alten.ambroiseJEE.model.dao.SkillRepository;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.RessourceNotFoundException;

/**
 * @author Thomas Decamp
 *
 */

@Service
public class SkillEntityController {
	
	@Autowired
	private SkillRepository skillRepository;

	public Optional<Skill> getSkill(String name) {
		return skillRepository.findByName(name);
	}

	/**
	 * Method to create a skill.
	 * 
	 * @param jSkill JsonNode with all skill parameters
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database and {@link CreatedException} if the skill is created
	 * @author Thomas Decamp
	 */
	public HttpException createSkill(JsonNode jSkill) {

		Skill newSkill = new Skill();
		newSkill.setName(jSkill.get("name").textValue());

		try {
			skillRepository.save(newSkill);
		} catch (Exception e) {
			return new ConflictException();
		}
		return new CreatedException();
	}

	/**
	 * @return the list of all skills
	 * @author Thomas Decamp
	 */
	public List<Skill> getSkills() {
		return skillRepository.findAll();
	}

	/**
	 * 
	 * @param jSkill JsonNode with all skill parameters and the old name to perform the update even if the name is changed
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link RessourceNotFoundException} if the resource is not found
	 *         and {@link CreatedException} if the skill is updated
	 * @author Thomas Decamp
	 */
	public HttpException updateSkill(JsonNode jSkill) {
		Optional<Skill> skillOptionnal = skillRepository.findByName(jSkill.get("oldName").textValue());
		
		if (skillOptionnal.isPresent()) {
			Skill skill = skillOptionnal.get();
			skill.setName(jSkill.get("name").textValue());
			
			skillRepository.save(skill);
		}
		else {
			throw new RessourceNotFoundException();
		}		
		return new OkException();
	}

	/**
	 * 
	 * @param name the skill name to fetch 
	 * @return {@link HttpException} corresponding to the status of the
	 *         request ({@link RessourceNotFoundException} if the resource is not found
	 *         and {@link OkException} if the skill is deactivated
	 * @author Thomas Decamp
	 */
	public HttpException deleteSkill(String name) {
		Optional<Skill> skillOptionnal = skillRepository.findByName(name);
		
		if (skillOptionnal.isPresent()) {
			Skill skill = skillOptionnal.get();
			skill.setName("deactivated" + System.currentTimeMillis());
			skillRepository.save(skill);
		}
		else {
			throw new RessourceNotFoundException();
		}		
		return new OkException();
	}

}

