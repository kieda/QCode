package com.salesforce.zkieda.qcode.server;

import java.io.IOException;
import java.io.InputStream;


/**
 * Watches for changes in an input stream, and notifies an InputStreamListener 
 * when the changes have occurred 
 *
 * Call the run() method for the watcher to continuously listen for a change in the 
 * input stream, and pass the changes along a listener
 *
 * @author zkieda
 * @since 190
 * @version 0.9
 */
public abstract class InputStreamWatcher implements Runnable{
    
    /**
     * listener waiting for changes in the input stream
     */
    private InputStreamListener isl;
    

    /**
     * true if we should reload the input stream
     */
    private boolean reload = false;
    
    /**
     * the thread continues as long as cont is true
     */
    private volatile boolean cont = true;
    
    /**
     * polls event that have happened, and checks if any have made any 
     * modifications to our input stream, or where we get our input stream 
     * from. If a change has occurred, return true. Otherwise, return false.
     * @return true iff a change has occurred in the input stream from the 
     * last time we called changeOccurred()
     */
    public abstract boolean changeOccurred();
    
    /**
     * reloads the stream. For example, if we are reloading a file, we would 
     * go to the beginning of it 
     * @return the input stream which points to the head of the updated stream
     */
    public abstract InputStream reloadStream()
        throws IOException;

    /**
     * @param isl the listening input stream
     */
    public void setListener(InputStreamListener isl){
        this.isl = isl;
    }
    
    /**
     * @return our listener
     */
    public InputStreamListener getListener(){
        return isl;
    }
    
    /**
     * continuously checks for updates in our input stream, and passes them 
     * along to our listener if necessary
     */
    @Override
    public void run(){
      while(cont){
          try {
              reload = changeOccurred();
              if(reload) reload();
              
          } catch (IOException x) {
               x.printStackTrace();
          }
      }
    }
    
    /**
     * reloads the stream completely and causes the event to change
     * 
     * can be used to manually reload the stream and call the listener
     */
    public void reload() throws IOException{
        if(isl != null)
            isl.onChange(reloadStream());
    }
    
    /**
     * stops watching and exits the thread
     */
    public void stop(){
        cont = false;
    }
}