package qcode;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.util.Arrays;
import qcode.frame.DiagnosticFrame;
import qcode.frame.Frame;

/**
 * you initialize a new instance of QCode from here. 
 * 
 * @author zkieda
 */
public class QCode {
    public QCode(){}
    
    public final String CLASS_NAME = "Pool";
    private final Dimension screen_dim = Toolkit.getDefaultToolkit().getScreenSize();
    public boolean setup = false;
    public boolean block = false; 
    public boolean block2 = false;
    public DiagnosticFrame df;
    public Frame ff;
    
    public void init() {
        float x_percent = .75f;
        float y_percent = .75f;
        float div = 2f/3f;
        int space = 10;
        x_percent *= screen_dim.width;
        y_percent *= screen_dim.height;
        
        float x_left = (int) (screen_dim.width - x_percent)>>1;
        df = new DiagnosticFrame(QCode.this);
        ff = new Frame(QCode.this);
        df.init((int)(x_left + x_percent*div) + space/2,
                (int) (screen_dim.height-y_percent)>>1,
                (int)(x_percent*(1-div)),
                (int)(y_percent));
        
        ff.init((int) x_left - space/2,
                (int) (screen_dim.height-y_percent)>>1,
                (int)(x_percent*div),
                (int)(y_percent));
        
    }
}
