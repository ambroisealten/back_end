package fr.alten.ambroiseJEE.controller.business;

import java.io.FileNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;

import fr.alten.ambroiseJEE.security.Roles;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;
import utils.config.ParseConfigFile;

/**
 * Business Controller for menu items configuration
 * @author Camille Schnell
 *
 */
public class ConfigMenuBusinessController {	
	
	/**
	 * 
	 * @param role the user role
	 * @return a json string containing every menu and submenu items
	 * @author Camille Schnell
	 * @throws FileNotFoundException
	 *  {@link ForbiddenException} if the user isn't connected
	 */
	public String getItems(int role) throws FileNotFoundException {
		if(Roles.ADMINISTRATOR_USER_ROLE.getValue() == role || Roles.DEFAULT_USER_ROLE.getValue() == role ) {
			return ParseConfigFile.getJsonMenuItems();
		}
		throw new ForbiddenException();
	}
}
