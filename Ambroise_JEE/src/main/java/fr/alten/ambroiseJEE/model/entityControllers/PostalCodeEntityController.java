/**
 *
 */
package fr.alten.ambroiseJEE.model.entityControllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.beans.PostalCode;
import fr.alten.ambroiseJEE.model.dao.PostalCodeRepository;
import fr.alten.ambroiseJEE.utils.httpStatus.ConflictException;
import fr.alten.ambroiseJEE.utils.httpStatus.CreatedException;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;

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
	public HttpException createPostalCode(final JsonNode jPostalCode) {

		final PostalCode newPostalCode = new PostalCode();
		newPostalCode.setName(jPostalCode.get("name").textValue());

		try {
			this.postalCodeRepository.save(newPostalCode);
		} catch (final Exception e) {
			return new ConflictException();
		}
		return new CreatedException();
	}

	/**
	 *
	 * @param jPostalCode JsonNode with all postal code parameters
	 * @return {@link HttpException} corresponding to the status of the request
	 *         ({@link ResourceNotFoundException} if the resource is not found and
	 *         {@link OkException} if the postalCode is deactivated
	 * @author Andy Chabalier
	 */
	public HttpException deletePostalCode(final JsonNode jPostalCode) {
		try {
			final PostalCode postalCode = this.postalCodeRepository.findByName(jPostalCode.get("name").textValue())
					.orElseThrow(ResourceNotFoundException::new);
			postalCode.setName("deactivated" + System.currentTimeMillis());
			this.postalCodeRepository.save(postalCode);
		} catch (final ResourceNotFoundException rnfe) {
			return rnfe;
		} catch (final Exception e) {
			return new ConflictException();
		}
		return new OkException();
	}

	/**
	 * Fetch a postal code from is name
	 *
	 * @param name the postal code name to fetch
	 * @return the fetched postalcode
	 * @throws @{@link ResourceNotFoundException} if the resource is not found
	 * @author Andy Chabalier
	 */
	public PostalCode getPostalCode(final String name) {
		return this.postalCodeRepository.findByName(name).orElseThrow(ResourceNotFoundException::new);
	}

	/**
	 * @return the list of all postalCodes
	 * @author Andy Chabalier
	 */
	public List<PostalCode> getPostalCodes() {
		return this.postalCodeRepository.findAll();
	}

	/**
	 *
	 * @param jPostalCode JsonNode with all postalCode parameters and the old name
	 *                    to perform the update even if the name is changed
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ResourceNotFoundException} if the resource is not
	 *         found and {@link CreatedException} if the postalCode is updated
	 * @author Andy Chabalier
	 */
	public HttpException updatePostalCode(final JsonNode jPostalCode) {
		try {
			final PostalCode postalCode = this.postalCodeRepository.findByName(jPostalCode.get("oldName").textValue())
					.orElseThrow(ResourceNotFoundException::new);
			postalCode.setName(jPostalCode.get("name").textValue());
			this.postalCodeRepository.save(postalCode);
		} catch (final ResourceNotFoundException rnfe) {
			return rnfe;
		} catch (final Exception e) {
			return new ConflictException();
		}
		return new OkException();
	}

}
