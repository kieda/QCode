package com.salesforce.zkieda.qcode.server;

import java.util.*;

import com.salesforce.zkieda.util.SelectList;

//import org.apache.commons.math3.util.FastMath;

/**  
 * manages and starts threads
 * 
 * @author zkieda
 * @since 190
 * @version 0.9
 */
public class ThreadEvictionManager {
    private List<ThTuple> threads;
    private boolean[] selected;
    private EvictionPolicy ep;
    
    
    
    /**
     * basic tuple for our thread, thread info.
     *
     * @author zkieda
     * @since 190
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
        
        @Override
        public int hashCode() {
            return info.hashCode();
        }
        
        private void stop(){
            try{
                thread.stop();
            } catch(SecurityException e){
                e.printStackTrace();
            }
            //help out gc
            thread = null;
            info = null;
        }
    }    

    private static final int MIN_SIZE = 8;
    private static final int DEFAULT_SIZE = 8;
    /**
     * creates a thread eviction manager with the given policy
     * @param ep our policy
     */
    public ThreadEvictionManager(EvictionPolicy ep) {
        threads = new ArrayList<>(); 
        selected = new boolean[DEFAULT_SIZE];
        this.ep = ep;
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
    
    private static final Comparator<ThTuple> NATURAL_COMPARE=new Comparator<ThTuple>() {
        @Override
        public int compare(ThTuple o1, ThTuple o2) {
            return o1.hashCode() - o2.hashCode();
        }};
    
    /**
     * evicts and stops any threads as necessary
     */
    private void checkEvict(){
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
        } else{
            //fill section with false
            Arrays.fill(selected, 0, threads.size(), false);
        }
        
        //sort collections from oldest to youngest
        Collections.sort(threads, NATURAL_COMPARE);
        
        //evict
        ep.evict(threadSelectList);
        
        //remove and stop threads
        for(int i = threads.size()-1; i >= 0; i--){
            if(selected[i]){
                threads.get(i).stop();
                threads.remove(i);
            }
        }
    }
    
    /**
     * the select list for the jobs that are running
     *
     * @author zkieda
     * @since 190
     */
    private final SelectList<ThreadInfo> threadSelectList = new SelectList<ThreadInfo>(){
        @Override
        public int length() {
            return threads.size();
        }
        @Override
        public void selectRange(int idx, int len) {
            if(idx >= threads.size() || idx+len >= threads.size() || (idx|len)<0 )
                throw new IndexOutOfBoundsException();
            len += idx;
            for(; idx < len; idx++){
                selected[idx] = true;
            }
        }
        @Override
        public void select(int idx) {
            if(idx >= threads.size())
                throw new IndexOutOfBoundsException();
            selected[idx] = true;
        }
        @Override
        public void deselect(int idx) {
            if(idx >= threads.size())
                throw new IndexOutOfBoundsException();
            selected[idx] = false;
        }
        @Override
        public boolean isSelected(int idx) {
            if(idx >= threads.size())
                throw new IndexOutOfBoundsException();
            return selected[idx];
        }
        @Override
        public ThreadInfo get(int idx) {
            return threads.get(idx).info;
        }
    };
}
