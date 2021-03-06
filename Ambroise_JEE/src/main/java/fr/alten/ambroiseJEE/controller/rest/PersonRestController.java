package fr.alten.ambroiseJEE.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.alten.ambroiseJEE.controller.business.PersonBusinessController;
import fr.alten.ambroiseJEE.security.UserRole;
import fr.alten.ambroiseJEE.utils.MailUtils;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;
import fr.alten.ambroiseJEE.utils.httpStatus.UnprocessableEntityException;

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
		final GsonBuilder builder = new GsonBuilder();
		this.gson = builder.create();
	}

	@DeleteMapping(value = "/person/{mail}")
	@ResponseBody
	public HttpException deletePerson(@PathVariable("mail") final String personMail,
			@RequestAttribute("mail") final String mail, @RequestAttribute("role") final UserRole role) {
		return MailUtils.validateMail(personMail) ? this.personBusinessController.deletePerson(personMail, role)
				: new UnprocessableEntityException();
	}

	/**
	 *
	 * @param personMail the person's mail
	 * @param mail       the current logged user's mail
	 * @param role       the user's role
	 * @return a person, given its mail
	 * @author Camille Schnell
	 */
	@GetMapping(value = "/person/{mail}")
	@ResponseBody
	public String getPerson(@PathVariable("mail") final String personMail, @RequestAttribute("mail") final String mail,
			@RequestAttribute("role") final UserRole role) {
		return this.gson.toJson(this.personBusinessController.getPerson(personMail, role));
	}

}
