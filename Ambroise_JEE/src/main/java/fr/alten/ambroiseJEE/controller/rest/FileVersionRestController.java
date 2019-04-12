/**
 * 
 */
package fr.alten.ambroiseJEE.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.alten.ambroiseJEE.controller.business.FileVersionBusinessController;
import fr.alten.ambroiseJEE.utils.httpStatus.HttpException;

/**
 * @author MAQUINGHEN MAXIME
 *
 */
@RestController
public class FileVersionRestController {

		@Autowired
		private FileVersionBusinessController fileVersionBusinessController;
		
		private final Gson gson;
		
		public FileVersionRestController() {
			GsonBuilder builder = new GsonBuilder();
			this.gson = builder.create();
		}
		
		
		@PostMapping("/version")
		@ResponseBody
		public HttpException getAppStatut() {
			return null;
		}
		
		
}
