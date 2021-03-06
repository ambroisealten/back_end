package fr.alten.ambroiseJEE.model.beans;

import java.io.Serializable;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Defines what a Job is in our application
 *
 * @author Lucas Royackkers
 *
 */
@Document(collection = "job")
public class Job implements Serializable {

	private static final long serialVersionUID = -8259196238254390780L;
	@Id
	private transient ObjectId _id;
	@Indexed(unique = true)
	private String title;

	public Job() {
		super();
	}

	public ObjectId get_id() {
		return this._id;
	}

	public String getTitle() {
		return this.title;
	}

	public void set_id(final ObjectId _id) {
		this._id = _id;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

}
