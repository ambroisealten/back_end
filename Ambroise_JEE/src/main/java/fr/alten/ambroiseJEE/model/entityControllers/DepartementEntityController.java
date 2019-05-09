/**
 *
 */
package fr.alten.ambroiseJEE.model.entityControllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import fr.alten.ambroiseJEE.model.beans.Departement;
import fr.alten.ambroiseJEE.model.dao.DepartementRepository;
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
public class DepartementEntityController {

	@Autowired
	private DepartementRepository departementRepository;

	/**
	 * Method to create a departement.
	 *
	 * @param jDepartement JsonNode with all departement parameters
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ConflictException} if there is a conflict in the
	 *         database and {@link CreatedException} if the departement is created
	 * @author Andy Chabalier
	 */
	public HttpException createDepartement(final JsonNode jDepartement) {

		final Departement newDepartement = new Departement();
		newDepartement.setNom(jDepartement.get("nom").textValue());
		newDepartement.setCode(jDepartement.get("code").textValue());
		newDepartement.setCodeRegion(jDepartement.get("codeRegion").textValue());

		try {
			this.departementRepository.save(newDepartement);
		} catch (final Exception e) {
			return new ConflictException();
		}
		return new CreatedException();
	}

	/**
	 *
	 * @param jDepartement  JsonNode with all departement parameters
	 * @return {@link HttpException} corresponding to the status of the request
	 *         ({@link ResourceNotFoundException} if the resource is not found and
	 *         {@link OkException} if the departement is deactivated
	 * @author Andy Chabalier
	 */
	public HttpException deleteDepartement(final JsonNode jDepartement) {
		try {
			final Departement departement = this.departementRepository.findByCode(jDepartement.get("code").textValue())
					.orElseThrow(ResourceNotFoundException::new);
			departement.setNom("deactivated" + System.currentTimeMillis());
			this.departementRepository.save(departement);
		} catch (final ResourceNotFoundException rnfe) {
			return rnfe;
		} catch (final Exception e) {
			return new ConflictException();
		}
		return new OkException();
	}

	/**
	 * Fetch a departement
	 *
	 * @param name the name of departement to fetch
	 * @return the fetched departement
	 * @throws @{@link ResourceNotFoundException} if the resource is not found
	 * @author Andy Chabalier
	 */
	public Departement getDepartement(final String name) {
		return this.departementRepository.findByNom(name).orElseThrow(ResourceNotFoundException::new);
	}

	/**
	 * @return the list of all departements
	 * @author Andy Chabalier
	 */
	public List<Departement> getDepartements() {
		return this.departementRepository.findAll();
	}

	/**
	 *
	 * @param jDepartement JsonNode with all departement parameters and the old name
	 *                     to perform the update even if the name is changed
	 * @return the @see {@link HttpException} corresponding to the status of the
	 *         request ({@link ResourceNotFoundException} if the resource is not
	 *         found and {@link OkException} if the departement is updated
	 * @author Andy Chabalier
	 * @author Camille Schnell
	 */
	public HttpException updateDepartement(final JsonNode jDepartement) {
		return this.departementRepository.findByCode(jDepartement.get("code").textValue())
				// optional is present
				.map(departement -> {
					departement.setNom(jDepartement.get("nom").textValue());
					try {
						this.departementRepository.save(departement);
					} catch (final DuplicateKeyException dke) {
						return new ConflictException();
					} catch (final Exception e) {
						e.printStackTrace();
					}
					return new OkException();

					// optional isn't present
				}).orElse(new ResourceNotFoundException());
	}

}
