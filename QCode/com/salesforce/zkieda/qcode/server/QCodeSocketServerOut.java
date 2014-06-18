package com.salesforce.zkieda.qcode.server;

import java.io.*;
import java.net.*;
import java.util.Scanner;

import com.salesforce.zkieda.util.MultiBufferedOutputStream;

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
    private int compilationOutPort, qCodeOutPort, qCodeErrPort;
    private ServerSocket compilationOutSocket, qCodeOutSocket, qCodeErrSocket;
    
    
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
        
        //terrible design...

        //todo custom structure for finding out groups in a series of integers. If negative, make a 
        //unique one.
        
        
        if(compilationOutPort < 0){
            compilationOutSocket = new ServerSocket(0);
            compilationOutPort = compilationOutSocket.getLocalPort();
            
            if(qCodeOutPort == compilationOutPort){
                qCodeOutSocket = compilationOutSocket;
                qCodeOutPort = compilationOutPort;
            }
            if(qCodeErrPort == compilationOutPort){
                qCodeErrSocket = compilationOutSocket;
                qCodeErrPort = compilationOutPort;
            }
        }
        if(qCodeOutPort < 0){
            qCodeOutSocket = new ServerSocket(0);
            qCodeOutPort = qCodeOutSocket.getLocalPort();
            if(qCodeErrPort == qCodeOutPort){
                qCodeErrPort = qCodeOutPort;
                qCodeErrSocket = qCodeOutSocket;
            }
        }
        if (qCodeErrPort < 0) {
            qCodeErrSocket = new ServerSocket(0);
            qCodeErrPort = qCodeErrSocket.getLocalPort();
        }
        
        this.compilationOutPort = compilationOutPort;
        this.qCodeErrPort = qCodeErrPort;
        this.qCodeOutPort = qCodeOutPort;
        
        if(qCodeErrSocket == null) qCodeErrSocket = new ServerSocket(qCodeErrPort);
        if(qCodeOutSocket == null) qCodeOutSocket = new ServerSocket(qCodeOutPort);
        if(compilationOutSocket == null) compilationOutSocket = new ServerSocket(compilationOutPort);
    }
    
    
    //todo : have an outputstream which aggregates connected clients. Then we will send a 
    //message from one, which will cause a thread pool to send a series of messages to the 
    //connected clients. We will attempt to buffer it.   
    
    //also have a custom structure + thread for connecting clients. These clients will be put 
    //into a list for us to send information to,
    
    //todo custom structure for this
    
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
        ).start();;
        
        
        Socket socket = new Socket(InetAddress.getLocalHost(), 1234);
        Scanner s = new Scanner(socket.getInputStream());
        
        while(s.hasNextLine()){
            System.out.println(s.nextLine());
        }
    }
}
