package es.upv.grc.grcbox.server.rulesdb;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import es.upv.grc.grcbox.common.*;

public class RulesSortedList {
	private SortedSet<GrcBoxRule> sortedSet = new TreeSet<GrcBoxRule>(new GrcBoxRuleComp());

	private class GrcBoxRuleComp implements Comparator<GrcBoxRule>{
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
	 * @param e
	 * @return
	 * @see java.util.Set#add(java.lang.Object)
	 */
	public boolean add(GrcBoxRule e) {
		return sortedSet.add(e);
	}
	
	/**
	 * @param c
	 * @return
	 * @see java.util.Set#addAll(java.util.Collection)
	 */
	public boolean addAll(Collection<? extends GrcBoxRule> c) {
		return sortedSet.addAll(c);
	}

	/**
	 * 
	 * @see java.util.Set#clear()
	 */
	public void clear() {
		sortedSet.clear();
	}

	/**
	 * @param o
	 * @return
	 * @see java.util.Set#contains(java.lang.Object)
	 */
	public boolean contains(GrcBoxRule o) {
		return sortedSet.contains(o);
	}

	/**
	 * @param c
	 * @return
	 * @see java.util.Set#containsAll(java.util.Collection)
	 */
	public boolean containsAll(Collection<?> c) {
		return sortedSet.containsAll(c);
	}

	/**
	 * @return
	 * @see java.util.SortedSet#first()
	 */
	public GrcBoxRule first() {
		return sortedSet.first();
	}

	/**
	 * @return
	 * @see java.util.Set#isEmpty()
	 */
	public boolean isEmpty() {
		return sortedSet.isEmpty();
	}

	/**
	 * @return
	 * @see java.util.SortedSet#last()
	 */
	public GrcBoxRule last() {
		return sortedSet.last();
	}

	/**
	 * @param o
	 * @return
	 * @see java.util.Set#remove(java.lang.Object)
	 */
	public boolean remove(Object o) {
		return sortedSet.remove(o);
	}

	/**
	 * @param c
	 * @return
	 * @see java.util.Set#removeAll(java.util.Collection)
	 */
	public boolean removeAll(Collection<?> c) {
		return sortedSet.removeAll(c);
	}

	/**
	 * @param c
	 * @return
	 * @see java.util.Set#retainAll(java.util.Collection)
	 */
	public boolean retainAll(Collection<?> c) {
		return sortedSet.retainAll(c);
	}

	/**
	 * @return
	 * @see java.util.Set#size()
	 */
	public int size() {
		return sortedSet.size();
	}

	/**
	 * @param fromElement
	 * @param toElement
	 * @return
	 * @see java.util.SortedSet#subSet(java.lang.Object, java.lang.Object)
	 */
	public SortedSet<GrcBoxRule> subSet(GrcBoxRule fromElement,
			GrcBoxRule toElement) {
		return sortedSet.subSet(fromElement, toElement);
	}

	/**
	 * @param fromElement
	 * @return
	 * @see java.util.SortedSet#tailSet(java.lang.Object)
	 */
	public SortedSet<GrcBoxRule> tailSet(GrcBoxRule fromElement) {
		return sortedSet.tailSet(fromElement);
	}
	
	public SortedSet<GrcBoxRule> getSet(){
		return new TreeSet<GrcBoxRule>(sortedSet);
	}
	
	public List<GrcBoxRule> getSortedList(){
		List<GrcBoxRule> list = new ArrayList<GrcBoxRule>(sortedSet);
		return list;
	}
}
