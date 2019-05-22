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

	public Availability(final long initDate, @Nullable final long finalDate, @Nullable final int duration,
			@Nullable final ChronoUnit durationType) {
		this.initDate = initDate;
		this.finalDate = finalDate;
		this.duration = duration;
		this.durationType = durationType;
	}

	public int getDuration() {
		return this.duration;
	}

	public ChronoUnit getDurationType() {
		return this.durationType;
	}

	public long getFinalDate() {
		return this.finalDate;
	}

	public long getInitDate() {
		return this.initDate;
	}

	public void setDuration(final int duration) {
		this.duration = duration;
	}

	public void setDurationType(final ChronoUnit durationType) {
		this.durationType = durationType;
	}

	public void setFinalDate(final long finalDate) {
		this.finalDate = finalDate;
	}

	public void setInitDate(final long initDate) {
		this.initDate = initDate;
	}

}
