/**
 *
 */
package fr.alten.ambroiseJEE.model.entityControllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.mongodb.DuplicateKeyException;

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
	public HttpException createSkillForum(final JsonNode jSkillForum) {

		final SkillForum newSkillForum = new SkillForum();
		newSkillForum.setName(jSkillForum.get("name").textValue());
		try {
			this.skillForumRepository.save(newSkillForum);
		} catch (final DuplicateKeyException dke) {
			return new ConflictException();
		} catch (final Exception e) {
			e.printStackTrace();
		}
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
	public HttpException deleteSkillForum(final JsonNode jSkillForum) {
		return this.skillForumRepository.findByName(jSkillForum.get("name").textValue())
				// optional is present
				.map(skillForum -> {
					skillForum.setName("deactivated" + System.currentTimeMillis());
					this.skillForumRepository.save(skillForum);
					return (HttpException) new OkException();
				})
				// optional isn't present
				.orElse(new ResourceNotFoundException());
	}

	public SkillForum getSkillForum(final JsonNode jSkillForum) {
		return this.skillForumRepository.findByName(jSkillForum.get("name").textValue())
				.orElseThrow(ResourceNotFoundException::new);
	}

	/**
	 * @return the list of all skills
	 * @author Thomas Decamp
	 */
	public List<SkillForum> getSkillsForum() {
		return this.skillForumRepository.findAll();
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
	public HttpException updateSkillForum(final JsonNode jSkillForum) {
		return this.skillForumRepository.findByName(jSkillForum.get("oldName").textValue())
				// optional is present
				.map(skillForum -> {
					skillForum.setName(jSkillForum.get("name").textValue());
					try {
						this.skillForumRepository.save(skillForum);
					} catch (final DuplicateKeyException dke) {
						return new ConflictException();
					} catch (final Exception e) {
						e.printStackTrace();
					}
					return (HttpException) new OkException();
				})
				// optional isn't present
				.orElse(new ResourceNotFoundException());
	}

}
