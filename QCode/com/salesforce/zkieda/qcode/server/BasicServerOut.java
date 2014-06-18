package com.salesforce.zkieda.qcode.server;

import java.io.OutputStream;

import com.salesforce.zkieda.util.ThreadIO;

/**
 * Is a basic server out that uses all of the default outputs.  
 * 
 * @author zkieda
 * @since 180
 * @see ThreadIO
 */
public class BasicServerOut implements QCodeServerOut{
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
