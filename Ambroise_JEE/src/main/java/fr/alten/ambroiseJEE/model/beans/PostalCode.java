/**
 * 
 */
package fr.alten.ambroiseJEE.model.beans;

import java.io.Serializable;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 
 * @author Andy Chabalier
 *
 */
@Document(collection = "postalCode")
public class PostalCode extends Geographic implements Serializable {

	private static final long serialVersionUID = 9058561126705436348L;

	@Id
	private transient ObjectId _id;

	@Indexed(unique = true)
	private String name;

	public PostalCode() {
		super();
	}

	public ObjectId get_id() {
		return _id;
	}

	public void set_id(ObjectId _id) {
		this._id = _id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String getIdentifier() {
		return this.getName();
	}
}

