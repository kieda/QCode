package org.zkieda.qcode.drivers;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;

import org.zkieda.qcode.qcodeserver.BasicQCodeServer;

/**
 * TODOS - 
 *      2 - refactor to make the compilation and output abstract
 *          a - should be able to handle java, scriptenginemanager, etc.
 *          b - ideally, we should have a set of items we are listening on. We define 
 *              what we should do based on the file type and the location of the file.
 *              This is contained in some xml file or json or something.
 *      3 - Make a new class that fits into this - recompiles java, reloads it
 *      4 - find out what's up with printing
 *      5 - Make spec for streamed data
 *      6 - implement the spec. Add in extras.
 *      7 - have listener generate a web-page based on the streamed data
 *      8 - implement for java, scriptenginemanager
 *      
 *      9 - implement advanced class reloading
 *      10 - implement class persistance
 *      11 - implement OSGi version 
 *      12 - implement for xml, json, etc. 
 *      
 * @author zkieda
 */
public class QMain {
//    public final static String CLASS_NAME = "$Pool$";
//    public static int VERSION = 0;
	
    //used for testing. 
    public static void main(String[] args) throws IOException, URISyntaxException, Exception{
    	
//        System.out.println(
//                Joiner.on('|').join(new String[]{"", "asdf"})
//            );
        File f = new File("qc/Test.qc");
        Path p = f.toPath();
//        
//        //send output through ports 8376, 8378, 8326
//        //we use the class name '$Pool$' in package 'drivers' in folder 'bin' (relative to 
//        //this project's folder)
        new BasicQCodeServer(8376, 8378, 8326, "./bin/drivers.$Pool$", p).start();
    }
}