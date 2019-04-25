package fr.alten.ambroiseJEE.controller.rest;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.alten.ambroiseJEE.controller.business.PersonBusinessController;
import fr.alten.ambroiseJEE.model.beans.Person;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;

/**
 * Rest Controller for Person
 *
 * @author Camille Schnell
 *
 */
@Controller
public class PersonRestController {
	
	@Autowired
	private PersonBusinessController personBusinessController;
	
	private final Gson gson;
	
	public PersonRestController() {
		GsonBuilder builder = new GsonBuilder();
		this.gson = builder.create();
	}
	
	/**
	 *
	 * @param personMail the person's mail
	 * @param mail          the current logged user's mail
	 * @param role          the user's role
	 * @return a person, given its mail
	 * @author Camille Schnell
	 */
	@GetMapping(value = "/person/{mail}")
	@ResponseBody
	public String getPerson(@PathVariable("mail") String personMail, @RequestAttribute("mail") String mail,
		@RequestAttribute("role") UserRole role) {
		return gson.toJson(personBusinessController.getPerson(personMail, role));
	}

}
