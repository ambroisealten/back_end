package fr.alten.ambroiseJEE.model;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import fr.alten.ambroiseJEE.model.beans.Person;

/**
 * Custom Set List for Person objects
 *
 * @author Lucas Royackkers
 * @author Andy Chabalier
 *
 */
public class PersonSetWithFilters implements Set<Person> {

	private final List<Person> persons = new ArrayList<Person>();
	private List<String> filters = new ArrayList<String>();

	public PersonSetWithFilters(final List<String> filters) {
		super();
		this.filters = filters;
		this.filters.replaceAll(String::toLowerCase);
	}

	/**
	 * Add a Person if the list doesn't contains it already and if Person validates
	 * all filters
	 */
	@Override
	public boolean add(final Person newPerson) {
		if (!contains(newPerson) && correspondAllFilter(newPerson)) {
			return this.persons.add(newPerson);
		}
		return false;
	}

	/**
	 * Add every element of a Collection of Person, if the element isn't contained
	 * already in the list, and if the element validates all the filters
	 */
	@Override
	public boolean addAll(final Collection<? extends Person> personCollection) {
		personCollection.forEach(person -> {
			add(person);
		});
		return true;
	}

	/**
	 * Clear all list (persons and filters) in the object
	 */
	@Override
	public void clear() {
		this.filters.clear();
		this.persons.clear();
	}

	@Override
	public boolean contains(final Object other) {
		return this.persons.contains(other);
	}

	@Override
	public boolean containsAll(final Collection<?> c) {
		return this.persons.containsAll(c);
	}

	/**
	 * Check if the Person object validates all filters contained in the object
	 *
	 * @param person the Person that we need to check its attributes (surname, name,
	 *               job, diploma)
	 * @return true if the Person validates all filters, otherwise false
	 * @author Lucas Royackkers
	 * @author Andy Chabalier
	 * @author Thomas Decamp
	 */
	private boolean correspondAllFilter(final Person person) {
		boolean filterMatch = true;
		final Iterator<String> filterIterator = this.filters.iterator();
		while (filterIterator.hasNext() && filterMatch) {
			final String filter = filterIterator.next();

			Person p = person;

			System.out.print("\n\n\n\n\n\n\n\n\n\n\n" + "FILTERMATCH : " + filterMatch + " || FILTER : " + filter + "\n\n\n\n\n\n\n");


			p.setName(this.noAccent(p.getName()));
			p.setSurname(this.noAccent(p.getSurname()));

			filterMatch = filterMatch && (p.getName().toLowerCase().contains(filter)
					|| p.getSurname().toLowerCase().contains(filter)
					|| p.getHighestDiploma().toLowerCase().equals(filter)
					|| p.getJob().toLowerCase().equals(filter)) || p.getOpinion().equals(filter);
//					|| p.getRole().toString().toLowerCase().equals(filter);
		}
		return filterMatch;
	}

	/**
	 * Remove accents
	 *
	 * @param String
	 * @return String without accents 
	 * @author Thomas Decamp
	 */
	private String noAccent(String s) {
			String strTemp = Normalizer.normalize(s, Normalizer.Form.NFD);
			Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
			return pattern.matcher(strTemp).replaceAll("");
	}

	/**
	 * @return all the persons
	 */
	public List<Person> getPersons() {
		return this.persons;
	}

	@Override
	public boolean isEmpty() {
		return this.persons.isEmpty();
	}

	@Override
	public Iterator<Person> iterator() {
		return this.persons.iterator();
	}

	@Override
	public boolean remove(final Object o) {
		return this.persons.remove(o);
	}

	@Override
	public boolean removeAll(final Collection<?> c) {
		return this.persons.removeAll(c);
	}

	@Override
	public boolean retainAll(final Collection<?> c) {
		return this.persons.retainAll(c);
	}

	@Override
	public int size() {
		return this.persons.size();
	}

	@Override
	public Object[] toArray() {
		return this.persons.toArray();
	}

	@Override
	public <T> T[] toArray(final T[] a) {
		return this.persons.toArray(a);
	}

}
