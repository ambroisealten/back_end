package fr.alten.ambroiseJEE.utils.routing;

import java.util.HashMap;
import java.util.Map;

import fr.alten.ambroiseJEE.utils.httpStatus.RessourceNotFoundException;

/**
 *
 * @author Kylian Gehier
 *
 */

// Singleton
public class AngularModules {

	// unique instance pré-initiate
	private static AngularModules INSTANCE = null;

	// unique access point
	public static AngularModules getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new AngularModules();
		}
		return INSTANCE;
	}

	private HashMap<AngularModule, String> jsonLinks;

	private AngularModules() {
		this.jsonLinks = new HashMap<AngularModule, String>();
		fillModules();
	}

	private void fillModules() {
		System.out.println(jsonLinks);
		this.jsonLinks.put(AngularModule.Skill, "src/main/resources/routing/skill.routing.json");
		System.out.println(jsonLinks);
	}

	/**
	 *
	 * @param module : AngularModule of the filePath
	 * @return filePath of the AngularModule Json associated
	 * @throws RessourceNotFoundException
	 */
	public String getFileByAngularModule(AngularModule module) throws RessourceNotFoundException {
		for (Map.Entry<AngularModule, String> e : this.jsonLinks.entrySet()) {
			if (e.getKey().equals(module)) {
				return e.getValue();
			}
		}

		throw new RessourceNotFoundException();
	}

}
