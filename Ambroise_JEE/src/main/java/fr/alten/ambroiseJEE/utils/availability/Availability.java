package fr.alten.ambroiseJEE.utils.availability;

import java.io.Serializable;

/**
 *
 * @author Kylian Gehier
 *
 */

public abstract class Availability implements Serializable {

	private static final long serialVersionUID = 2571407068970184920L;
	private long initDate;

	public Availability(long initDate) {
		this.initDate = initDate;
	}

	public long getInitDate() {
		return initDate;
	}

	public void setInitDate(long initDate) {
		this.initDate = initDate;
	}

}
