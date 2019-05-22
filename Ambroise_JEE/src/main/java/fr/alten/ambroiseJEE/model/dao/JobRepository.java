package fr.alten.ambroiseJEE.model.dao;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import fr.alten.ambroiseJEE.model.beans.Job;

/**
 * @author Lucas Royackkers
 *
 */
public interface JobRepository extends MongoRepository<Job, Long> {

	Optional<Job> findBy_id(ObjectId objectId);

	/**
	 * @param name the job's title
	 * @return An Optional with the corresponding job or not.
	 * @author Lucas Royackkers
	 */
	Optional<Job> findByTitle(String title);

}
