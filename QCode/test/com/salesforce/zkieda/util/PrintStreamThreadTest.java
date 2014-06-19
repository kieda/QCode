package com.salesforce.zkieda.util;

import static org.junit.Assert.assertTrue;

import java.io.*;
import java.util.Scanner;

import org.junit.Test;

import com.google.common.base.Joiner;

public class PrintStreamThreadTest {
	private static String[] inTest1Lines = {"this is a test","new line"};
	private static String[] inTest2Lines = {"hello","world!", "sup?"};
	private static String[] inTest3Lines = {};
	private static String[] inTest4Lines = {""};
	
	
	
	//replace the system's IO with one that can have different 
    //io for each thread
    static{
        ThreadIO.replaceSystemIO();
    }
    
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
            	
            	int i = 0;
            	for(; s.hasNextLine(); i++){
            		assertTrue(lines[i].equals(s.nextLine()));
            	}
            	
            	assertTrue(lines.length == i);
            	s.close();
            }
        }).dup2(System.in, stringInputStream(Joiner.on('\n').join(lines)))
          .start();
	}
	
	@Test
	public void testDup2InLines1() {
		checkRead(inTest1Lines);
	}
	@Test
	public void testDup2InLines2() {
	    checkRead(inTest2Lines);
	}
	@Test
	public void testDup2InLines3() {
	    checkRead(inTest3Lines);
	}
	@Test
	public void testDup2InLines4() {
	    checkRead(inTest4Lines);
	}
	
	/**
	 * Creates an output stream that asserts that each character 
	 * matches the string<br/><br/>
	 * 
	 * Note that when this is closed, we also assert that we are at 
	 * the end of the string (so the entire string has been properly 
	 * printed)
	 * 
	 * @param s a string we will check the output stream
	 * @return an ouptut stream that asserts that each printed char matches up
	 * with each char of the string, and that too many characters are not 
	 * written
	 */
	private static OutputStream checkOutputStreamString(final String s){
		return new OutputStream() {
			private int pos = 0;
			@Override
			public void write(int b) throws IOException {
				assertTrue(pos < s.length());
				assertTrue(s.charAt(pos) == b);
				pos++;
			}
			@Override
			public void close() throws IOException {
			    assertTrue(s, pos == s.length());
			    super.close();
			}
		};
		
	}
	
	/**
	 * fires a new thread where we print one string to standard out, and another to 
	 * standard err. We dup2 the outputs to check that the strings are correctly 
	 * printed 
	 * @param outString the string to be printed to standard error
	 * @param errString the string to be printed to standard out
	 */
	private static void fireCheckThreadWithOutputs(final String outString, final String errString){
	    new PrintStreamThread(new Runnable(){
            @Override public void run(){ 
               System.out.println(errString); 
               System.err.println(outString); 
               
               System.err.close();
               System.out.close();
            }
        })
        .dup2(System.err, new PrintStream(checkOutputStreamString(outString)))
        .dup2(System.out, new PrintStream(checkOutputStreamString(errString)))
        .start();
	}
	
	@Test
	public void testIndependentDup2() {
		final String errString = "ERROR : hello world!\nThis is an error!";
		final String outString = "OUTPUT : hello world!\nThis is output!";
		
		//check that the threads independently print to different output streams
		//and do not interfere with each other
		fireCheckThreadWithOutputs(outString, errString);
		fireCheckThreadWithOutputs(errString, outString);
        
		//check that normal printing is not affected
        //manually assert that 'bye out' is printed to system.out
        //manually assert that 'bye err' is printed to system.err
        System.out.println("bye out!");
        System.err.println("bye err!");
	}
}