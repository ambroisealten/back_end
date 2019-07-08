package fr.alten.ambroiseJEE.utils.availability;

import java.time.temporal.ChronoUnit;

/**
 *
 * @author Kylian Gehier
 *
 */

public class OnTimeAvailability extends Availability {

	private int duration;
	private ChronoUnit durationType;

	public OnTimeAvailability(final long initDate, final int duration, final ChronoUnit durationType) {
		super(initDate, 0, duration, durationType);
		this.duration = duration;
		this.durationType = durationType;
	}

	@Override
	public int getDuration() {
		return this.duration;
	}

	public ChronoUnit getDurationtype() {
		return this.durationType;
	}

	@Override
	public void setDuration(final int duration) {
		this.duration = duration;
	}

	public void setDurationtype(final ChronoUnit durationtype) {
		this.durationType = durationtype;
	}

}
