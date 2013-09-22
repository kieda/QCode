package qcode.frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import qcode.QCode;

public class DiagnosticFrame {
    private QCode env;
    public DiagnosticFrame(QCode env){this.env = env;}
    public long begin_time = System.currentTimeMillis();
    final JFrame frame = new JFrame();
    private final JTextPane text_pane = new JTextPane();
    private final JPanel pane = new JPanel();
    private final JLabel compiling = new JLabel("Compiling...");
    private final Color DARK_GREEN = new Color(0,166,30);
    
    boolean vis = false;
    
    {
        text_pane.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        pane.setLayout(new BorderLayout());
        pane.add(compiling, "West");
        text_pane.setForeground(DARK_GREEN);
        text_pane.setEditable(false);
        compiling.setVisible(false);
        frame.getContentPane().add(pane, "North");
        
        frame.addWindowFocusListener(new WindowFocusListener() {
            @Override
            public synchronized void windowGainedFocus(WindowEvent we) {
                if(!env.block2){
                    env.block = true;
                    env.ff.toFront();
                    toFront();
                }
            }
            @Override
            public void windowLostFocus(WindowEvent we) {env.block2 = false;}
        });
        frame.getContentPane().add(new JScrollPane(text_pane), "Center");
        frame.setTitle("Diagnostic Report");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    public void init(int x, int y, int w, int h){
        frame.setBounds(x, y, w, h);
//        setBoundPercentage(x_per,y_per);
        frame.setVisible(true);
    }
    public void beginCompl(){
        compiling.setVisible(true);
    }
    public void endCompl(){
        compiling.setVisible(false);
    }
    private String current_text;
    public void setText(String tex){
        current_text = tex;
        text_pane.setForeground(Color.RED);
        text_pane.setText(tex);
    }
    
    public void success(){
        text_pane.setForeground(DARK_GREEN);
        final String success = "Compilation Successful.";
        text_pane.setText(success);
        current_text = "Compilation Successful.";
    }
    public void setResult(String res){
        text_pane.setText(current_text + "\nResult : \n\n"+res);
    }
    public void resetText(){    
        text_pane.setText(current_text);
    }
    public void toFront(){
        frame.toFront(); frame.repaint();
    }
}
