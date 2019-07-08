package fr.alten.ambroiseJEE.controller.rest;

import java.io.FileNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.ResponseBody;

import fr.alten.ambroiseJEE.controller.business.ConfigBusinessController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.routing.AngularModule;

/**
 * Rest Controller for Menu items configuration
 *
 * @author Camille Schnell
 * @author Kylian Gehier
 *
 */
@Controller
public class ConfigRestController {

	@Autowired
	private ConfigBusinessController configMenuBusinessController;

	public ConfigRestController() {

	}

	/**
	 * Rest controller to fetch all menu items for the header menu. HTTP Method :
	 * GET
	 *
	 * @param role the user role
	 * @return json reponse containing all menus and submenus items
	 * @author Camille Schnell
	 * @author Kylian Gehier
	 * @throws FileNotFoundException
	 */
	@GetMapping(value = "/configMenu")
	@ResponseBody
	public String getMenuItems(@RequestAttribute("mail") final String mail,
			@RequestAttribute("role") final UserRole role) throws FileNotFoundException {
		return this.configMenuBusinessController.getMenuItems(role);
	}

	/**
	 *
	 * @param role the user role
	 * @return json reponse containing Routing list depending of user's role
	 * @author Kylian Gehier
	 * @throws FileNotFoundException
	 */
	@GetMapping(value = "/configRouting/{angularModule}")
	@ResponseBody
	public String getRoutes(@PathVariable("angularModule") final AngularModule module,
			@RequestAttribute("mail") final String mail, @RequestAttribute("role") final UserRole role)
			throws FileNotFoundException {
		return this.configMenuBusinessController.getRoutes(role, module);
	}

}
