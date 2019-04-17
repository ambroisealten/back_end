package fr.alten.ambroiseJEE.utils.routing;

import java.util.HashMap;
import java.util.Map;

import fr.alten.ambroiseJEE.utils.CustomLogger;
import fr.alten.ambroiseJEE.utils.LogLevel;
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
		//CustomLogger.log(jsonLinks, LogLevel.DEBUG);
		this.jsonLinks.put(AngularModule.Skills, "src/main/resources/routing/skills.routing.json");
		this.jsonLinks.put(AngularModule.Init, "src/main/resources/routing/init.routing.json");
		//CustomLogger.log(jsonLinks, LogLevel.DEBUG);
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
