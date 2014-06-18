package test.com.salesforce.zkieda.util;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.Scanner;

import org.junit.BeforeClass;
import org.junit.Test;

import com.salesforce.zkieda.util.PrintStreamThread;
import com.salesforce.zkieda.util.ThreadIO;
import com.sun.xml.internal.ws.util.StringUtils;

public class PrintStreamThreadTest {
	private static String[] inTest1Lines = {"this is a test","new line"};
	private static String[] inTest2Lines = {"hello","world!", "sup?"};
	
	private static InputStream stringInputStream(final String s){
		return new InputStream() {
			Reader r = new StringReader(s);
			@Override
			public int read() throws IOException {
				// TODO Auto-generated method stub
				return r.read();
			}
		};
	}
	
	@BeforeClass
	public void beforeClass(){
		//replace the system's IO with one that can have different 
        //io for each thread
        ThreadIO.replaceSystemIO();
	}
	@Test
	public void testSystemIOIsReplaced(){
		assertTrue(System.out instanceof ThreadIO.ThreadPrintStream);
		assertTrue(System.err instanceof ThreadIO.ThreadPrintStream);
		assertTrue(System.in instanceof ThreadIO.ThreadInputStream);
	}
	
	private static void checkRead(final String[] lines){
		new PrintStreamThread(new Runnable(){
            @Override public void run(){  
            	Scanner s = new Scanner(System.in);
            	
            	for(int i = 0; s.hasNextLine(); i++){
            		assertTrue(lines[i].equals(s.nextLine()));
            	}
            }
        }).dup2(System.in, stringInputStream(StringUtils.join(lines, "\n"))).start();
	}
	
	@Test
	public void testDup2In() {
		checkRead(inTest1Lines);
		checkRead(inTest2Lines);
		
		Scanner s = new Scanner(stringInputStream(StringUtils.join(inTest1Lines, "\n")));
    	
    	for(int i = 0; s.hasNextLine(); i++){
    		assertTrue(inTest1Lines[i].equals(s.nextLine()));
    	}
	}
	private static OutputStream checkString(final String s){
		return new OutputStream() {
			private int pos = 0;
			@Override
			public void write(int b) throws IOException {
				assertTrue(pos < s.length());
				assertTrue(s.charAt(pos) == b);
				pos++;
			}
		};
	}
	@Test
	public void testDup2() {
		final String errString = "hello world!\nThis is an error!";
		final String outString = "hello world!\nThis is output!";
		final OutputStream os1 = checkString(errString);
		final OutputStream os2 = checkString(outString);
		
        new PrintStreamThread(new Runnable(){
            @Override public void run(){ 
               System.out.println(outString); 
               System.err.println(errString); 
            }
        })
        .dup2(System.err, new PrintStream(os1))
        .dup2(System.out, new PrintStream(os2))
        .start();
        
        new PrintStreamThread(new Runnable(){
            @Override public void run(){ 
               System.out.println(errString); 
               System.err.println(outString); 
            }
        })
        .dup2(System.err, new PrintStream(checkString(outString)))
        .dup2(System.out, new PrintStream(checkString(errString)))
        .start();
        
        System.out.println("bye out!");
        System.err.println("bye err!");
	}
}