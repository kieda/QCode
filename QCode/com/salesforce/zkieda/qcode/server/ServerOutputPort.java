package com.salesforce.zkieda.qcode.server;

import java.io.*;

public class ServerOutputPort extends BufferedOutputStream{
    private final OutputStreamInternal os_internal;
    private static class OutputStreamInternal extends OutputStream{
        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            //delegate a runnable to writing each of these
            
        }
        @Override
        public void write(int b) throws IOException {
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
    }
    public ServerOutputPort() {
        super(new OutputStreamInternal());
        os_internal = (OutputStreamInternal)super.out;
        
    }

}
