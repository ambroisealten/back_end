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
import fr.alten.ambroiseJEE.utils.httpStatus.InternalServerErrorException;
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
	 * @author MAQUINGHEN MAXIME, Kylian Gehier
	 */
	public HttpException createForum(final JsonNode jForum) {

		try {
			final Optional<Forum> forumOptional = this.forumRepository.findByNameAndDateAndPlace(
					jForum.get("name").textValue(), jForum.get("date").textValue(), jForum.get("place").textValue());

			if (forumOptional.isPresent()) {
				return new ConflictException();
			}

			final Forum newForum = new Forum();

			newForum.setName(jForum.get("name").textValue());
			newForum.setDate(jForum.get("date").textValue());
			newForum.setPlace(jForum.get("place").textValue());

			this.forumRepository.save(newForum);
		} catch (final Exception e) {
			return new InternalServerErrorException(e);
		}

		return new CreatedException();
	}

	/**
	 * Delete a Forum
	 *
	 * @param jForum contain the name, date and place of the forum to delete
	 * @return {@link HttpException} corresponding to the status of the request
	 *         ({@link ResourceNotFoundException} if the resource cannot be found
	 *         {@link OkException} if the forum is deleted
	 * @author MAQUINGHEN MAXIME, Kylian Gehier
	 */
	public HttpException deleteForum(final JsonNode jForum) {
		try {
			final Forum forum = this.forumRepository.findByNameAndDateAndPlace(jForum.get("name").textValue(),
					jForum.get("date").textValue(), jForum.get("place").textValue())
					.orElseThrow(ResourceNotFoundException::new);
			this.forumRepository.delete(forum);
		} catch (final ResourceNotFoundException rnfe) {
			return rnfe;
		} catch (final Exception e) {
			return new InternalServerErrorException(e);
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
	public Forum getForum(final String name, final String date, final String place) {
		return this.forumRepository.findByNameAndDateAndPlace(name, date, place)
				.orElseThrow(ResourceNotFoundException::new);
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
	 * @author MAQUINGHEN MAXIME, Kylian Gehier
	 */
	public HttpException updateForum(final JsonNode params) {
		try {
			this.forumRepository.findByNameAndDateAndPlace(params.get("name").textValue(),
					params.get("date").textValue(), params.get("place").textValue())
					.orElseThrow(ConflictException::new);

			final Forum forum = this.forumRepository.findByNameAndDateAndPlace(params.get("oldName").textValue(),
					params.get("oldDate").textValue(), params.get("oldPlace").textValue())
					.orElseThrow(ResourceNotFoundException::new);

			forum.setName(params.get("name").textValue());
			forum.setDate(params.get("date").textValue());
			forum.setPlace(params.get("place").textValue());
			this.forumRepository.save(forum);

		} catch (final ResourceNotFoundException rnfe) {
			return rnfe;
		} catch (final ConflictException ce) {
			return ce;
		} catch (final Exception e) {
			return new InternalServerErrorException(e);
		}
		return new OkException();
	}

}
