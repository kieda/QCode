package org.zkieda.qcode.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.math3.util.FastMath;
import org.zkieda.util.Requires;

/**
 * TODO make this actually standard and not confusing.
 * Have some sort of immutable data structure
 * 
 * Then have an EvictionManager which merges SelectList with the EvictionPolicy
 *  
 * @author zkieda
 */
public class BasicSelectList<T> implements SelectList<T>{
    private boolean[] selected;
    private final List<T> data;
    private final int minSize;
    
    private static final int MIN_SIZE = 8;
    private static final int DEFAULT_SIZE = 8;
    
    /**
     * used if we should sort the list when we reset()
     */
    private final Comparator<T> sortFn;
    
    public BasicSelectList(List<T> data) {
        this(data, null, DEFAULT_SIZE, MIN_SIZE);
    }
    
    public BasicSelectList(List<T> data, Comparator<T> sortFn) {
        this(data, sortFn, DEFAULT_SIZE, MIN_SIZE);
    }
    
    public BasicSelectList(List<T> data, Comparator<T> sortFn, int defaultSize, int minSize) {
        Requires.that(minSize>=0 && defaultSize >= 0);
        Requires.nonNull(data);
        this.data = data;
        this.minSize = minSize;
        this.selected = new boolean[FastMath.max(defaultSize, minSize)];
        this.sortFn = sortFn;
    }
    
    @Override
    public void reset() {
        int si = data.size();
        //resize selected if necessary. Notice newly allocated arrays will be 
        //filled with false.
        if(si >= selected.length){
            //double size
            selected = new boolean[si * 2];
        } else if(4*si <= selected.length && selected.length > minSize){
            //halve size
            
            selected = new boolean[
                   FastMath.max(minSize, selected.length / 2)
               ];
        } else {
            //fill section with false
            Arrays.fill(selected, 0, data.size(), false);
        }
        
        if(sortFn != null) {
            Collections.sort(data, sortFn);
        }
    }
    
    @Override
    public int length() {
        return data.size();
    }
    
    @Override
    public void selectRange(int idx, int len) {
        Requires.inBounds(idx, len, length());
        
        len += idx;
        for(; idx < len; idx++){
            selected[idx] = true;
        }
    }
    
    @Override
    public void select(int idx) {
        Requires.index(idx, length());
        selected[idx] = true;
    }
    
    @Override
    public void deselect(int idx) {
        Requires.index(idx, length());
        selected[idx] = false;
    }
    
    @Override
    public boolean isSelected(int idx) {
        Requires.index(idx, length());
        return selected[idx];
    }
    
    @Override
    public T get(int idx) {
        return data.get(idx);
    }
}
