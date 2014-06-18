package com.salesforce.zkieda.qcode.server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

import com.salesforce.zkieda.util.MultiBufferedOutputStream;
import com.salesforce.zkieda.util.PrintStreamThread;
import com.salesforce.zkieda.util.ThreadIO;

/**
 * Updates all of the clients that connect to a server socket to whatever is printed to this 
 * output stream
 * @temp
 * @I_want_to send {a message, information} to muliple {clients, sockets}
 * @author zkieda
 */
public class ServerOutputPort extends OutputStream implements Runnable{
    private final MultiBufferedOutputStream outputInternal;
    private final ServerSocket serverSocket;
    private static final int LISTENER_PRIORITY = 2;
    private Thread socketGatherer;
    private final AtomicBoolean cont = new AtomicBoolean(true);
    private final PrintStream errorPrintStream;
    
    private final Runnable serverSocketListener = new Runnable() {
		@Override
		public void run() {
			while(cont.get()){
				try{
					Socket socket = serverSocket.accept();
					outputInternal.addOutputStream(socket.getOutputStream());
				} catch(IOException e){
					if(errorPrintStream != null)
						e.printStackTrace(errorPrintStream);
				}
			}
		}
	};
	
	/**
	 * any errors that occur when a client connects are printed to 
	 * {@code System.err} by default. 
	 * 
	 * @param serverSocket the socket we will send messages to
	 */
	public ServerOutputPort(ServerSocket serverSocket){
		this(serverSocket, System.err);
	}
	
    public ServerOutputPort(ServerSocket serverSocket, PrintStream errorPrintStream) {
        outputInternal = new MultiBufferedOutputStream();
        this.serverSocket = serverSocket;
        this.errorPrintStream = errorPrintStream;
        initSocketGatherer();
    }
    
    private void initSocketGatherer(){
    	socketGatherer = new Thread(serverSocketListener);
        socketGatherer.setPriority(LISTENER_PRIORITY);
    }
    
    /**
     * starts listening for clients to connect to the socket on a new thread
     */
    @Override
    public void run() {
    	if(!cont.get()) {
    		cont.set(true);
    		initSocketGatherer();
    	}
    	socketGatherer.start();
    }
    
    /**
     * stops listening for clients to connect to the socket a new thread
     */
    public void stop(){
    	cont.set(false);
    }
    
    @Override
    public void write(int b) throws IOException {
    	outputInternal.write(b);
    }
    
    @Override
    public void flush() throws IOException {
    	outputInternal.flush();
    }
}
class ServerOutputPortTest{
	public static void main(String[] args) throws Exception{
		final int PORT_NUM = 1234;
		final ServerSocket ss = new ServerSocket(PORT_NUM);
		final ServerOutputPort os = new ServerOutputPort(ss);
		
		ThreadIO.replaceSystemOut();
		os.run();
		
		//print hello to port 1234 continuously
		new PrintStreamThread(
			new Runnable() {
				@Override
				public void run() {
					try {
						int i = 0;
						while(true){
							long id = Thread.currentThread().getId();
							System.out.println("ID:"+id + " "+ i);
							i++;
//							Thread.sleep(10);
						}

					} catch (Exception e) {}
					
				}
			}
		).dup2(System.out, new PrintStream(os, true))
		 .start();
		
		//listener that prints output from port 1234 and broadcasts its output
//		for(int i = 0; i < 4; i++)
		new Thread(){
			@Override
			public void run(){
				try{
					Socket socket = new Socket(InetAddress.getLocalHost(), PORT_NUM);
			        Scanner s = new Scanner(socket.getInputStream());
			        
			        while(s.hasNextLine()){
			            System.out.println(Thread.currentThread().getId() + " " + s.nextLine());
			        }
				}catch(Exception e){}
			}
		}.start();
	}
}
