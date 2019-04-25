/**
 * 
 */
package fr.alten.ambroiseJEE.model.beans;

import java.io.Serializable;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.util.Pair;

/**
 * @author MAQUINGHEN MAXIME
 *
 */
@Document(collection = "documentSet")
public class DocumentSet implements Serializable {

	private static final long serialVersionUID = 8130484628750269655L;
	
	@Id
	private transient ObjectId _id;
	
	@Indexed(unique= true)
	private String name;
	
	private List<Pair<String, Integer>> files;

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public List<Pair<String, Integer>> getFiles() {
		return files;
	}
	
	public void setFiles(List<Pair<String, Integer>> files) {
		this.files = files;
	}

	
	
}
