package com.salesforce.zkieda.qcode.server;

import java.lang.Thread.State;

/**
 * contains info about a thread
 *
 * @author zkieda
 * @since 190
 * @version 0.9
 */
public class ThreadInfo {
    private long startTime;
    private Thread internal;
    private long expectedTimeElaped; 
    public ThreadInfo(long startTime, long expectedTimeElapsed, Thread thr) {
        this.startTime = startTime;
        this.internal = thr;
        this.expectedTimeElaped = expectedTimeElapsed;
    }
    public int getPriority(){
        return internal.getPriority();
    }
    public State getState(){
        return internal.getState();
    }
    public long getStartTime() {
        return startTime;
    }
    public long getElapsedTime() {
        return System.currentTimeMillis() - startTime;
    }
    public long getExpectedTimeElapsed() {
        return expectedTimeElaped;
    }
    @Override
    public int hashCode() {
        return (int)getElapsedTime();
    }
}