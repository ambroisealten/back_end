package fr.alten.ambroiseJEE.controller.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author swathi
 *
 */
@RestController
public class SpringRestController {
	
	@GetMapping(value = "/test/api/tdd/{data}")
	@ResponseBody
	public String getData(@PathVariable("data") String data) {
		return data;
	}

	// inner class
	class Response {
		private String data;

		public Response(String data) {
			this.data = data;
		}

		public String getData() {
			return data;
		}
	}
}
