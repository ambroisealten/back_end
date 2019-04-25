package fr.alten.ambroiseJEE.model.beans.mobileDoc;

public class MobileDoc {

	private String name;
	private int order;

	public MobileDoc(final String name, final int order) {
		this.name = name;
		this.order = order;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public int getOrder() {
		return this.order;
	}

	public void setOrder(final int order) {
		this.order = order;
	}

	public static MobileDoc of(final String name, final int order) {
		return new MobileDoc(name, order);
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}

		if (!(obj instanceof MobileDoc)) {
			return false;
		}

		final MobileDoc other = (MobileDoc) obj;

		return this.name.equals(other.name) && this.order == other.order;
	}

}