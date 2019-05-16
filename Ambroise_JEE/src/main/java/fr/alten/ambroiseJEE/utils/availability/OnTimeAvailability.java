package fr.alten.ambroiseJEE.utils.availability;

/**
 *
 * @author Kylian Gehier
 *
 */

public class OnTimeAvailability extends Availability{

	private int duration;
	private DurationType durationtype;
	
	public OnTimeAvailability() {}
	
	public OnTimeAvailability(long initDate, int duration, DurationType durationType) {
		super(initDate);
		this.duration = duration;
		this.durationtype = durationType;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public DurationType getDurationtype() {
		return durationtype;
	}

	public void setDurationtype(DurationType durationtype) {
		this.durationtype = durationtype;
	}

}
