package fr.alten.ambroiseJEE.utils.routing;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.alten.ambroiseJEE.utils.httpStatus.ResourceNotFoundException;

/**
 *
 * @author Kylian Gehier
 *
 */

// Singleton
public class AngularModules {

	private static final Logger logger = LoggerFactory.getLogger(AngularModule.class);

	// unique instance pré-initiate
	private static AngularModules INSTANCE = null;

	// unique access point
	public static AngularModules getInstance() {
		if (AngularModules.INSTANCE == null) {
			AngularModules.INSTANCE = new AngularModules();
		}
		return AngularModules.INSTANCE;
	}

	private final HashMap<AngularModule, String> jsonLinks;

	private AngularModules() {
		this.jsonLinks = new HashMap<AngularModule, String>();
		fillModules();
	}

	/**
	 * Filling the singleton HashMap. Each new pair AngularModule / XXX.routing.json
	 * has to be added here
	 */
	private void fillModules() {
		this.jsonLinks.put(AngularModule.Skills, "src/main/resources/routing/skills.routing.json");
		this.jsonLinks.put(AngularModule.Init, "src/main/resources/routing/init.routing.json");
	}

	/**
	 *
	 * @param module : AngularModule of the filePath
	 * @return filePath of the AngularModule Json associated
	 * @throws ResourceNotFoundException
	 */
	public String getFileByAngularModule(final AngularModule module) throws ResourceNotFoundException {
		for (final Map.Entry<AngularModule, String> e : this.jsonLinks.entrySet()) {
			if (e.getKey().equals(module)) {
				AngularModules.logger.debug("Récuperation de la resource suivante pour parsing : {}", e.getValue());
				return e.getValue();
			}
		}

		throw new ResourceNotFoundException();
	}

}
