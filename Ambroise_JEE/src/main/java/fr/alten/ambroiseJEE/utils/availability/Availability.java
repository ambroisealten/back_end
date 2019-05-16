package fr.alten.ambroiseJEE.utils.availability;

import java.io.Serializable;

/**
 *
 * @author Kylian Gehier
 *
 */

public abstract class Availability{

	private long initDate;

	public Availability() {}
	
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
