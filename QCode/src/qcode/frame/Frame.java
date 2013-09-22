package qcode.frame;

import qcode.compl.CompileJava;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import qcode.QCode;

/**
 * note : most of QCode was made in a day (it's not very expandable or well 
 * written).
 */
public class Frame {
    private QCode env;
    public Frame(QCode env){this.env=env;}
    public long begin_time = System.currentTimeMillis();
    final JFrame frame = new JFrame();
    private final JTextPane text_pane = new JTextPane();
    private final JPanel pane = new JPanel();
    private final JButton run = new JButton("Run");
    public final String WELCOME_MESSAGE = 
  "/**\n"
+ " * Welcome to QCode! It works (almost) the same as Java, except everything that\n"
+ " * is in this file is compiled and run as the main method. \n"
//+ " * You can add in new methods in between these tokens:\n"
//+ " *                         <-- -->\n"
//+ " * You can add in new classes in between these tokens:\n"
//+ " *                         @-- --@\n"
//+ " * And you can add in imports between these tokens:\n"
//+ " *                         #-- --#\n"
+ " * \n"
+ " * The default/main class is named \"Pool\".\n"
+ " * Note: you don't have to type out System.out.println(...), as sout(...) is\n"
+ " * built in as the same function.\n"
+ " * Note: ALT+R is a shortcut to compile and run. \n"
+ "*/\n";
    boolean vis = true;
    private final Thread t = new Thread(new Runnable() {
        @Override public void run() {
            while(true){
                try {
                    if(flag && System.currentTimeMillis() - begin_time > 1000)t:{
                        flag = false;
                        begin_time = System.currentTimeMillis();
                        String s = getClassForCompile(); 
                        if(s!=null){
//                        System.out.println(s);
                            env.df.beginCompl();
                            boolean worked = CompileJava.doCompilation(s, env);

                            env.df.endCompl();

                            if(worked){
                                CompileJava.run(env);
                            }
                        }
                    }
                    Thread.currentThread().sleep(30);
                } catch (Exception e) {e.printStackTrace();}
            }
        }
    });
    boolean flag = false;
    int i = 0;
    {
        run.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                String s = getClassForCompile(); if(s==null)return;
                CompileJava.doCompilation(s, env);
                CompileJava.run(env);
            }
        });
        text_pane.addKeyListener(new KeyListener() {
            final FileFilter qFil = new FileNameExtensionFilter("qcode files", "qcode");
            final FileFilter jFil = new FileNameExtensionFilter("java files", "java");
            File jF;
            File qF;
            {
                try {
                    jF = new File(new File("./src/qcode/fun_sources/Pool.java").getCanonicalPath());
                    qF = new File(new File("./src/qcode/fun_sources/Pool.qcode").getCanonicalPath());
                } catch (Exception e) {}
            }
            @Override
            public void keyTyped(KeyEvent ke) {}
            @Override
            public void keyPressed(KeyEvent ke) {
                begin_time = System.currentTimeMillis();
                flag = true;
                if(ke.isAltDown() && ke.getKeyCode()==KeyEvent.VK_R){
//                    CompileJava.doCompilation(getClassForCompile());
                    CompileJava.run(env);
                }
                if(ke.isControlDown() && ke.getKeyCode()==KeyEvent.VK_S){
                    JFileChooser jfc = new JFileChooser();

                    FileFilter filter;
                    String text=null;
                    if(ke.isAltDown()){//save java
                        filter = qFil;
                        text = text_pane.getText();
                        try {
                            //set the default name
                            jfc.setSelectedFile(qF);
                        } catch (Exception e) {}
                        
                    }else{//save file
                        filter = jFil;
                        
                        try {
                            jfc.setSelectedFile(jF);
                        } catch (Exception e) {}
                    }
                    jfc.addChoosableFileFilter(filter);

                    int ret = jfc.showSaveDialog(null);//showDialog(null, "Save file");

                    if (ret == JFileChooser.APPROVE_OPTION) {
                        File file = jfc.getSelectedFile();
                        
                        if(text==null) text = getClassForCompile(file.getName().split("\\.")[0]);
                        
                        Etc.save(file, text);
                    }
                }
                if(ke.isControlDown() && ke.getKeyCode()==KeyEvent.VK_O){
                    JFileChooser jfc = new JFileChooser();

                    jfc.setFileFilter(qFil);
                    try {
                        //set the default name
                        jfc.setSelectedFile(qF);
                    } catch (Exception e) {}
                    int ret = jfc.showOpenDialog(null);
                    
                    if(ret == JFileChooser.APPROVE_OPTION){
                        File f = jfc.getSelectedFile();
                        
                        text_pane.setText(Etc.read(f));
                    }
                    flag=true;
                }
            }
            @Override
            public void keyReleased(KeyEvent ke) {}
        });
        
        frame.addWindowFocusListener(new WindowFocusListener() {
            @Override
            public synchronized void windowGainedFocus(WindowEvent we) {
                if(i == 2){ i = 0;return;}
                i++;
                if(!env.block){
                    env.block2 = true;
                    env.df.toFront();
                    toFront();
                }
            }
            @Override
            public void windowLostFocus(WindowEvent we){
                    env.block = false;
                    env.block2 = true;
            }
        });
        text_pane.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        text_pane.setText(WELCOME_MESSAGE);
        pane.setLayout(new BorderLayout());
        pane.add(run, "West");
        frame.getContentPane().add(pane, "North");
        
        frame.getContentPane().add(new JScrollPane(text_pane), "Center");
        frame.setTitle("QCode");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
   
   
    public void init(int x, int y, int w, int h){
        frame.setBounds(x, y, w, h);
        frame.setVisible(true);
        t.start();
    }
    public static final String[] DEFAULT_METHODS = {
        
        "public static void sout(Object o){System.out.println(o);}",
        "public static void sout(int o){System.out.println(o);}",
        "public static void sout(char o){System.out.println(o);}",
        "public static void sout(char[] o){System.out.println(o);}",
        "public static void sout(long o){System.out.println(o);}",
        "public static void sout(short o){System.out.println(o);}",
        "public static void sout(byte o){System.out.println(o);}",
        "public static void sout(boolean o){System.out.println(o);}",
        "public static void sout(){System.out.println();}",
        "public static int pow(int d1, int d2){return (int)(Math.pow(d1, d2));}",
        "public static final double PI = Math.PI;",
        "public static final double E = Math.E;",
        //todo : append lists, append arrays, set operations.
        
        //todo : place these in a utililty class, then we just statically import
        //the methods we need.
        
        //todo : make a better system than this
        
    };
    public String getClassForCompile(){
        return getClassForCompile(env.CLASS_NAME);
    }
    
    public String getClassForCompile(String class_name){
        String[] s = modifiedText(); if(s==null)return null;
//        System.out.println(Arrays.toString(s));
        StringBuilder sb = new StringBuilder(s[3]).append("\nimport java.util.*;import static java.lang.Math.*; import java.math.*;public class ").append(class_name).append("{");
        for(String method : DEFAULT_METHODS){
            sb.append(method);
        }
        sb.append('\n').append(s[1]);
//        sb.append("public static void main(String[] a){\n").append(modifiedText()).append("\n}}");
        
        sb.append("\npublic Object main(){if(true){\n").append(s[0]).append("\n}return null;}}\n").append(s[2]).append('\n');
        return sb.toString();
    }
    
    
    //todo : make a modular system for modifying text. Sure, a hard coded DFA is
    //faster than java regexes, but they are a pain in the ass to code. 
    /**
     * returns the modified version of the text.
     * result[0] = default
     * result[1] = <-- -->
     * result[2] = @-- --@
     * result[3] = #-- --#
     * @return 
     */
    char seek = '?';//which one are we seeking for? If we're not in an environment the char is ?
    //please dear god change this shit
    public String[] modifiedText(){
        char[] buf = text_pane.getText().concat("  ").toCharArray();
        StringBuilder[] stuff = {new StringBuilder(), new StringBuilder(), new StringBuilder(), new StringBuilder()};
        int start_pos = -1;
        for(int i = 0; ; i++){
            boolean state = seek == '?';
            if(i >= (state? buf.length-2 : buf.length-3) ) break;
            if(buf[i+1]=='-' && buf[i+2]=='-'){
                if(state) {
                    start_pos = i+3;
                    if(buf[i]=='<'){
                        seek = '>'; i++;
                    } else if(buf[i]=='@'){
                        //we enter a thing for classes
                        seek = '@'; i++;
                    } else if(buf[i]=='#'){
                        //we enter an environment for imports
                        seek = '#'; i++;
                    } else stuff[0].append(buf[i]);

                } else{
                    if(buf[i+3] == seek){
                        
                        byte opt;
                        if(seek == '>'){
                            opt = 1;
                        }else if(seek == '@'){
                            opt = 2;
                        }else{
                            //assert seek == '#'
                            opt = 3;
                        }
                        stuff[opt].append(buf, start_pos, i+1-start_pos);
                        i += 3;
                        seek = '?';
                    }
                } continue;
            }
//            stuff[0].append(buf[state?i:i+1]);
            if(state)
                stuff[0].append(buf[i]);
        }
        if(seek != '?'){
            env.df.setText("QCode Error: '--"+seek+"' expected.\nReached end of file before pattern was found.");
            seek='?';
            return null;
        }
//        stuff[0].append(buf[buf.length-2]).append(buf[buf.length-1]);
        return new String[]{stuff[0].toString(), stuff[1].toString(), stuff[2].toString(), stuff[3].toString()};//text_pane.getText();
    }
    public void toFront(){
        frame.toFront(); frame.repaint();
    }
    
}
