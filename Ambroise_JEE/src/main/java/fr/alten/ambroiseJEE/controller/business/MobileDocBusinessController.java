package fr.alten.ambroiseJEE.controller.business;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.alten.ambroiseJEE.model.beans.MobileDoc;
import fr.alten.ambroiseJEE.model.entityControllers.MobileDocEntityController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.ForbiddenException;
/**
 * 
 * @author Kylian Gehier
 *
 */
@Service
public class MobileDocBusinessController {

	@Autowired
	private MobileDocEntityController mobileDocEntityController;
	
	
	public List<MobileDoc> getMobileDocs(UserRole role) {
		if (role == UserRole.CDR_ADMIN || role == UserRole.MANAGER_ADMIN) {
			return mobileDocEntityController.getMobileDocs();
		}
		throw new ForbiddenException();
	}

}
