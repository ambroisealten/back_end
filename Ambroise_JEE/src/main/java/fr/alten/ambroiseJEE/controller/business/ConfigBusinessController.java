package fr.alten.ambroiseJEE.controller.business;

import java.io.FileNotFoundException;

import org.springframework.stereotype.Service;

import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.config.ParseConfigFile;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;

/**
 * Business Controller for menu items configuration
 * @author Camille Schnell
 * @author Kylian Gehier
 *
 */
@Service
public class ConfigBusinessController {	
	
	/**
	 * 
	 * @param role the user role
	 * @return a json string containing every menu and submenu items
	 * @author Camille Schnell
	 * @author Kylian Gehier
	 * @throws FileNotFoundException
	 *  {@link ForbiddenException} if the user isn't connected
	 */
	public String getMenuItems(UserRole role) throws FileNotFoundException {
		if(!(UserRole.DESACTIVATED == role))
			return ParseConfigFile.getJsonMenuItemsByRole(role);
		throw new ForbiddenException();
	}
	
	/**
	 * 
	 * @param role the user role
	 * @return a json string containing Routing list depending of user's role
	 * @author Kylian Gehier
	 * @throws FileNotFoundException
	 *  {@link ForbiddenException} if the user isn't connected
	 */
	public String getRoutingItems(UserRole role) throws FileNotFoundException {
		if(!(UserRole.DESACTIVATED == role))
			return ParseConfigFile.getJsonRoutingItemsByRole(role);
		throw new ForbiddenException();
	}
}
