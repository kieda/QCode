package org.zkieda.qcode.server;

import java.io.OutputStream;

import org.zkieda.qcode.util.ThreadIO;

/**
 * Is a basic server out that uses all of the default outputs.  
 * 
 * @author zkieda
 * @see ThreadIO
 */
public class BasicServerOut implements CompilationServerOut{
    /** use default (typically System.out)*/
    @Override
    public OutputStream getCompilationOut() {
        return null;
    }

    /** use default (typically System.out) */
    @Override
    public OutputStream getQCodeOut() {
        return null;
    }

    /** use default (typically System.err)*/
    @Override
    public OutputStream getQCodeErr() {
        return null;
    }

}
