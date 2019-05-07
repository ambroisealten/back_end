/**
 *
 */
package fr.alten.ambroiseJEE.model.beans.mobileDoc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * @author Andy Chabalier
 *
 */
public class MobileDocArrayList implements List<MobileDoc> {

	private final List<MobileDoc> mobileDocs = new ArrayList<MobileDoc>();

	public MobileDocArrayList() {
		super();
	}

	public MobileDocArrayList(final List<MobileDoc> mobileDocs) {
		this.addAll(mobileDocs);
	}

	/**
	 * Compare an other MobileDocArrayList and return the comparison
	 *
	 * @param other the other MobileDocArrayList to compare
	 * @return an HashMap with 2 List<MobileDoc>: additions, changes
	 * @author Andy Chabalier
	 */
	public HashMap<String, List<MobileDoc>> compare(final MobileDocArrayList other) {
		final List<MobileDoc> additions = new ArrayList<MobileDoc>();
		final List<MobileDoc> changes = new ArrayList<MobileDoc>();

		for (final MobileDoc mobileDoc : other) {
			final boolean containsDoc = contains(mobileDoc);
			// if the other doc have the same name but an different order
			if (containsChangedDoc(mobileDoc)) {
				changes.add(mobileDoc);
			} else if (containsDoc) { // if the two list have the same MobileDoc
				// do nothing
			} else if (!containsDoc) { // if the current list don't have the other MobileDoc
				additions.add(mobileDoc);
			}
		}

		final HashMap<String, List<MobileDoc>> result = new HashMap<String, List<MobileDoc>>();
		result.put("additions", additions);
		result.put("changes", changes);
		return result;
	}

	/**
	 * check if this MobileDocArrayList contain the other name Could be not
	 * optimized ( o(n) calculs)
	 * 
	 * @param other the mobiledoc to compare
	 * @return true if other have a same name as one of the first MobileDocArrayList
	 * @author Andy Chabalier
	 */
	public boolean containsChangedDoc(final MobileDoc other) {
		for (final MobileDoc mobileDoc : this.mobileDocs) {
			if (mobileDoc.getName().equals(other.getName())) {
				return mobileDoc.getOrder() != other.getOrder();
			}
		}
		return false;
	}

	@Override
	public boolean contains(final Object o) {
		return this.mobileDocs.contains(o);
	}

	@Override
	public boolean retainAll(final Collection<?> c) {
		return this.mobileDocs.retainAll(c);
	}

	@Override
	public boolean add(final MobileDoc element) {
		return this.mobileDocs.add(element);
	}

	@Override
	public void add(final int index, final MobileDoc element) {
		this.mobileDocs.add(index, element);
	}

	@Override
	public boolean addAll(final Collection<? extends MobileDoc> collection) {
		return this.mobileDocs.addAll(collection);
	}

	@Override
	public boolean addAll(final int index, final Collection<? extends MobileDoc> collection) {
		return this.mobileDocs.addAll(index, collection);

	}

	@Override
	public void clear() {
		this.mobileDocs.clear();
	}

	@Override
	public boolean containsAll(final Collection<?> c) {
		return this.mobileDocs.containsAll(c);
	}

	@Override
	public MobileDoc get(final int index) {
		return this.mobileDocs.get(index);
	}

	@Override
	public int indexOf(final Object o) {
		return this.mobileDocs.indexOf(o);
	}

	@Override
	public boolean isEmpty() {
		return this.mobileDocs.isEmpty();
	}

	@Override
	public Iterator<MobileDoc> iterator() {
		return this.mobileDocs.iterator();
	}

	@Override
	public int lastIndexOf(final Object o) {
		return this.mobileDocs.lastIndexOf(o);
	}

	@Override
	public ListIterator<MobileDoc> listIterator() {
		return this.mobileDocs.listIterator();
	}

	@Override
	public ListIterator<MobileDoc> listIterator(final int index) {
		return this.mobileDocs.listIterator(index);
	}

	@Override
	public boolean remove(final Object o) {
		return this.mobileDocs.remove(o);
	}

	@Override
	public MobileDoc remove(final int index) {
		return this.mobileDocs.remove(index);
	}

	@Override
	public boolean removeAll(final Collection<?> c) {
		return this.mobileDocs.removeAll(c);
	}

	@Override
	public MobileDoc set(final int index, final MobileDoc element) {
		return this.mobileDocs.set(index, element);
	}

	@Override
	public int size() {
		return this.mobileDocs.size();
	}

	@Override
	public List<MobileDoc> subList(final int fromIndex, final int toIndex) {
		return this.mobileDocs.subList(fromIndex, toIndex);
	}

	@Override
	public Object[] toArray() {
		return this.mobileDocs.toArray();
	}

	@Override
	public <T> T[] toArray(final T[] a) {
		return this.mobileDocs.toArray(a);
	}

}
