package org.zkieda.qcode.lang;

import java.io.*;

import org.zkieda.qcode.server.JavaOutputPath;

public class Parsers {
    /** shady default methods */
    private static final String[] DEFAULT_METHODS = {
        "public static void sout(Object o){System.out.println(o);}",
        "public static void sout(int o){System.out.println(o);}",
        "public static void sout(char o){System.out.println(o);}",
        "public static void sout(char[] o){System.out.println(o);}",
        "public static void sout(long o){System.out.println(o);}",
        "public static void sout(short o){System.out.println(o);}",
        "public static void sout(byte o){System.out.println(o);}",
        "public static void sout(boolean o){System.out.println(o);}",
        "public static void sout(){System.out.println();}",
        "public static final double PI = Math.PI;",
        "public static final double E = Math.E;",
    };
    
    /**
     * converts an input stream into a string
     * 
     * @param is the input stream
     * @return the input stream represented as a string
     * @throws IOException
     */
    public static String inputStreamToString(InputStream is) throws IOException{
        int len;
        StringBuilder sb = new StringBuilder(is.available());
        char[] chars = new char[is.available()];
        Reader r = new InputStreamReader(is);
        
        while((len = r.read(chars)) > 0){
            sb.append(chars, 0, len);
        }
        return sb.toString();
    }
    
    /**
     * takes markup and turns it into a string java class
     * @param classTxt the markup text
     * @return string java src form of the markup
     */
    public static String getClassForCompile(JavaOutputPath classPath, String classTxt){
        String[] s = parse(classTxt);
        if(s==null)return null;
        
        StringBuilder sb = new StringBuilder(classTxt.length() * 2);
        if(classPath.getJavaPackage()!=null)
            sb.append("package ")
              .append(classPath.getJavaPackage())
              .append(";");
         
             //header
         sb .append(s[3])
            .append("\nimport java.util.*;"
                   +"import static java.lang.Math.*;"
                   +"import java.math.*;"
                   +"public class ")
                       .append(classPath.getJavaClass()).append("{");
        
                        //method declarations 
                        for(String method : DEFAULT_METHODS)
                            sb.append(method);
                        sb.append('\n')
                          .append(s[1]);
                        
                        //main method
                        sb.append("\npublic Object main(){try{if(true){\n")
                          .append(s[0])
                          .append("\n}return null;}catch(Exception e){e.printStackTrace();return null;}}}\n")
                          
                          //definitions outside (class level)
                          .append(s[2])
                          .append('\n');
                        
        return sb.toString();
    }
    
    /**
     * @return the modified version of the text.
     * result[0] = default  : definitions within the main function
     * result[1] = <-- -->  : method definitions
     * result[2] = @-- --@  : class definitions
     * result[3] = #-- --#  : import definitions
     */
    public static String[] parse(String txt){
        char seek = '?';
        //which one are we seeking for? If we're not in an environment the char is ?
        
        char[] buf = txt.concat("  ").toCharArray();
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
            if(state)
                stuff[0].append(buf[i]);
        }
        if(seek != '?'){
            System.out.println("QCode Error: '--"+seek+"' expected.\nReached end of file before pattern was found.");
            return null;
        }
        return new String[]{stuff[0].toString(), stuff[1].toString(), stuff[2].toString(), stuff[3].toString()};
    }
}
