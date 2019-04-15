/**
 * 
 */
package fr.alten.ambroiseJEE.model.entityControllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.beans.Forum;
import fr.alten.ambroiseJEE.model.dao.ForumRepository;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.RessourceNotFoundException;

/**
 * @author MAQUINGHEN MAXIME
 *
 */
@Service
public class ForumEntityController {

	@Autowired
	private ForumRepository forumRepository;

	/**
	 * Method to create a Forum.
	 * 
	 * @param jForum JsonNode with all forum parameters (Name, date, place)
	 * @return
	 * @author MAQUINGHEN MAXIME
	 */
	public HttpException createForum(JsonNode jForum) {

		Forum newForum = new Forum();

		newForum.setName(jForum.get("name").textValue());
		newForum.setDate(jForum.get("date").textValue());
		newForum.setPlace(jForum.get("place").textValue());

		try {
			forumRepository.save(newForum);
		} catch (Exception e) {
			return new ConflictException();
		}
		return new CreatedException();
	}

	/**
	 * try to fetch all forums
	 * 
	 * @return the list of all forum
	 * @author MAQUINGHEN MAXIME
	 */
	public List<Forum> getForums() {
		return forumRepository.findAll();
	}

	/**
	 * Desactivate a forum TO DO = Nothing happen now
	 * 
	 * @param id the id of the forum to fetch
	 * @return {@link HttpException} corresponding to the statut of the request
	 *         ({@link RessourceNotFoundException} if the ressource is not found and
	 *         {@link CreatedException} if the forum is desactivated
	 * @author MAQUINGHEN MAXIME
	 */
	public HttpException deleteForum(String id) {
		Optional<Forum> forumOptional = forumRepository.findBy_id(id);

		if (forumOptional.isPresent()) {
			Forum forum = forumOptional.get();
			forumRepository.save(forum);
		} else {
			throw new RessourceNotFoundException();
		}
		return new OkException();
	}

	public HttpException updateForum(JsonNode params) {
		// Optional<Forum> forumOptional= forumRepository.findById();

		return null;
	}

	/**
	 * Get Forum by ID
	 * 
	 * @param id the unique Id of the forum
	 * @return the forum
	 * @author MAQUINGHEN MAXIME
	 */
	public Optional<Forum> getForumById(String id) {
		return forumRepository.findBy_id(id);
	}

}
