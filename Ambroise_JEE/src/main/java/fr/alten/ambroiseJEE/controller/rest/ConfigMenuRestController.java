package fr.alten.ambroiseJEE.controller.rest;

import java.io.FileNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.ResponseBody;

import fr.alten.ambroiseJEE.controller.business.ConfigMenuBusinessController;

/**
 * Rest Controller for Menu items configuration
 * @author Camille Schnell
 *
 */
@Controller
public class ConfigMenuRestController {

	private ConfigMenuBusinessController configMenuBusinessController;
	
	public ConfigMenuRestController() {
		
	}
	
	/**
	 *
	 * @param role the user role
	 * @return json reponse containing all menus and submenus items
	 * @author Camille Schnell
	 * @throws FileNotFoundException 
	 */
	@GetMapping(value = "/configMenu")
	@ResponseBody
	public String getMenuItems(@RequestAttribute("mail") String mail, @RequestAttribute("role") int role) throws FileNotFoundException {
		return configMenuBusinessController.getItems(role);
	}
	
}
