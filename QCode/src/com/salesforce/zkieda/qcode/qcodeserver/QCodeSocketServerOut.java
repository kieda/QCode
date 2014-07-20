package com.salesforce.zkieda.qcode.qcodeserver;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;

import com.salesforce.zkieda.qcode.server.CompilationServerOut;
import com.salesforce.zkieda.qcode.server.ServerOutputPort;

/**
 * implementation which outputs qcode information via sockets on the server.
 * This is useful for having some other controller pick up and display the 
 * compilation and output information not running on the server itself. 
 * 
 * (also sfdc uses some jvm args which will throw a headless exception for 
 * anything swing)
 *
 * @author zkieda
 * @since 190
 * @version 0.3
 */
public class QCodeSocketServerOut implements CompilationServerOut{
    private final int compilationOutPort, qCodeOutPort, qCodeErrPort;
    private ServerSocket compilationOutSocket, qCodeOutSocket, qCodeErrSocket;
    private ServerOutputPort compilationOut, qCodeOut, qCodeErr;
    
    /**
     * Note that if you choose the same port for multiple outputs, we will create only 
     * one output stream for multiple.<br/><br/>
     * 
     * For negative numbers : we choose free ports, and all that are the same negative number will be 
     * redirected to the same output. For example : 
     * <pre>
     * @code{
     *     new QCodeSocketServerOut(-1, -1, -2)
     * }
     * </pre>
     * will assign a new port shared between the compilationOutPort, and the qCodeOutPort.
     * This will also assign a unique port to qCodeErrPort. 
     * @param compilationOutPort the port we will use for compilation output, or 0 for a system selected free port
     * @param qCodeOutPort the port we will use for the output of the qcode program, or 0 for a system selected free port
     * @param qCodeErrPort the port we will use for the err output of the qcode program, or 0 for a system selected free port
     */
    public QCodeSocketServerOut(int compilationOutPort, int qCodeOutPort, int qCodeErrPort) 
            throws IOException{
        
    	//TODO zak this is terrible.

        //todo custom structure for finding out groups in a series of integers. If negative, make a 
        //unique one.
    	
    	//generalize --> comparables, grouping equivalents.
    	//we can then use Function<...> on each group. This will be used to get the port, server 
    	//socket, and output stream of each.  
        
    	//TODO zak (trivial refactoring) ServerSocketDecider uses above structure, has methods 
    	//getComplSocket, getQOutSocket, getQErrSocket, getComplPort ... 
    	
    	//TODO zak important : have a way for us to block till a user connects. We can then boot up 
    	//qcode, then have enough time to boot up a listener without race condition or losing data
    	
        if(compilationOutPort < 0){
            compilationOutSocket = new ServerSocket(0);
            compilationOutPort = compilationOutSocket.getLocalPort();
            compilationOut = new ServerOutputPort(compilationOutSocket);
            
            if(qCodeOutPort == compilationOutPort){
                qCodeOutSocket = compilationOutSocket;
                qCodeOutPort = compilationOutPort;
                qCodeOut = compilationOut;
            }
            if(qCodeErrPort == compilationOutPort){
                qCodeErrSocket = compilationOutSocket;
                qCodeErrPort = compilationOutPort;
                qCodeErr = compilationOut;
            }
            compilationOut.run();
        }
        if(qCodeOutPort < 0){
            qCodeOutSocket = new ServerSocket(0);
            qCodeOutPort = qCodeOutSocket.getLocalPort();
            qCodeOut = new ServerOutputPort(qCodeOutSocket);
            if(qCodeErrPort == qCodeOutPort){
                qCodeErrPort = qCodeOutPort;
                qCodeErrSocket = qCodeOutSocket;
                qCodeErr = qCodeOut;
            }
            qCodeOut.run();
        }
        if (qCodeErrPort < 0) {
            qCodeErrSocket = new ServerSocket(0);
            qCodeErrPort = qCodeErrSocket.getLocalPort();
            qCodeErr = new ServerOutputPort(qCodeErrSocket);
            qCodeErr.run();
        }
        
        this.compilationOutPort = compilationOutPort;
        this.qCodeErrPort = qCodeErrPort;
        this.qCodeOutPort = qCodeOutPort;
        
        if(qCodeErrSocket == null) {
        	qCodeErrSocket = new ServerSocket(qCodeErrPort);
        	qCodeErr = new ServerOutputPort(qCodeErrSocket);
        	qCodeErr.run();
        }
        if(qCodeOutSocket == null) {
        	qCodeOutSocket = new ServerSocket(qCodeOutPort);
        	qCodeOut = new ServerOutputPort(qCodeOutSocket);
        	qCodeOut.run();
        }
        if(compilationOutSocket == null){ 
        	compilationOutSocket = new ServerSocket(compilationOutPort);
        	compilationOut = new ServerOutputPort(compilationOutSocket);
        	compilationOut.run();
        }
    }
    
    @Override
    public OutputStream getCompilationOut() {
        return compilationOut;
    }

    @Override
    public OutputStream getQCodeOut() {
        return qCodeOut;
    }

    @Override
    public OutputStream getQCodeErr() {
        return qCodeErr;
    }
    
    public int getComplOutPort() {
		return compilationOutPort;
	}
    public int getqCodeOutPort() {
		return qCodeOutPort;
	}
    public int getqCodeErrPort() {
		return qCodeErrPort;
	}
}