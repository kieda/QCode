package com.salesforce.zkieda.qcode.server;

import com.salesforce.zkieda.util.SelectList;

/**
 * represents an eviction policy for removing threads when we attempt to 
 * make a new one
 *
 * @author zkieda
 * @since 190
 * @version 1.0
 */
public interface EvictionPolicy {
    
    /**
     * @param threads the threads that are currently running. Selected threads will be 
     * terminated.
     * The thread info contains information useful for stopping threads, like we could remove 
     * lower priority or ones that have been around for too long
     */
    public void evict(SelectList<ThreadInfo> threads);
}
