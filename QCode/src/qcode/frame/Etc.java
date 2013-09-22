package qcode.frame;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;
import qcode.QCode;

public class Etc {
//    private QCode instance;
//    public Etc(QCode instance){
//        assert instance !=null;
//        this.instance = instance;
//    }
    public static void save(File loc, String text){
        assert loc !=null &&loc.canWrite() & text!=null;
        try{
            FileWriter wf= new FileWriter(loc);
            wf.write(text);
            wf.close();
        }catch(Exception e){}
    }
    
    public static String read(File loc) {
        try {
            BufferedReader bbr = new BufferedReader(new FileReader(loc));
            char[] text = new char[(int)(loc.length())];
            bbr.read(text);
            return new String(text);
        } catch (Exception e) {
            return null;
        }        
    }
}
