package com.salesforce.zkieda.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * an output stream that writes to nothing
 * @author zkieda
 */
public class DevNullStream {
	public static class DevNullOutputStream extends OutputStream{ 
		@Override public void write(int b) throws IOException {}
	}
	public static class DevNullInputStream extends InputStream{
		@Override public int read() throws IOException {return -1;}
	}
	public static final DevNullOutputStream NULL_OUTPUT_STREAM
		= new DevNullOutputStream();
	public static final DevNullInputStream NULL_INPUT_STREAM
		= new DevNullInputStream();

}
