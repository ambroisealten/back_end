/**
 *
 */
package fr.alten.ambroiseJEE.model.entityControllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.beans.SkillForum;
import fr.alten.ambroiseJEE.model.dao.SkillForumRepository;
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
public class SkillForumEntityController {

	@Autowired
	private SkillForumRepository skillForumRepository;

	/**
	 * Method to create a skill.
	 *
	 * @param jSkill JsonNode with all skill parameters
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database and {@link CreatedException} if the skill is created
	 * @author Thomas Decamp
	 */
	public HttpException createSkillForum(JsonNode jSkillForum) {

		SkillForum newSkillForum = new SkillForum();
		newSkillForum.setName(jSkillForum.get("name").textValue());

		skillForumRepository.save(newSkillForum);
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
	public HttpException deleteSkillForum(String name) {
		Optional<SkillForum> skillForumOptionnal = skillForumRepository.findByName(name);

		if (skillForumOptionnal.isPresent()) {
			SkillForum skillForum = skillForumOptionnal.get();
			skillForum.setName("deactivated" + System.currentTimeMillis());
			skillForumRepository.save(skillForum);
		} else {
			throw new ResourceNotFoundException();
		}
		return new OkException();
	}

	public Optional<SkillForum> getSkillForum(String name) {
		return skillForumRepository.findByName(name);
	}

	/**
	 * @return the list of all skills
	 * @author Thomas Decamp
	 */
	public List<SkillForum> getSkillsForum() {
		return skillForumRepository.findAll();
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
	public HttpException updateSkillForum(JsonNode jSkillForum) {
		Optional<SkillForum> skillForumOptionnal = skillForumRepository.findByName(jSkillForum.get("oldName").textValue());

		if (skillForumOptionnal.isPresent()) {
			SkillForum skillForum = skillForumOptionnal.get();
			skillForum.setName(jSkillForum.get("name").textValue());

			skillForumRepository.save(skillForum);
		} else {
			throw new ResourceNotFoundException();
		}
		return new OkException();
	}

}
