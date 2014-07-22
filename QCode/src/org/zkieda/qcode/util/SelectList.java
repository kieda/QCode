package org.zkieda.qcode.util;

import org.zkieda.qcode.server.EvictionPolicy;

/**
 * selects a portion of a list. This is used in conjuntion with {@link EvictionPolicy} to 
 * evict parts of the list as necessary
 * 
 * SelectList should be ordered from the oldest thread to the youngest thread.
 * 
 * @author zkieda
 * @version 1.0
 */
public interface SelectList<T> {
    
    /** the length of the list */
    public int length();
    
    /** selects a range in the list */
    public void selectRange(int idx, int len);
    
    /** selects an item in the list */
    public void select(int idx);
    
    /** deselects an individual component */
    public void deselect(int idx);
    
    /** returns true iff ann individual component is selected */
    public boolean isSelected(int idx);
    
    /** returns the element at the given index */
    public T get(int idx);
}
