package org.zkieda.qcode.util;

/**
 * represents an eviction policy for removing threads when we attempt to 
 * make a new one
 *
 * @author zkieda
 * @version 1.0
 */
public interface EvictionPolicy<T> {
    
    /**
     * @param threads the threads that are currently running. Selected threads will be 
     * terminated.
     * The thread info contains information useful for stopping threads, like we could remove 
     * lower priority or ones that have been around for too long
     */
    public void evict(SelectList<T> threads);
}
