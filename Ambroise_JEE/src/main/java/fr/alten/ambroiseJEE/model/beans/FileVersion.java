/**
 * 
 */
package fr.alten.ambroiseJEE.model.beans;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author MAQUINGHEN MAXIME
 *
 */
@Document(collection="filesVersion")
public class FileVersion extends File {
	
	@Indexed(unique = true)
	private int ordre;
	
}
