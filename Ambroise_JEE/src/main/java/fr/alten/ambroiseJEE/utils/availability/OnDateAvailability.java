package fr.alten.ambroiseJEE.utils.availability;

import java.time.temporal.ChronoUnit;

/**
 *
 * @author Kylian Gehier
 *
 */

public class OnDateAvailability extends Availability {

	private long finalDate;
	
	public OnDateAvailability(long initDate, long finalDate) {
		super(initDate,finalDate,0,ChronoUnit.FOREVER);
		this.finalDate = finalDate;
	}

	public long getFinalDate() {
		return finalDate;
	}

	public void setFinalDate(long finalDate) {
		this.finalDate = finalDate;
	}

}
