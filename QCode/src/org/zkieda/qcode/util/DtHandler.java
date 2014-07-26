package org.zkieda.qcode.util;

/**
 * Keeps track of the time between events 
 *
 * @author zkieda
 */
public class DtHandler {
    //current time 
    private long lastTime;
    
    /**
     * instantiates a new DtHandler with a default policy (true when 
     * dt >= 200 ms)
     */
    public DtHandler(){
        updateTime();
    }
    
    /**
     * updates the current time
     */
    public void updateTime() {
        lastTime = System.currentTimeMillis();
    }
    
    /**
     * poll updates the time, and returns true if the difference in 
     * time since the last call is acceptable
     * 
     * @return true if the difference in time from the last call to poll to 
     * now is acceptable by {@link DtHandler#allowDt(long)} 
     */
    public boolean poll(){
        return allowDt(
            -lastTime + (lastTime = System.currentTimeMillis())
        );
    }

    /**
     * by default accepts if dt >= 200. Override this method to have 
     * different rules for accepting (or not accepting) a request 
     * given the change in time.
     * 
     * Example usage : if the user clicks a button multiple times and want a 
     * cooldown
     * 
     * To ensure certain actions are relatively close in time
     * 
     * In QCode we use the default behavior to prevent the code from being 
     * run too many times (occasionally the file watcher will report that 
     * more than one modification has occurred to a file)
     * 
     * 
     * @param amt the change in time between two events
     * @return true if {@code amt} has an acceptable difference in time
     */
    public boolean allowDt(long amt){
        return amt >= 200;
    }
}
