package com.salesforce.zkieda.qcode.server;

import java.io.OutputStream;

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
public class QCodeSocketServerOut implements QCodeServerOut{
//    private int compilationOutPort, qCodeOutPort, qCodeErrPort;
    
    public QCodeSocketServerOut(int compilationOutPort, int qCodeOutPort, int qCodeErrPort) {
        //todo
    }
    
    @Override
    public OutputStream getCompilationOut() {
        return null;
    }

    @Override
    public OutputStream getQCodeOut() {
        return null;
    }

    @Override
    public OutputStream getQCodeErr() {
        return null;
    }
    
}
