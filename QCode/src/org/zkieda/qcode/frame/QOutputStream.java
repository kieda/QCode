package org.zkieda.qcode.frame;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Buffers an output stream to 
 *
 * @author zkieda
 */
public class QOutputStream extends OutputStream{
    private final OutputStream other;
    
    public QOutputStream(OutputStream other){
        this.other = other;
    }
    @Override
    public void write(int b) throws IOException {
        switch(b){
            case '(' :
                other.write('\\');
                other.write('(');
                break;
            case ')' :
                other.write('\\');
                other.write(')');
                break;
            case '\\' :
                other.write('\\');
                other.write('\\');
                break;
            default : 
                other.write(b);
        }
    }
}
