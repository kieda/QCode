package org.zkieda.qcode.frame;
//
//import java.awt.BorderLayout;
//import java.awt.Color;
//import java.awt.Font;
//import java.awt.event.WindowEvent;
//import java.awt.event.WindowFocusListener;
//import javax.swing.JFrame;
//import javax.swing.JLabel;
//import javax.swing.JPanel;
//import javax.swing.JScrollPane;
//import javax.swing.JTextPane;
//
///**
// * frame for showing system diagnostics (whether or not the compilation was
// * successful or not )
// */
//public class DiagnosticFrame {
//    private DiagnosticFrame(){}
//    public static long begin_time = System.currentTimeMillis();
//    static final JFrame frame = new JFrame();
//    private static final JTextPane text_pane = new JTextPane();
//    private static final JPanel pane = new JPanel();
//    private static final JLabel compiling = new JLabel("Compiling...");
//    private static final Color DARK_GREEN = new Color(0,166,30);
//    
//    //do not look here -- super shady shiat
//    static boolean vis = false;
//    
//    static {
//        text_pane.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
//        pane.setLayout(new BorderLayout());
//        pane.add(compiling, "West");
//        text_pane.setForeground(DARK_GREEN);
//        text_pane.setEditable(false);
//        compiling.setVisible(false);
//        frame.getContentPane().add(pane, "North");
//        
//        frame.addWindowFocusListener(new WindowFocusListener() {
//            @Override
//            public synchronized void windowGainedFocus(WindowEvent we) {
//                if(!QMain.block2){
//                    QMain.block = true;
//                    Frame.toFront();
//                    toFront();
//                    
//                }
//            }
//            @Override
//            public void windowLostFocus(WindowEvent we) {QMain.block2 = false;}
//        });
//        frame.getContentPane().add(new JScrollPane(text_pane), "Center");
//        frame.setTitle("Diagnostic Report");
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//    }
//    public static void init(int x, int y, int w, int h){
//        frame.setBounds(x, y, w, h);
////        setBoundPercentage(x_per,y_per);
//        frame.setVisible(true);
//    }
//    public static void beginCompl(){
//        compiling.setVisible(true);
//    }
//    public static void endCompl(){
//        compiling.setVisible(false);
//    }
//    private static String current_text;
//    public static void setText(String tex){
//        current_text = tex;
//        text_pane.setForeground(Color.RED);
//        text_pane.setText(tex);
//    }
//    
//    public static void success(){
//        text_pane.setForeground(DARK_GREEN);
//        final String success = "Compilation Successful.";
//        text_pane.setText(success);
//        current_text = "Compilation Successful.";
//    }
//    public static void setResult(String res){
//        text_pane.setText(current_text + "\nResult : \n\n"+res);
//    }
//    public static void resetText(){    
//        text_pane.setText(current_text);
//    }
//    public static void toFront(){
//        frame.toFront(); frame.repaint();
//    }
//}