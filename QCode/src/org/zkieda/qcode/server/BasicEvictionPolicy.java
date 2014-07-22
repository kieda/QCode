package org.zkieda.qcode.server;

import org.zkieda.qcode.util.SelectList;

/**
 * Basic eviction policy. Has a thread cap which removes the oldest, and 
 * removes all threads that have lived too long
 *   
 * @author zkieda
 * @version 0.9
 */
public class BasicEvictionPolicy implements EvictionPolicy{
    //max threads allowed. int < 0 for infinite number of threads allowed 
    private final int maxThreads;
    //leeway for the timeout
    private final float leeway;
    
    
    /**
     * @param maxThreads the max threads we should have running. The oldest 
     * are terminated. If this parameter is < 0, no upper bound is specified
     * @param leeway the extra leeway for thread timeouts. For example, with 
     * leeway 2.0f a thread that has expected time of 20 millis will expire 
     * after 40 millis
     */
    public BasicEvictionPolicy(int maxThreads, float leeway) {
        assert leeway > 0;
        this.maxThreads = maxThreads;
        this.leeway = leeway;
    }
    
    /**
     * our eviction policy
     */
    @Override
    public void evict(SelectList<ThreadInfo> threads) {
        int remaining = threads.length();
        //destroy all old processes
        for (int i = 0; i < threads.length(); i++) {
            ThreadInfo ti = threads.get(i);
            
            if(ti.getElapsedTime()* leeway > ti.getExpectedTimeElapsed()){
                threads.select(i);
                remaining--;
            }
        }
        if(maxThreads < 0) return;
        
        //select for deletion till remaining = maxThreads
        //todo -- increase efficiency with selectRange
        for(int i = 0; remaining > maxThreads; ){
            if(!threads.isSelected(i)){
                threads.select(i);
                remaining--;
            }
            i++;
        }
    }
}
