package com.salesforce.zkieda.qcode.qcodeserver;

import java.io.IOException;
import java.nio.file.Path;

import com.salesforce.zkieda.qcode.lang.JavaClassParser;
import com.salesforce.zkieda.qcode.server.BasicEvictionPolicy;
import com.salesforce.zkieda.qcode.server.BasicServerOut;
import com.salesforce.zkieda.qcode.serverlistener.PathInputStreamWatcher;

/**
 * represents using a basic qcode server with the default settings
 *
 * todo - transfer 
 * @author zkieda
 * @since 190
 * @version 1.0
 */
public class BasicQCodeServer extends QCodeServer{
    /** 
     * the folder, package, and main class that we will listen to
     */
    public static final String
        DEFAULT_OUT_CLASSPATH = "bin/drivers.$Pool$";
    
    /**
     * prints intro information
     */
    private static void printro(int compPort, int outPort, int errPort, Path p) {
        System.out.format(
            "QCode Server\n[Listening on file : %s]\n[Compil Port : %d, QOut Port : %d, QErr Port : %d]\n",
            p,
            compPort,
            outPort,
            errPort
        );
    }
    
    /**
     * creates a new basic server for qcode using all of the standard types 
     * (a file path for the input, server ports for the output, and a basic 
     * eviction policy)
     *
     * Uses the default compilation folder, package, and class name
     * 
     * @param compilationPort clients that connect to this port will receive information about the compilation process (failures, successes, errors, etc)
     * @param outPort clients that connect to this port will receive the actual output of the program, and the result of running the QCode program
     * @param errPort clients that connect to this port will receive the actual error output of the qcode program, and any thrown errors that might occur
     * @param p the path where we will get our input from
     * @throws IOException if there are any problems reading the file or using the ports
     */
    public BasicQCodeServer(int compilationPort, int outPort, int errPort, Path p) throws IOException{        
        this(compilationPort, outPort, errPort, DEFAULT_OUT_CLASSPATH, p);
    }

    /**
     * creates a new basic server for qcode using all of the standard types 
     * (a file path for the input, server ports for the output, and a basic 
     * eviction policy)
     * 
     * @param compilationPort clients that connect to this port will receive information about the compilation process (failures, successes, errors, etc)
     * @param outPort clients that connect to this port will receive the actual output of the program, and the result of running the QCode program
     * @param errPort clients that connect to this port will receive the actual error output of the qcode program, and any thrown errors that might occur
     * @param outClassPath a non-null string in the form 
     * <pre>
     * @code{
     *    path/to/file/package.path.MainClassName
     * }
     * </pre><br/>
     * the program parses the string into its respective components<br/>
     * note that you can leave out the file path or the package path, or both. The only required field is the main class's name.
     * @param p the path where we will get our input from
     * @throws IOException if there are any problems reading the file or using the ports
     * @throws NullPointerException if {@code outClassPath} is {@code null}
     * @throws IllegalArgumentException if the string is not in the desired format
     */
    public BasicQCodeServer(int compilationPort, int outPort, int errPort, String outClassPath, Path p) throws IOException{
        super(
            new BasicEvictionPolicy(32, 1.f),
//            new QCodeSocketServerOut(compilationPort, outPort, errPort),
            new BasicServerOut(),
            new PathInputStreamWatcher(p),
            new JavaClassParser(outClassPath)
        );
        
        printro(outPort, errPort, errPort, p);
    }
}
