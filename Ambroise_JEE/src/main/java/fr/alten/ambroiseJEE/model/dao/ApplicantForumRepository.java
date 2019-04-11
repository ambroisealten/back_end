/**
 * 
 */
package fr.alten.ambroiseJEE.model.dao;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import fr.alten.ambroiseJEE.model.beans.ApplicantForum;

/**
 * @author Andy Chabalier
 *
 */
public interface ApplicantForumRepository extends MongoRepository<ApplicantForum, Long> {

	/**
	 * @param mail the applicant's mail
	 * @return An Optional with the corresponding applicant or not.
	 * @author Andy Chabalier
	 */
	Optional<ApplicantForum> findByMail(String mail);

}
