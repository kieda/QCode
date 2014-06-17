package com.salesforce.zkieda.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;


public class MultiBufferedOutputStream extends BufferedOutputStream {
    private final OutputStreamInternal os_internal;
    
    
    private static class OutputStreamInternal extends OutputStream{
        private ExecutorService exec;
        {
            exec = java.util.concurrent.Executors.newFixedThreadPool(2);
        }    
        private List<OutputStream> clients = new ArrayList<>();
        private List<Runnable> clientRunnables = new ArrayList<>();
        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            //delegate a runnable to writing each of these
            
            for (final Runnable r : clientRunnables) {
                exec.submit(r);
            }
            exec.awaitTermination(len, null);
            
        }
        @Override
        public void write(int b) throws IOException {
            
        }
        
        void addClient(OutputStream os){
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
        os_internal.addClient(client);
    }
    public MultiBufferedOutputStream() {
        super(new OutputStreamInternal());
        os_internal = (OutputStreamInternal)super.out;
        
    }
}
