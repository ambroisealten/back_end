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
		return this._id;
	}

	@Override
	public String getIdentifier() {
		return getName();
	}

	public String getName() {
		return this.name;
	}

	public void set_id(final ObjectId _id) {
		this._id = _id;
	}

	public void setName(final String name) {
		this.name = name;
	}
}
