package com.salesforce.zkieda.util;

import java.io.*;
import java.net.URISyntaxException;
import java.util.concurrent.atomic.AtomicBoolean;


/** 
 * This is a modified version of 
 * http://maiaco.com/articles/java/threadOut.php
 * to allow more extensibility
 * 
 * A ThreadPrintStream replaces the normal System.out and ensures
 * that output to System.out goes to a different PrintStream for
 * each thread.  It does this by using ThreadLocal to maintain a
 * PrintStream for each thread. 
 * 
 * Acts as a singleton
 * 
 * @see PrintStreamThread
 */
public class ThreadIO  {
    //atomic since we expect multiple threads using this class
    private static final AtomicBoolean initialized = new AtomicBoolean(false);
    //values of System.in, System.out, and System.err when thread io is initially called
    private static PrintStream consoleOut;
    private static PrintStream consoleErr;
    private static InputStream consoleIn;
    {
        new File("").get
    }
    //discards all input
    private static final OutputStream NULL_OUTPUT_STREAM = new OutputStream() {
        @Override public void write(int b) throws IOException {}};
    
    //these are just markers, so we know we should replace them when the time comes
    public static final PrintStream OUT = new PrintStream(NULL_OUTPUT_STREAM, false);
    public static final PrintStream ERR = new PrintStream(NULL_OUTPUT_STREAM, false);
    public static final InputStream IN = new InputStream(){
        @Override public int read() throws IOException {return 0;}};
    
    
    
    public static class ThreadPrintStream
        extends PrintStream {
        /** Thread specific storage to hold a PrintStream for each thread */
        private ThreadLocal<PrintStream> out;

        private ThreadPrintStream(final PrintStream initial) {
            super(new ByteArrayOutputStream(0));
            out = new ThreadLocal<PrintStream>(){
                @Override
                protected PrintStream initialValue() {
                    return initial;
                }
            };
            setThreadOut(initial);
        }

        /** Sets the PrintStream for the currently executing thread. */
        public void setThreadOut(PrintStream out) {
            this.out.set(out);
        }

        /** Returns the PrintStream for the currently executing thread. */
        public PrintStream getThreadOut() {
            return this.out.get();
        }

        @Override public boolean checkError() {
            return getThreadOut().checkError();
        }

        @Override public void write(byte[] buf, int off, int len) {
            getThreadOut().write(buf, off, len);
        }

        @Override public void write(int b) { getThreadOut().write(b); }
        @Override public void flush() { getThreadOut().flush(); }
        @Override public void close() { getThreadOut().close(); }
    }

    
    public static class ThreadInputStream
        extends InputStream{
        /** Thread specific storage to hold a PrintStream for each thread */
        private ThreadLocal<InputStream> in;

        public ThreadInputStream(final InputStream initial) {
            // TODO Auto-generated constructor stub
            in = new ThreadLocal<InputStream>(){
                @Override
                protected InputStream initialValue() {
                    return initial;
                }
            };
            setThreadIn(initial);
        }
        
        /** Sets the InputStream for the currently executing thread. */
        public void setThreadIn(InputStream in) {
            this.in.set(in);
        }

        /** Returns the InputStream for the currently executing thread. */
        public InputStream getThreadIn() {
            return this.in.get();
        }
        
        @Override
        public int read() throws IOException {
            return getThreadIn().read();
        }
    }
    
    
    /**
     * initializer. Finds what we assume are non thread-local, standard outputs and inputs
     */
    private static void init() {
        if(initialized.compareAndSet(false, true)){
            // Save the existing System.out
            consoleOut = System.out;
            consoleErr = System.err;
            consoleIn = System.in;
        }
    }
    
    /**
     * replaces the system in, out, err
     */
    public static void replaceSystemIO() {
        replaceSystemErr();
        replaceSystemOut();
        replaceSystemIn();
    }
    
    /** Changes System.out to a ThreadPrintStream which will
     * send output to a separate file for each thread. */
    public static void replaceSystemOut() {
        init();
        if(!(System.out instanceof ThreadPrintStream)){
            ThreadPrintStream threadOut = new ThreadPrintStream(consoleOut);
            System.setOut(threadOut);
        }
    }
    
    /**
     * replaces system in with a thread local input stream
     */
    public static void replaceSystemIn() {
        init();
        if(!(System.in instanceof ThreadInputStream)){
            ThreadInputStream threadIn = new ThreadInputStream(consoleIn);
            System.setIn(threadIn);
        }
    }
    
    /**
     * replaces system err with a thread local printer
     */
    public static void replaceSystemErr() {
        init();
        if(!(System.err instanceof ThreadPrintStream)){
            ThreadPrintStream threadErr = new ThreadPrintStream(consoleErr);
            System.setErr(threadErr);
        }
    }
    
    /**
     * replaces system out with a thread local printer, then sets the thread 
     * local output on this thread to print to {@param out}
     * @param out the printer we will print to 
     */
    public static void replaceSystemOut(PrintStream out) {
        replaceSystemOut();
        dup2(System.out, out);
    }
    /**
     * replaces system err with a thread local printer, then sets the thread 
     * local output on this thread to print to {@param err}
     * @param err the printer we will print to 
     */
    public static void replaceSystemErr(PrintStream err) {
        replaceSystemErr();
        dup2(System.err, err);
    }
    /**
     * replaces system out with a thread local input stream, then sets 
     * the thread local input on this thread to {@param in}
     * @param in the input stream 
     */
    public static void replaceSystemIn(InputStream in) {
        replaceSystemIn();
        dup2(System.in, in);
    }
    
    /**
     * Sets the printstream for an individual thread
     * 
     * @param threadOut a PrintStream that is an instance of ThreadPrintStream 
     * @param newTarget the new target that the PrintStream will print to. If this is 
     * {@link ThreadIO#ERR} or {@link ThreadIO#OUT} we will use the 
     * original console's output. This can be used to avoid cycles in printing  
     * @throws IllegalArgumentException if threadOut is not instance of ThreadPrintStream
     */
    public static void dup2(PrintStream threadOut, PrintStream newTarget){
        //redirect to consoleOut or consoleErr as necessary 
        if(newTarget == OUT){
            newTarget = consoleOut;
        } else if(newTarget == ERR){
            newTarget = consoleErr;
        }
        if(threadOut instanceof ThreadPrintStream){
            ((ThreadPrintStream)threadOut).setThreadOut(newTarget);
        } else 
            throw new IllegalArgumentException(
                String.format("Expected a ThreadPrintStream, got %s of class %s", 
                        String.valueOf(threadOut),
                        threadOut == null ? "null" : threadOut.getClass()
                    ));
    }
    
    /**
     * sets the inputstream for an individual thread
     * @param threadIn a InputStream that is an instance of ThreadInputStream
     * @param newTarget the new target that the inputstream will read from on this 
     * {@link Thread} If this is {@link ThreadIO#IN} we will use the original 
     * console's input. This can be used to avoid cycles 
     * @throws IllegalArgumentException if threadIn is not instance of ThreadInputStream
     */
    public static void dup2(InputStream threadIn, InputStream newTarget){
        if(newTarget == IN){
            newTarget = consoleIn;
        }
        
        if (threadIn instanceof ThreadInputStream) {
            ((ThreadInputStream)threadIn).setThreadIn(newTarget);
        } else 
            throw new IllegalArgumentException(
                    String.format("Expected a ThreadInputStream, got %s of class %s", 
                            String.valueOf(threadIn),
                            threadIn == null ? "null" : threadIn.getClass()
                        ));
    }
    
}

/**
 * A test to ensure the correctness of this stuff
 *  
 * @author zkieda
 * @since 180
 */
class ThreadIOTest {
    public static void main(String[] args)
        throws URISyntaxException, InterruptedException{

        ThreadIO.replaceSystemOut();
        System.out.println("Regular output");


        Thread t ;
        (t = new Thread(
            new Runnable() {
                @Override
                public void run() {
                    //dup2 sys err
                    ThreadIO.dup2(System.out, System.err);
                    System.out.println("Sys out printing to sys err");
                }
            }
        )).start();
        t.join();
        System.out.println("Sys out is not changed");
        System.err.println("Sys err is not changed");
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("asdf");
                System.out.println("sdf");
            }
        }){
            @Override
            public void run() {
                ThreadIO.dup2(System.out, System.err);
                super.run();
            }
        }. start();
        
        new Thread(new Runnable() {
            
            @Override
            public void run() {
                System.out.println("Sys out prints regularly");
                System.err.println("Sys err prints regularly");
                ThreadIO.replaceSystemErr();
                ThreadIO.dup2(System.err, System.out);
                System.out.println("Sys out test");
                System.err.println("Sys err test");
            }
        }).start();
    }
}