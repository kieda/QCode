package org.zkieda.qcode.frame;
//
//import java.awt.BorderLayout;
//import java.awt.Font;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.awt.event.KeyEvent;
//import java.awt.event.KeyListener;
//import java.awt.event.WindowEvent;
//import java.awt.event.WindowFocusListener;
//import javax.swing.JButton;
//import javax.swing.JFrame;
//import javax.swing.JPanel;
//import javax.swing.JScrollPane;
//import javax.swing.JTextPane;
//
//
//
///**
// * the frame that displays the text that we will compile and execute
// *
// * todo : use a file change listener, then just plug the system into gedit
// * todo : have an input stream / output stream to abstract away from this. 
// *        this will allow us to use another editor, and have that display the
// *        error messages
// */
//public final class Frame {
//    private Frame(){}
//    public static long begin_time = System.currentTimeMillis();
//    static final JFrame frame = new JFrame();
//    private static final JTextPane text_pane = new JTextPane();
//    private static final JPanel pane = new JPanel();
//    private static final JButton run = new JButton("Run");
//    public static final String WELCOME_MESSAGE = 
//  "/**\n"
//+ " * Welcome to QCode! It works (almost) the same as Java, except everything that\n"
//+ " * is in this file is compiled and run as the main method. \n"
////+ " * You can add in new methods in between these tokens:\n"
////+ " *                         <-- -->\n"
////+ " * You can add in new classes in between these tokens:\n"
////+ " *                         @-- --@\n"
////+ " * And you can add in imports between these tokens:\n"
////+ " *                         #-- --#\n"
//+ " * \n"
//+ " * The default/main class is named \"Pool\".\n"
//+ " * Note: you don't have to type out System.out.println(...), as sout(...) is\n"
//+ " * built in as the same function.\n"
//+ " * Note: ALT+R is a shortcut to compile and run. \n"
//+ "*/\n";
//    static boolean vis = true;
//    private static final Thread t = new Thread(new Runnable() {
//        @Override public void run() {
//            while(true){
//                try {
//                    if(flag && System.currentTimeMillis() - begin_time > 1000){
//                        flag = false;
//                        begin_time = System.currentTimeMillis();
//                        String s = getClassForCompile(); if(s==null)return;
////                        System.out.println(s);
//                        DiagnosticFrame.beginCompl();
//                        boolean worked = CompileJava.doCompilation(s);
//                        
//                        
//                        DiagnosticFrame.endCompl();
//                        
//                        if(worked){
//                            CompileJava.run();
//                        }
//                    }
//                    Thread.currentThread().sleep(30);
//                } catch (Exception e) {}
//            }
//        }
//    });
//    static boolean flag = false;
//    static int i = 0;
//    static {
//        run.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent ae) {
//                String s = getClassForCompile(); if(s==null)return;
//                
//                CompileJava.doCompilation(s);
//                CompileJava.run();
//            }
//        });
//        text_pane.addKeyListener(new KeyListener() {
//            @Override
//            public void keyTyped(KeyEvent ke) {}
//            @Override
//            public void keyPressed(KeyEvent ke) {
//                begin_time = System.currentTimeMillis();
//                flag = true;
//                if(ke.isAltDown() && ke.getKeyCode()==KeyEvent.VK_R){
////                    CompileJava.doCompilation(getClassForCompile());
//                    CompileJava.run();
//                }
//            }
//            @Override
//            public void keyReleased(KeyEvent ke) {}
//        });
//        
//        frame.addWindowFocusListener(new WindowFocusListener() {
//            @Override
//            public synchronized void windowGainedFocus(WindowEvent we) {
//                if(i == 2){ i = 0;return;}
//                i++;
//                if(!QMain.block){
//                    QMain.block2 = true;
//                    DiagnosticFrame.toFront();
//                    toFront();
//                }
//            }
//            @Override
//            public void windowLostFocus(WindowEvent we){
//                    QMain.block = false;
//                    QMain.block2 = true;
//            }
//        });
//        text_pane.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
//        text_pane.setText(WELCOME_MESSAGE);
//        pane.setLayout(new BorderLayout());
//        pane.add(run, "West");
//        frame.getContentPane().add(pane, "North");
//        
//        frame.getContentPane().add(new JScrollPane(text_pane), "Center");
//        frame.setTitle("QCode");
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//    }
//   
//   
//    public static void init(int x, int y, int w, int h){
//        frame.setBounds(x, y, w, h);
//        frame.setVisible(true);
//        t.run();
//    }
//    /** shady default methods */
//    public static final String[] DEFAULT_METHODS = {
//        "public static void sout(Object o){System.out.println(o);}",
//        "public static void sout(int o){System.out.println(o);}",
//        "public static void sout(char o){System.out.println(o);}",
//        "public static void sout(char[] o){System.out.println(o);}",
//        "public static void sout(long o){System.out.println(o);}",
//        "public static void sout(short o){System.out.println(o);}",
//        "public static void sout(byte o){System.out.println(o);}",
//        "public static void sout(boolean o){System.out.println(o);}",
//        "public static void sout(){System.out.println();}",
//        "public static int pow(int d1, int d2){return (int)(Math.pow(d1, d2));}",
//        "public static final double PI = Math.PI;",
//        "public static final double E = Math.E;",
//    };
//    public static String getClassForCompile(){
//        String[] s = modifiedText(); if(s==null)return null;
////        System.out.println(Arrays.toString(s));
//        StringBuilder sb = new StringBuilder(s[3]).append("\nimport java.util.*;import static java.lang.Math.*; import java.math.*;public class ").append(QMain.CLASS_NAME).append("{");
//        for(String method : DEFAULT_METHODS){
//            sb.append(method);
//        }
//        sb.append('\n').append(s[1]);
////        sb.append("public static void main(String[] a){\n").append(modifiedText()).append("\n}}");
//        
//        sb.append("\npublic Object main(){if(true){\n").append(s[0]).append("\n}return null;}}\n").append(s[2]).append('\n');
//        return sb.toString();
//       
//    }
//
//    /**
//     * returns the modified version of the text.
//     * result[0] = default
//     * result[1] = <-- -->
//     * result[2] = @-- --@
//     * result[3] = #-- --#
//     * @return 
//     * todo - use thread local storage instead to make this safe
//     *      - make the class not completely static
//     */
//    static char seek = '?';//which one are we seeking for? If we're not in an envoronment the char is ?
//    public static String[] modifiedText(){
//        char[] buf = text_pane.getText().concat("  ").toCharArray();
//        StringBuilder[] stuff = {new StringBuilder(), new StringBuilder(), new StringBuilder(), new StringBuilder()};
//        int start_pos = -1;
//        for(int i = 0; ; i++){
//            boolean state = seek == '?';
//            if(i >= (state? buf.length-2 : buf.length-3) ) break;
//            if(buf[i+1]=='-' && buf[i+2]=='-'){
//                if(state) {
//                    start_pos = i+3;
//                    if(buf[i]=='<'){
//                        seek = '>'; i++;
//                    } else if(buf[i]=='@'){
//                        //we enter a thing for classes
//                        seek = '@'; i++;
//                    } else if(buf[i]=='#'){
//                        //we enter an environment for imports
//                        seek = '#'; i++;
//                    } else stuff[0].append(buf[i]);
//
//                } else{
//                    if(buf[i+3] == seek){
//                        
//                        byte opt;
//                        if(seek == '>'){
//                            opt = 1;
//                        }else if(seek == '@'){
//                            opt = 2;
//                        }else{
//                            //assert seek == '#'
//                            opt = 3;
//                        }
//                        stuff[opt].append(buf, start_pos, i+1-start_pos);
//                        i += 3;
//                        seek = '?';
//                    }
//                } continue;
//            }
////            stuff[0].append(buf[state?i:i+1]);
//            if(state)
//                stuff[0].append(buf[i]);
//        }
//        if(seek != '?'){
////            System.out.println(Arrays.toString(new String[]{stuff[0].toString(), stuff[1].toString(), stuff[2].toString(), stuff[3].toString()}));
//            DiagnosticFrame.setText("QCode Error: '--"+seek+"' expected.\nReached end of file before pattern was found.");
//            return null;
//        }
////        stuff[0].append(buf[buf.length-2]).append(buf[buf.length-1]);
//        return new String[]{stuff[0].toString(), stuff[1].toString(), stuff[2].toString(), stuff[3].toString()};//text_pane.getText();
//    }
//    public static void toFront(){
//        frame.toFront(); frame.repaint();
//    }
//}