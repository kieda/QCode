package com.salesforce.zkieda.qcode.drivers;

import java.io.IOException;
import java.net.URISyntaxException;

import com.google.common.base.Joiner;
/**
 * main function for making qcode THIS IS SHADY  SUPER CONFIDENTIAL TM SALESFORCE DO NOT LOOK
 */
public class QMain {
//    public final static String CLASS_NAME = "$Pool$";
//    public static int VERSION = 0;
	
    //used for testing. 
    public static void main(String[] args) throws IOException, URISyntaxException, Exception{
    	
        System.out.println(
                Joiner.on('|').join(new String[]{"", "asdf"})
            );
//        File f = new File(QMain.class.getResource("Hello.qc").toURI());
//        Path p = f.toPath();
//        
//        //send output through ports 8376, 8378, 8326
//        //we use the class name '$Pool$' in package 'drivers' in folder 'bin' (relative to 
//        //this project's folder)
//        new BasicQCodeServer(8376, 8378, 8326, "./bin/drivers.$Pool$", p).start();
    }
}