package org.zkieda.qcode.util;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


/**
 * This class is an output stream used to send information to multiple clients.
 * 
 * @temp
 * @wanna
 * {concurrent, threaded} one to many output stream 
 * 
 * @author zkieda
 */
public class MultiBufferedOutputStream extends OutputStream {
    private final List<OutputStreamInternal> os_internal;
    private final ExecutorService printExecutor = Executors.newFixedThreadPool(4); 
    
    //thread timeout
    private final long timeOut;
    private final TimeUnit timeOutUnit;
    
    //time we should wait between flushing
    private static final int WAIT_TIME = 20;
    
    /**
     * This output stream is used as a single incoming stream, and multiple 
     * clients are used as outgoing streams. 
     * 
     * Internally, we buffer each of the outgoing streams. When the buffer is 
     * full, we add a  runnable to the executor service which flushes the buffer 
     * to the underlying outgoing stream. 
     *
     * @author zkieda
     */
    private class OutputStreamInternal extends FilterOutputStream{
        
        FlushRunnable run;
        
        /**
         * The internal buffer where data is stored.
         */
        byte buf[];

        /**
         * The number of valid bytes in the buffer. This value is always
         * in the range <tt>0</tt> through <tt>buf.length</tt>; elements
         * <tt>buf[0]</tt> through <tt>buf[count-1]</tt> contain valid
         * byte data.
         */
        int count;

        /**
         * our output stream
         */
        OutputStream out;
        
        /**
         * Creates a new buffered output stream to write data to the
         * specified underlying output stream.
         *
         * @param   out   the underlying output stream.
         */
        OutputStreamInternal(OutputStream out) {
            this(out, 8192);
        }

        /**
         * Creates a new buffered output stream to write data to the
         * specified underlying output stream with the specified buffer
         * size.
         *
         * @param   out    the underlying output stream.
         * @param   size   the buffer size.
         * @exception IllegalArgumentException if size &lt;= 0.
         */
        OutputStreamInternal(OutputStream out, int size) {
            super(out);
            this.out = out;
            if (size <= 0) {
                throw new IllegalArgumentException("Buffer size <= 0");
            }
            buf = new byte[size];
            run = new FlushRunnable();
        }

        /** Flush the internal buffer */
        private synchronized void flushBuffer() throws IOException {
        	
            try{            	
                Future fu = printExecutor.submit(run);
                
                if(timeOutUnit==null) fu.get();
                else fu.get(timeOut, timeOutUnit);
                
                if(run.getException() != null)
                    throw run.getException();
            }catch(TimeoutException | InterruptedException | ExecutionException e){
                //close our output stream
                out.close();
                
                //reset
                count = 0;
                
                //the client has timed out, or there is a problem sending the 
                //message
                throw new IOException("Problem sending buffer to client output stream", e);
            }
        }

        /**
         * Writes the specified byte to this buffered output stream.
         *
         * @param      b   the byte to be written.
         * @exception  IOException  if an I/O error occurs.
         */
        @Override
        public synchronized void write(int b) throws IOException {
            if (count >= buf.length) {
                flushBuffer();
            }
            buf[count++] = (byte)b;
        }

        /**
         * Writes <code>len</code> bytes from the specified byte array
         * starting at offset <code>off</code> to this buffered output stream.
         *
         * <p> Ordinarily this method stores bytes from the given array into this
         * stream's buffer, flushing the buffer to the underlying output stream as
         * needed.  If the requested length is at least as large as this stream's
         * buffer, however, then this method will flush the buffer and write the
         * bytes directly to the underlying output stream.  Thus redundant
         * <code>BufferedOutputStream</code>s will not copy data unnecessarily.
         *
         * @param      b     the data.
         * @param      off   the start offset in the data.
         * @param      len   the number of bytes to write.
         * @exception  IOException  if an I/O error occurs.
         */
        @Override
        public synchronized void write(byte b[], int off, int len) throws IOException {
            if (len >= buf.length) {
                /* If the request length exceeds the size of the output buffer,
                   flush the output buffer and then write the data directly.
                   In this way buffered streams will cascade harmlessly. */
                flushBuffer();
                out.write(b, off, len);
                return;
            }
            if (len > buf.length - count) {
                flushBuffer();
            }
            System.arraycopy(b, off, buf, count, len);
            count += len;
        }

        /** 
         * if we have many rapid succession calls we can slow down the system
         * only publicly / manually be able to flush every {@code WAIT_TIME}
         * milliseconds
         */
        private final DtHandler timeBetweenManualFlushes = new DtHandler(){
        	@Override
        	public boolean allowDt(long amt) {
        		return amt >= WAIT_TIME;
        	}
        };
        
        /**
         * Flushes this buffered output stream. This forces any buffered
         * output bytes to be written out to the underlying output stream.
         *
         * @exception  IOException  if an I/O error occurs.
         * @see        java.io.FilterOutputStream#out
         */
        @Override
        public synchronized void flush() throws IOException {
        	if(timeBetweenManualFlushes.poll()){
	            flushBuffer();
	            out.flush();
        	}
        }
        
        class FlushRunnable implements Runnable {
            private IOException exception;
            public IOException getException() {
                return exception;
            }
            
            @Override
            public void run() {
                try {
                    if (count > 0) {
                        out.write(buf, 0, count);
                        count = 0;
                    }
                    this.exception = null;
                } catch (IOException e) {
                    this.exception = e;
                } 
            }
        }
    }
    
    /**
     * adds an output stream that we serve to. This class buffers its output, and when the 
     * time comes we start a few threads that clears the buffers to the connected clients.
     *  
     * @param client the client output stream that we will also update
     */
    public void addOutputStream(OutputStream client){
        //actually just decorate the client with a buffer. When the buffer is flushed 
        //we send a runnable to flush out
        os_internal.add(
            new OutputStreamInternal(client)    
        );
    }
    /**
     * instantiates with a default timeout of 500 milliseconds
     */
    public MultiBufferedOutputStream() {
        this(500, TimeUnit.MILLISECONDS);
    }
    
    /**
     * creates a multi buffered output stream with the given timeout 
     * for thread<br/><br/> 
     * 
     * if {@code timeOutUnit==null}, we have no timeout.<br/><br/>
     * 
     * for example, if we have a timeout of 1 second, an output stream that is 
     * taking longer than one second to flush the data will be closed.<br/><br/>
     */
    public MultiBufferedOutputStream(long timeOut, TimeUnit timeOutUnit) {
        os_internal = Collections.synchronizedList(new ArrayList<OutputStreamInternal>());
        this.timeOut = timeOut;
        this.timeOutUnit = timeOutUnit;
    }
    
    /**
     * @throws IOException when writing to one of our underlying OutputStreams 
     * fails. When this occurs, we do not immediately throw the exception. 
     * Instead, we bundle all of the exceptions together as suppressed  
     */
    @Override
    public void write(int b) throws IOException {
        IOException exception = null;
        synchronized(os_internal){
	        for (OutputStreamInternal os : os_internal) {
	            try{
	                os.write(b);
	            } catch(IOException e){
	                if(exception == null){
	                    exception = new IOException("Exception while printing to OutputStream " + os.out);
	                }
	                exception.addSuppressed(e);
	            }
	        }
        }
        if(exception != null) throw exception;
    }
    
    @Override
    public void flush() throws IOException {
    	IOException exception = null;
        synchronized(os_internal){
	        for (OutputStreamInternal os : os_internal) {
	            try{
	                os.flush();
	            } catch(IOException e){
	                if(exception == null){
	                    exception = new IOException("Exception while printing to OutputStream " + os.out);
	                }
	                exception.addSuppressed(e);
	            }
	        }
        }
        if(exception != null) throw exception;
    }
}

/**
 * basic test for MultiBufferedOutputStream
 * @author zkieda
 */
class MultiBufferedOutputStreamTest{
	public static void main(String[] args) throws Exception {
        final ServerSocket ss = new ServerSocket(1234);
        final MultiBufferedOutputStream mbs
             = new MultiBufferedOutputStream();
//        final Object lock = new Object();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        Socket client = ss.accept();
                        mbs.addOutputStream(client.getOutputStream());
                    } catch (Exception e) {}
                }
            }
        });
        t.start();
        
        new Thread(
            new Runnable() {
                @Override
                public void run() {
                    PrintStream ps = new PrintStream(mbs);
                    try {
                        int i = 0;
                        while (true) {
                            ps.println("hello " + (i++));
                            Thread.sleep(1000);
                        }
                    } catch (Exception e) {}
                }
            }
        ).start();
        
        
        Socket socket = new Socket(InetAddress.getLocalHost(), 1234);
        Scanner s = new Scanner(socket.getInputStream());
        
        while(s.hasNextLine()){
            System.out.println(s.nextLine());
        }
    }
}
