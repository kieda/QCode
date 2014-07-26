package org.zkieda.qcode.util;

import java.lang.Thread.State;

/**
 * contains info about a thread
 *
 * @author zkieda
 */
public class ThreadInfo {
    private final long startTime;
    private final Thread internal;
    private final long expectedTimeElaped; 
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
}