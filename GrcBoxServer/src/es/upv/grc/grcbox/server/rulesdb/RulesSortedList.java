package es.upv.grc.grcbox.server.rulesdb;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import es.upv.grc.grcbox.common.*;
/**
 * The Class RulesSortedList.
 */
public class RulesSortedList {
	
	/** The sorted set. */
	private SortedSet<GrcBoxRule> sortedSet = new TreeSet<GrcBoxRule>(new GrcBoxRuleComp());

	/**
	 * The Comparator
	 * Sorts rules by priority
	 * More restrictive rules have priority over simpler rules
	 * If two rules have the same size, the older one has priority
	 */
	private class GrcBoxRuleComp implements Comparator<GrcBoxRule>{
			
			/* (non-Javadoc)
			 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
			 */
			@Override
			public int compare(GrcBoxRule r1, GrcBoxRule r2){
				int l1 = r1.ipTablesSize();
				int l2 = r2.ipTablesSize();
				if(l1 < l2){
					return 1;
				}
				else if(l1 > l2){
					return -1;
				}
				else {
					return r1.getId() < r2.getId() ? -1:r1.getId() == r2.getId() ? 0 : 1;
				}
			}
	}
	
	/**
	 * Adds the element
	 *
	 * @param e the element
	 * @return true, if successful
	 * @see java.util.Set#add(java.lang.Object)
	 */
	public boolean add(GrcBoxRule e) {
		return sortedSet.add(e);
	}
	
	/**
	 * Adds the all.
	 *
	 * @param c the c
	 * @return true, if successful
	 * @see java.util.Set#addAll(java.util.Collection)
	 */
	public boolean addAll(Collection<? extends GrcBoxRule> c) {
		return sortedSet.addAll(c);
	}

	/**
	 * Clear the list
	 *
	 * @see java.util.Set#clear()
	 */
	public void clear() {
		sortedSet.clear();
	}

	/**
	 * Contains.
	 *
	 * @param o the o
	 * @return true, if successful
	 * @see java.util.Set#contains(java.lang.Object)
	 */
	public boolean contains(GrcBoxRule o) {
		return sortedSet.contains(o);
	}

	/**
	 * Contains all.
	 *
	 * @param c the c
	 * @return true, if successful
	 * @see java.util.Set#containsAll(java.util.Collection)
	 */
	public boolean containsAll(Collection<?> c) {
		return sortedSet.containsAll(c);
	}

	/**
	 * First.
	 *
	 * @return the grc box rule
	 * @see java.util.SortedSet#first()
	 */
	public GrcBoxRule first() {
		return sortedSet.first();
	}

	/**
	 * Checks if is empty.
	 *
	 * @return true, if is empty
	 * @see java.util.Set#isEmpty()
	 */
	public boolean isEmpty() {
		return sortedSet.isEmpty();
	}

	/**
	 * Last.
	 *
	 * @return the grc box rule
	 * @see java.util.SortedSet#last()
	 */
	public GrcBoxRule last() {
		return sortedSet.last();
	}

	/**
	 * Removes the.
	 *
	 * @param o the o
	 * @return true, if successful
	 * @see java.util.Set#remove(java.lang.Object)
	 */
	public boolean remove(Object o) {
		return sortedSet.remove(o);
	}

	/**
	 * Removes the all.
	 *
	 * @param c the c
	 * @return true, if successful
	 * @see java.util.Set#removeAll(java.util.Collection)
	 */
	public boolean removeAll(Collection<?> c) {
		return sortedSet.removeAll(c);
	}

	/**
	 * Retain all.
	 *
	 * @param c the c
	 * @return true, if successful
	 * @see java.util.Set#retainAll(java.util.Collection)
	 */
	public boolean retainAll(Collection<?> c) {
		return sortedSet.retainAll(c);
	}

	/**
	 * Size.
	 *
	 * @return the int
	 * @see java.util.Set#size()
	 */
	public int size() {
		return sortedSet.size();
	}

	/**
	 * Sub set.
	 *
	 * @param fromElement the from element
	 * @param toElement the to element
	 * @return the sorted set
	 * @see java.util.SortedSet#subSet(java.lang.Object, java.lang.Object)
	 */
	public SortedSet<GrcBoxRule> subSet(GrcBoxRule fromElement,
			GrcBoxRule toElement) {
		return sortedSet.subSet(fromElement, toElement);
	}

	/**
	 * Tail set.
	 *
	 * @param fromElement the from element
	 * @return the sorted set
	 * @see java.util.SortedSet#tailSet(java.lang.Object)
	 */
	public SortedSet<GrcBoxRule> tailSet(GrcBoxRule fromElement) {
		return sortedSet.tailSet(fromElement);
	}
	
	/**
	 * Gets the sets the.
	 *
	 * @return the sets the
	 */
	public SortedSet<GrcBoxRule> getSet(){
		return new TreeSet<GrcBoxRule>(sortedSet);
	}
	
	/**
	 * Gets the sorted list.
	 *
	 * @return the sorted list
	 */
	public List<GrcBoxRule> getSortedList(){
		List<GrcBoxRule> list = new ArrayList<GrcBoxRule>(sortedSet);
		return list;
	}
}
