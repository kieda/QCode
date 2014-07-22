package org.zkieda.qcode.server;

import java.io.OutputStream;

/**
 * used for sending information out of the server
 * 
 * to use the default, console out/err, return null. 
 * 
 * @author zkieda
 */
public interface CompilationServerOut extends ProgramOutput {
    
    /**
     * @return the output stream associated with the compilation of qcode and of the 
     * resulting java class. If {@code null}, the qcode server should print to 
     * standard out.
     * 
     * It is not suggested to return {@code System.out} or {@code System.err}, since 
     * {@code System.out} might get redirected to {@code System.err}, and 
     * {@code System.err} could be redirected to {@code System.out}, which could cause 
     * an infinite loop. 
     */
    OutputStream getCompilationOut();
}
