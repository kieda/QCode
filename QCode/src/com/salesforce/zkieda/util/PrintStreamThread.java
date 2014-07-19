package com.salesforce.zkieda.util;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Constructs a list of print streams and input streams that will be 
 * redirected when this thread runs. The redirected io will be local 
 * to this thread only. <br><br>  

 * Example usage :
 * <pre>
 * {@code
 * //replace the system's IO with one that can have different 
 * //io for each thread
 * ThreadIO.replaceSystemIO();
 * 
 * //This new thread has different IO
 * new PrintStreamThread(new Runnable(){
 *     public void run(){ 
 *        System.out.println("hello err!"); 
 *        System.err.println("hello out!"); 
 *     }
 * }).dup2(System.out, ThreadIO.ERR)
 *   .dup2(System.err, ThreadIO.OUT)
 *   .start();
 * 
 * System.out.println("bye out!");
 * System.err.println("bye err!");
 * }
 * </pre>
 * <br>
 * The above code should print {@code hello err!} to standard err, 
 * and {@code hello out!} to standard out. It should also print 
 * {@code bye out!} to standard out and {@code bye err!} to standard 
 * err.<br><br>
 * 
 * Note that if you want to redirect input in a cyclic manner, use 
 * {@link ThreadIO#ERR}, {@link ThreadIO#OUT}, or 
 * {@link ThreadIO#IN}. Otherwise, we could redirect System.out
 * to print to System.err, and System.err to print to System.out, 
 * resulting in an infinite loop. 
 *  
 * @author zkieda
 * @see ThreadIO
 */
public class PrintStreamThread extends Thread {
    //what we make system out, in, and err dup 2
    private final Map<PrintStream, PrintStream> dupPrintMap 
        = new HashMap<>(4);
    private final Map<InputStream, InputStream> dupInputMap 
        = new HashMap<>(4);
    

    public PrintStreamThread() {
    }

    public PrintStreamThread(Runnable target) {
        super(target);I_want_to
    }
    public PrintStreamThread(String name) {
        super(name);
    }
    public PrintStreamThread(ThreadGroup group, Runnable target) {
        super(group, target);
    }
    public PrintStreamThread(ThreadGroup group, String name) {
        super(group, name);
    }
    public PrintStreamThread(Runnable target, String name) {
        super(target, name);
    }

    public PrintStreamThread(ThreadGroup group, Runnable target, String name) {
        super(group, target, name);
    }
    
    public PrintStreamThread(ThreadGroup group, Runnable target, String name, long stackSize) {
        super(group, target, name, stackSize);
    }
    
    /**
     * When this thread runs, the src print stream will be redirected to the 
     * target print stream 
     * 
     * @param src note that src must be instance of {@link ThreadIO.ThreadPrintStream}, otherwise src is not redirected
     * @param target target destination. If {@code null}, this method does nothing
     * @return this object
     */
    public PrintStreamThread dup2(PrintStream src, PrintStream target){
        dupPrintMap.put(src, target);
        return this;
    }
    
    /**
     * When this thread runs, the src input stream will be redirected to the 
     * target input stream 
     * 
     * @param src note that src must be instance of {@link ThreadIO.ThreadInputStream}, otherwise src is not redirected
     * @param target target destination. If {@code null}, this method does nothing
     * @return this object
     */
    public PrintStreamThread dup2(InputStream src, InputStream target){
        dupInputMap.put(src, target);
        
        return this;
    }
    
    
    /**
     * If we are replacing System.out, System.in, or System.err : 
     * before we call the run() method on this thread or start this thread, ensure that 
     * {@link ThreadIO#replaceSystemOut()} has been called, and standard out 
     * has not been replaced since then. If this has not been called, we use 
     * System.out in place.
     */
    @Override
    public final void run() {
        //insert things we should print
        for (Iterator<Entry<PrintStream, PrintStream>> it = dupPrintMap.entrySet().iterator(); it.hasNext();) {
            Entry<PrintStream, PrintStream> e = it.next();
            PrintStream k = e.getKey();
            PrintStream v = e.getValue();
            if(v != null && k instanceof ThreadIO.ThreadPrintStream){
                ThreadIO.dup2(k, v);
            }
        }
        //insert things we should change the input stream of
        for (Iterator<Entry<InputStream, InputStream>> it = dupInputMap.entrySet().iterator(); it.hasNext();) {
            Entry<InputStream, InputStream> e = it.next();
            InputStream k = e.getKey();
            InputStream v = e.getValue();
            if(v != null && k instanceof ThreadIO.ThreadInputStream){
                ThreadIO.dup2(k, v);
            }
        }
        
        //run the thread
        super.run();
    }
    
    /**
     * Equivalent to {@link Thread#start()}, but returns this instance of the thread. For ease of access - for example 
     * we can call {@code new PrintStreamThread().go().join();} 
     * 
     * @return this object
     */
    public PrintStreamThread go(){
        super.start();
        return this;
    }
}