package fr.alten.ambroiseJEE.utils.availability;

/**
 *
 * @author Kylian Gehier
 *
 */

public class OnDateAvailability extends Availability {

	private static final long serialVersionUID = -8339571496282667753L;
	private long finalDate;
	
	public OnDateAvailability(long initDate, long finalDate) {
		super(initDate);
		this.finalDate = finalDate;
	}

	public long getFinalDate() {
		return finalDate;
	}

	public void setFinalDate(long finalDate) {
		this.finalDate = finalDate;
	}

}
