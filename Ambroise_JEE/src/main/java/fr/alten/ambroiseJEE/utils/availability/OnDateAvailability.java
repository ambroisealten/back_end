package fr.alten.ambroiseJEE.utils.availability;

import java.time.temporal.ChronoUnit;

/**
 *
 * @author Kylian Gehier
 *
 */

public class OnDateAvailability extends Availability {

	private long finalDate;

	public OnDateAvailability(final long initDate, final long finalDate) {
		super(initDate, finalDate, 0, ChronoUnit.FOREVER);
		this.finalDate = finalDate;
	}

	@Override
	public long getFinalDate() {
		return this.finalDate;
	}

	@Override
	public void setFinalDate(final long finalDate) {
		this.finalDate = finalDate;
	}

}
