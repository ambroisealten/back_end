/**
 *
 */
package fr.alten.ambroiseJEE.model.entityControllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Optionals;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.mongodb.DuplicateKeyException;

import fr.alten.ambroiseJEE.model.beans.Skill;
import fr.alten.ambroiseJEE.model.dao.SkillRepository;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;

/**
 * @author Thomas Decamp
 *
 */

@Service
public class SkillEntityController {

	@Autowired
	private SkillRepository skillRepository;

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
			if (!DuplicateKeyException.class.isInstance(e)) {
				e.printStackTrace();
			} else
				return new ConflictException();
		}
		return new CreatedException();

	}

	public HttpException saveNewSkill(JsonNode jSkill) {

		Skill newSkill = new Skill();
		newSkill.setName(jSkill.get("name").textValue());
		skillRepository.save(newSkill);

		return new CreatedException();
	}

	/**
	 *
	 * @param name the skill name to fetch
	 * @return {@link HttpException} corresponding to the status of the request
	 *         ({@link ResourceNotFoundException} if the resource is not found and
	 *         {@link OkException} if the skill is deactivated
	 * @author Thomas Decamp
	 */
	public HttpException deleteSkill(JsonNode jSkill) {
		return skillRepository.findByName(jSkill.get("name").textValue())
				// optional is present
				.map(skill -> {
					skill.setName("deactivated" + System.currentTimeMillis());
					skillRepository.save(skill);
					return (HttpException) new OkException();
				})
				// optional isn't present
				.orElse(new ResourceNotFoundException());
	}

	public Optional<Skill> getSkill(String name) {
		return skillRepository.findByName(name);
	}

	/**
	 * Try to fetch a soft skill by its name and grade
	 *
	 * @param name  the soft skill's name to fetch
	 * @param grade the soft skill's grade to fetch
	 * @return An Optional with the corresponding soft skill or not.
	 * @author Lucas Royackkers
	 */
	public Optional<Skill> getSkillByNameAndGrade(String name, float grade) {
		return skillRepository.findByNameAndGrade(name, grade);
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
	 * @param jSkill JsonNode with all skill parameters and the old name to perform
	 *               the update even if the name is changed
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ResourceNotFoundException} if the resource is not
	 *         found and {@link CreatedException} if the skill is updated
	 * @author Thomas Decamp
	 */
	public HttpException updateSkill(JsonNode jSkill) {
		return skillRepository.findByName(jSkill.get("name").textValue())
				// optional is present
				.map(skill -> {
					skill.setName(jSkill.get("name").textValue());
					skillRepository.save(skill);
					return (HttpException) new OkException();
				})
				// optional isn't present
				.orElse(new ResourceNotFoundException());
		
		/*Optional<Skill> skillOptionnal = skillRepository.findByName(jSkill.get("oldName").textValue());

		if (!skillOptionnal.isPresent()) {
			return new ResourceNotFoundException();
		}
		skillOptionnal.setName(jSkill.get("name").textValue());
		skillRepository.save(skillOptionnal);
		return new OkException();*/
	}

}
