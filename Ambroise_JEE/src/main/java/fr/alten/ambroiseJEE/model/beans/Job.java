package fr.alten.ambroiseJEE.model.beans;

import java.io.Serializable;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

/**
 *
 *
 * @author Lucas Royackkers
 *
 */
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
		return _id;
	}

	public String getTitle() {
		return title;
	}

	public void set_id(ObjectId _id) {
		this._id = _id;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
