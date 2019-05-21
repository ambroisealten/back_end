package fr.alten.ambroiseJEE.utils.availability;

import java.time.temporal.ChronoUnit;

import com.mongodb.lang.Nullable;

/**
 *
 * @author Kylian Gehier
 *
 */

public /* abstract */ class Availability {

	private long initDate;
	private long finalDate;
	private int duration;
	private ChronoUnit durationType;

	public Availability(long initDate, @Nullable long finalDate, @Nullable int duration, @Nullable ChronoUnit durationType) {
		this.initDate = initDate;
		this.finalDate = finalDate;
		this.duration = duration;
		this.durationType = durationType;
	}


	public long getInitDate() {
		return initDate;
	}

	public void setInitDate(long initDate) {
		this.initDate = initDate;
	}

	public long getFinalDate() {
		return finalDate;
	}

	public void setFinalDate(long finalDate) {
		this.finalDate = finalDate;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public ChronoUnit getDurationType() {
		return durationType;
	}

	public void setDurationType(ChronoUnit durationType) {
		this.durationType = durationType;
	}

}
