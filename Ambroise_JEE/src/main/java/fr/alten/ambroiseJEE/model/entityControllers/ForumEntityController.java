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
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;

/**
 * @author MAQUINGHEN MAXIME
 *
 */
@Service
public class ForumEntityController {

	@Autowired
	private ForumRepository forumRepository;

	/**
	 * Create a Forum
	 *
	 * @param jForum contain the forum name, the date and the place to create
	 * @return {@link HttpException} corresponding to the status of the request
	 *         ({@link ConflictException} if the resource cannot be create
	 *         {@link CreatedException} if the forum is create
	 * @author MAQUINGHEN MAXIME
	 */
	public HttpException createForum(final JsonNode jForum) {
		final Optional<Forum> forumOptional = this.forumRepository.findByNameAndDateAndPlace(
				jForum.get("name").textValue(), jForum.get("date").textValue(), jForum.get("place").textValue());

		if (forumOptional.isPresent()) {
			return new ConflictException();
		}

		final Forum newForum = new Forum();

		newForum.setName(jForum.get("name").textValue());
		newForum.setDate(jForum.get("date").textValue());
		newForum.setPlace(jForum.get("place").textValue());

		try {
			this.forumRepository.save(newForum);
		} catch (final Exception e) {
			return new ConflictException();
		}
		return new CreatedException();
	}

	/**
	 * Delete a Forum
	 *
	 * @param params contain the name, date and place of the forum to delete
	 * @return {@link HttpException} corresponding to the status of the request
	 *         ({@link ResourceNotFoundException} if the resource cannot be found
	 *         {@link OkException} if the forum is deleted
	 * @author MAQUINGHEN MAXIME
	 */
	public HttpException deleteForum(final JsonNode params) {
		final Optional<Forum> forumOptional = this.forumRepository.findByNameAndDateAndPlace(
				params.get("name").textValue(), params.get("date").textValue(), params.get("place").textValue());

		if (forumOptional.isPresent()) {
			final Forum forum = forumOptional.get();
			this.forumRepository.delete(forum);
		} else {
			return new ResourceNotFoundException();
		}
		return new OkException();
	}

	/**
	 * Get a specific forum
	 *
	 * @param name  the name of the forum
	 * @param date  the date of the forum
	 * @param place the place of the forum
	 * @return an Optional forum data
	 * @author MAQUINGHEN MAXIME
	 */
	public Optional<Forum> getForum(final String name, final String date, final String place) {
		return this.forumRepository.findByNameAndDateAndPlace(name, date, place);
	}

	/**
	 * Fetch all forums
	 *
	 * @return the list of all forum
	 * @author MAQUINGHEN MAXIME
	 */
	public List<Forum> getForums() {
		return this.forumRepository.findAll();
	}

	/**
	 * Update a forum data
	 *
	 * @param params contain the forum oldname, olddate and oldplace to update
	 * @return {@link HttpException} corresponding to the status of the request
	 *         ({@link ResourceNotFoundException} if the resource cannot be found
	 *         {@link OkException} if the forum is updated
	 * @author MAQUINGHEN MAXIME
	 */
	public HttpException updateForum(final JsonNode params) {
		final Optional<Forum> forumOptional = this.forumRepository.findByNameAndDateAndPlace(
				params.get("oldname").textValue(), params.get("olddate").textValue(),
				params.get("oldplace").textValue());

		if (forumOptional.isPresent()) {
			final Forum forum = forumOptional.get();

			final Optional<Forum> newForumOptional = this.forumRepository.findByNameAndDateAndPlace(
					params.get("name").textValue(), params.get("date").textValue(), params.get("place").textValue());
			if (newForumOptional.isPresent()) {
				return new ConflictException();
			}

			forum.setName(params.get("name").textValue());
			forum.setDate(params.get("date").textValue());
			forum.setPlace(params.get("place").textValue());
			this.forumRepository.save(forum);

			try {
				this.forumRepository.save(forum);
			} catch (final Exception e) {
				return new ConflictException();
			}
		} else {
			return new ResourceNotFoundException();
		}
		return new OkException();
	}

}
