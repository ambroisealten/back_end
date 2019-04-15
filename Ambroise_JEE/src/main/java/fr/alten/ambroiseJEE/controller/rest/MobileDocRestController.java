package fr.alten.ambroiseJEE.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.alten.ambroiseJEE.controller.business.MobileDocBusinessController;
import fr.alten.ambroiseJEE.security.UserRole;

/**
 * 
 * Rest controller for MobileDoc administration
 * 
 * @author Kylian Gehier
 *
 */
@Controller
public class MobileDocRestController {

	@Autowired
	private MobileDocBusinessController mobileDocBusinessController;
	
	private final Gson gson;
	
	public MobileDocRestController() {
		GsonBuilder builder = new GsonBuilder();
		this.gson = builder.create();
	}
	
	/**
	 * 
	 * @param mail the current loggde user's mail
	 * @param role	the user(s role
	 * @return the list of all Mobile Documents
	 * @author Kylian Gehier
	 */
	@GetMapping(value = "/mobileDocs")
	@ResponseBody
	public String getMobileDocs(@RequestAttribute("mail") String mail, @RequestAttribute("role") UserRole role) {
		return gson.toJson(mobileDocBusinessController.getMobileDocs(role));
	}
	
}
