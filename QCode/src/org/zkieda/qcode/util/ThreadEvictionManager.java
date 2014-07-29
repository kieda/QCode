package org.zkieda.qcode.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.zkieda.util.Requires;

/**  
 * manages and starts threads
 * 
 * TODO make EvictionManager.java which is abstract. 
 *      takes EvictionPolicy<ThreadInfo> (what else??)
 *      
 * @author zkieda
 * @version 0.9
 */
public class ThreadEvictionManager {
    private final List<ThTuple> threads;
    private final EvictionPolicy<ThreadInfo> evictionPolicy;
    private boolean[] selected;
    
    private static final long THREAD_JOIN_TIME = 200;
    private static final int MIN_SIZE = 8;
    private static final int DEFAULT_SIZE = 8;
    private static final Comparator<ThTuple> SORT_OLDEST = Comparator.comparingLong(t -> t.info.getStartTime());
    
    /**
     * basic tuple for our thread, thread info.
     *
     * @author zkieda
     */
    private static class ThTuple{
        private ThreadInfo info;
        private Thread thread;
        private ThTuple(
                ThreadInfo info,
                Thread thread) {
            this.info = info;
            this.thread = thread;
        }
        
        private void stop(){
            try{
                //interrupt to attempt stopping it
                thread.interrupt();
                
                try {
                    thread.join(THREAD_JOIN_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                
                //if it's not dead yet destroy it manually
                if(thread.isAlive())
                    thread.stop();
            } catch(SecurityException e){
                e.printStackTrace();
            }
            
            //destroy info
            thread = null;
            info = null;
        }
    }    

    /**
     * creates a thread eviction manager with the given policy
     * @param ep our policy
     */
    public ThreadEvictionManager(EvictionPolicy<ThreadInfo> ep) {
        threads = new ArrayList<>(); 
        this.evictionPolicy = ep;
        selected = new boolean[DEFAULT_SIZE];
    }
    
    /**
     * adds a thread and starts it. Evicts and stops any threads if necessary
     * 
     * @param t the thread we are adding
     * @param timeout estimated time for the thread to complete
     */
    public void addThread(Thread t, long timeout){
        threads.add(new ThTuple(
                new ThreadInfo(System.currentTimeMillis(),timeout,t),
                t
            ));
        
        t.start();
        checkEvict();
    }
    
    /**
     * evicts and stops any threads as necessary
     */
    private void checkEvict(){
        threadSelectList.reset();
        
        //evict
        evictionPolicy.evict(threadSelectList);
        
        //remove and stop threads
        for(int i = threads.size()-1; i >= 0; i--){
            if(threadSelectList.isSelected(i)) {
                threads.get(i).stop();
                threads.remove(i);
            }
        }
    }
    
    private final SelectList<ThreadInfo> threadSelectList = new SelectList<ThreadInfo>(){
        @Override
        public void reset() {
            int si = threads.size();
            //resize selected if necessary. Notice newly allocated arrays will be 
            //filled with false.
            if(si >= selected.length){
                //double size
                selected = new boolean[si * 2];
            } else if(4*si <= selected.length && selected.length > MIN_SIZE){
                //halve size
                
                selected = new boolean[
                       Math.max(MIN_SIZE, selected.length / 2)
                   ];
            } else {
                //fill section with false
                Arrays.fill(selected, 0, threads.size(), false);
            }
            
            Collections.sort(threads, SORT_OLDEST);
        }
        
        @Override
        public int length() {
            return threads.size();
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
        public ThreadInfo get(int idx) {
            return threads.get(idx).info;
        }
    };
}
