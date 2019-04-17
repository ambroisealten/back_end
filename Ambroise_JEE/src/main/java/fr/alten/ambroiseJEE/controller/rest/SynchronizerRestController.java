/**
 *
 */
package fr.alten.ambroiseJEE.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.ResponseBody;

import fr.alten.ambroiseJEE.controller.business.geographic.GeographicBusinessController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.InternalServerErrorException;
import fr.alten.ambroiseJEE.utils.httpStatus.OkException;

/**
 * Rest controller for the agency web service
 *
 * @author Andy Chabalier
 *
 */
@Controller
public class SynchronizerRestController {

	@Autowired
	private GeographicBusinessController geographicBusinessController;

	/**
	 *
	 * @param mail the current logged user mail
	 * @param role the current logged user role
	 * @return {@link HttpException} corresponding to the status of the request
	 *         ({@link InternalServerErrorException} a problem occur and
	 *         {@link OkException} if the process is successful
	 * @throws Exception @see ForbiddenException if wrong identifiers
	 * @author Andy Chabalier
	 */
	@PostMapping(value = "/admin/synchronize/geographics")
	@ResponseBody
	public HttpException synchronize(@RequestAttribute("mail") String mail, @RequestAttribute("role") UserRole role)
			throws Exception {
		return geographicBusinessController.synchronize(role);
	}

}
