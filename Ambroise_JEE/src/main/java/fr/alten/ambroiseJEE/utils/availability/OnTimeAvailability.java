package fr.alten.ambroiseJEE.utils.availability;

import java.time.temporal.ChronoUnit;

/**
 *
 * @author Kylian Gehier
 *
 */

public class OnTimeAvailability extends Availability{

	private int duration;
	private ChronoUnit durationType;
	
	public OnTimeAvailability(long initDate, int duration, ChronoUnit durationType) {
		super(initDate,0,duration,durationType);
		this.duration = duration;
		this.durationType = durationType;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public ChronoUnit getDurationtype() {
		return durationType;
	}

	public void setDurationtype(ChronoUnit durationtype) {
		this.durationType = durationtype;
	}

}
