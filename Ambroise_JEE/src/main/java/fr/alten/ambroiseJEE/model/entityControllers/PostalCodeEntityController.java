/**
 *
 */
package fr.alten.ambroiseJEE.model.entityControllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.beans.PostalCode;
import fr.alten.ambroiseJEE.model.dao.PostalCodeRepository;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.RessourceNotFoundException;

/**
 * @author Andy Chabalier
 *
 */

@Service
public class PostalCodeEntityController {

	@Autowired
	private PostalCodeRepository postalCodeRepository;

	/**
	 * Method to create a postalCode.
	 *
	 * @param jPostalCode JsonNode with all postalCode parameters
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database and {@link CreatedException} if the postalCode is created
	 * @author Andy Chabalier
	 */
	public HttpException createPostalCode(JsonNode jPostalCode) {

		PostalCode newPostalCode = new PostalCode();
		newPostalCode.setName(jPostalCode.get("name").textValue());

		try {
			postalCodeRepository.save(newPostalCode);
		} catch (Exception e) {
			return new ConflictException();
		}
		return new CreatedException();
	}

	/**
	 *
	 * @param name the postalCode name to fetch
	 * @return {@link HttpException} corresponding to the status of the request
	 *         ({@link RessourceNotFoundException} if the resource is not found and
	 *         {@link OkException} if the postalCode is deactivated
	 * @author Andy Chabalier
	 */
	public HttpException deletePostalCode(String name) {
		Optional<PostalCode> postalCodeOptionnal = postalCodeRepository.findByName(name);

		if (postalCodeOptionnal.isPresent()) {
			PostalCode postalCode = postalCodeOptionnal.get();
			postalCode.setName("deactivated" + System.currentTimeMillis());
			postalCodeRepository.save(postalCode);
		} else {
			throw new RessourceNotFoundException();
		}
		return new OkException();
	}

	public Optional<PostalCode> getPostalCode(String name) {
		return postalCodeRepository.findByName(name);
	}

	/**
	 * @return the list of all postalCodes
	 * @author Andy Chabalier
	 */
	public List<PostalCode> getPostalCodes() {
		return postalCodeRepository.findAll();
	}

	/**
	 *
	 * @param jPostalCode JsonNode with all postalCode parameters and the old name
	 *                    to perform the update even if the name is changed
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link RessourceNotFoundException} if the resource is not
	 *         found and {@link CreatedException} if the postalCode is updated
	 * @author Andy Chabalier
	 */
	public HttpException updatePostalCode(JsonNode jPostalCode) {
		Optional<PostalCode> postalCodeOptionnal = postalCodeRepository
				.findByName(jPostalCode.get("oldName").textValue());

		if (postalCodeOptionnal.isPresent()) {
			PostalCode postalCode = postalCodeOptionnal.get();
			postalCode.setName(jPostalCode.get("name").textValue());

			postalCodeRepository.save(postalCode);
		} else {
			throw new RessourceNotFoundException();
		}
		return new OkException();
	}

}
